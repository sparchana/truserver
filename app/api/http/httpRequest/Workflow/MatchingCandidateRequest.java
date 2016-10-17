package api.http.httpRequest.Workflow;

import api.http.httpRequest.Workflow.WorkflowRequest;

import java.util.List;

/**
 * Created by zero on 7/10/16.
 */
public class MatchingCandidateRequest extends WorkflowRequest{
    public Integer minAge ;
    public Integer maxAge ;
    public Long minSalary ;
    public Long maxSalary ;
    public Integer experienceId ;
    public Integer gender ;
    public Long jobPostJobRoleId ;
    public Integer jobPostEducationId ;
    public List<Long> jobPostLocalityIdList ;
    public List<Integer> jobPostLanguageIdList ;
    public Double distanceRadius ;

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Integer getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Integer experienceId) {
        this.experienceId = experienceId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getJobPostJobRoleId() {
        return jobPostJobRoleId;
    }

    public void setJobPostJobRoleId(Long jobPostJobRoleId) {
        this.jobPostJobRoleId = jobPostJobRoleId;
    }

    public Integer getJobPostEducationId() {
        return jobPostEducationId;
    }

    public void setJobPostEducationId(Integer jobPostEducationId) {
        this.jobPostEducationId = jobPostEducationId;
    }

    public List<Long> getJobPostLocalityIdList() {
        return jobPostLocalityIdList;
    }

    public void setJobPostLocalityIdList(List<Long> jobPostLocalityIdList) {
        this.jobPostLocalityIdList = jobPostLocalityIdList;
    }

    public List<Integer> getJobPostLanguageIdList() {
        return jobPostLanguageIdList;
    }

    public void setJobPostLanguageIdList(List<Integer> jobPostLanguageIdList) {
        this.jobPostLanguageIdList = jobPostLanguageIdList;
    }

    public Double getDistanceRadius() {
        return distanceRadius;
    }

    public void setDistanceRadius(Double distanceRadius) {
        this.distanceRadius = distanceRadius;
    }
}

