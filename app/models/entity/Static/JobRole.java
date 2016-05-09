package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.JobHistory;
import models.entity.OM.JobPreference;
import models.entity.OM.JobToSkill;
import models.entity.OO.CandidateCurrentJobDetail;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "jobrole")
@Table(name = "jobrole")
public class JobRole extends Model {
    @Id
    @Column(name = "JobRoleId", columnDefinition = "bigint signed not null", unique = true)
    public long jobRoleId = 0;

    @Column(name = "JobName", columnDefinition = "varchar(255) null")
    public String jobName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    public List<JobHistory> jobHistoryList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    public List<JobPreference> jobPreferenceList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    public List<CandidateCurrentJobDetail> candidateCurrentJobDetailList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole", cascade = CascadeType.REMOVE)
    public List<JobToSkill> jobToSkillList;

    // static functions

    public long getJobRoleId() {
        return jobRoleId;
    }

    public void setJobRoleId(long jobRoleId) {
        this.jobRoleId = jobRoleId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public static Finder<String, JobRole> find = new Finder(JobRole.class);

}
