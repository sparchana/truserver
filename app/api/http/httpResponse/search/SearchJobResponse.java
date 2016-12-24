package api.http.httpResponse.search;

import api.http.httpResponse.search.helper.FilterParamsResponse;
import api.http.httpResponse.search.helper.SearchParamsResponse;
import api.http.httpResponse.search.helper.SortParamsResponse;

import java.util.LinkedList;
import java.util.List;

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
    private List<Object> results;

    public SearchJobResponse() {
        this.results = new LinkedList<>();
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

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }
}
