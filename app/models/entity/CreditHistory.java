package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 10/10/16.
 */

@Entity(name = "credit_history")
@Table(name = "credit_history")
public class CreditHistory extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "credit_history_id", columnDefinition = "int signed", unique = true)
    private long creditHistoryId;

    @Column(name = "credits_available", columnDefinition = "int signed null")
    private Long creditsAvailable;

    @Column(name = "credits_used", columnDefinition = "int signed null")
    private Long creditsUsed;

    @Column(name = "credit_history_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creditHistoryCreateTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "RecruiterCreditCategory")
    private RecruiterCreditCategory recruiterCreditCategory;

    public static Model.Finder<String, CreditHistory> find = new Model.Finder(CreditHistory.class);

    public CreditHistory() {
        this.creditHistoryCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public long getCreditHistoryId() {
        return creditHistoryId;
    }

    public void setCreditHistoryId(long creditHistoryId) {
        this.creditHistoryId = creditHistoryId;
    }

    public RecruiterCreditCategory getRecruiterCreditCategory() {
        return recruiterCreditCategory;
    }

    public void setRecruiterCreditCategory(RecruiterCreditCategory recruiterCreditCategory) {
        this.recruiterCreditCategory = recruiterCreditCategory;
    }

    public Long getCreditsAvailable() {
        return creditsAvailable;
    }

    public void setCreditsAvailable(Long creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }

    public Long getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(Long creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public Timestamp getCreditHistoryCreateTimestamp() {
        return creditHistoryCreateTimestamp;
    }

    public void setCreditHistoryCreateTimestamp(Timestamp creditHistoryCreateTimestamp) {
        this.creditHistoryCreateTimestamp = creditHistoryCreateTimestamp;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }
}
