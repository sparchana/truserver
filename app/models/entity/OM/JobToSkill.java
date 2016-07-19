package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.JobRole;
import models.entity.Static.Skill;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "jobtoskill")
@Table(name = "jobtoskill")
public class JobToSkill  extends Model {
    @Id
    @JsonBackReference
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "jobToSkillId", columnDefinition = "int signed", unique = true)
    private int jobToSkillId;

    @UpdatedTimestamp
    @JsonBackReference
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;

    public static Model.Finder<String, JobToSkill> find = new Model.Finder(JobToSkill.class);

    public int getJobToSkillId() {
        return jobToSkillId;
    }

    public void setJobToSkillId(int jobToSkillId) {
        this.jobToSkillId = jobToSkillId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
