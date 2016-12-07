package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Candidate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 6/12/16.
 */

@Entity(name = "candidate_feedback")
@Table(name = "candidate_feedback")
public class CandidateFeedback extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id", columnDefinition = "int signed", unique = true)
    private Integer feedbackId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "feedback_comments", columnDefinition = "text null")
    private String feedbackComments;

    @Column(name = "feedback_rating", columnDefinition = "int null")
    private Integer feedbackRating;

    @Column(name = "feedback_channel", columnDefinition = "int signed not null", nullable = false)
    private int feedbackChannel; // website, android, partner

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    public static Model.Finder<String, CandidateFeedback> find = new Model.Finder(CandidateFeedback.class);

    public CandidateFeedback() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getFeedbackComments() {
        return feedbackComments;
    }

    public void setFeedbackComments(String feedbackComments) {
        this.feedbackComments = feedbackComments;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public int getFeedbackChannel() {
        return feedbackChannel;
    }

    public void setFeedbackChannel(int feedbackChannel) {
        this.feedbackChannel = feedbackChannel;
    }
}