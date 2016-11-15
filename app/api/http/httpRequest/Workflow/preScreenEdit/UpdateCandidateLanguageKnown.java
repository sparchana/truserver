package api.http.httpRequest.Workflow.preScreenEdit;

import api.http.CandidateKnownLanguage;

import java.util.List;

/**
 * Created by zero on 10/11/16.
 */
public class UpdateCandidateLanguageKnown {
    public List<CandidateKnownLanguage> candidateKnownLanguageList;

    public List<CandidateKnownLanguage> getCandidateKnownLanguageList() {
        return candidateKnownLanguageList;
    }

    public void setCandidateKnownLanguageList(List<CandidateKnownLanguage> candidateKnownLanguageList) {
        this.candidateKnownLanguageList = candidateKnownLanguageList;
    }
}
