package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OO.CandidateEducation;

import javax.persistence.*;

/**
 * Created by zero on 13/5/16.
 */
@Entity(name = "degree")
@Table(name = "degree")
public class Degree extends Model {
    @Id
    @Column(name = "DegreeId", columnDefinition = "int signed not null", unique = true)
    public int degreeId = 0;

    @Column(name = "DegreeName", columnDefinition = "varchar(100) null")
    public String degreeName;

    @JsonBackReference
    @OneToMany(mappedBy = "degree", cascade = CascadeType.REMOVE)
    public CandidateEducation candidateEducation;

    public static Model.Finder<String, Degree> find = new Model.Finder(Degree.class);
}
