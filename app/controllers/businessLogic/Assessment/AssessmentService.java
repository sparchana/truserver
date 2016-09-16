package controllers.businessLogic.Assessment;

import models.entity.Static.AssessmentQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 15/9/16.
 */
public class AssessmentService {
    public static List<AssessmentQuestion> getQuestions(Long jobRoleId){
        List<AssessmentQuestion> assessmentQuestionList = new ArrayList<>();
        if(jobRoleId != null){
            assessmentQuestionList.add(AssessmentQuestion.find.where().eq("jobRoleId", jobRoleId).findUnique());
        } else {
            assessmentQuestionList = AssessmentQuestion.find.all();
        }
        return assessmentQuestionList;
    }
}
