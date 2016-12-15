package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.InterviewTimeSlot;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 29/9/16.
 */

@Entity(name = "interview_details")
@Table(name = "interview_details")
public class InterviewDetails extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "interview_details_id", columnDefinition = "int signed", unique = true)
    private int interviewDetailsId;

    @UpdatedTimestamp
    @Column(name = "update_time_stamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @Column(name = "interview_days", columnDefinition = "binary(7) null")
    private Byte interviewDays;

    @Column(name = "Latitude", columnDefinition = "double(10,6) null")
    private Double lat;

    @Column(name = "Longitude", columnDefinition = "double(10,6) null")
    private Double lng;

    @Column(name = "PlaceId", columnDefinition = "text null")
    private String placeId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "jobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "interview_time_slot_id", referencedColumnName = "interview_time_slot_id")
    private InterviewTimeSlot interviewTimeSlot;

    @Column(name = "ReviewApplication", columnDefinition = "int(1) null")
    private Integer reviewApplication;

    @Column(name = "interview_building_no", columnDefinition = "text null")
    private String interviewBuildingNo;

    @Column(name = "interview_address", columnDefinition = "text null")
    private String interviewAddress;

    @Column(name = "interview_landmark", columnDefinition = "text null")
    private String interviewLandmark;

    public static Finder<String, InterviewDetails> find = new Finder(InterviewDetails.class);

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public int getInterviewDetailsId() {
        return interviewDetailsId;
    }

    public void setInterviewDetailsId(int interviewDetailsId) {
        this.interviewDetailsId = interviewDetailsId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public InterviewTimeSlot getInterviewTimeSlot() {
        return interviewTimeSlot;
    }

    public void setInterviewTimeSlot(InterviewTimeSlot interviewTimeSlot) {
        this.interviewTimeSlot = interviewTimeSlot;
    }

    public Byte getInterviewDays() {
        return interviewDays;
    }

    public void setInterviewDays(Byte interviewDays) {
        this.interviewDays = interviewDays;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getReviewApplication() {
        return reviewApplication;
    }

    public void setReviewApplication(Integer reviewApplication) {
        this.reviewApplication = reviewApplication;
    }

    public String getInterviewBuildingNo() {
        return interviewBuildingNo;
    }

    public void setInterviewBuildingNo(String interviewBuildingNo) {
        this.interviewBuildingNo = interviewBuildingNo;
    }

    public String getInterviewAddress() {
        return interviewAddress;
    }

    public void setInterviewAddress(String interviewAddress) {
        this.interviewAddress = interviewAddress;
    }

    public String getInterviewLandmark() {
        return interviewLandmark;
    }

    public void setInterviewLandmark(String interviewLandmark) {
        this.interviewLandmark = interviewLandmark;
    }
}