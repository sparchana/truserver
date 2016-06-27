package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.JobBenefit;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "jobposttobenefits")
@Table(name = "jobposttobenefits")
public class JobPostToBenefits extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobPostToBenefitsId", columnDefinition = "bigint signed", unique = true)
    private Long jobPostToBenefitsId;

    @Column(name = "JobPostToBenefitsCreateTimeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp jobPostToBenefitsCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "JobPostToBenefitsUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToBenefitsUpdateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobBenefitId", referencedColumnName = "JobBenefitId")
    private JobBenefit jobBenefit;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName= "JobPostId")
    private JobPost jobPost;

    public JobPostToBenefits(){
        this.jobPostToBenefitsCreateTimeStamp = new Timestamp(System.currentTimeMillis());
    }
}
