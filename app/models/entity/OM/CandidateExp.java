package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.JobExpQuestion;
import models.entity.Static.JobExpResponse;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 22/6/16.
 */
@Entity(name = "candidateexp")
@Table(name = "candidateexp")
public class CandidateExp extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CandidateExpId", columnDefinition = "int signed", unique = true)
    private int candidateExpId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobExpQuestionId", referencedColumnName = "JobExpQuestionId")
    private JobExpQuestion jobExpQuestion;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobExpResponseId", referencedColumnName = "JobExpResponseId")
    private JobExpResponse jobExpResponse;

    @UpdatedTimestamp
    @Column(name = "CreateTimeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    public static Model.Finder<String, CandidateExp> find = new Model.Finder(CandidateExp.class);

    public CandidateExp(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public int getCandidateExpId() {
        return candidateExpId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public JobExpQuestion getJobExpQuestion() {
        return jobExpQuestion;
    }

    public void setJobExpQuestion(JobExpQuestion jobExpQuestion) {
        this.jobExpQuestion = jobExpQuestion;
    }

    public JobExpResponse getJobExpResponse() {
        return jobExpResponse;
    }

    public void setJobExpResponse(JobExpResponse jobExpResponse) {
        this.jobExpResponse = jobExpResponse;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }


}
