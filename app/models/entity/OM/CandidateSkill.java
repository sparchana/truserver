package models.entity.OM;

import com.avaje.ebean.Model;
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
    @Column(name = "candidateSkillId", columnDefinition = "int signed not null", unique = true)
    public int candidateSkillId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    public Skill skill;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillQualifierId", referencedColumnName = "skillqualifierId")
    public SkillQualifier skillQualifier;

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

    public static Model.Finder<String, CandidateSkill> find = new Model.Finder(CandidateSkill.class);

}
