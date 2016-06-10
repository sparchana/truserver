package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.IDProofReference;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "idproof")
@Table(name = "idproof")
public class IdProof extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "IdProofId", columnDefinition = "int signed", unique = true)
    private int idProofId;

    @Column(name = "IdProofName", columnDefinition = "varchar(255) null")
    private String idProofName;

    @JsonBackReference
    @OneToMany(mappedBy = "idProof", cascade = CascadeType.REMOVE)
    private List<IDProofReference> idProofReferenceList;

    public static Model.Finder<String, IdProof> find = new Model.Finder(IdProof.class);

    public int getIdProofId() {
        return idProofId;
    }

    public void setIdProofId(int idProofId) {
        this.idProofId = idProofId;
    }

    public String getIdProofName() {
        return idProofName;
    }

    public void setIdProofName(String idProofName) {
        this.idProofName = idProofName;
    }

    public List<IDProofReference> getIdProofReferenceList() {
        return idProofReferenceList;
    }

    public void setIdProofReferenceList(List<IDProofReference> idProofReferenceList) {
        this.idProofReferenceList = idProofReferenceList;
    }
}
