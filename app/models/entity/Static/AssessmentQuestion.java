package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.LanguageKnown;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 14/9/16.
 */

@Entity(name = "assessment_question")
@Table(name = "assessment_question")
public class AssessmentQuestion extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "assessment_question_id", columnDefinition = "int unsigned", unique = true)
    private int  assessmentQuestionId;

    @Column(name = "question_text", columnDefinition = "text null")
    private String questionText;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "assessment_question_type_id", referencedColumnName = "assessment_question_type_id")
    private AssessmentQuestionType assessmentQuestionType;

    @Column(name = "OptionA", columnDefinition = "text null")
    private String optionA;

    @Column(name = "OptionB", columnDefinition = "text null")
    private String optionB;

    @Column(name = "OptionC", columnDefinition = "text null")
    private String optionC;

    @Column(name = "OptionD", columnDefinition = "text null")
    private String optionD;

    @Column(name = "OptionE", columnDefinition = "text null")
    private String optionE;

    @JsonBackReference
    @Column(name = "Answer", columnDefinition = "text null")
    private String answer;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;

    public static Finder<String, AssessmentQuestion> find = new Finder(AssessmentQuestion.class);

    public int getAssessmentQuestionId() {
        return assessmentQuestionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionE() {
        return optionE;
    }

    public void setOptionE(String optionE) {
        this.optionE = optionE;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public AssessmentQuestionType getAssessmentQuestionType() {
        return assessmentQuestionType;
    }

    public void setAssessmentQuestionType(AssessmentQuestionType assessmentQuestionType) {
        this.assessmentQuestionType = assessmentQuestionType;
    }
}
