package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.OM.IDProofReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 11/10/16.
 */
@CacheStrategy
@Entity(name = "job_role_to_document")
@Table(name = "job_role_to_document")
public class JobRoleToDocument extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "job_role_to_document_id", columnDefinition = "bigint unsigned", unique = true)
    private long jobRoleToDocumentId;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_role_id", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "IdProofId", referencedColumnName = "idProofId")
    private IdProof idProof;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp creationTimestamp;

    public JobRoleToDocument(){
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, JobRoleToDocument> find = new Finder(JobRoleToDocument.class);

    public long getJobRoleToDocumentId() {
        return jobRoleToDocumentId;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public IdProof getIdProof() {
        return idProof;
    }

    public void setIdProof(IdProof idProof) {
        this.idProof = idProof;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
