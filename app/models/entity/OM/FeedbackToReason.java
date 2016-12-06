package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Static.CandidateFeedbackReason;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 6/12/16.
 */
@Entity(name = "feedback_to_reason")
@Table(name = "feedback_to_reason")
public class FeedbackToReason extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_to_reason_id", columnDefinition = "bigint signed", unique = true)
    private Long feedbackToFeedbackReasonId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "feedback_id", referencedColumnName = "feedback_id")
    private CandidateFeedback candidateFeedback;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "reason_id", referencedColumnName = "reason_id")
    private CandidateFeedbackReason candidateFeedbackReason;

    public FeedbackToReason() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, FeedbackToReason> find = new Finder(FeedbackToReason.class);

    public Long getFeedbackToFeedbackReasonId() {
        return feedbackToFeedbackReasonId;
    }

    public void setFeedbackToFeedbackReasonId(Long feedbackToFeedbackReasonId) {
        this.feedbackToFeedbackReasonId = feedbackToFeedbackReasonId;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public CandidateFeedback getCandidateFeedback() {
        return candidateFeedback;
    }

    public void setCandidateFeedback(CandidateFeedback candidateFeedback) {
        this.candidateFeedback = candidateFeedback;
    }

    public CandidateFeedbackReason getCandidateFeedbackReason() {
        return candidateFeedbackReason;
    }

    public void setCandidateFeedbackReason(CandidateFeedbackReason candidateFeedbackReason) {
        this.candidateFeedbackReason = candidateFeedbackReason;
    }
}