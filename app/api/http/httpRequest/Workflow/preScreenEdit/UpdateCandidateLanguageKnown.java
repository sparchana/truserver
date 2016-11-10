package api.http.httpRequest.Workflow.preScreenEdit;

import api.http.CandidateKnownLanguage;

import java.util.List;

/**
 * Created by zero on 10/11/16.
 */
public class UpdateCandidateLanguageKnown {
    public List<CandidateKnownLanguage> candidateLanguageKnown;

    public List<CandidateKnownLanguage> getCandidateLanguageKnown() {
        return candidateLanguageKnown;
    }

    public void setCandidateLanguageKnown(List<CandidateKnownLanguage> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }
}
