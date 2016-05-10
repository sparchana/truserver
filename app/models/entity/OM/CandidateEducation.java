package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Education;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 10/5/16.
 */
@Entity(name = "candidateeducation")
@Table(name = "candidateeducation")
public class CandidateEducation extends Model{
    @Id
    @Column(name = "candidateEducationId", columnDefinition = "int signed not null", unique = true)
    public int candidateEducationId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "EducationId", referencedColumnName = "educationId")
    public Education education;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "candidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;
}
