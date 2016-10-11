package models.entity.Recruiter;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Recruiter.Static.RecruiterCreditCategory;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 11/10/16.
 */
@Entity(name = "recruiter_payment")
@Table(name = "recruiter_payment")
public class RecruiterPayment extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "recruiter_payment_id", columnDefinition = "bigint signed", unique = true)
    private Integer recruiterPaymentId;

    @Column(name = "recruiter_payment_amount", columnDefinition = "bigint unsigned not null")
    private long recruiterPaymentAmount;

    @Column(name = "recruiter_payment_credit_unit_price", columnDefinition = "bigint unsigned null")
    private long recruiterCreditUnitPrice;

    @Column(name = "recruiter_payment_mode", columnDefinition = "int signed null")
    private Integer recruiterPaymentMode; // 0 -> prepaid; 1-> postpaid

    @Column(name = "recruiter_payment_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterPaymentCreateTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recruiter_credit_category_id", referencedColumnName = "recruiter_credit_category_id")
    private RecruiterCreditCategory recruiterCreditCategory;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    public static Finder<String, RecruiterPayment> find = new Finder(RecruiterPayment.class);

    public RecruiterPayment(){
        this.recruiterPaymentCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Integer getRecruiterPaymentId() {
        return recruiterPaymentId;
    }

    public void setRecruiterPaymentId(Integer recruiterPaymentId) {
        this.recruiterPaymentId = recruiterPaymentId;
    }

    public long getRecruiterPaymentAmount() {
        return recruiterPaymentAmount;
    }

    public void setRecruiterPaymentAmount(long recruiterPaymentAmount) {
        this.recruiterPaymentAmount = recruiterPaymentAmount;
    }

    public long getRecruiterCreditUnitPrice() {
        return recruiterCreditUnitPrice;
    }

    public void setRecruiterCreditUnitPrice(long recruiterCreditUnitPrice) {
        this.recruiterCreditUnitPrice = recruiterCreditUnitPrice;
    }

    public Integer getRecruiterPaymentMode() {
        return recruiterPaymentMode;
    }

    public void setRecruiterPaymentMode(Integer recruiterPaymentMode) {
        this.recruiterPaymentMode = recruiterPaymentMode;
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

    public Timestamp getRecruiterPaymentCreateTimestamp() {
        return recruiterPaymentCreateTimestamp;
    }

    public void setRecruiterPaymentCreateTimestamp(Timestamp recruiterPaymentCreateTimestamp) {
        this.recruiterPaymentCreateTimestamp = recruiterPaymentCreateTimestamp;
    }
}