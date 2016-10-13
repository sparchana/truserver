package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 10/10/16.
 */
@Entity(name = "recruiter_credit_category")
@Table(name = "recruiter_credit_category")
public class RecruiterCreditCategory extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "recruiter_credit_category_id", columnDefinition = "bigint signed", unique = true)
    private Integer recruiterCreditCategoryId;

    @Column(name = "recruiter_credit_type", columnDefinition = "varchar(50) not null")
    private String recruiterCreditType;

    @Column(name = "recruiter_credit_unit_price", columnDefinition = "int signed null")
    private Long recruiterCreditUnitPrice;

    public static Finder<String, RecruiterCreditCategory> find = new Finder(RecruiterCreditCategory.class);

    public Integer getRecruiterCreditCategoryId() {
        return recruiterCreditCategoryId;
    }

    public void setRecruiterCreditCategoryId(Integer recruiterCreditCategoryId) {
        this.recruiterCreditCategoryId = recruiterCreditCategoryId;
    }

    public String getRecruiterCreditType() {
        return recruiterCreditType;
    }

    public void setRecruiterCreditType(String recruiterCreditType) {
        this.recruiterCreditType = recruiterCreditType;
    }

    public Long getRecruiterCreditUnitPrice() {
        return recruiterCreditUnitPrice;
    }

    public void setRecruiterCreditUnitPrice(Long recruiterCreditUnitPrice) {
        this.recruiterCreditUnitPrice = recruiterCreditUnitPrice;
    }
}