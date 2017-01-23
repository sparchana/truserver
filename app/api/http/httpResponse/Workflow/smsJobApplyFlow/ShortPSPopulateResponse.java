package api.http.httpResponse.Workflow.smsJobApplyFlow;

import models.entity.Static.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 14/1/17.
 */
public class ShortPSPopulateResponse {

    /* if any list is empty, that data is not required to be collected in front end
    *  if not null then data needs to be collected in front end */
    private List<IdProof> documentList;
    private List<Language> languageList;
    private List<Asset> assetList;

    private boolean isDobMissing;
    private boolean isGenderMissing;
    private boolean isSalaryMissing;

    private ExperienceResponse experienceResponse;
    private EducationResponse educationResponse;

    private Status status;

    private Long jobPostId;
    private Long candidateId;

    private List<Integer> propertyIdList;

    public ShortPSPopulateResponse() {

        /* should not render in front end if its empty */
        this.documentList = new ArrayList<>();
        this.languageList = new ArrayList<>();
        this.assetList = new ArrayList<>();

        // should not be render in front end unless its set false
        this.isDobMissing = false;
        this.isGenderMissing = false;
        this.isSalaryMissing = false;

        this.experienceResponse = new ExperienceResponse(false);
        this.educationResponse = new EducationResponse(false);

        this.status = Status.UNKNOWN;
    }

    /* if not available then use these data to create ui elements */

    public static class ExperienceResponse {

        private boolean isExperienceMissing;
        private List<JobRole> jobRoleList; // all jobrole

        public ExperienceResponse(boolean isExperienceMissing) {
            this.isExperienceMissing = isExperienceMissing;
        }

        public ExperienceResponse(boolean isExperienceMissing, List<JobRole> jobRoleList) {
            this.isExperienceMissing = isExperienceMissing;
            this.jobRoleList = jobRoleList;
        }

        public boolean isExperienceMissing() {
            return isExperienceMissing;
        }

        public void setExperienceMissing(boolean experienceMissing) {
            isExperienceMissing = experienceMissing;
        }

        public List<JobRole> getJobRoleList() {
            return jobRoleList;
        }

        public void setJobRoleList(List<JobRole> jobRoleList) {
            this.jobRoleList = jobRoleList;
        }
    }

    public static class EducationResponse {

        private boolean isEducationMissing;
        private List<Education> educationList; // all education
        private List<Degree> degreeList; // all degree

        public EducationResponse(boolean isEducationMissing) {
            this.isEducationMissing = isEducationMissing;
        }

        public EducationResponse(boolean isEducationMissing,
                                 List<Education> educationList,
                                 List<Degree> degreeList) {

            this.isEducationMissing = isEducationMissing;
            this.educationList = educationList;
            this.degreeList = degreeList;
        }

        public boolean isEducationMissing() {
            return isEducationMissing;
        }

        public void setEducationMissing(boolean educationMissing) {
            isEducationMissing = educationMissing;
        }

        public List<Education> getEducationList() {
            return educationList;
        }

        public void setEducationList(List<Education> educationList) {
            this.educationList = educationList;
        }

        public List<Degree> getDegreeList() {
            return degreeList;
        }

        public void setDegreeList(List<Degree> degreeList) {
            this.degreeList = degreeList;
        }
    }

    public enum Status {
        UNKNOWN,
        FAILURE,
        SUCCESS,
        INVALID
    }

    public List<IdProof> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<IdProof> documentList) {
        this.documentList = documentList;
    }

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<Asset> assetList) {
        this.assetList = assetList;
    }

    public boolean isDobMissing() {
        return isDobMissing;
    }

    public void setDobMissing(boolean dobMissing) {
        isDobMissing = dobMissing;
    }

    public boolean isGenderMissing() {
        return isGenderMissing;
    }

    public void setGenderMissing(boolean genderMissing) {
        isGenderMissing = genderMissing;
    }

    public boolean isSalaryMissing() {
        return isSalaryMissing;
    }

    public void setSalaryMissing(boolean salaryMissing) {
        isSalaryMissing = salaryMissing;
    }

    public ExperienceResponse getExperienceResponse() {
        return experienceResponse;
    }

    public void setExperienceResponse(ExperienceResponse experienceResponse) {
        this.experienceResponse = experienceResponse;
    }

    public EducationResponse getEducationResponse() {
        return educationResponse;
    }

    public void setEducationResponse(EducationResponse educationResponse) {
        this.educationResponse = educationResponse;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public List<Integer> getPropertyIdList() {
        return propertyIdList;
    }

    public void setPropertyIdList(List<Integer> propertyIdList) {
        this.propertyIdList = propertyIdList;
    }
}
