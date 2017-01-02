package models.entity.Recruiter;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.Static.RecruiterCreditCategory;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by dodo on 27/12/16.
 */

@Entity(name = "recruiter_credit_pack")
@Table(name = "recruiter_credit_pack")
public class RecruiterCreditPack extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "Recruiter_credit_pack_id", columnDefinition = "int signed", unique = true)
    private long recruiterCreditPackId;

    @Column(name = "Recruiter_credit_pack_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String recruiterCreditPackUuid;

    @Column(name = "Recruiter_credit_pack_no", columnDefinition = "int signed")
    private long recruiterCreditPackNo;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "RecruiterCreditCategory")
    private RecruiterCreditCategory recruiterCreditCategory;

    @Column(name = "credits_available", columnDefinition = "int signed null")
    private Integer creditsAvailable;

    @Column(name = "credits_used", columnDefinition = "int signed null")
    private Integer creditsUsed;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimestamp;

    @Column(name = "expiry_date", columnDefinition = "date null")
    private Date expiryDate;

    @Column(name = "Credit_is_expired", columnDefinition = "int signed null")
    private Boolean creditIsExpired;

    public RecruiterCreditPack() {
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
        this.recruiterCreditPackUuid = UUID.randomUUID().toString();
    }

    public static Model.Finder<String, RecruiterCreditPack> find = new Model.Finder(RecruiterCreditPack.class);

    public long getRecruiterCreditPackId() {
        return recruiterCreditPackId;
    }

    public void setRecruiterCreditPackId(long recruiterCreditPackId) {
        this.recruiterCreditPackId = recruiterCreditPackId;
    }

    public String getRecruiterCreditPackUuid() {
        return recruiterCreditPackUuid;
    }

    public void setRecruiterCreditPackUuid(String recruiterCreditPackUuid) {
        this.recruiterCreditPackUuid = recruiterCreditPackUuid;
    }

    public long getRecruiterCreditPackNo() {
        return recruiterCreditPackNo;
    }

    public void setRecruiterCreditPackNo(long recruiterCreditPackNo) {
        this.recruiterCreditPackNo = recruiterCreditPackNo;
    }

    public Integer getCreditsAvailable() {
        return creditsAvailable;
    }

    public void setCreditsAvailable(Integer creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }

    public Integer getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(Integer creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public RecruiterCreditCategory getRecruiterCreditCategory() {
        return recruiterCreditCategory;
    }

    public void setRecruiterCreditCategory(RecruiterCreditCategory recruiterCreditCategory) {
        this.recruiterCreditCategory = recruiterCreditCategory;
    }

    public Boolean getCreditIsExpired() {
        return creditIsExpired;
    }

    public void setCreditIsExpired(Boolean creditIsExpired) {
        this.creditIsExpired = creditIsExpired;
    }
}
