package api.http.httpResponse;

/**
 * Created by hawk on 19/1/17.
 */
public class BulkUploadResponse {
    Integer totalNumberOfCandidateCreated;
    Integer totalNumberOfCandidateUploaded;

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


}
