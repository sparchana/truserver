package models.entity.Recruiter.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.JobRole;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 5/10/16.
 */

@Entity(name = "recruiterleadtojobrole")
@Table(name = "recruiterleadtojobrole")
public class RecruiterLeadToJobRole extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_lead_to_job_role_id", columnDefinition = "bigint signed", unique = true)
    private Long recruiterLeadToJobRoleId;

    @Column(name = "recruiter_lead_to_job_role_create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterLeadToJobRoleCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "recruiter_lead_to_job_role_update_timeStamp", columnDefinition = "timestamp")
    private Timestamp recruiterLeadToJobRoleUpdateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recruiter_lead_id", referencedColumnName = "recruiter_lead_id")
    private RecruiterLead recruiterLead;

    public static Finder<String, RecruiterLeadToJobRole> find = new Finder(RecruiterLeadToJobRole.class);

    public RecruiterLeadToJobRole(){
        this.recruiterLeadToJobRoleCreateTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public Long getRecruiterLeadToJobRoleId() {
        return recruiterLeadToJobRoleId;
    }

    public void setRecruiterLeadToJobRoleId(Long recruiterLeadToJobRoleId) {
        this.recruiterLeadToJobRoleId = recruiterLeadToJobRoleId;
    }

    public Timestamp getRecruiterLeadToJobRoleCreateTimeStamp() {
        return recruiterLeadToJobRoleCreateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleCreateTimeStamp(Timestamp recruiterLeadToJobRoleCreateTimeStamp) {
        this.recruiterLeadToJobRoleCreateTimeStamp = recruiterLeadToJobRoleCreateTimeStamp;
    }

    public Timestamp getRecruiterLeadToJobRoleUpdateTimeStamp() {
        return recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleUpdateTimeStamp(Timestamp recruiterLeadToJobRoleUpdateTimeStamp) {
        this.recruiterLeadToJobRoleUpdateTimeStamp = recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public RecruiterLead getRecruiterLead() {
        return recruiterLead;
    }

    public void setRecruiterLead(RecruiterLead recruiterLead) {
        this.recruiterLead = recruiterLead;
    }
}


