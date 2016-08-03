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
    @Column(name = "JobPostToLocalityId", columnDefinition = "bigint signed", unique = true)
    private Long jobPostToLocalityId;

    @Column(name = "JobPostToLocalityCreateTimeStamp", columnDefinition = "timestamp not null default current_timestamp")
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

    @Column(name = "Latitude", columnDefinition = "double null")
    private Double latitude;

    @Column(name = "Longitude", columnDefinition = "double null")
    private Double longitude;

    /**
     *  distance - field is not persistable. It is used in matching-engine-distance calculation
     *  There will be no column called distance in table.
     */
    @Transient
    private Double distance;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
