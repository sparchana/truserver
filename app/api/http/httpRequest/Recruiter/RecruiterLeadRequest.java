package api.http.httpRequest.Recruiter;

import api.http.httpRequest.TruRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.Logger;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by dodo on 5/10/16.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecruiterLeadRequest extends TruRequest{
    protected Long recruiterLeadId;
    protected Integer recruiterLeadStatus;
    protected String recruiterLeadName;
    protected String recruiterLeadMobile;
    protected String recruiterLeadAltNumber;
    protected String recruiterLeadEmail;
    protected Integer recruiterLeadChannel;
    protected String recruiterRequirement;
    protected Integer recruiterLeadSourceType;
    protected Integer recruiterLeadSourceName;
    protected Date recruiterLeadSourceDate;
    protected CompanyLeadRequest companyLeadRequest;
    protected List<RecruiterLeadToJobRoleRequest> recruiterLeadToJobRoleRequestList;

    public Long getRecruiterLeadId() {
        return recruiterLeadId;
    }

    public void setRecruiterLeadId(Long recruiterLeadId) {
        this.recruiterLeadId = recruiterLeadId;
    }

    public int getRecruiterLeadStatus() {
        return recruiterLeadStatus;
    }

    public void setRecruiterLeadStatus(int recruiterLeadStatus) {
        this.recruiterLeadStatus = recruiterLeadStatus;
    }

    public String getRecruiterLeadName() {
        return recruiterLeadName;
    }

    public void setRecruiterLeadName(String recruiterLeadName) {
        this.recruiterLeadName = recruiterLeadName;
    }

    public String getRecruiterLeadMobile() {
        return recruiterLeadMobile;
    }

    public void setRecruiterLeadMobile(String recruiterLeadMobile) {
        this.recruiterLeadMobile = recruiterLeadMobile;
    }

    public String getRecruiterLeadAltNumber() {
        return recruiterLeadAltNumber;
    }

    public void setRecruiterLeadAltNumber(String recruiterLeadAltNumber) {
        this.recruiterLeadAltNumber = recruiterLeadAltNumber;
    }

    public String getRecruiterLeadEmail() {
        return recruiterLeadEmail;
    }

    public void setRecruiterLeadEmail(String recruiterLeadEmail) {
        this.recruiterLeadEmail = recruiterLeadEmail;
    }

    public int getRecruiterLeadChannel() {
        return recruiterLeadChannel;
    }

    public void setRecruiterLeadChannel(int recruiterLeadChannel) {
        this.recruiterLeadChannel = recruiterLeadChannel;
    }

    public String getRecruiterRequirement() {
        return recruiterRequirement;
    }

    public void setRecruiterRequirement(String recruiterRequirement) {
        this.recruiterRequirement = recruiterRequirement;
    }

    public Integer getRecruiterLeadSourceType() {
        return recruiterLeadSourceType;
    }

    public void setRecruiterLeadSourceType(Integer recruiterLeadSourceType) {
        this.recruiterLeadSourceType = recruiterLeadSourceType;
    }

    public Integer getRecruiterLeadSourceName() {
        return recruiterLeadSourceName;
    }

    public void setRecruiterLeadSourceName(Integer recruiterLeadSourceName) {
        this.recruiterLeadSourceName = recruiterLeadSourceName;
    }

    public Date getRecruiterLeadSourceDate() {
        return recruiterLeadSourceDate;
    }

    public void setRecruiterLeadSourceDate(Date recruiterLeadSourceDate) {
        this.recruiterLeadSourceDate = recruiterLeadSourceDate;
    }

    public List<RecruiterLeadToJobRoleRequest> getRecruiterLeadToJobRoleRequestList() {
        return recruiterLeadToJobRoleRequestList;
    }

    public void setRecruiterLeadToJobRoleRequestList(List<RecruiterLeadToJobRoleRequest> recruiterLeadToJobRoleRequestList) {
        this.recruiterLeadToJobRoleRequestList = recruiterLeadToJobRoleRequestList;
    }

    public CompanyLeadRequest getCompanyLeadRequest() {
        return companyLeadRequest;
    }

    public void setCompanyLeadRequest(CompanyLeadRequest companyLeadRequest) {
        this.companyLeadRequest = companyLeadRequest;
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
