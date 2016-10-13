package api.http.httpRequest.Recruiter;

/**
 * Created by batcoder1 on 18/7/16.
 */
public class AddRecruiterRequest {
    public String recruiterName;
    public String recruiterMobile;
    public String recruiterLandline;
    public String recruiterEmail;
    public Long recruiterCompany;
    public String recruiterCompanyName;
    public Integer recruiterInterviewCredits;
    public Integer recruiterContactCredits;

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterMobile() {
        return recruiterMobile;
    }

    public void setRecruiterMobile(String recruiterMobile) {
        this.recruiterMobile = recruiterMobile;
    }

    public String getRecruiterLandline() {
        return recruiterLandline;
    }

    public void setRecruiterLandline(String recruiterLandline) {
        this.recruiterLandline = recruiterLandline;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public Long getRecruiterCompany() {
        return recruiterCompany;
    }

    public void setRecruiterCompany(Long recruiterCompany) {
        this.recruiterCompany = recruiterCompany;
    }

    public String getRecruiterCompanyName() {
        return recruiterCompanyName;
    }

    public void setRecruiterCompanyName(String recruiterCompanyName) {
        this.recruiterCompanyName = recruiterCompanyName;
    }

    public Integer getRecruiterInterviewCredits() {
        return recruiterInterviewCredits;
    }

    public void setRecruiterInterviewCredits(Integer recruiterInterviewCredits) {
        this.recruiterInterviewCredits = recruiterInterviewCredits;
    }

    public Integer getRecruiterContactCredits() {
        return recruiterContactCredits;
    }

    public void setRecruiterContactCredits(Integer recruiterContactCredits) {
        this.recruiterContactCredits = recruiterContactCredits;
    }
}
