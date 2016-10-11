package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

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

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "RecruiterProfileId", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    public static Finder<String, RecruiterPayment> find = new Finder(RecruiterPayment.class);

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
}