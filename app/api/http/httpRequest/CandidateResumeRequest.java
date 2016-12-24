package api.http.httpRequest;

import java.sql.Timestamp;

/**
 * Created by User on 24-12-2016.
 */

public class CandidateResumeRequest extends TruRequest {

    private long candidateResumeId;
    private Timestamp createTimestamp;
    private String createdBy;
    private Long candidate;
    private String filePath;
    private String externalKey;
    private String parsedResume;

    public long getCandidateResumeId() {
        return candidateResumeId;
    }

    public void setCandidateResumeId(long candidateResumeId) {
        this.candidateResumeId = candidateResumeId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCandidate() {
        return candidate;
    }

    public void setCandidate(Long candidate) {
        this.candidate = candidate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public String getParsedResume() {
        return parsedResume;
    }

    public void setParsedResume(String parsedResume) {
        this.parsedResume = parsedResume;
    }
}
