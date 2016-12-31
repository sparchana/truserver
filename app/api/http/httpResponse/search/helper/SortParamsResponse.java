package api.http.httpResponse.search.helper;

/**
 * Created by zero on 24/12/16.
 */
public class SortParamsResponse {
    private int sortBySalary; // 0 low-to-high, 1 high-to-low
    private int sortByLatest; // 0 low-to-high, 1 high-to-low

    public SortParamsResponse() {
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
