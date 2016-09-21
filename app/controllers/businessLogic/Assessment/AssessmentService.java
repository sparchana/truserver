package controllers.businessLogic.Assessment;

import api.GoogleSheetHttpRequest;
import api.ServerConstants;
import api.http.httpRequest.AssessmentRequest;
import models.entity.Candidate;
import models.entity.OM.*;
import models.entity.Static.AssessmentQuestion;
import models.entity.Static.JobRole;
import play.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static play.libs.Json.toJson;

/**
 * Created by zero on 15/9/16.
 */
public class AssessmentService {
    public static List<AssessmentQuestion> getQuestions(List<Long> jobRoleIdList){
        List<AssessmentQuestion> assessmentQuestionList = new ArrayList<>();
        if(jobRoleIdList.size() > 0) {
            assessmentQuestionList.addAll(AssessmentQuestion.find.where().in("jobRoleId", jobRoleIdList).orderBy().asc("jobRoleId").findList());
        }
        return assessmentQuestionList;
    }

    public static String addAssessedInfoToGS(AssessmentRequest assessmentRequest, Long candidateId){
        if(candidateId != null){
            Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
            if(!assessmentRequest.getResponseList().isEmpty()){

                List<AssessmentSheetCol> colList = new ArrayList<>();
                List<AssessmentRequest.AssessmentOption> optionList = assessmentRequest.getResponseList();
                List<Long> assessmentQuestionIdList = new ArrayList<>();
                List<Long> assessmentJobRoleIdList = new ArrayList<>();

                optionList.sort((o1, o2) -> o1.getJobRoleId() >= o2.getJobRoleId() ? 1 : 0);

                for(AssessmentRequest.AssessmentOption option : optionList) {
                    assessmentQuestionIdList.add(option.getAssessmentQuestionId());
                    if(!assessmentJobRoleIdList.contains(option.getJobRoleId())) {
                        assessmentJobRoleIdList.add(option.getJobRoleId());
                    }
                }
                int optionSize = optionList.size();
                if(optionSize == 0){
                    return "NA";
                }
                Long prevJobRoleId =  optionList.get(0).getJobRoleId();

                List<AssessmentQuestion> assessmentQuestionList = AssessmentQuestion.find.where().in("assessmentQuestionId", assessmentQuestionIdList).findList();
                for(int i =0; i < optionSize; i++) {
                    AssessmentSheetCol assessmentSheetCol = new AssessmentSheetCol();
                    assessmentSheetCol.question = assessmentQuestionList.get(i).getQuestionText();
                    assessmentSheetCol.correctAnswer = assessmentQuestionList.get(i).getAnswer();
                    assessmentSheetCol.answer =  optionList.get(i).getAssessmentResponse();
                    colList.add(assessmentSheetCol);
                    if(prevJobRoleId != optionList.get(i).getJobRoleId() || (i == optionSize - 1)) {
                        try {
                            JobRole jobRole;
                            if(prevJobRoleId != null || (i == optionSize - 1)) {
                                jobRole = JobRole.find.where().eq("jobRoleId", prevJobRoleId).findUnique();
                                saveAttemptAndWriteToGS(candidate, jobRole, colList);
                                colList = new ArrayList<>();
                            }
                            if(i == optionSize-1 && prevJobRoleId != optionList.get(i).getJobRoleId()) {
                                jobRole = JobRole.find.where().eq("jobRoleId", optionList.get(i).getJobRoleId()).findUnique();
                                saveAttemptAndWriteToGS(candidate, jobRole, colList);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        prevJobRoleId = optionList.get(i).getJobRoleId();
                    }
                }

                /* check if eligible to be marked as is assessed */
                List<Long> jobPrefJobRoleIdList = new ArrayList<>();
                assessmentJobRoleIdList = new ArrayList<>();

                List<JobPreference> jobPreferenceList = candidate.getJobPreferencesList();
                for(JobPreference jobPreference: jobPreferenceList){
                    jobPrefJobRoleIdList.add(jobPreference.getJobRole().getJobRoleId());
                }
                assessmentQuestionList = AssessmentQuestion.find.where().in("jobRoleId", jobPrefJobRoleIdList).findList();
                if (assessmentQuestionList.size() > 0) {
                    jobPrefJobRoleIdList = new ArrayList<>();
                    for(AssessmentQuestion assessmentQuestion: assessmentQuestionList) {
                        if(!jobPrefJobRoleIdList.contains(assessmentQuestion.getJobRole().getJobRoleId())){
                            jobPrefJobRoleIdList.add(assessmentQuestion.getJobRole().getJobRoleId());
                        }
                    }
                }

                List<CandidateAssessmentAttempt> candidateAssessmentAttemptList = CandidateAssessmentAttempt.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                for(CandidateAssessmentAttempt candidateAssessmentAttempt : candidateAssessmentAttemptList){
                    assessmentJobRoleIdList.add(candidateAssessmentAttempt.getJobRole().getJobRoleId());
                }
                if(shouldBeMarkedAsAssessed(assessmentJobRoleIdList, jobPrefJobRoleIdList)){
                    candidate.setCandidateIsAssessed(ServerConstants.CANDIDATE_ASSESSED);
                    candidate.candidateUpdate();
                    return "assessed";
                }
                return "ok";
            }
        }
        return "NA";
    }

    private static void saveAttemptAndWriteToGS(Candidate candidate, JobRole jobRole, List<AssessmentSheetCol> colList) throws UnsupportedEncodingException {
        /* save response to db for each attempt */
        CandidateAssessmentAttempt caAttempt = new CandidateAssessmentAttempt();
        caAttempt.setCandidate(candidate);
        caAttempt.setJobRole(jobRole);
        caAttempt.save();

        Logger.info("colList: "+toJson(colList));
        /* write response to google sheet */
        writeAssessmentToGoogleSheet(candidate.getCandidateId(),
                candidate.getCandidateMobile(), candidate.getCandidateFullName(),
                jobRole.getJobName(), colList);
    }

    private static boolean shouldBeMarkedAsAssessed(List<Long> assessmentJobRoleIdList, List<Long> jobPrefJobRoleIdList){
        if(assessmentJobRoleIdList.size() == 0 || jobPrefJobRoleIdList.size() == 0){
            return false;
        }
        for(Long prefRoleId: jobPrefJobRoleIdList){
            if(!assessmentJobRoleIdList.contains(prefRoleId)){
                return false;
            }
        }
        return true;
    }


    private static void writeAssessmentToGoogleSheet(Long candidateId, String candidateMobile, String candidateName, String jobName,
                                                   List<AssessmentSheetCol> colList) throws UnsupportedEncodingException {
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

        if(colList.size() > 0) {
            question1Val = colList.get(0).question == null ? "" : colList.get(0).question ;
            candidateAnswer1Val = colList.get(0).answer == null ? "" : colList.get(0).answer;
            correctAnswer1Val = colList.get(0).correctAnswer == null ? "" : colList.get(0).correctAnswer ;
        }
        if(colList.size() > 1) {
            question2Val = colList.get(1).question == null ? "" : colList.get(1).question ;
            correctAnswer2Val = colList.get(1).correctAnswer == null ? "" : colList.get(1).correctAnswer ;
            candidateAnswer2Val = colList.get(1).answer == null ? "" : colList.get(1).answer ;
        }
        if(colList.size() > 2) {
            question3Val = colList.get(2).question == null ? "" : colList.get(2).question ;
            candidateAnswer3Val = colList.get(2).answer == null ? "" : colList.get(2).answer ;
            correctAnswer3Val = colList.get(2).correctAnswer == null ? "" : colList.get(2).correctAnswer ;
        }
        if(colList.size() > 3) {
            question4Val = colList.get(3).question == null ? "" : colList.get(3).question ;
            candidateAnswer4Val = colList.get(3).answer == null ? "" : colList.get(3).answer ;
            correctAnswer4Val = colList.get(3).correctAnswer == null ? "" : colList.get(3).correctAnswer ;
        }
        if(colList.size() > 4){
            question5Val = colList.get(4).question == null ? "" : colList.get(4).question ;
            candidateAnswer5Val = colList.get(4).answer == null ? "" : colList.get(4).answer ;
            correctAnswer5Val = colList.get(4).correctAnswer == null ? "" : colList.get(4).correctAnswer ;
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

        String url = ServerConstants.PROD_GOOGLE_FORM_FOR_ASSESSMENT;

        String postBody;

        postBody =
                  candidateIdKey  +"=" + URLEncoder.encode(candidateIdVal,"UTF-8") + "&"
                + candidateMobileKey  + "=" + URLEncoder.encode(candidateMobileVal,"UTF-8") + "&"
                + candidateNameKey  + "=" + URLEncoder.encode(candidateNameVal,"UTF-8") + "&"
                + jobRoleKey  + "=" + URLEncoder.encode(jobRoleVal,"UTF-8") + "&"
                + question1Key + "=" + URLEncoder.encode(question1Val,"UTF-8") + "&"
                + candidateAnswer1Key + "=" + URLEncoder.encode(candidateAnswer1Val,"UTF-8") + "&"
                + correctAnswer1Key + "=" + URLEncoder.encode(correctAnswer1Val,"UTF-8") + "&"
                + question2Key + "=" + URLEncoder.encode(question2Val,"UTF-8") + "&"
                + candidateAnswer2Key + "=" + URLEncoder.encode(candidateAnswer2Val,"UTF-8") + "&"
                + correctAnswer2Key + "=" + URLEncoder.encode(correctAnswer2Val,"UTF-8")+ "&"
                + question3Key + "=" + URLEncoder.encode(question3Val,"UTF-8") + "&"
                + candidateAnswer3Key + "=" + URLEncoder.encode(candidateAnswer3Val,"UTF-8") + "&"
                + correctAnswer3Key + "=" + URLEncoder.encode(correctAnswer3Val,"UTF-8")+ "&"
                + question4Key + "=" + URLEncoder.encode(question4Val,"UTF-8") + "&"
                + candidateAnswer4Key + "=" + URLEncoder.encode(candidateAnswer4Val,"UTF-8") + "&"
                + correctAnswer4Key + "=" + URLEncoder.encode(correctAnswer4Val,"UTF-8")+ "&"
                + question5Key + "=" + URLEncoder.encode(question5Val,"UTF-8") + "&"
                + candidateAnswer5Key + "=" + URLEncoder.encode(candidateAnswer5Val,"UTF-8") + "&"
                + correctAnswer5Key + "=" + URLEncoder.encode(correctAnswer5Val,"UTF-8");

        try {
            GoogleSheetHttpRequest googleSheetHttpRequest = new GoogleSheetHttpRequest();
            googleSheetHttpRequest.sendPost(url, postBody);
        }catch (Exception exception){
            Logger.info("Exception in writing to google sheet");
        }
    }

    private static class AssessmentSheetCol {
        String question;
        String answer;
        String correctAnswer;
    }
}
