package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.JobRole;
import models.entity.Static.Skill;

import javax.persistence.*;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "jobtoskill")
@Table(name = "jobtoskill")
public class JobToSkill  extends Model {
    @Id
    @Column(name = "jobToSkillId", columnDefinition = "int signed not null", unique = true)
    public int jobToSkillId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    public JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "skillId")
    public Skill skill;

    public static Model.Finder<String, JobToSkill> find = new Model.Finder(JobToSkill.class);
}
