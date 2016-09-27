package controllers.businessLogic.Assessment;

import api.GoogleSheetHttpRequest;
import api.ServerConstants;
import api.http.httpRequest.AssessmentRequest;
import api.http.httpResponse.AssessmentSubmissionResponse;
import api.http.httpResponse.CandidateJobPrefs;
import models.entity.Candidate;
import models.entity.OM.CandidateAssessmentAttempt;
import models.entity.OM.CandidateAssessmentResponse;
import models.entity.OM.JobPreference;
import models.entity.Static.AssessmentQuestion;
import models.entity.Static.JobRole;
import play.Logger;
import play.api.Play;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static models.util.Util.RoundTo1Decimals;
import static play.libs.Json.toJson;

/**
 * Created by zero on 15/9/16.
 */
public class AssessmentService {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static List<AssessmentQuestion> getQuestions(List<Long> jobRoleIdList) {
        List<AssessmentQuestion> assessmentQuestionList = new ArrayList<>();
        if (jobRoleIdList.size() > 0) {
            assessmentQuestionList.addAll(AssessmentQuestion.find.where().in("jobRoleId", jobRoleIdList).orderBy().asc("jobRoleId").findList());
        }
        return assessmentQuestionList;
    }

    public static AssessmentSubmissionResponse addAssessedInfoToGS(AssessmentRequest assessmentRequest, Long candidateId) {
        AssessmentSubmissionResponse response = new AssessmentSubmissionResponse();
        if (candidateId != null) {
            Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
            if (!assessmentRequest.getResponseList().isEmpty()) {

                List<AssessmentSheetCol> colList = new ArrayList<>();
                List<AssessmentRequest.AssessmentOption> optionList = assessmentRequest.getResponseList();
                List<Long> assessmentQuestionIdList = new ArrayList<>();
                List<Long> assessmentJobRoleIdList = new ArrayList<>();

                optionList.sort((o1, o2) -> o1.getJobRoleId() >= o2.getJobRoleId() ? 1 : 0);

                for (AssessmentRequest.AssessmentOption option : optionList) {
                    assessmentQuestionIdList.add(option.getAssessmentQuestionId());
                    if (!assessmentJobRoleIdList.contains(option.getJobRoleId())) {
                        assessmentJobRoleIdList.add(option.getJobRoleId());
                    }
                }
                int optionSize = optionList.size();
                if (optionSize == 0) {
                    response.setStatus(AssessmentSubmissionResponse.Status.UNKNOWN);
                    Logger.info(String.valueOf(toJson(response)));
                    return response;
                }
                Long prevJobRoleId = optionList.get(0).getJobRoleId();

                List<AssessmentQuestion> assessmentQuestionList = AssessmentQuestion.find.where().in("assessmentQuestionId", assessmentQuestionIdList).findList();
                for (int i = 0; i < optionSize; i++) {
                    AssessmentSheetCol assessmentSheetCol = new AssessmentSheetCol();
                    assessmentSheetCol.setQuestion(assessmentQuestionList.get(i));
                    assessmentSheetCol.setCandidateAnswer(optionList.get(i).getAssessmentResponse());
                    colList.add(assessmentSheetCol);
                    if (prevJobRoleId != optionList.get(i).getJobRoleId() || (i == optionSize - 1)) {
                        try {
                            JobRole jobRole;
                            if (prevJobRoleId != null || (i == optionSize - 1)) {
                                jobRole = JobRole.find.where().eq("jobRoleId", prevJobRoleId).findUnique();
                                saveAttemptAndWriteToGS(candidate, jobRole, colList);
                                colList = new ArrayList<>();
                            }
                            if (i == optionSize - 1 && prevJobRoleId != optionList.get(i).getJobRoleId() && !colList.isEmpty()) {
                                jobRole = JobRole.find.where().eq("jobRoleId", optionList.get(i).getJobRoleId()).findUnique();
                                saveAttemptAndWriteToGS(candidate, jobRole, colList);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        prevJobRoleId = optionList.get(i).getJobRoleId();
                    }
                }
                if (shouldBeMarkedAsAssessed(getJobPrefVsIsAssessedList(candidate.getCandidateId(), candidate.getJobPreferencesList()))) {
                    candidate.setCandidateIsAssessed(ServerConstants.CANDIDATE_ASSESSED);
                    candidate.candidateUpdate();
                    response.setStatus(AssessmentSubmissionResponse.Status.ALL_ASSESSED);
                    Logger.info(String.valueOf(toJson(response)));
                    return response;
                }
                response.setStatus(AssessmentSubmissionResponse.Status.SUCCESS);
                response.setJobRoleId(prevJobRoleId);
                Logger.info(String.valueOf(toJson(response)));
                return response;
            }
        }

        response.setStatus(AssessmentSubmissionResponse.Status.FAILED);
        Logger.info(String.valueOf(toJson(response)));
        return response;
    }

    private static void saveAttemptAndWriteToGS(Candidate candidate, JobRole jobRole, List<AssessmentSheetCol> colList) throws UnsupportedEncodingException {
        /* save response to db for each attempt */

        CandidateAssessmentAttempt caAttempt = new CandidateAssessmentAttempt();
        caAttempt.setCandidate(candidate);
        caAttempt.setJobRole(jobRole);
        caAttempt.setCandidateAssessmentResponseList(getAssessmentResponses(colList));
        Double score = calculateAttemptScore(caAttempt.getCandidateAssessmentResponseList());
        if(score!=null){
            caAttempt.setResult(RoundTo1Decimals(score));
        } else {
            // since we don't have correct answers for these questions, scrore is not getting calc for these
            caAttempt.setResult(score);
        }
        caAttempt.save();

        /* write response to google sheet */
        writeAssessmentToGoogleSheet(candidate.getCandidateId(), candidate.getCandidateMobile(),
                candidate.getCandidateFullName(), jobRole.getJobName(), colList, caAttempt.getResult());

    }

    private static List<CandidateAssessmentResponse> getAssessmentResponses(List<AssessmentSheetCol> colList) {
        List<CandidateAssessmentResponse> candidateAssessmentResponseList = new ArrayList<>();
        for (AssessmentSheetCol response : colList) {
            CandidateAssessmentResponse candidateAssessmentResponse = new CandidateAssessmentResponse();

            candidateAssessmentResponse.setAssessmentQuestion(response.getQuestion());
            candidateAssessmentResponse.setCandidateAnswer(response.getCandidateAnswer());
            if(response.getQuestion().getAnswer() != null) {
                candidateAssessmentResponse.setScore(response.getQuestion().getAnswer().equalsIgnoreCase(response.getCandidateAnswer()) ? 1 : 0);
            } else {
                candidateAssessmentResponse.setScore(null);
            }
            candidateAssessmentResponseList.add(candidateAssessmentResponse);
        }
        return candidateAssessmentResponseList;
    }

    private static boolean shouldBeMarkedAsAssessed(List<CandidateJobPrefs.JobPrefWithAssessmentBundle> jobPrefWithAssessmentBundleList) {
        for (CandidateJobPrefs.JobPrefWithAssessmentBundle jobPrefWithAssessmentBundle : jobPrefWithAssessmentBundleList) {
            if (!jobPrefWithAssessmentBundle.isAssessed()) {
                return false;
            }
        }
        return true;
    }

    private static void writeAssessmentToGoogleSheet(Long candidateId, String candidateMobile, String candidateName, String jobName,
                                                     List<AssessmentSheetCol> colList, Double score) throws UnsupportedEncodingException {
        /*
        *
        * writing to job application sheet excel sheet
        * */
        String candidateIdVal = String.valueOf(candidateId);
        String candidateMobileVal = candidateMobile.substring(3, 13);
        String candidateNameVal = candidateName;
        String jobRoleVal = jobName;
        String question1Val = "";
        String candidateAnswer1Val = "";
        String correctAnswer1Val = "";
        String question2Val = "";
        String correctAnswer2Val = "";
        String candidateAnswer2Val = "";
        String question3Val = "";
        String candidateAnswer3Val = "";
        String correctAnswer3Val = "";
        String question4Val = "";
        String candidateAnswer4Val = "";
        String correctAnswer4Val = "";
        String question5Val = "";
        String candidateAnswer5Val = "";
        String correctAnswer5Val = "";
        String finalScoreVal = score == null ? "NA" : String.valueOf(score);

        if (colList.size() > 0) {
            question1Val = colList.get(0).getQuestion() == null ? "" : colList.get(0).getQuestion().getQuestionText();
            candidateAnswer1Val = colList.get(0).getCandidateAnswer() == null ? "" : colList.get(0).getCandidateAnswer();
            correctAnswer1Val = colList.get(0).getQuestion().getAnswer() == null ? "" : colList.get(0).getQuestion().getAnswer();
        }
        if (colList.size() > 1) {
            question2Val = colList.get(1).getQuestion() == null ? "" : colList.get(1).getQuestion().getQuestionText();
            correctAnswer2Val = colList.get(1).getCandidateAnswer() == null ? "" : colList.get(1).getCandidateAnswer();
            candidateAnswer2Val = colList.get(1).getQuestion().getAnswer() == null ? "" : colList.get(1).getQuestion().getAnswer();
        }
        if (colList.size() > 2) {
            question3Val = colList.get(2).getQuestion()== null ? "" : colList.get(2).getQuestion().getQuestionText();
            candidateAnswer3Val = colList.get(2).getCandidateAnswer() == null ? "" : colList.get(2).getCandidateAnswer();
            correctAnswer3Val = colList.get(2).getQuestion().getAnswer() == null ? "" : colList.get(2).getQuestion().getAnswer();
        }
        if (colList.size() > 3) {
            question4Val = colList.get(3).getQuestion()== null ? "" : colList.get(3).getQuestion().getQuestionText();
            candidateAnswer4Val = colList.get(3).getCandidateAnswer() == null ? "" : colList.get(3).getCandidateAnswer();
            correctAnswer4Val = colList.get(3).getQuestion().getAnswer() == null ? "" : colList.get(3).getQuestion().getAnswer();
        }
        if (colList.size() > 4) {
            question5Val = colList.get(4).getQuestion()== null ? "" : colList.get(4).getQuestion().getQuestionText();
            candidateAnswer5Val = colList.get(4).getCandidateAnswer() == null ? "" : colList.get(4).getCandidateAnswer();
            correctAnswer5Val = colList.get(4).getQuestion().getAnswer() == null ? "" : colList.get(4).getQuestion().getAnswer();
        }

        // field key values
        String candidateIdKey = "entry.1500708937";
        String candidateMobileKey = "entry.376755547";
        String candidateNameKey = "entry.1068268802";
        String jobRoleKey = "entry.513880976";
        String question1Key = "entry.1782805791";
        String candidateAnswer1Key = "entry.1711253213";
        String correctAnswer1Key = "entry.1425634513";
        String question2Key = "entry.407123817";
        String candidateAnswer2Key = "entry.1223787183";
        String correctAnswer2Key = "entry.677828971";
        String question3Key = "entry.1086588287";
        String candidateAnswer3Key = "entry.1289982581";
        String correctAnswer3Key = "entry.1377227957";
        String question4Key = "entry.1539927616";
        String candidateAnswer4Key = "entry.1083585855";
        String correctAnswer4Key = "entry.70056176";
        String question5Key = "entry.1529670097";
        String candidateAnswer5Key = "entry.1461389399";
        String correctAnswer5Key = "entry.1748947125";
        String scoreKey = "entry.1797418359";
        String url;
        if(isDevMode){
            url = ServerConstants.DEV_GOOGLE_FORM_FOR_ASSESSMENT;
        } else {
            url = ServerConstants.PROD_GOOGLE_FORM_FOR_ASSESSMENT;
        }

        String postBody;

        postBody =
                candidateIdKey + "=" + URLEncoder.encode(candidateIdVal, "UTF-8") + "&"
                        + candidateMobileKey + "=" + URLEncoder.encode(candidateMobileVal, "UTF-8") + "&"
                        + candidateNameKey + "=" + URLEncoder.encode(candidateNameVal, "UTF-8") + "&"
                        + jobRoleKey + "=" + URLEncoder.encode(jobRoleVal, "UTF-8") + "&"
                        + question1Key + "=" + URLEncoder.encode(question1Val, "UTF-8") + "&"
                        + candidateAnswer1Key + "=" + URLEncoder.encode(candidateAnswer1Val, "UTF-8") + "&"
                        + correctAnswer1Key + "=" + URLEncoder.encode(correctAnswer1Val, "UTF-8") + "&"
                        + question2Key + "=" + URLEncoder.encode(question2Val, "UTF-8") + "&"
                        + candidateAnswer2Key + "=" + URLEncoder.encode(candidateAnswer2Val, "UTF-8") + "&"
                        + correctAnswer2Key + "=" + URLEncoder.encode(correctAnswer2Val, "UTF-8") + "&"
                        + question3Key + "=" + URLEncoder.encode(question3Val, "UTF-8") + "&"
                        + candidateAnswer3Key + "=" + URLEncoder.encode(candidateAnswer3Val, "UTF-8") + "&"
                        + correctAnswer3Key + "=" + URLEncoder.encode(correctAnswer3Val, "UTF-8") + "&"
                        + question4Key + "=" + URLEncoder.encode(question4Val, "UTF-8") + "&"
                        + candidateAnswer4Key + "=" + URLEncoder.encode(candidateAnswer4Val, "UTF-8") + "&"
                        + correctAnswer4Key + "=" + URLEncoder.encode(correctAnswer4Val, "UTF-8") + "&"
                        + question5Key + "=" + URLEncoder.encode(question5Val, "UTF-8") + "&"
                        + candidateAnswer5Key + "=" + URLEncoder.encode(candidateAnswer5Val, "UTF-8") + "&"
                        + correctAnswer5Key + "=" + URLEncoder.encode(correctAnswer5Val, "UTF-8") + "&"
                        + scoreKey + "=" + URLEncoder.encode(finalScoreVal, "UTF-8");

        try {
            GoogleSheetHttpRequest googleSheetHttpRequest = new GoogleSheetHttpRequest();
            googleSheetHttpRequest.sendPost(url, postBody);
        } catch (Exception exception) {
            Logger.info("Exception in writing to google sheet");
        }
    }

    /**
     * Calculate Attempt Score based on responseList
     *
     * @param candidateAssessmentResponseList
     */
    private static Double calculateAttemptScore(List<CandidateAssessmentResponse> candidateAssessmentResponseList) {
        double finalScore = 0D;
        for (CandidateAssessmentResponse candidateAssessmentResponse : candidateAssessmentResponseList) {
            if(candidateAssessmentResponse.getScore() == null) return null;
            if (candidateAssessmentResponse.getScore() !=null &&candidateAssessmentResponse.getScore() > 0) {
                ++finalScore;
            }
        }
        return finalScore / 5;
    }

    public static List<CandidateJobPrefs.JobPrefWithAssessmentBundle> getJobPrefVsIsAssessedList(Long candidateId, List<JobPreference> jobPreferenceList) {
        List<CandidateJobPrefs.JobPrefWithAssessmentBundle> jobPrefWithAssessmentBundleList = new ArrayList<>();
        for (JobPreference jobPreference : jobPreferenceList) {
            CandidateJobPrefs.JobPrefWithAssessmentBundle jobPrefWithAssessmentBundle = new CandidateJobPrefs.JobPrefWithAssessmentBundle();
            // check if there is atleast one question for this jobRole
            if (AssessmentQuestion.find.where().in("jobRoleId", jobPreference.getJobRole().getJobRoleId()).setMaxRows(1).findUnique() != null) {
                // check if this jobRole is attempted or not
                CandidateAssessmentAttempt candidateAssessmentAttempt = CandidateAssessmentAttempt.find.where()
                        .eq("candidateId", candidateId)
                        .eq("jobRoleId", jobPreference.getJobRole().getJobRoleId())
                        .setMaxRows(1)
                        .findUnique();
                // if attempted already then mark it as complete
                if (candidateAssessmentAttempt != null) {
                    jobPrefWithAssessmentBundle.setAssessed(true);
                }
            } else {
                // if there is no questions for this jobRole, mark it as complete in Front End
                jobPrefWithAssessmentBundle.setAssessed(true);
            }

            jobPrefWithAssessmentBundle.setJobPreference(jobPreference);
            jobPrefWithAssessmentBundleList.add(jobPrefWithAssessmentBundle);
        }
        return jobPrefWithAssessmentBundleList;
    }

    public static List<JobRoleWithAssessmentBundle> getJobRoleIdsVsIsAssessedList(Long candidateId, List<Long> jobRoleIdList) {
        List<JobRoleWithAssessmentBundle> jobRoleBundleList = new ArrayList<>();
        for (Long jobRoleId : jobRoleIdList) {
            JobRoleWithAssessmentBundle jobRoleBundle = new JobRoleWithAssessmentBundle();
            jobRoleBundle.setJobRoleId(jobRoleId);
            // check if there is atleast one question for this jobRole
            if (AssessmentQuestion.find.where().in("jobRoleId", jobRoleId).setMaxRows(1).findUnique() != null) {
                // check if this jobRole is attempted or not
                CandidateAssessmentAttempt candidateAssessmentAttempt = CandidateAssessmentAttempt.find.where()
                        .eq("candidateId", candidateId)
                        .eq("jobRoleId", jobRoleId)
                        .setMaxRows(1)
                        .findUnique();
                // if attempted already then mark it as complete
                if (candidateAssessmentAttempt != null) {
                    jobRoleBundle.setAssessed(true);
                    Logger.info(" jobRoleId:" + jobRoleId + " is already attempted by candidate: " + candidateId);
                }
            } else {
                // if there is no questions for this jobRole, mark it as complete in Front End
                Logger.info("No Questions Available in db for JobRoleId: " + jobRoleId);
                jobRoleBundle.setAssessed(true);
            }

            jobRoleBundleList.add(jobRoleBundle);
        }
        return jobRoleBundleList;
    }

    public static Map<Long, Boolean> getJobRoleIdsVsIsAssessedMap(Long candidateId, List<Long> jobRoleIdList) {
        Map<Long, Boolean> map = new HashMap<>();
        List<JobRoleWithAssessmentBundle> jobRoleBundleList = getJobRoleIdsVsIsAssessedList(candidateId, jobRoleIdList);
        for (JobRoleWithAssessmentBundle bundle : jobRoleBundleList) {
            map.put(bundle.getJobRoleId(), bundle.isAssessed());
        }
        return map;
    }
    private static class AssessmentSheetCol {
        AssessmentQuestion question;
        String candidateAnswer;

        public AssessmentQuestion getQuestion() {
            return question;
        }

        public void setQuestion(AssessmentQuestion question) {
            this.question = question;
        }

        public String getCandidateAnswer() {
            return candidateAnswer;
        }

        public void setCandidateAnswer(String candidateAnswer) {
            this.candidateAnswer = candidateAnswer;
        }
    }

    public static class JobRoleWithAssessmentBundle {
        Long jobRoleId;
        boolean isAssessed;

        public Long getJobRoleId() {
            return jobRoleId;
        }

        public void setJobRoleId(Long jobRoleId) {
            this.jobRoleId = jobRoleId;
        }

        public boolean isAssessed() {
            return isAssessed;
        }

        public void setAssessed(boolean assessed) {
            isAssessed = assessed;
        }
    }
}
