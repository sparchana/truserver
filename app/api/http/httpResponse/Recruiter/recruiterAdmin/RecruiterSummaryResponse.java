package api.http.httpResponse.Recruiter.recruiterAdmin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 24/1/17.
 */
public class RecruiterSummaryResponse {
   public String companyName;
    public List<RecruiterSummary> recruiterSummaryList;

    public RecruiterSummaryResponse() {
        this.companyName = "";
        this.recruiterSummaryList = new ArrayList<>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<RecruiterSummary> getRecruiterSummaryList() {
        return recruiterSummaryList;
    }

    public void setRecruiterSummaryList(List<RecruiterSummary> recruiterSummaryList) {
        this.recruiterSummaryList = recruiterSummaryList;
    }
}
