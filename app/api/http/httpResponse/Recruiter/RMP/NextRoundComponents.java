package api.http.httpResponse.Recruiter.RMP;

import api.http.httpResponse.Workflow.InterviewSlotPopulateResponse;

import java.util.List;

/**
 * Created by zero on 11/2/17.
 */
public class NextRoundComponents {

    private List<Recruiter> recruiterList;
    private InterviewSlotPopulateResponse interviewSlotPopulateResponse;
    private Location location;
    private Long jobPostId;


    public static class Location {
        private String jobPostAddress;
        private Double latitude;
        private Double longitude;
        private Long jobPostPinCode;

        public String getJobPostAddress() {
            return jobPostAddress;
        }

        public void setJobPostAddress(String jobPostAddress) {
            this.jobPostAddress = jobPostAddress;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Long getJobPostPinCode() {
            return jobPostPinCode;
        }

        public void setJobPostPinCode(Long jobPostPinCode) {
            this.jobPostPinCode = jobPostPinCode;
        }
    }

    public static class Recruiter {
        private Long recruiterProfileId;
        private String recruiterProfileName;
        private String recruiterProfileMobile;

        public Long getRecruiterProfileId() {
            return recruiterProfileId;
        }

        public void setRecruiterProfileId(Long recruiterProfileId) {
            this.recruiterProfileId = recruiterProfileId;
        }

        public String getRecruiterProfileName() {
            return recruiterProfileName;
        }

        public void setRecruiterProfileName(String recruiterProfileName) {
            this.recruiterProfileName = recruiterProfileName;
        }

        public String getRecruiterProfileMobile() {
            return recruiterProfileMobile;
        }

        public void setRecruiterProfileMobile(String recruiterProfileMobile) {
            this.recruiterProfileMobile = recruiterProfileMobile;
        }
    }
    
    public List<Recruiter> getRecruiterList() {
        return recruiterList;
    }

    public void setRecruiterList(List<Recruiter> recruiterList) {
        this.recruiterList = recruiterList;
    }

    public InterviewSlotPopulateResponse getInterviewSlotPopulateResponse() {
        return interviewSlotPopulateResponse;
    }

    public void setInterviewSlotPopulateResponse(InterviewSlotPopulateResponse interviewSlotPopulateResponse) {
        this.interviewSlotPopulateResponse = interviewSlotPopulateResponse;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }
}
