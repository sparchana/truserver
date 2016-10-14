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

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "jobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "interview_time_slot_id", referencedColumnName = "interview_time_slot_id")
    private InterviewTimeSlot interviewTimeSlot;

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
}