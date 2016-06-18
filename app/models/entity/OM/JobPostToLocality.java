package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.Locality;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 16/6/16.
 */

@Entity(name = "jobposttolocality")
@Table(name = "jobposttolocality")
public class JobPostToLocality extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobPostToLocalityId", columnDefinition = "bigint signed not null", unique = true)
    private Long jobPostToLocalityId;

    @Column(name = "JobPostToLocalityUpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToLocalityUpdateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    private Locality locality;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName= "JobPostId")
    private JobPost jobPost;

    public Long getJobPostToLocalityId() {
        return jobPostToLocalityId;
    }

    public void setJobPostToLocalityId(Long jobPostToLocalityId) {
        this.jobPostToLocalityId = jobPostToLocalityId;
    }

    public Timestamp getJobPostToLocalityUpdateTimeStamp() {
        return jobPostToLocalityUpdateTimeStamp;
    }

    public void setJobPostToLocalityUpdateTimeStamp(Timestamp jobPostToLocalityUpdateTimeStamp) {
        this.jobPostToLocalityUpdateTimeStamp = jobPostToLocalityUpdateTimeStamp;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }
}
