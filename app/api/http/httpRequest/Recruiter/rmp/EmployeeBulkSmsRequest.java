package api.http.httpRequest.Recruiter.rmp;

import java.util.List;

/**
 * Created by zero on 21/2/17.
 */
public class EmployeeBulkSmsRequest {
    public List<Long> employeeIdList;
    public List<Long> jobPostIdList;
    public int smsType;
    public String smsText;

    public List<Long> getEmployeeIdList() {
        return employeeIdList;
    }

    public void setEmployeeIdList(List<Long> employeeIdList) {
        this.employeeIdList = employeeIdList;
    }

    public List<Long> getJobPostIdList() {
        return jobPostIdList;
    }

    public void setJobPostIdList(List<Long> jobPostIdList) {
        this.jobPostIdList = jobPostIdList;
    }

    public int getSmsType() {
        return smsType;
    }

    public void setSmsType(int smsType) {
        this.smsType = smsType;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }
}
