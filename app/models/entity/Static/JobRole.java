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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "JobRoleId", columnDefinition = "bigint signed", unique = true)
    private long jobRoleId;

    @Column(name = "JobName", columnDefinition = "varchar(255) null")
    private String jobName;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    private List<JobHistory> jobHistoryList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    private List<JobPreference> jobPreferenceList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole")
    private List<CandidateCurrentJobDetail> candidateCurrentJobDetailList;

    @JsonBackReference
    @OneToMany(mappedBy = "jobRole", cascade = CascadeType.REMOVE)
    private List<JobToSkill> jobToSkillList;

    public static Finder<String, JobRole> find = new Finder(JobRole.class);

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

    public List<JobHistory> getJobHistoryList() {
        return jobHistoryList;
    }

    public void setJobHistoryList(List<JobHistory> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }

    public List<JobPreference> getJobPreferenceList() {
        return jobPreferenceList;
    }

    public void setJobPreferenceList(List<JobPreference> jobPreferenceList) {
        this.jobPreferenceList = jobPreferenceList;
    }

    public List<CandidateCurrentJobDetail> getCandidateCurrentJobDetailList() {
        return candidateCurrentJobDetailList;
    }

    public void setCandidateCurrentJobDetailList(List<CandidateCurrentJobDetail> candidateCurrentJobDetailList) {
        this.candidateCurrentJobDetailList = candidateCurrentJobDetailList;
    }

    public List<JobToSkill> getJobToSkillList() {
        return jobToSkillList;
    }

    public void setJobToSkillList(List<JobToSkill> jobToSkillList) {
        this.jobToSkillList = jobToSkillList;
    }
}
