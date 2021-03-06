package api.http.httpRequest.Workflow;

import java.util.List;

/**
 * Created by zero on 7/10/16.
 */
public class MatchingCandidateRequest extends WorkflowRequest{
    public Integer maxAge ;
    public Long minSalary ;
    public Long maxSalary ;
    public List<Integer> experienceIdList ;
    public Integer experienceId ;
    public Integer gender ;
    public Long jobPostJobRoleId;
    public List<Integer> jobPostEducationIdList ;
    public Integer jobPostEducationId ;
    public List<Long> jobPostLocalityIdList ;
    public List<Integer> jobPostLanguageIdList ;
    public List<Integer> jobPostDocumentIdList;
    public List<Integer> jobPostAssetIdList;
    public Double distanceRadius ;
    public Integer initialValue;
    public Integer sortBy; //1-> latest active, 2-> High to low salary 3-> low to high salary
    public Boolean showOnlyFreshCandidate; //fresh candidate: those candidate to whom recruiter hasn't sent any sms yet

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

    public Integer getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Integer experienceId) {
        this.experienceId = experienceId;
    }


    public Integer getJobPostEducationId() {
        return jobPostEducationId;
    }

    public void setJobPostEducationId(Integer jobPostEducationId) {
        this.jobPostEducationId = jobPostEducationId;
    }

    public List<Integer> getExperienceIdList() {
        return experienceIdList;
    }

    public void setExperienceIdList(List<Integer> experienceIdList) {
        this.experienceIdList = experienceIdList;
    }

    public List<Integer> getJobPostEducationIdList() {
        return jobPostEducationIdList;
    }

    public void setJobPostEducationIdList(List<Integer> jobPostEducationIdList) {
        this.jobPostEducationIdList = jobPostEducationIdList;
    }

    public List<Integer> getJobPostDocumentIdList() {
        return jobPostDocumentIdList;
    }

    public void setJobPostDocumentIdList(List<Integer> jobPostDocumentIdList) {
        this.jobPostDocumentIdList = jobPostDocumentIdList;
    }

    public List<Integer> getJobPostAssetIdList() {
        return jobPostAssetIdList;
    }

    public void setJobPostAssetIdList(List<Integer> jobPostAssetIdList) {
        this.jobPostAssetIdList = jobPostAssetIdList;
    }

    public Integer getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }

    public Integer getSortBy() {
        return sortBy;
    }

    public void setSortBy(Integer sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getShowOnlyFreshCandidate() {
        return showOnlyFreshCandidate;
    }

    public void setShowOnlyFreshCandidate(Boolean showOnlyFreshCandidate) {
        this.showOnlyFreshCandidate = showOnlyFreshCandidate;
    }
}

