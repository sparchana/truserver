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
    @Column(name = "IDProofReferenceId", columnDefinition = "int signed not null", unique = true)
    public int idProofReferenceId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "candidateId")
    public Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "IdProofId", referencedColumnName = "idProofId")
    public IdProof idProof;

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setIdProof(IdProof idProof) {
        this.idProof = idProof;
    }

    public static Finder<String, IDProofReference> find = new Finder(IDProofReference.class);
}
