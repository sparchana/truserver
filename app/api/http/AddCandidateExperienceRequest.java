package api.http;

import java.util.List;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateExperienceRequest extends AddCandidateRequest {
    public int candidateTotalExperience ;
    public Integer candidateIsEmployed ;
    public String candidateCurrentCompany;
    public long candidateCurrentSalary;

    public int candidateMotherTongue ;
    public List<SkillMapClass> candidateSkills;
    public List<LanguageClass> candidateLanguageKnown;


    /* setters and getters */

    public String getCandidateCurrentCompany() {
        return candidateCurrentCompany;
    }

    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public long getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateLanguageKnown(List<LanguageClass> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }
    public void setCandidateSkills(List<SkillMapClass> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public int getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public int getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

}
