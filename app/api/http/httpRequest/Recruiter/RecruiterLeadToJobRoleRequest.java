package api.http.httpRequest.Recruiter;

import api.http.httpRequest.TruRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.entity.Static.JobRole;
import play.Logger;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by User on 24-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RecruiterLeadToJobRoleRequest extends TruRequest{

    private Long recruiterLeadToJobRoleId;
    private String recruiterLeadToJobRoleUUId;
    private Timestamp recruiterLeadToJobRoleCreateTimeStamp;
    private Timestamp recruiterLeadToJobRoleUpdateTimeStamp;
    private Long jobRole;
    private Long recruiterLeadId;
    private String jobInterviewAddress;
    private Long jobVacancies;
    private Long jobSalaryMin;
    private Long jobSalaryMax;
    private String jobGender;
    private String jobDetailRequirement;
    protected List<RecruiterLeadToLocalityRequest> recruiterLeadToLocalityRequestList;

    public Long getRecruiterLeadToJobRoleId() {
        return recruiterLeadToJobRoleId;
    }

    public void setRecruiterLeadToJobRoleId(Long recruiterLeadToJobRoleId) {
        this.recruiterLeadToJobRoleId = recruiterLeadToJobRoleId;
    }

    public String getRecruiterLeadToJobRoleUUId() {
        return recruiterLeadToJobRoleUUId;
    }

    public void setRecruiterLeadToJobRoleUUId(String recruiterLeadToJobRoleUUId) {
        this.recruiterLeadToJobRoleUUId = recruiterLeadToJobRoleUUId;
    }

    public Timestamp getRecruiterLeadToJobRoleCreateTimeStamp() {
        return recruiterLeadToJobRoleCreateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleCreateTimeStamp(Timestamp recruiterLeadToJobRoleCreateTimeStamp) {
        this.recruiterLeadToJobRoleCreateTimeStamp = recruiterLeadToJobRoleCreateTimeStamp;
    }

    public Timestamp getRecruiterLeadToJobRoleUpdateTimeStamp() {
        return recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public void setRecruiterLeadToJobRoleUpdateTimeStamp(Timestamp recruiterLeadToJobRoleUpdateTimeStamp) {
        this.recruiterLeadToJobRoleUpdateTimeStamp = recruiterLeadToJobRoleUpdateTimeStamp;
    }

    public Long getJobRole() {
        return jobRole;
    }

    public void setJobRole(Long jobRole) {
        this.jobRole = jobRole;
    }

    public long getRecruiterLeadId() {
        return recruiterLeadId;
    }

    public void setRecruiterLeadId(long recruiterLeadId) {
        this.recruiterLeadId = recruiterLeadId;
    }

    public String getJobInterviewAddress() {
        return jobInterviewAddress;
    }

    public void setJobInterviewAddress(String jobInterviewAddress) {
        this.jobInterviewAddress = jobInterviewAddress;
    }

    public Long getJobVacancies() {
        return jobVacancies;
    }

    public void setJobVacancies(Long jobVacancies) {
        this.jobVacancies = jobVacancies;
    }

    public Long getJobSalaryMin() {
        return jobSalaryMin;
    }

    public void setJobSalaryMin(Long jobSalaryMin) {
        this.jobSalaryMin = jobSalaryMin;
    }

    public Long getJobSalaryMax() {
        return jobSalaryMax;
    }

    public void setJobSalaryMax(Long jobSalaryMax) {
        this.jobSalaryMax = jobSalaryMax;
    }

    public String getJobGender() {
        return jobGender;
    }

    public void setJobGender(String jobGender) {
        this.jobGender = jobGender;
    }

    public String getJobDetailRequirement() {
        return jobDetailRequirement;
    }

    public void setJobDetailRequirement(String jobDetailRequirement) {
        this.jobDetailRequirement = jobDetailRequirement;
    }

    public List<RecruiterLeadToLocalityRequest> getRecruiterLeadToLocalityRequestList() {
        return recruiterLeadToLocalityRequestList;
    }

    public void setRecruiterLeadToLocalityRequestList(List<RecruiterLeadToLocalityRequest> recruiterLeadToLocalityRequestList) {
        this.recruiterLeadToLocalityRequestList = recruiterLeadToLocalityRequestList;
    }

    @Override
    public String toString(Object caller) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( caller.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = caller.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(caller) );
            } catch ( IllegalAccessException e ) {
                Logger.info(e.toString());
            }
            result.append(newLine);
        }
        result.append("}");
        return result.toString();
    }
}
