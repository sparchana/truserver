package api.http.httpRequest.Workflow.preScreenEdit;

import api.http.httpRequest.AddSupportCandidateRequest;

import java.util.List;

/**
 * Created by zero on 10/11/16.
 */
public class UpdateCandidateWorkExperience {
    public Integer candidateTotalExperience ;
    public List<AddSupportCandidateRequest.PastCompany> pastCompanyList;
    public Boolean candidateIsEmployed;
    public Boolean extraDetailAvailable;

    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public void setCandidateTotalExperience(Integer candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }

    public List<AddSupportCandidateRequest.PastCompany> getPastCompanyList() {
        return pastCompanyList;
    }

    public void setPastCompanyList(List<AddSupportCandidateRequest.PastCompany> pastCompanyList) {
        this.pastCompanyList = pastCompanyList;
    }

    public Boolean getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateIsEmployed(Boolean candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public Boolean getExtraDetailAvailable() {
        return extraDetailAvailable;
    }

    public void setExtraDetailAvailable(Boolean extraDetailAvailable) {
        this.extraDetailAvailable = extraDetailAvailable;
    }
}
