package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.ApplyJobRequest;
import api.GoogleSheetHttpRequest;
import api.http.httpResponse.AddJobPostResponse;
import api.http.httpResponse.ApplyJobResponse;
import com.amazonaws.util.json.JSONException;
import models.entity.*;
import models.entity.OM.*;
import models.entity.Static.*;
import models.util.SmsUtil;
import play.Logger;
import play.api.Play;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class JobService {
    public static AddJobPostResponse addJobPost(AddJobPostRequest addJobPostRequest) {
        AddJobPostResponse addJobPostResponse = new AddJobPostResponse();
        List<Integer> jobPostLocalityList = addJobPostRequest.getJobPostLocalities();
        /* checking if jobPost already exists or not */
        JobPost existingJobPost = JobPost.find.where().eq("jobPostId", addJobPostRequest.getJobPostId()).findUnique();
        if(existingJobPost == null){
            Logger.info("Job post does not exists. Creating a new job Post");
            JobPost newJobPost = new JobPost();
            newJobPost = getAndSetJobPostValues(addJobPostRequest, newJobPost, jobPostLocalityList);
            newJobPost.save();
            addJobPostResponse.setJobPost(newJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_SUCCESS);
            Logger.info("JobPost with jobId: " + newJobPost.getJobPostId() + " and job title: " + newJobPost.getJobPostTitle() + " created successfully");
        } else{
            Logger.info("Job post already exists. Updating existing job Post");
            existingJobPost = getAndSetJobPostValues(addJobPostRequest, existingJobPost, jobPostLocalityList);
            existingJobPost.update();
            addJobPostResponse.setJobPost(existingJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_UPDATE_SUCCESS);
            Logger.info("JobPost with jobId: " + existingJobPost.getJobPostId() + " and job title: " + existingJobPost.getJobPostTitle() + " updated successfully");
        }
        if(Play.isDev(Play.current()) == false){
            addJobPostResponse.setFormUrl(ServerConstants.PROD_GOOGLE_FORM_FOR_JOB_POSTS);
        } else{
            addJobPostResponse.setFormUrl(ServerConstants.DEV_GOOGLE_FORM_FOR_JOB_POSTS);
        }
        return addJobPostResponse;
    }

    public static JobPost getAndSetJobPostValues(AddJobPostRequest addJobPostRequest, JobPost newJobPost, List<Integer> jobPostLocalityList){
        newJobPost.setJobPostMinSalary(addJobPostRequest.getJobPostMinSalary());
        newJobPost.setJobPostMaxSalary(addJobPostRequest.getJobPostMaxSalary());
        newJobPost.setJobPostStartTime(addJobPostRequest.getJobPostStartTime());
        newJobPost.setJobPostEndTime(addJobPostRequest.getJobPostEndTime());
        newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());
        newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());
        newJobPost.setJobPostDescription(addJobPostRequest.getJobPostDescription());
        newJobPost.setJobPostTitle(addJobPostRequest.getJobPostTitle());
        newJobPost.setJobPostIncentives(addJobPostRequest.getJobPostIncentives());
        newJobPost.setJobPostMinRequirement(addJobPostRequest.getJobPostMinRequirement());
        newJobPost.setJobPostAddress(addJobPostRequest.getJobPostAddress());
        newJobPost.setJobPostPinCode(addJobPostRequest.getJobPostPinCode());
        newJobPost.setJobPostVacancies(addJobPostRequest.getJobPostVacancies());
        newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());
        newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());

        if (addJobPostRequest.getJobPostWorkingDays() != null) {
            Byte workingDayByte = Byte.parseByte(addJobPostRequest.getJobPostWorkingDays(), 2);
            newJobPost.setJobPostWorkingDays(workingDayByte);
        }
        newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));

        PricingPlanType pricingPlanType = PricingPlanType.find.where().eq("pricingPlanTypeId", addJobPostRequest.getJobPostPricingPlanId()).findUnique();
        newJobPost.setPricingPlanType(pricingPlanType);

        JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", addJobPostRequest.getJobPostStatusId()).findUnique();
        newJobPost.setJobPostStatus(jobStatus);

        JobRole jobRole = JobRole.find.where().eq("jobRoleId", addJobPostRequest.getJobPostJobRoleId()).findUnique();
        newJobPost.setJobRole(jobRole);

        Company company = Company.find.where().eq("companyId", addJobPostRequest.getJobPostCompanyId()).findUnique();
        newJobPost.setCompany(company);

        TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", addJobPostRequest.getJobPostShiftId()).findUnique();
        newJobPost.setJobPostShift(timeShift);

        Experience experience = Experience.find.where().eq("experienceId", addJobPostRequest.getJobPostExperienceId()).findUnique();
        newJobPost.setJobPostExperience(experience);

        Education education = Education.find.where().eq("educationId", addJobPostRequest.getJobPostEducationId()).findUnique();
        newJobPost.setJobPostEducation(education);

        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", addJobPostRequest.getJobPostRecruiterId()).findUnique();
        newJobPost.setRecruiterProfile(recruiterProfile);

        return newJobPost;
    }

    public static ApplyJobResponse applyJob(ApplyJobRequest applyJobRequest, InteractionService.InteractionChannelType channelType) throws IOException, JSONException {
        Logger.info("checking user and jobId: " + applyJobRequest.getCandidateMobile() + " + " + applyJobRequest.getJobId());
        ApplyJobResponse applyJobResponse = new ApplyJobResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(applyJobRequest.getCandidateMobile());
        if(existingCandidate != null){
            JobPost existingJobPost = JobPost.find.where().eq("jobPostId",applyJobRequest.getJobId()).findUnique();
            if(existingJobPost == null ){
                applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_JOB);
                Logger.info("JobPost with jobId: " + applyJobRequest.getJobId() + " does not exists");
            }
            else{
                JobApplication existingJobApplication = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).eq("jobPostId", applyJobRequest.getJobId()).findUnique();
                if(existingJobApplication == null){
                    JobApplication jobApplication = new JobApplication();
                    jobApplication.setCandidate(existingCandidate);
                    jobApplication.setJobPost(existingJobPost);

                    Locality locality = Locality.find.where().eq("localityId", applyJobRequest.getLocalityId()).findUnique();
                    if(locality != null){
                        jobApplication.setLocality(locality);
                    } else{
                        Logger.info("Location with locality ID: " + applyJobRequest.getLocalityId() + " does not exists");
                    }

                    String interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB;
                    InteractionService.createInteractionForJobApplication(
                            existingCandidate.getCandidateUUId(),
                            existingJobPost.getJobPostUUId(),
                            interactionResult + existingJobPost.getJobPostTitle() + " at " + existingJobPost.getCompany().getCompanyName() + "@" + locality.getLocalityName(),
                            channelType
                    );

                    jobApplication.save();
                    writeJobApplicationToGoogleSheet(existingJobPost.getJobPostId(), applyJobRequest.getCandidateMobile(), channelType, applyJobRequest.getLocalityId());

                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " applied to the jobPost of JobPostId:" + existingJobPost.getJobPostId());

                    SmsUtil.sendJobApplicationSms(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName());
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_SUCCESS);
                } else{
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_EXISTS);
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " already applied to jobPost with jobId:" + existingJobPost.getJobPostId());
                }
            }
        } else{
            applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_CANDIDATE);
            Logger.info("Candidate Does not exists");
        }
        return applyJobResponse;
    }

    public static void writeJobApplicationToGoogleSheet(Long jobPostId, String candidateMobile, InteractionService.InteractionChannelType channelType, Integer localityId) throws UnsupportedEncodingException {
        String jobIdVal = "-";
        String companyNameVal = "-";
        String jobPostNameVal = "-";
        String candidateLeadIdVal = "-";
        String candidateNameVal = "-";
        String candidateMobileVal = "-";
        String candidateGenderVal = "-";
        String candidateTotalExpVal = "-";
        String candidateIsEmployedVal = "-";
        String candidateIsAssessedVal = "-";
        String candidateLanguageKnownVal = "";
        String candidateMotherTongueVal = "-";
        String candidateHomeLocalityVal = "-";
        String candidateLocalityPrefVal = "";
        String candidateJobPrefVal = "";
        String candidateCurrentSalaryVal = "-";
        String candidateEducationVal = "-";
        String candidateSkillsVal = "";
        String candidateCreationVal = "-";
        String candidatePrescreenLocationVal = "-";
        String candidateProfileStatusVal = "-";
        String candidateExpiryDateVal = "-";
        String candidateAgeVal = "-";
        String jobApplicationChannelVal = "-";
        String jobIsHotVal = "";
        int sheetId = ServerConstants.SHEET_MAIN;

        if(channelType == InteractionService.InteractionChannelType.SELF_ANDROID){
            jobApplicationChannelVal = "Android";
        } else if(channelType == InteractionService.InteractionChannelType.SELF){
            jobApplicationChannelVal = "Website";
        }

        Locality preScreenLocality = Locality.find.where().eq("localityId", localityId).findUnique();
        if(preScreenLocality != null){
            candidatePrescreenLocationVal = preScreenLocality.getLocalityName();
        }

        JobPost jobpost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if(jobpost != null){
            jobIdVal = String.valueOf(jobpost.getJobPostId());
            jobPostNameVal = jobpost.getJobPostTitle();
            companyNameVal = jobpost.getCompany().getCompanyName();
            if(jobpost.getJobPostIsHot()){
                jobIsHotVal = "Hot";
            } else{
                jobIsHotVal = "Not Hot";
            }

            /* check source for the job and save it to appropriate sheet */
            if(jobpost.getSource() != null && jobpost.getSource() != ServerConstants.SOURCE_INTERNAL){
                sheetId = ServerConstants.SHEET_SCRAPPED;
            }
        }

        // get candidate information
        Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateMobile));
        if(candidate!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(candidate.getCandidateCreateTimestamp().getTime());

            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMin = calendar.get(Calendar.MINUTE);
            int mSecond = calendar.get(Calendar.SECOND);

            candidateCreationVal = mDay + "/" + mMonth + "/" + mYear + " " + mHour + ":" + mMin + ":" + mSecond;
            candidateMobileVal = String.valueOf(candidate.getCandidateMobile());

            if(candidate.getCandidateLastName() == null){
                candidateNameVal = candidate.getCandidateFirstName();
            } else{
                candidateNameVal = candidate.getCandidateFirstName() + " " +candidate.getCandidateLastName();
            }
            candidateLeadIdVal = String.valueOf(candidate.getLead().getLeadId());

            if(candidate.getCandidateGender() != null){
                if(candidate.getCandidateGender() == 0){
                    candidateGenderVal = "Male";
                } else{
                    candidateGenderVal = "Female";
                }
            }

            if(candidate.getCandidateTotalExperience() != null){
                candidateTotalExpVal = String.valueOf(Math.round((candidate.getCandidateTotalExperience()/12)*100)/100);
            }
            if(candidate.getCandidateIsAssessed() == 0){
                candidateIsAssessedVal = "No";
            } else{
                candidateIsAssessedVal = "Yes";
            }

            if(candidate.getCandidateIsEmployed() != null){
                if(candidate.getCandidateIsEmployed() == 1){
                    candidateIsEmployedVal = "No";
                } else{
                    candidateIsEmployedVal = "Yes";
                }
            }

            //Languages Known
            if(candidate.getLanguageKnownList() != null && candidate.getLanguageKnownList().size() > 0) {
                List<LanguageKnown> languageKnownList = candidate.getLanguageKnownList();

                for(LanguageKnown l : languageKnownList){
                    candidateLanguageKnownVal += l.getLanguage().getLanguageName() + "(" + l.getUnderstanding() + ", " +
                            l.getVerbalAbility() + ", " + l.getReadWrite() + "), ";
                }
            }

            //Skill
            if(candidate.getCandidateSkillList()!= null && candidate.getCandidateSkillList().size() > 0){
                List<CandidateSkill> candidateSkillList = candidate.getCandidateSkillList();

                for(CandidateSkill skill : candidateSkillList){
                    candidateSkillsVal += skill.getSkill().getSkillName() + ", ";
                }
            }
            
            if(candidate.getLocality() != null){
                candidateHomeLocalityVal = candidate.getLocality().getLocalityName();
            }
            if(candidate.getCandidateCurrentJobDetail() != null){
                candidateCurrentSalaryVal = String.valueOf(candidate.getCandidateCurrentJobDetail().getCandidateCurrentSalary());
            }
            if(candidate.getCandidateEducation() != null){
                candidateEducationVal = candidate.getCandidateEducation().getEducation().getEducationName();
            }

            //Job Pref
            List<JobPreference> jobRolePrefList = candidate.getJobPreferencesList();
            for(JobPreference job : jobRolePrefList){
                candidateJobPrefVal += job.getJobRole().getJobName() + ", ";
            }

            //Locality Pref
            List<LocalityPreference> localityPrefList = candidate.getLocalityPreferenceList();
            for(LocalityPreference locality : localityPrefList){
                candidateLocalityPrefVal += locality.getLocality().getLocalityName() + ", ";
            }

            candidateProfileStatusVal = candidate.getCandidateprofilestatus().getProfileStatusName();
            if(candidate.getCandidateprofilestatus().getProfileStatusId() == ServerConstants.CANDIDATE_STATE_DEACTIVE){
                Date expDate = candidate.getCandidateStatusDetail().getStatusExpiryDate();
                if(expDate != null){
                    calendar.setTimeInMillis(candidate.getCandidateStatusDetail().getStatusExpiryDate().getTime());
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    mHour = calendar.get(Calendar.HOUR_OF_DAY);
                    mMin = calendar.get(Calendar.MINUTE);
                    mSecond = calendar.get(Calendar.SECOND);

                    candidateExpiryDateVal = mDay + "/" + mMonth + "/" + mYear + " " + mHour + ":" + mMin + ":" + mSecond;
                } else{
                    candidateExpiryDateVal = String.valueOf("-");
                }
            } else{
                candidateExpiryDateVal = String.valueOf("-");
            }

            if(candidate.getCandidateDOB() != null){
                Date current = new Date();
                Date bday = new Date(candidate.getCandidateDOB().getTime());

                final Calendar calender = new GregorianCalendar();
                calender.set(Calendar.HOUR_OF_DAY, 0);
                calender.set(Calendar.MINUTE, 0);
                calender.set(Calendar.SECOND, 0);
                calender.set(Calendar.MILLISECOND, 0);
                calender.setTimeInMillis(current.getTime() - bday.getTime());

                int age = 0;
                age = calender.get(Calendar.YEAR) - 1970;
                age += (float) calender.get(Calendar.MONTH) / (float) 12;
                candidateAgeVal = String.valueOf(age);
            }
        }

        /*
        * writing to job application sheet excel sheet
        * */

        // field key values
        String jobIdKey = "entry.1388755113";
        String companyNameKey = "entry.1115234203";
        String jobPostNameKey = "entry.1422779518";
        String candidateLeadIdKey = "entry.942294281";
        String candidateNameKey = "entry.1345077393";
        String candidateMobileKey = "entry.1859090779";
        String candidateGenderKey = "entry.2079461892";
        String candidateTotalExpKey = "entry.2071290015";
        String candidateIsEmployedKey = "entry.179139422";
        String candidateIsAssessedKey = "entry.1488146275";
        String candidateLanguageKnownKey = "entry.67497584";
        String candidateMotherTongueKey = "entry.441069988";
        String candidateHomeLocalityKey = "entry.1350761294";
        String candidateLocalityPrefKey = "entry.2057814300";
        String candidateJobPrefKey = "entry.598773915";
        String candidateCurrentSalaryKey = "entry.125850326";
        String candidateEducationKey = "entry.240702722";
        String candidateSkillsKey = "entry.190053755";
        String candidateCreationKey = "entry.971982828";
        String candidatePrescreenLocationKey = "entry.98308337";
        String candidateProfileStatusKey = "entry.46689276";
        String candidateExpiryDateKey = "entry.1180627971";
        String candidateAgeKey = "entry.791725694";
        String jobApplicationChannelKey = "entry.528024717";
        String jobIsHotKey = "entry.1165618058";


        String url;
        if(!Play.isDev(Play.current())){
            if(sheetId == ServerConstants.SHEET_MAIN){
                url = ServerConstants.PROD_GOOGLE_FORM_FOR_JOB_APPLICATION;
            } else {
                url = ServerConstants.PROD_GOOGLE_FORM_FOR_SCRAPPED_JOB_APPLICATION;
            }
        } else {
            url = ServerConstants.DEV_GOOGLE_FORM_FOR_JOB_APPLICATION;
        }
        String postBody;

        postBody = jobIdKey +"=" + URLEncoder.encode(jobIdVal,"UTF-8") + "&"
                + companyNameKey + "=" + URLEncoder.encode(companyNameVal,"UTF-8") + "&"
                + jobPostNameKey + "=" + URLEncoder.encode(jobPostNameVal,"UTF-8") + "&"
                + candidateLeadIdKey + "=" + URLEncoder.encode(candidateLeadIdVal,"UTF-8") + "&"
                + candidateNameKey + "=" + URLEncoder.encode(candidateNameVal,"UTF-8") + "&"
                + candidateMobileKey + "=" + URLEncoder.encode(candidateMobileVal,"UTF-8") + "&"
                + candidateGenderKey + "=" + URLEncoder.encode(candidateGenderVal,"UTF-8") + "&"
                + candidateTotalExpKey + "=" + URLEncoder.encode(candidateTotalExpVal,"UTF-8") + "&"
                + candidateIsEmployedKey + "=" + URLEncoder.encode(candidateIsEmployedVal,"UTF-8") + "&"
                + candidateIsAssessedKey + "=" + URLEncoder.encode(candidateIsAssessedVal,"UTF-8") + "&"
                + candidateLanguageKnownKey + "=" + URLEncoder.encode(candidateLanguageKnownVal,"UTF-8") + "&"
                + candidateMotherTongueKey + "=" + URLEncoder.encode(candidateMotherTongueVal,"UTF-8") + "&"
                + candidateHomeLocalityKey + "=" + URLEncoder.encode(candidateHomeLocalityVal,"UTF-8") + "&"
                + candidateLocalityPrefKey + "=" + URLEncoder.encode(candidateLocalityPrefVal,"UTF-8") + "&"
                + candidateJobPrefKey + "=" + URLEncoder.encode(candidateJobPrefVal,"UTF-8") + "&"
                + candidateCurrentSalaryKey + "=" + URLEncoder.encode(candidateCurrentSalaryVal,"UTF-8") + "&"
                + candidateEducationKey + "=" + URLEncoder.encode(candidateEducationVal,"UTF-8") + "&"
                + candidateSkillsKey + "=" + URLEncoder.encode(candidateSkillsVal,"UTF-8") + "&"
                + candidateCreationKey + "=" + URLEncoder.encode(candidateCreationVal,"UTF-8") + "&"
                + candidatePrescreenLocationKey + "=" + URLEncoder.encode(candidatePrescreenLocationVal,"UTF-8") + "&"
                + candidateProfileStatusKey + "=" + URLEncoder.encode(candidateProfileStatusVal,"UTF-8") + "&"
                + candidateAgeKey + "=" + URLEncoder.encode(candidateAgeVal,"UTF-8") + "&"
                + candidateExpiryDateKey + "=" + URLEncoder.encode(candidateExpiryDateVal,"UTF-8") + "&"
                + jobApplicationChannelKey + "=" + URLEncoder.encode(jobApplicationChannelVal,"UTF-8") + "&"
                + jobIsHotKey + "=" + URLEncoder.encode(jobIsHotVal,"UTF-8");

                try {
                    GoogleSheetHttpRequest googleSheetHttpRequest = new GoogleSheetHttpRequest();
                    googleSheetHttpRequest.sendPost(url, postBody);
                }catch (Exception exception){
                    Logger.info("Exception in writing to google sheet");
                }
    }

    public static List<JobPostToLocality> getJobPostLocality(List<Integer> localityList, JobPost jobPost) {
        List<JobPostToLocality> jobPostToLocalityList = new ArrayList<>();
        for(Integer  localityId : localityList) {
            JobPostToLocality jobPostToLocality = new JobPostToLocality();
            jobPostToLocality.setJobPost(jobPost);
            jobPostToLocality.setJobPostToLocalityCreateTimeStamp(new Timestamp(System.currentTimeMillis()));
            Locality locality = Locality.find.where()
                    .eq("localityId", localityId).findUnique();
            jobPostToLocality.setLocality(locality);
            jobPostToLocalityList.add(jobPostToLocality);
        }
        return jobPostToLocalityList;
    }
}