package api.http.httpRequest;

import java.util.List;

/**
 * Created by zero on 19/7/16.
 */
public class DeactiveToActiveRequest {
    List<Long> deactiveToActiveList;

    public List<Long> getDeactiveToActiveList() {
        return deactiveToActiveList;
    }

    public void setDeactiveToActiveList(List<Long> deactiveToActiveList) {
        this.deactiveToActiveList = deactiveToActiveList;
    }
}
