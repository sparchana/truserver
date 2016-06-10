package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.IdProof;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "idproofreference")
@Table(name = "idproofreference")
public class IDProofReference extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "IDProofReferenceId", columnDefinition = "int signed", unique = true)
    private int idProofReferenceId;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "candidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "IdProofId", referencedColumnName = "idProofId")
    private IdProof idProof;

    public static Finder<String, IDProofReference> find = new Finder(IDProofReference.class);

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setIdProof(IdProof idProof) {
        this.idProof = idProof;
    }

    public int getIdProofReferenceId() {
        return idProofReferenceId;
    }

    public void setIdProofReferenceId(int idProofReferenceId) {
        this.idProofReferenceId = idProofReferenceId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public IdProof getIdProof() {
        return idProof;
    }
}
