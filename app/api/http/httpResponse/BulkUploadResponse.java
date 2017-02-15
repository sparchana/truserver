package api.http.httpResponse;

import java.util.List;

/**
 * Created by hawk on 19/1/17.
 */
public class BulkUploadResponse {
    Integer totalNumberOfCandidateCreated;
    Integer totalNumberOfCandidateUploaded;

    //for recruiter job upload
    Integer totalJobsCreated;
    Integer totalJobsUploaded;
    String invalidFields;

    public Integer getTotalNumberOfCandidateCreated() {
        return totalNumberOfCandidateCreated;
    }

    public void setTotalNumberOfCandidateCreated(Integer totalTotalNumberOfCandidateCreated) {
        this.totalNumberOfCandidateCreated = totalTotalNumberOfCandidateCreated;
    }

    public Integer getTotalNumberOfCandidateUploaded() {
        return totalNumberOfCandidateUploaded;
    }

    public void setTotalNumberOfCandidateUploaded(Integer totalNumberOfCandidateUploaded) {
        this.totalNumberOfCandidateUploaded = totalNumberOfCandidateUploaded;
    }

    public Integer getTotalJobsCreated() {
        return totalJobsCreated;
    }

    public void setTotalJobsCreated(Integer totalJobsCreated) {
        this.totalJobsCreated = totalJobsCreated;
    }

    public Integer getTotalJobsUploaded() {
        return totalJobsUploaded;
    }

    public void setTotalJobsUploaded(Integer totalJobsUploaded) {
        this.totalJobsUploaded = totalJobsUploaded;
    }

    public String getInvalidFields() {
        return invalidFields;
    }

    public void setInvalidFields(String invalidFields) {
        this.invalidFields = invalidFields;
    }
}
