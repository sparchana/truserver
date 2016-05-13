package models.entity.OO;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Degree;
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

    @Column(name = "CandidateLastInstitute", columnDefinition = "varchar(256) null")
    public String candidateLastInstitute;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "EducationId", referencedColumnName = "EducationId")
    public Education education;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "DegreeId", referencedColumnName = "DegreeId")
    public Degree degree;

    public static Model.Finder<String, CandidateEducation> find = new Model.Finder(CandidateEducation.class);

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setCandidateLastInstitute(String candidateLastInstitute) {
        this.candidateLastInstitute = candidateLastInstitute;
    }

}
