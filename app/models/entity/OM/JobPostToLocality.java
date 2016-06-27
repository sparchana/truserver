package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
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

    @Column(name = "JobPostToLocalityCreateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToLocalityCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "JobPostToLocalityUpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToLocalityUpdateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    private Locality locality;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName= "JobPostId")
    private JobPost jobPost;

    public JobPostToLocality(){
        this.jobPostToLocalityCreateTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public Long getJobPostToLocalityId() {
        return jobPostToLocalityId;
    }

    public void setJobPostToLocalityId(Long jobPostToLocalityId) {
        this.jobPostToLocalityId = jobPostToLocalityId;
    }

    public Timestamp getJobPostToLocalityCreateTimeStamp() {
        return jobPostToLocalityCreateTimeStamp;
    }

    public void setJobPostToLocalityCreateTimeStamp(Timestamp jobPostToLocalityCreateTimeStamp) {
        this.jobPostToLocalityCreateTimeStamp = jobPostToLocalityCreateTimeStamp;
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
