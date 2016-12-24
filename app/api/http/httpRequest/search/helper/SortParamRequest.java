package api.http.httpRequest.search.helper;

/**
 * Created by zero on 24/12/16.
 */
public class SortParamRequest {
    int sortBySalary; // 0 low-to-high, 1 high-to-low
    int sortByLatest; // 0 low-to-high, 1 high-to-low

    public SortParamRequest() {
    }

    public int getSortBySalary() {
        return sortBySalary;
    }

    public void setSortBySalary(int sortBySalary) {
        this.sortBySalary = sortBySalary;
    }

    public int getSortByLatest() {
        return sortByLatest;
    }

    public void setSortByLatest(int sortByLatest) {
        this.sortByLatest = sortByLatest;
    }
}
