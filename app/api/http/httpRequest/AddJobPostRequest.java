package api.http.httpRequest;

import java.sql.Time;
import java.util.List;

/**
 * Created by batcoder1 on 18/6/16.
 */
public class AddJobPostRequest {
    public Long jobPostMinSalary;
    public Long jobPostMaxSalary;
    public Time jobPostStartTime;
    public Time jobPostEndTime;
    public Boolean jobPostBenefitPf;
    public Boolean jobPostBenefitFuel;
    public Boolean jobPostBenefitInsurance;
    public Boolean jobPostWorkFromHome;
    public String jobPostDescription;
    public String jobPostTitle;
    public Integer jobPostVacancy;
    public String jobPostDescriptionAudio;
    public Integer jobPostStatus;
    public Integer jobPostJobRole;
    public Integer jobPostCompany;
    public Integer jobPostShift;
    public Integer jobPostExperience;
    public Integer jobPostEducation;

    protected List<Integer> jobPostLocality;

    public Long getJobPostMinSalary() {
        return jobPostMinSalary;
    }

    public List<Integer> getJobPostLocality() {
        return jobPostLocality;
    }

    public void setJobPostLocality(List<Integer> jobPostLocality) {
        this.jobPostLocality = jobPostLocality;
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

    public Time getJobPostStartTime() {
        return jobPostStartTime;
    }

    public void setJobPostStartTime(Time jobPostStartTime) {
        this.jobPostStartTime = jobPostStartTime;
    }

    public Time getJobPostEndTime() {
        return jobPostEndTime;
    }

    public void setJobPostEndTime(Time jobPostEndTime) {
        this.jobPostEndTime = jobPostEndTime;
    }

    public Boolean getJobPostBenefitPf() {
        return jobPostBenefitPf;
    }

    public void setJobPostBenefitPf(Boolean jobPostBenefitPf) {
        this.jobPostBenefitPf = jobPostBenefitPf;
    }

    public Boolean getJobPostBenefitFuel() {
        return jobPostBenefitFuel;
    }

    public void setJobPostBenefitFuel(Boolean jobPostBenefitFuel) {
        this.jobPostBenefitFuel = jobPostBenefitFuel;
    }

    public Boolean getJobPostBenefitInsurance() {
        return jobPostBenefitInsurance;
    }

    public void setJobPostBenefitInsurance(Boolean jobPostBenefitInsurance) {
        this.jobPostBenefitInsurance = jobPostBenefitInsurance;
    }

    public Boolean getJobPostWorkFromHome() {
        return jobPostWorkFromHome;
    }

    public void setJobPostWorkFromHome(Boolean jobPostWorkFromHome) {
        this.jobPostWorkFromHome = jobPostWorkFromHome;
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

    public Integer getJobPostVacancy() {
        return jobPostVacancy;
    }

    public void setJobPostVacancy(Integer jobPostVacancy) {
        this.jobPostVacancy = jobPostVacancy;
    }

    public String getJobPostDescriptionAudio() {
        return jobPostDescriptionAudio;
    }

    public void setJobPostDescriptionAudio(String jobPostDescriptionAudio) {
        this.jobPostDescriptionAudio = jobPostDescriptionAudio;
    }

    public Integer getJobPostStatus() {
        return jobPostStatus;
    }

    public void setJobPostStatus(Integer jobPostStatus) {
        this.jobPostStatus = jobPostStatus;
    }

    public Integer getJobPostJobRole() {
        return jobPostJobRole;
    }

    public void setJobPostJobRole(Integer jobPostJobRole) {
        this.jobPostJobRole = jobPostJobRole;
    }

    public Integer getJobPostCompany() {
        return jobPostCompany;
    }

    public void setJobPostCompany(Integer jobPostCompany) {
        this.jobPostCompany = jobPostCompany;
    }

    public Integer getJobPostShift() {
        return jobPostShift;
    }

    public void setJobPostShift(Integer jobPostShift) {
        this.jobPostShift = jobPostShift;
    }

    public Integer getJobPostExperience() {
        return jobPostExperience;
    }

    public void setJobPostExperience(Integer jobPostExperience) {
        this.jobPostExperience = jobPostExperience;
    }

    public Integer getJobPostEducation() {
        return jobPostEducation;
    }

    public void setJobPostEducation(Integer jobPostEducation) {
        this.jobPostEducation = jobPostEducation;
    }
}
