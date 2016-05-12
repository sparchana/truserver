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
    @Column(name = "IdProofId", columnDefinition = "int signed null", unique = true)
    public int idProofId = 0;

    @Column(name = "IdProofName", columnDefinition = "varchar(255) null")
    public String idProofName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "idProof", cascade = CascadeType.REMOVE)
    public List<IDProofReference> idProofReferenceList;

    public static Model.Finder<String, IdProof> find = new Model.Finder(IdProof.class);
}
