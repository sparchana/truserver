package api.http.httpRequest.Recruiter;

import api.http.httpRequest.TruRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.Logger;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by User on 30-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CompanyLeadRequest extends TruRequest {

    private Long companyLeadId;
    private String companyLeadUUId;
    private Timestamp companyLeadCreateTimeStamp;
    private Timestamp companyLeadUpdateTimeStamp;
    private String companyLeadName;
    private Long companyLeadType;
    private String companyLeadWebsite;
    private Long companyLeadIndustry;
    private List<Long> recruiterLeadList;

    public Long getCompanyLeadId() {
        return companyLeadId;
    }

    public void setCompanyLeadId(Long companyLeadId) {
        this.companyLeadId = companyLeadId;
    }

    public String getCompanyLeadUUId() {
        return companyLeadUUId;
    }

    public void setCompanyLeadUUId(String companyLeadUUId) {
        this.companyLeadUUId = companyLeadUUId;
    }

    public Timestamp getCompanyLeadCreateTimeStamp() {
        return companyLeadCreateTimeStamp;
    }

    public void setCompanyLeadCreateTimeStamp(Timestamp companyLeadCreateTimeStamp) {
        this.companyLeadCreateTimeStamp = companyLeadCreateTimeStamp;
    }

    public Timestamp getCompanyLeadUpdateTimeStamp() {
        return companyLeadUpdateTimeStamp;
    }

    public void setCompanyLeadUpdateTimeStamp(Timestamp companyLeadUpdateTimeStamp) {
        this.companyLeadUpdateTimeStamp = companyLeadUpdateTimeStamp;
    }

    public String getCompanyLeadName() {
        return companyLeadName;
    }

    public void setCompanyLeadName(String companyLeadName) {
        this.companyLeadName = companyLeadName;
    }

    public Long getCompanyLeadType() {
        return companyLeadType;
    }

    public void setCompanyLeadType(Long companyLeadType) {
        this.companyLeadType = companyLeadType;
    }

    public String getCompanyLeadWebsite() {
        return companyLeadWebsite;
    }

    public void setCompanyLeadWebsite(String companyLeadWebsite) {
        this.companyLeadWebsite = companyLeadWebsite;
    }

    public Long getCompanyLeadIndustry() {
        return companyLeadIndustry;
    }

    public void setCompanyLeadIndustry(Long companyLeadIndustry) {
        this.companyLeadIndustry = companyLeadIndustry;
    }

    public List<Long> getRecruiterLeadList() {
        return recruiterLeadList;
    }

    public void setRecruiterLeadList(List<Long> recruiterLeadList) {
        this.recruiterLeadList = recruiterLeadList;
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
