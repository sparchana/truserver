package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.JobToSkill;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "skill")
@Table(name = "skill")
public class Skill extends Model{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "SkillId", columnDefinition = "int signed", unique = true)
    private int skillId;

    @Column(name = "skillName", columnDefinition = "varchar(100) null")
    private String skillName;

    @Column(name = "skillQuestion", columnDefinition = "varchar(255) null")
    private String skillQuestion;

    @JsonBackReference
    @OneToMany(mappedBy = "skill", cascade = CascadeType.REMOVE)
    private List<JobToSkill> jobToSkillList;

    @JsonBackReference
    @OneToMany(mappedBy = "skill", cascade = CascadeType.REMOVE)
    private List<SkillQualifier> skillQualifierList;

    @JsonBackReference
    @OneToMany(mappedBy = "skill", cascade = CascadeType.REMOVE)
    private List<AssessmentQuestion> assessmentQuestionList;

    public String getSkillName() {
        return skillName;
    }

    public static Model.Finder<String, Skill> find = new Model.Finder(Skill.class);

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public List<JobToSkill> getJobToSkillList() {
        return jobToSkillList;
    }

    public void setJobToSkillList(List<JobToSkill> jobToSkillList) {
        this.jobToSkillList = jobToSkillList;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillQuestion() {
        return skillQuestion;
    }

    public void setSkillQuestion(String skillQuestion) {
        this.skillQuestion = skillQuestion;
    }

    public List<SkillQualifier> getSkillQualifierList() {
        return skillQualifierList;
    }

    public void setSkillQualifierList(List<SkillQualifier> skillQualifierList) {
        this.skillQualifierList = skillQualifierList;
    }

    public int getSkillId() {
        return skillId;
    }

    public List<AssessmentQuestion> getAssessmentQuestionList() {
        return assessmentQuestionList;
    }

    public void setAssessmentQuestionList(List<AssessmentQuestion> assessmentQuestionList) {
        this.assessmentQuestionList = assessmentQuestionList;
    }
}
