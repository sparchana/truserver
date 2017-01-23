package api.http.httpResponse.Recruiter;

import java.util.List;

/**
 * Created by zero on 20/1/17.
 */
public class JobPostFilterResponse {

    private Long jobPostId;
    private Integer gender;
    private Long minSalary;
    private Long maxSalary;
    private Long jobPostJobRoleId;
    private String jobPostJobRoleTitle;


    private Integer jobPostEducationId ;
    private Integer jobPostExperienceId;
    private List<Long> jobPostLocalityIdList ;
    private List<Integer> jobPostLanguageIdList ;
    private List<Integer> jobPostDocumentIdList;
    private List<Integer> jobPostAssetIdList;

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Integer getJobPostEducationId() {
        return jobPostEducationId;
    }

    public void setJobPostEducationId(Integer jobPostEducationId) {
        this.jobPostEducationId = jobPostEducationId;
    }

    public Integer getJobPostExperienceId() {
        return jobPostExperienceId;
    }

    public void setJobPostExperienceId(Integer jobPostExperienceId) {
        this.jobPostExperienceId = jobPostExperienceId;
    }

    public String getJobPostJobRoleTitle() {
        return jobPostJobRoleTitle;
    }

    public void setJobPostJobRoleTitle(String jobPostJobRoleTitle) {
        this.jobPostJobRoleTitle = jobPostJobRoleTitle;
    }
}
