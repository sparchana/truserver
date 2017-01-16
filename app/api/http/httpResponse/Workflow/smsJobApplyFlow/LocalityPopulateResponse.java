package api.http.httpResponse.Workflow.smsJobApplyFlow;

import java.util.Map;

/**
 * Created by zero on 16/1/17.
 */
public class LocalityPopulateResponse {
    private Map<Long, String> localityMap;
    private String jobTitle;
    private String jobRole;
    private String companyName;

    public LocalityPopulateResponse(Map<Long, String> localityMap,
                                    String jobTitle,
                                    String jobRole,
                                    String companyName) {
        this.localityMap = localityMap;
        this.jobTitle = jobTitle;
        this.jobRole = jobRole;
        this.companyName = companyName;
    }

    public Map<Long, String> getLocalityMap() {
        return localityMap;
    }

    public void setLocalityMap(Map<Long, String> localityMap) {
        this.localityMap = localityMap;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
