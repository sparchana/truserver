package api.http.httpResponse.Workflow;

import models.entity.Static.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 15/10/16.
 */
public class PreScreenPopulateResponse {

    public enum Status{
        UNKNOWN,
        FAILURE,
        SUCCESS,
        INVALID
    }

    public static class PreScreenElement {
        public String propertyTitle;

        public List<Object> propertyIdList;
        public List<Object> jobPostElementList;
        public List<Object> candidateElementList;
        public boolean isMatching;
        public boolean isSingleEntity;
        public boolean isMinReq;

        // used with single entity
        public Object jobPostElement;
        public Object candidateElement;

        public PreScreenElement() {
            this.isMatching = true;
            this.isMinReq = true;
            this.isSingleEntity = true;
            this.propertyIdList = new ArrayList<>();
        }

        public String getPropertyTitle() {
            return propertyTitle;
        }

        public void setPropertyTitle(String propertyTitle) {
            this.propertyTitle = propertyTitle;
        }

        public List<Object> getPropertyIdList() {
            return propertyIdList;
        }

        public void setPropertyIdList(List<Object> propertyIdList) {
            this.propertyIdList = propertyIdList;
        }

        public List<Object> getJobPostElementList() {
            return jobPostElementList;
        }

        public void setJobPostElementList(List<Object> jobPostElementList) {
            this.jobPostElementList = jobPostElementList;
        }

        public List<Object> getCandidateElementList() {
            return candidateElementList;
        }

        public void setCandidateElementList(List<Object> candidateElementList) {
            this.candidateElementList = candidateElementList;
        }
        public Object getJobPostElement() {
            return jobPostElement;
        }

        public void setJobPostElement(Object jobPostElement) {
            this.jobPostElement = jobPostElement;
        }

        public Object getCandidateElement() {
            return candidateElement;
        }

        public void setCandidateElement(Object candidateElement) {
            this.candidateElement = candidateElement;
        }
    }

    // min req
    public Long jobPostId;
    public Long candidateId;
    public String preScreenTitleMsg;
    public List<PreScreenElement> elementList;
    public Status status;
    public String jobPostMinReq;

    public PreScreenPopulateResponse(){
        this.elementList = new ArrayList<>();
    }

    public List<PreScreenElement> getElementList() {
        return elementList;
    }

    public void setElementList(List<PreScreenElement> elementList) {
        this.elementList = elementList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getJobPostMinReq() {
        return jobPostMinReq;
    }

    public void setJobPostMinReq(String jobPostMinReq) {
        this.jobPostMinReq = jobPostMinReq;
    }
}
