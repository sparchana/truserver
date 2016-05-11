package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.IDProofreference;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "idproof")
@Table(name = "idproof")
public class IdProof extends Model {
    @Id
    @Column(name = "IdProofId", columnDefinition = "int signed not null", unique = true)
    public int idProofId = 0;

    @Column(name = "IdProofName", columnDefinition = "varchar(255) null")
    public String idProofName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "idProof", cascade = CascadeType.REMOVE)
    public List<IDProofreference> idProofreferenceList;

    public static Model.Finder<String, IdProof> find = new Model.Finder(IdProof.class);
}
