package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Skill;
import models.entity.Static.SkillQualifier;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "candidateskill")
@Table(name = "candidateskill")
public class CandidateSkill extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "candidateSkillId", columnDefinition = "int signed not null", unique = true)
    private int candidateSkillId;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillQualifierId", referencedColumnName = "skillqualifierId")
    private SkillQualifier skillQualifier;

    public static Model.Finder<String, CandidateSkill> find = new Model.Finder(CandidateSkill.class);

    public int getCandidateSkillId() {
        return candidateSkillId;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setSkillQualifier(SkillQualifier skillQualifier) {
        this.skillQualifier = skillQualifier;
    }

    public void setCandidateSkillId(int candidateSkillId) {
        this.candidateSkillId = candidateSkillId;
    }

    public Skill getSkill() {
        return skill;
    }

    public SkillQualifier getSkillQualifier() {
        return skillQualifier;
    }
}
