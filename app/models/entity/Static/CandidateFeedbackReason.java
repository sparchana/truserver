package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 6/12/16.
 */

@Entity(name = "candidate_feedback_reason")
@Table(name = "candidate_feedback_reason")
public class CandidateFeedbackReason extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "reason_id", columnDefinition = "int unsigned", unique = true)
    private long reasonId;

    @Column(name = "reason_name", columnDefinition = "text null")
    private String reasonName;

    @Column(name = "reason_type", columnDefinition = "int null")
    private Integer reasonType;

    public static Finder<String, CandidateFeedbackReason> find = new Finder(CandidateFeedbackReason.class);

    public long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public Integer getReasonType() {
        return reasonType;
    }

    public void setReasonType(Integer reasonType) {
        this.reasonType = reasonType;
    }
}