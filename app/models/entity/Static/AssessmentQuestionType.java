package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 19/9/16.
 */
@CacheStrategy
@Entity(name = "assessment_question_type")
@Table(name = "assessment_question_type")
public class AssessmentQuestionType  extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "assessment_question_type_id", columnDefinition = "int unsigned", unique = true)
    private long assessmentQuestionTypeId;

    @Column(name = "assessment_question_type_title", columnDefinition = "varchar(255) null")
    private String assessmentQuestionTypeTitle;

    public static Finder<String, AssessmentQuestionType> find = new Finder(AssessmentQuestionType.class);

    public long getAssessmentQuestionTypeId() {
        return assessmentQuestionTypeId;
    }

    public String getAssessmentQuestionTypeTitle() {
        return assessmentQuestionTypeTitle;
    }

    public void setAssessmentQuestionTypeTitle(String assessmentQuestionTypeTitle) {
        this.assessmentQuestionTypeTitle = assessmentQuestionTypeTitle;
    }
}
