package api.http.httpResponse.search;

import api.http.httpResponse.JobPostResponse;
import api.http.httpResponse.search.helper.FilterParamsResponse;
import api.http.httpResponse.search.helper.SearchParamsResponse;
import api.http.httpResponse.search.helper.SortParamsResponse;

/**
 * Created by zero on 24/12/16.
 */

public class SearchJobResponse {
    private enum STATUS {
        UNKNOWN,
        FAILED,
        SUCCESS
    }

    private SearchJobResponse.STATUS status;
    private String message;
    private int page;
    private SearchParamsResponse searchParams;
    private FilterParamsResponse filterParams;
    private SortParamsResponse sortParams;
    private JobPostResponse results;
    public boolean isURLInvalid;
    public boolean isUserLoggedIn;


    public SearchJobResponse() {
        this.status = STATUS.UNKNOWN;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public SearchParamsResponse getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchParamsResponse searchParams) {
        this.searchParams = searchParams;
    }

    public FilterParamsResponse getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(FilterParamsResponse filterParams) {
        this.filterParams = filterParams;
    }

    public SortParamsResponse getSortParams() {
        return sortParams;
    }

    public void setSortParams(SortParamsResponse sortParams) {
        this.sortParams = sortParams;
    }

    public JobPostResponse getResults() {
        return results;
    }

    public void setResults(JobPostResponse results) {
        this.results = results;
    }

    public boolean isURLInvalid() {
        return isURLInvalid;
    }

    public void setURLInvalid(boolean URLInvalid) {
        isURLInvalid = URLInvalid;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
    }
}
