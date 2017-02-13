package api.http.httpResponse;

import models.entity.Candidate;
import models.entity.Partner;

import java.util.Date;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateWorkflowData {
    public Candidate candidate;
    private CandidateExtraData extraData;
    private CandidateScoreData scoreData;
    private Integer applicationChannel;
    private Date appliedOn;
    private Partner partner;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public CandidateExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(CandidateExtraData candidateExtraData) {
        this.extraData = candidateExtraData;
    }

    public CandidateScoreData getScoreData() {
        return scoreData;
    }

    public void setScoreData(CandidateScoreData scoreData) {
        this.scoreData = scoreData;
    }

    public Integer getApplicationChannel() {
        return applicationChannel;
    }

    public void setApplicationChannel(Integer applicationChannel) {
        this.applicationChannel = applicationChannel;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Date getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(Date appliedOn) {
        this.appliedOn = appliedOn;
    }
}
