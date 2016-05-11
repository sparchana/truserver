package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.JobRole;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "jobhistory")
@Table(name = "jobhistory")
public class JobHistory extends Model {
    @Id
    @Column(name = "JobHistoryId", columnDefinition = "bigint signed not null", unique = true)
    public long jobHistoryId = 0;

    @Column(name = "CandidatePastCompany", columnDefinition = "bigint signed null")
    public long candidatepastCompany = 0;

    @Column(name = "CandidatePastSalary", columnDefinition = "bigint signed null")
    public long candidatepastSalary = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    public JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    public static Finder<String, JobHistory> find = new Finder(JobHistory.class);

}
