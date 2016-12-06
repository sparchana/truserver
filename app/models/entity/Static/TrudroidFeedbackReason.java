package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 6/12/16.
 */

@Entity(name = "trudroid_feedback_reason")
@Table(name = "trudroid_feedback_reason")
public class TrudroidFeedbackReason extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "feedback_reason_id", columnDefinition = "int unsigned", unique = true)
    private long feedbackReasonId;

    @Column(name = "feedback_reason_name", columnDefinition = "text null")
    private String feedbackReasonName;

    @Column(name = "feedback_reason_type", columnDefinition = "int null")
    private Integer feedbackReasonType;

    public static Finder<String, TrudroidFeedbackReason> find = new Finder(TrudroidFeedbackReason.class);

    public long getFeedbackReasonId() {
        return feedbackReasonId;
    }

    public void setFeedbackReasonId(long feedbackReasonId) {
        this.feedbackReasonId = feedbackReasonId;
    }

    public String getFeedbackReasonName() {
        return feedbackReasonName;
    }

    public void setFeedbackReasonName(String feedbackReasonName) {
        this.feedbackReasonName = feedbackReasonName;
    }

    public Integer getFeedbackReasonType() {
        return feedbackReasonType;
    }

    public void setFeedbackReasonType(Integer feedbackReasonType) {
        this.feedbackReasonType = feedbackReasonType;
    }
}
