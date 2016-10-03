package api.http.httpRequest;

import java.util.List;

/**
 * Created by batcoder1 on 18/6/16.
 */
public class AddJobPostRequest {
    public Long jobPostId;
    public Long jobPostMinSalary;
    public Long jobPostMaxSalary;
    public String jobPostWorkingDays;
    public Integer jobPostStartTime;
    public Integer jobPostEndTime;
    public Boolean jobPostIsHot;
    public String jobPostDescription;
    public String jobPostTitle;
    public String jobPostIncentives;
    public String jobPostMinRequirement;
    public String jobPostAddress;
    public Long jobPostPinCode;
    public Integer jobPostVacancies;
    public String jobPostDescriptionAudio;
    public Boolean jobPostWorkFromHome;
    public Integer jobPostStatusId;
    public Integer jobPostJobRoleId;
    public Integer jobPostCompanyId;
    public Integer jobPostShiftId;
    public Integer jobPostExperienceId;
    public Integer jobPostEducationId;
    public Integer jobPostPricingPlanId;
    public Long jobPostRecruiterId;
    public Long partnerInterviewIncentive;
    public Long partnerJoiningIncentive;
    public String jobPostInterviewDays;

    public List<Integer> interviewTimeSlot;

    public List<Integer> jobPostLocalities;

    public Long getJobPostRecruiterId() {
        return jobPostRecruiterId;
    }

    public void setJobPostRecruiterId(Long jobPostRecruiterId) {
        this.jobPostRecruiterId = jobPostRecruiterId;
    }

    public String getJobPostWorkingDays() {
        return jobPostWorkingDays;
    }

    public Integer getJobPostPricingPlanId() {
        return jobPostPricingPlanId;
    }

    public void setJobPostPricingPlanId(Integer jobPostPricingPlanId) {
        this.jobPostPricingPlanId = jobPostPricingPlanId;
    }

    public void setJobPostWorkingDays(String jobPostWorkingDays) {
        this.jobPostWorkingDays = jobPostWorkingDays;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Long getJobPostMinSalary() {
        return jobPostMinSalary;
    }

    public void setJobPostMinSalary(Long jobPostMinSalary) {
        this.jobPostMinSalary = jobPostMinSalary;
    }

    public Long getJobPostMaxSalary() {
        return jobPostMaxSalary;
    }

    public void setJobPostMaxSalary(Long jobPostMaxSalary) {
        this.jobPostMaxSalary = jobPostMaxSalary;
    }

    public Integer getJobPostStartTime() {
        return jobPostStartTime;
    }

    public void setJobPostStartTime(Integer jobPostStartTime) {
        this.jobPostStartTime = jobPostStartTime;
    }

    public Integer getJobPostEndTime() {
        return jobPostEndTime;
    }

    public void setJobPostEndTime(Integer jobPostEndTime) {
        this.jobPostEndTime = jobPostEndTime;
    }

    public Boolean getJobPostIsHot() {
        return jobPostIsHot;
    }

    public void setJobPostIsHot(Boolean jobPostIsHot) {
        this.jobPostIsHot = jobPostIsHot;
    }

    public String getJobPostDescription() {
        return jobPostDescription;
    }

    public void setJobPostDescription(String jobPostDescription) {
        this.jobPostDescription = jobPostDescription;
    }

    public String getJobPostTitle() {
        return jobPostTitle;
    }

    public void setJobPostTitle(String jobPostTitle) {
        this.jobPostTitle = jobPostTitle;
    }

    public String getJobPostIncentives() {
        return jobPostIncentives;
    }

    public void setJobPostIncentives(String jobPostIncentives) {
        this.jobPostIncentives = jobPostIncentives;
    }

    public String getJobPostMinRequirement() {
        return jobPostMinRequirement;
    }

    public void setJobPostMinRequirement(String jobPostMinRequirement) {
        this.jobPostMinRequirement = jobPostMinRequirement;
    }

    public String getJobPostAddress() {
        return jobPostAddress;
    }

    public void setJobPostAddress(String jobPostAddress) {
        this.jobPostAddress = jobPostAddress;
    }

    public Long getJobPostPinCode() {
        return jobPostPinCode;
    }

    public void setJobPostPinCode(Long jobPostPinCode) {
        this.jobPostPinCode = jobPostPinCode;
    }

    public Integer getJobPostVacancies() {
        return jobPostVacancies;
    }

    public void setJobPostVacancies(Integer jobPostVacancy) {
        this.jobPostVacancies = jobPostVacancy;
    }

    public String getJobPostDescriptionAudio() {
        return jobPostDescriptionAudio;
    }

    public void setJobPostDescriptionAudio(String jobPostDescriptionAudio) {
        this.jobPostDescriptionAudio = jobPostDescriptionAudio;
    }

    public Boolean getJobPostWorkFromHome() {
        return jobPostWorkFromHome;
    }

    public void setJobPostWorkFromHome(Boolean jobPostWorkFromHome) {
        this.jobPostWorkFromHome = jobPostWorkFromHome;
    }

    public Integer getJobPostStatusId() {
        return jobPostStatusId;
    }

    public void setJobPostStatusId(Integer jobPostStatusId) {
        this.jobPostStatusId = jobPostStatusId;
    }

    public Integer getJobPostJobRoleId() {
        return jobPostJobRoleId;
    }

    public void setJobPostJobRoleId(Integer jobPostJobRoleId) {
        this.jobPostJobRoleId = jobPostJobRoleId;
    }

    public Integer getJobPostCompanyId() {
        return jobPostCompanyId;
    }

    public void setJobPostCompanyId(Integer jobPostCompanyId) {
        this.jobPostCompanyId = jobPostCompanyId;
    }

    public Integer getJobPostShiftId() {
        return jobPostShiftId;
    }

    public void setJobPostShiftId(Integer jobPostShiftId) {
        this.jobPostShiftId = jobPostShiftId;
    }

    public Integer getJobPostExperienceId() {
        return jobPostExperienceId;
    }

    public void setJobPostExperienceId(Integer jobPostExperienceId) {
        this.jobPostExperienceId = jobPostExperienceId;
    }

    public Integer getJobPostEducationId() {
        return jobPostEducationId;
    }

    public void setJobPostEducationId(Integer jobPostEducationId) {
        this.jobPostEducationId = jobPostEducationId;
    }

    public List<Integer> getJobPostLocalities() {
        return jobPostLocalities;
    }

    public void setJobPostLocalities(List<Integer> jobPostLocalities) {
        this.jobPostLocalities = jobPostLocalities;
    }

    public Long getPartnerInterviewIncentive() {
        return partnerInterviewIncentive;
    }

    public void setPartnerInterviewIncentive(Long partnerInterviewIncentive) {
        this.partnerInterviewIncentive = partnerInterviewIncentive;
    }

    public Long getPartnerJoiningIncentive() {
        return partnerJoiningIncentive;
    }

    public void setPartnerJoiningIncentive(Long partnerJoiningIncentive) {
        this.partnerJoiningIncentive = partnerJoiningIncentive;
    }

    public String getJobPostInterviewDays() {
        return jobPostInterviewDays;
    }

    public void setJobPostInterviewDays(String jobPostInterviewDays) {
        this.jobPostInterviewDays = jobPostInterviewDays;
    }

    public List<Integer> getInterviewTimeSlot() {
        return interviewTimeSlot;
    }

    public void setInterviewTimeSlot(List<Integer> interviewTimeSlot) {
        this.interviewTimeSlot = interviewTimeSlot;
    }
}
