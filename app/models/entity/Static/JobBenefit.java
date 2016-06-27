package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "jobbenefit")
@Table(name = "jobbenefit")
public class JobBenefit extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobBenefitId", columnDefinition = "bigint signed", unique = true)
    private Integer jobBenefitId;

    @Column(name = "JobBenefitName", columnDefinition = "varchar(20) not null")
    private String jobBenefitName;

    public Integer getJobBenefitId() {
        return jobBenefitId;
    }

    public void setJobBenefitId(Integer jobBenefitId) {
        this.jobBenefitId = jobBenefitId;
    }

    public String getJobBenefitName() {
        return jobBenefitName;
    }

    public void setJobBenefitName(String jobBenefitName) {
        this.jobBenefitName = jobBenefitName;
    }
}
