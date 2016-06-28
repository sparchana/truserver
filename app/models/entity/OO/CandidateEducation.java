package models.entity.OO;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "candidateEducationId", columnDefinition = "int signed", unique = true)
    private int candidateEducationId;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @Column(name = "CandidateLastInstitute", columnDefinition = "varchar(256) null")
    private String candidateLastInstitute;

    @JsonBackReference
    @OneToOne(mappedBy = "candidateEducation")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "EducationId", referencedColumnName = "EducationId")
    private Education education;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "DegreeId", referencedColumnName = "DegreeId")
    private Degree degree;

    public static Model.Finder<String, CandidateEducation> find = new Model.Finder(CandidateEducation.class);

    public int getCandidateEducationId() {
        return candidateEducationId;
    }

    public void setCandidateEducationId(int candidateEducationId) {
        this.candidateEducationId = candidateEducationId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getCandidateLastInstitute() {
        return candidateLastInstitute;
    }

    public void setCandidateLastInstitute(String candidateLastInstitute) {
        this.candidateLastInstitute = candidateLastInstitute;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }
}
