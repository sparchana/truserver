package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

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

    @Column(name = "recruiter_credit_history_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String recruiterCreditHistoryUuid;

    @Column(name = "recruiter_credits_available", columnDefinition = "int signed null")
    private Integer recruiterCreditsAvailable;

    @Column(name = "recruiter_credits_used", columnDefinition = "int signed null")
    private Integer recruiterCreditsUsed;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimestamp;

    @Column(name = "recruiter_credits_added_by", columnDefinition = "varchar(50) not null")
    private String recruiterCreditsAddedBy;

    @Column(name = "units", columnDefinition = "int signed null")
    private Integer units;

    @Column(name = "credits_added", columnDefinition = "int signed null")
    private Integer creditsAdded;

    @Column(name = "recruiter_credit_pack_no", columnDefinition = "int signed null")
    private Integer recruiterCreditPackNo;

    @Column(name = "expiry_date", columnDefinition = "date null")
    private Date expiryDate;

    @Column(name = "credit_is_expired", columnDefinition = "int signed null")
    private Boolean creditIsExpired;

    @Column(name = "is_latest", columnDefinition = "int signed null")
    private Boolean isLatest;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "RecruiterCreditCategory")
    private RecruiterCreditCategory recruiterCreditCategory;

    public static Model.Finder<String, RecruiterCreditHistory> find = new Model.Finder(RecruiterCreditHistory.class);

    public RecruiterCreditHistory()
    {
        this.recruiterCreditHistoryUuid = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
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

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
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

    public String getRecruiterCreditHistoryUuid() {
        return recruiterCreditHistoryUuid;
    }

    public void setRecruiterCreditHistoryUuid(String recruiterCreditHistoryUuid) {
        this.recruiterCreditHistoryUuid = recruiterCreditHistoryUuid;
    }

    public String getRecruiterCreditsAddedBy() {
        return recruiterCreditsAddedBy;
    }

    public void setRecruiterCreditsAddedBy(String recruiterCreditsAddedBy) {
        this.recruiterCreditsAddedBy = recruiterCreditsAddedBy;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getRecruiterCreditPackNo() {
        return recruiterCreditPackNo;
    }

    public void setRecruiterCreditPackNo(Integer recruiterCreditPackNo) {
        this.recruiterCreditPackNo = recruiterCreditPackNo;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getCreditIsExpired() {
        return creditIsExpired;
    }

    public void setCreditIsExpired(Boolean creditIsExpired) {
        this.creditIsExpired = creditIsExpired;
    }

    public Boolean getLatest() {
        return isLatest;
    }

    public void setLatest(Boolean latest) {
        isLatest = latest;
    }

    public Integer getCreditsAdded() {
        return creditsAdded;
    }

    public void setCreditsAdded(Integer creditsAdded) {
        this.creditsAdded = creditsAdded;
    }
}