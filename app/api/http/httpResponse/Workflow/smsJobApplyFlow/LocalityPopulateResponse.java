package api.http.httpResponse.Workflow.smsJobApplyFlow;

import java.util.Map;

/**
 * Created by zero on 16/1/17.
 */
public class LocalityPopulateResponse {
    private Map<Long, String> localityMap;

    public LocalityPopulateResponse(Map<Long, String> localityMap) {
        this.localityMap = localityMap;
    }

    public Map<Long, String> getLocalityMap() {
        return localityMap;
    }

    public void setLocalityMap(Map<Long, String> localityMap) {
        this.localityMap = localityMap;
    }
}
