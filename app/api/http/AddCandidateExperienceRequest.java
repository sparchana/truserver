package api.http;

import java.util.List;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateExperienceRequest extends AddCandidateRequest {
    public Integer candidateTotalExperience ;
    public Integer candidateIsEmployed ;
    public String candidateCurrentCompany;
    public Long candidateCurrentSalary;

    public Integer candidateMotherTongue ;
    public List<SkillMapClass> candidateSkills;
    public List<LanguageClass> candidateLanguageKnown;


    /* setters and getters */

    public String getCandidateCurrentCompany() {
        return candidateCurrentCompany;
    }

    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public Long getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateLanguageKnown(List<LanguageClass> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }
    public void setCandidateSkills(List<SkillMapClass> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public Integer getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

}
