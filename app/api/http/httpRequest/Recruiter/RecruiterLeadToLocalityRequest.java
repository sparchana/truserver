package api.http.httpRequest.Recruiter;

import api.http.httpRequest.TruRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.Logger;

import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * Created by User on 24-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RecruiterLeadToLocalityRequest extends TruRequest{
    private Long recruiterLeadToLocalityId;
    private String recruiterLeadToLocalityUUID;
    private Timestamp recruiterLeadToLocalityCreateTimeStamp;
    private Timestamp recruiterLeadToLocalityUpdateTimeStamp;
    private Long locality;
    private Long recruiterLeadToJobRoleId;

    public Long getRecruiterLeadToLocalityId() {
        return recruiterLeadToLocalityId;
    }

    public void setRecruiterLeadToLocalityId(Long recruiterLeadToLocalityId) {
        this.recruiterLeadToLocalityId = recruiterLeadToLocalityId;
    }

    public String getRecruiterLeadToLocalityUUID() {
        return recruiterLeadToLocalityUUID;
    }

    public void setRecruiterLeadToLocalityUUID(String recruiterLeadToLocalityUUID) {
        this.recruiterLeadToLocalityUUID = recruiterLeadToLocalityUUID;
    }

    public Timestamp getRecruiterLeadToLocalityCreateTimeStamp() {
        return recruiterLeadToLocalityCreateTimeStamp;
    }

    public void setRecruiterLeadToLocalityCreateTimeStamp(Timestamp recruiterLeadToLocalityCreateTimeStamp) {
        this.recruiterLeadToLocalityCreateTimeStamp = recruiterLeadToLocalityCreateTimeStamp;
    }

    public Timestamp getRecruiterLeadToLocalityUpdateTimeStamp() {
        return recruiterLeadToLocalityUpdateTimeStamp;
    }

    public void setRecruiterLeadToLocalityUpdateTimeStamp(Timestamp recruiterLeadToLocalityUpdateTimeStamp) {
        this.recruiterLeadToLocalityUpdateTimeStamp = recruiterLeadToLocalityUpdateTimeStamp;
    }

    public Long getLocality() {
        return locality;
    }

    public void setLocality(Long locality) {
        this.locality = locality;
    }

    public Long getRecruiterLeadToJobRoleId() {
        return recruiterLeadToJobRoleId;
    }

    public void setRecruiterLeadToJobRoleId(Long recruiterLeadToJobRoleId) {
        this.recruiterLeadToJobRoleId = recruiterLeadToJobRoleId;
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
