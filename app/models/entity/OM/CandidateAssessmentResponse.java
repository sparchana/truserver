package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Static.AssessmentQuestion;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 22/9/16.
 */

@Entity(name = "candidate_assessment_response")
@Table(name = "candidate_assessment_response")
public class CandidateAssessmentResponse extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ca_response_id", columnDefinition = "bigint signed", unique = true)
    private int responseId;

    @Column(name = "create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "update_timeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "ca_attempt_id", referencedColumnName = "ca_attempt_id")
    private CandidateAssessmentAttempt candidateAssessmentAttempt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "assessment_question_id", referencedColumnName = "assessment_question_id")
    private AssessmentQuestion assessmentQuestion;

    @Column(name = "candidate_answer", columnDefinition = "text null")
    private String candidateAnswer;

    @Column(name = "score", columnDefinition = "int null")
    private Integer score;

    public CandidateAssessmentResponse(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, CandidateAssessmentResponse> find = new Finder(CandidateAssessmentResponse.class);

    public int getResponseId() {
        return responseId;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public CandidateAssessmentAttempt getCandidateAssessmentAttempt() {
        return candidateAssessmentAttempt;
    }

    public void setCandidateAssessmentAttempt(CandidateAssessmentAttempt candidateAssessmentAttempt) {
        this.candidateAssessmentAttempt = candidateAssessmentAttempt;
    }

    public AssessmentQuestion getAssessmentQuestion() {
        return assessmentQuestion;
    }

    public void setAssessmentQuestion(AssessmentQuestion assessmentQuestion) {
        this.assessmentQuestion = assessmentQuestion;
    }

    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    public void setCandidateAnswer(String candidateAnswer) {
        this.candidateAnswer = candidateAnswer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
