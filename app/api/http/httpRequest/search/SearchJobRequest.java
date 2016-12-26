package api.http.httpRequest.search;

import api.http.httpRequest.search.helper.FilterParamRequest;
import api.http.httpRequest.search.helper.SearchParamRequest;
import api.http.httpRequest.search.helper.SortParamRequest;

/**
 * Created by zero on 24/12/16.
 */
public class SearchJobRequest {

    // TODO remove this comment if this works for Object mapper, while json to object conversion
    public SearchParamRequest searchParamRequest;
    public FilterParamRequest filterParamRequest;
    public SortParamRequest sortParamRequest;
    public Integer page;


    public SearchParamRequest getSearchParamRequest() {
        return searchParamRequest;
    }

    public void setSearchParamRequest(SearchParamRequest searchParamRequest) {
        this.searchParamRequest = searchParamRequest;
    }

    public FilterParamRequest getFilterParamRequest() {
        return filterParamRequest;
    }

    public void setFilterParamRequest(FilterParamRequest filterParamRequest) {
        this.filterParamRequest = filterParamRequest;
    }

    public SortParamRequest getSortParamRequest() {
        return sortParamRequest;
    }

    public void setSortParamRequest(SortParamRequest sortParamRequest) {
        this.sortParamRequest = sortParamRequest;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
