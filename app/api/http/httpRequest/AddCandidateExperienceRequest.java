package api.http.httpRequest;

import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;

import java.util.List;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateExperienceRequest extends AddCandidateRequest {
    public Integer candidateTotalExperience ;
    public Integer candidateIsEmployed ;
    public String candidateCurrentCompany;
    public Long candidateLastWithdrawnSalary;

    public Integer candidateMotherTongue ;
    public List<CandidateSkills> candidateSkills;
    public List<CandidateKnownLanguage> candidateLanguageKnown;

    /* setters and getters */
    public String getCandidateCurrentCompany() {
        return candidateCurrentCompany;
    }

    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateLanguageKnown(List<CandidateKnownLanguage> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }
    public void setCandidateSkills(List<CandidateSkills> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public Integer getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

    public void setCandidateTotalExperience(Integer candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }

    public void setCandidateIsEmployed(Integer candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public void setCandidateCurrentCompany(String candidateCurrentCompany) {
        this.candidateCurrentCompany = candidateCurrentCompany;
    }

    public void setCandidateMotherTongue(Integer candidateMotherTongue) {
        this.candidateMotherTongue = candidateMotherTongue;
    }

    public List<CandidateSkills> getCandidateSkills() {
        return candidateSkills;
    }

    public List<CandidateKnownLanguage> getCandidateLanguageKnown() {
        return candidateLanguageKnown;
    }

    public Long getCandidateLastWithdrawnSalary() {
        return candidateLastWithdrawnSalary;
    }

    public void setCandidateLastWithdrawnSalary(Long candidateLastWithdrawnSalary) {
        this.candidateLastWithdrawnSalary = candidateLastWithdrawnSalary;
    }
}
