package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 16/6/16.
 */
@Entity(name = "jobstatus")
@Table(name = "jobstatus")
public class JobStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobStatusId", columnDefinition = "bigint signed", unique = true)
    private Integer jobStatusId;

    @Column(name = "JobStatusName", columnDefinition = "varchar(20) not null")
    private String jobStatusName;

    public static Model.Finder<String, JobStatus> find = new Model.Finder(JobStatus.class);

    public Integer getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(Integer jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public String getJobStatusName() {
        return jobStatusName;
    }

    public void setJobStatusName(String jobStatusName) {
        this.jobStatusName = jobStatusName;
    }
}
