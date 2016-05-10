package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Skill;

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
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "skillId")
    public Skill skill;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "candidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    public int getCandidateSkillId() {
        return candidateSkillId;
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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public static Model.Finder<String, JobToSkill> find = new Model.Finder(JobToSkill.class);

}
