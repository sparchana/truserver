package api.http.httpRequest;

import java.util.List;

/**
 * Created by zero on 17/9/16.
 */
public class AssessmentRequest {
    public static class AssessmentOption {
        Long jobRoleId;
        Long assessmentQuestionId;
        String assessmentResponse;

        public AssessmentOption(){
        }
        public AssessmentOption(AssessmentRequest.AssessmentOption assessmentOption){
            //constructor Code
        }

        public Long getJobRoleId() {
            return jobRoleId;
        }

        public void setJobRoleId(Long jobRoleId) {
            this.jobRoleId = jobRoleId;
        }

        public Long getAssessmentQuestionId() {
            return assessmentQuestionId;
        }

        public void setAssessmentQuestionId(Long assessmentQuestionId) {
            this.assessmentQuestionId = assessmentQuestionId;
        }

        public String getAssessmentResponse() {
            return assessmentResponse;
        }

        public void setAssessmentResponse(String assessmentResponse) {
            this.assessmentResponse = assessmentResponse;
        }
    }
    public List<AssessmentOption> responseList;

    public List<AssessmentOption> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<AssessmentOption> responseList) {
        this.responseList = responseList;
    }
}
