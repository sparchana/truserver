package api.http.httpRequest.search.helper;

/**
 * Created by zero on 24/12/16.
 */
public class SortParamRequest {
    Integer sortBy; // 0 low-to-high-sal, 1 high-to-low-sal, 2 newest-on-top:byDatePosted

    public SortParamRequest() {
    }

    public Integer getSortBy() {
        return sortBy;
    }

    public void setSortBy(Integer sortBy) {
        this.sortBy = sortBy;
    }
}
