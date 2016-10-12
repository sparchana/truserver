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

@Entity(name = "recruiter_credit_history")
@Table(name = "recruiter_credit_history")
public class RecruiterCreditHistory extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "recruiter_credit_history_id", columnDefinition = "int signed", unique = true)
    private long recruiterCreditHistoryId;

    @Column(name = "recruiter_credits_available", columnDefinition = "int signed null")
    private Integer recruiterCreditsAvailable;

    @Column(name = "recruiter_credits_used", columnDefinition = "int signed null")
    private Integer recruiterCreditsUsed;

    @Column(name = "recruiter_credit_history_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterCreditHistoryCreateTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "RecruiterCreditCategory")
    private RecruiterCreditCategory recruiterCreditCategory;

    public static Model.Finder<String, RecruiterCreditHistory> find = new Model.Finder(RecruiterCreditHistory.class);

    public RecruiterCreditHistory() {
        this.recruiterCreditHistoryCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public long getRecruiterCreditHistoryId() {
        return recruiterCreditHistoryId;
    }

    public void setRecruiterCreditHistoryId(long recruiterCreditHistoryId) {
        this.recruiterCreditHistoryId = recruiterCreditHistoryId;
    }

    public Integer getRecruiterCreditsAvailable() {
        return recruiterCreditsAvailable;
    }

    public void setRecruiterCreditsAvailable(Integer recruiterCreditsAvailable) {
        this.recruiterCreditsAvailable = recruiterCreditsAvailable;
    }

    public Integer getRecruiterCreditsUsed() {
        return recruiterCreditsUsed;
    }

    public void setRecruiterCreditsUsed(Integer recruiterCreditsUsed) {
        this.recruiterCreditsUsed = recruiterCreditsUsed;
    }

    public Timestamp getRecruiterCreditHistoryCreateTimestamp() {
        return recruiterCreditHistoryCreateTimestamp;
    }

    public void setRecruiterCreditHistoryCreateTimestamp(Timestamp recruiterCreditHistoryCreateTimestamp) {
        this.recruiterCreditHistoryCreateTimestamp = recruiterCreditHistoryCreateTimestamp;
    }

    public RecruiterCreditCategory getRecruiterCreditCategory() {
        return recruiterCreditCategory;
    }

    public void setRecruiterCreditCategory(RecruiterCreditCategory recruiterCreditCategory) {
        this.recruiterCreditCategory = recruiterCreditCategory;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }
}