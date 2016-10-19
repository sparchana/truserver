package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.IdProof;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 13/10/16.
 */
@Entity(name = "job_post_document_requirement")
@Table(name = "job_post_document_requirement")
public class JobPostDocumentRequirement extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "job_post_document_id", columnDefinition = "bigint unsigned", unique = true)
    private long documentRequirementId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "job_post_id", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "id_proof_id", referencedColumnName = "IdProofId")
    private IdProof idProof;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    public JobPostDocumentRequirement(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }
    public static Finder<String, JobPostDocumentRequirement> find = new Finder(JobPostDocumentRequirement.class);

    public long getDocumentRequirementId() {
        return documentRequirementId;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public IdProof getIdProof() {
        return idProof;
    }

    public void setIdProof(IdProof idProof) {
        this.idProof = idProof;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

}
