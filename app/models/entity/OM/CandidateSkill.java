package models.entity.OM;

import com.avaje.ebean.Model;
import models.entity.Candidate;
import models.entity.Static.Skill;

import javax.persistence.*;

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
    public long updateTimeStamp = 0;

    @ManyToOne
    @JoinColumn(name = "candidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "SkillId", referencedColumnName = "skillId")
    public Skill skill;

    public static Model.Finder<String, JobToSkill> find = new Model.Finder(JobToSkill.class);

}
