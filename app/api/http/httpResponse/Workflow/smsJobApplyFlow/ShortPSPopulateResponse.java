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

    private boolean isDobAvailable;
    private boolean isGenerAvailable;
    private boolean isSalaryAvailable;;

    private ExperienceResponse experienceResponse;
    private EducationResponse educationResponse;

    private Status status;

    private Long jobPostId;
    private Long candidateId;

    public ShortPSPopulateResponse() {

        /* should not render in front end if its empty */
        this.documentList = new ArrayList<>();
        this.languageList = new ArrayList<>();
        this.assetList = new ArrayList<>();

        // should not be render in front end unless its set false
        this.isDobAvailable = true;
        this.isGenerAvailable = true;
        this.isSalaryAvailable = true;

        this.experienceResponse = new ExperienceResponse(true);
        this.educationResponse = new EducationResponse(true);

        this.status = Status.UNKNOWN;
    }

    /* if not available then use these data to create ui elements */

    public static class ExperienceResponse {

        private boolean isExperienceAvailable;
        private List<JobRole> jobRoleList; // all jobrole

        public ExperienceResponse(boolean isExperienceAvailable) {
            this.isExperienceAvailable = isExperienceAvailable;
        }

        public ExperienceResponse(boolean isExperienceAvailable, List<JobRole> jobRoleList) {
            this.isExperienceAvailable = isExperienceAvailable;
            this.jobRoleList = jobRoleList;
        }

        public boolean isExperienceAvailable() {
            return isExperienceAvailable;
        }

        public void setExperienceAvailable(boolean experienceAvailable) {
            isExperienceAvailable = experienceAvailable;
        }

        public List<JobRole> getJobRoleList() {
            return jobRoleList;
        }

        public void setJobRoleList(List<JobRole> jobRoleList) {
            this.jobRoleList = jobRoleList;
        }
    }

    public static class EducationResponse {

        private boolean isEducationAvailable;
        private List<Education> educationList; // all education
        private List<Degree> degreeList; // all degree

        public EducationResponse(boolean isEducationAvailable) {
            this.isEducationAvailable = isEducationAvailable;
        }

        public EducationResponse(boolean isEducationAvailable,
                                 List<Education> educationList,
                                 List<Degree> degreeList) {

            this.isEducationAvailable = isEducationAvailable;
            this.educationList = educationList;
            this.degreeList = degreeList;
        }

        public boolean isEducationAvailable() {
            return isEducationAvailable;
        }

        public void setEducationAvailable(boolean educationAvailable) {
            isEducationAvailable = educationAvailable;
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

    public boolean isDobAvailable() {
        return isDobAvailable;
    }

    public void setDobAvailable(boolean dobAvailable) {
        isDobAvailable = dobAvailable;
    }

    public boolean isGenerAvailable() {
        return isGenerAvailable;
    }

    public void setGenerAvailable(boolean generAvailable) {
        isGenerAvailable = generAvailable;
    }

    public boolean isSalaryAvailable() {
        return isSalaryAvailable;
    }

    public void setSalaryAvailable(boolean salaryAvailable) {
        isSalaryAvailable = salaryAvailable;
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
}
