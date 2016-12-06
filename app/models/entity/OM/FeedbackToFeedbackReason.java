package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Static.TrudroidFeedbackReason;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 6/12/16.
 */
@Entity(name = "feedback_to_feedback_reason")
@Table(name = "feedback_to_feedback_reason")
public class FeedbackToFeedbackReason extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_to_feedback_reason_id", columnDefinition = "bigint signed", unique = true)
    private Long feedbackToFeedbackReasonId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "feedback_id", referencedColumnName = "feedback_id")
    private TrudroidFeedback trudroidFeedback;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "feedback_reason_id", referencedColumnName = "feedback_reason_id")
    private TrudroidFeedbackReason trudroidFeedbackReason;

    public FeedbackToFeedbackReason() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, FeedbackToFeedbackReason> find = new Finder(FeedbackToFeedbackReason.class);

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

    public TrudroidFeedback getTrudroidFeedback() {
        return trudroidFeedback;
    }

    public void setTrudroidFeedback(TrudroidFeedback trudroidFeedback) {
        this.trudroidFeedback = trudroidFeedback;
    }

    public TrudroidFeedbackReason getTrudroidFeedbackReason() {
        return trudroidFeedbackReason;
    }

    public void setTrudroidFeedbackReason(TrudroidFeedbackReason trudroidFeedbackReason) {
        this.trudroidFeedbackReason = trudroidFeedbackReason;
    }
}