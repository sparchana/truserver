package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Candidate;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "education")
@Table(name = "education")
public class Education extends Model {
    @Id
    @Column(name = "EducationId", columnDefinition = "int signed null", unique = true)
    public int educationId = 0;

    @Column(name = "EducationName", columnDefinition = "varchar(255) null")
    public String educationName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "education", cascade = CascadeType.REMOVE)
    public List<Candidate> candidateList;

    public static Model.Finder<String, Education> find = new Model.Finder(Education.class);

}
