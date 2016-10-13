package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 11/10/16.
 */

@Entity(name = "pre_screen_requirement")
@Table(name = "pre_screen_requirement")
public class PreScreenRequirement  extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "pre_screen_requirement_id", columnDefinition = "bigint unsigned", unique = true)
    private long preScreenRequirementId;

    @Column(name = "pre_screen_requirement_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String preScreenRequirementUUId; // UUID

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp creationTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_id", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @Column(name = "category", columnDefinition = "int ", nullable = false)
    private Integer category;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "id_proof_id", referencedColumnName = "IdProofId")
    private IdProof idProof;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "asset_id", referencedColumnName = "asset_id")
    private Asset asset;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "requirements_category_id", referencedColumnName = "requirements_category_id")
    private RequirementsCategory requirementsCategory;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "language_id", referencedColumnName = "LanguageId")
    private Language language;

    public PreScreenRequirement(){
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public IdProof getIdProof() {
        return idProof;
    }

    public void setIdProof(IdProof idProof) {
        this.idProof = idProof;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public RequirementsCategory getRequirementsCategory() {
        return requirementsCategory;
    }

    public void setRequirementsCategory(RequirementsCategory requirementsCategory) {
        this.requirementsCategory = requirementsCategory;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
