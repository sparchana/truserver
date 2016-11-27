package api.http.httpResponse.Workflow;

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
    public static class PreScreenCustomObject {
        Object object;
        boolean isObjectAvailable;
        Object placeHolder;

        public PreScreenCustomObject(Object object, Object placeHolder , boolean isObjectAvailable) {
            this.object = object;
            this.placeHolder = placeHolder;
            this.isObjectAvailable = isObjectAvailable;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getPlaceHolder() {
            return placeHolder;
        }

        public void setPlaceHolder(Object placeHolder) {
            this.placeHolder = placeHolder;
        }

        public boolean isObjectAvailable() {
            return isObjectAvailable;
        }

        public void setObjectAvailable(boolean objectAvailable) {
            isObjectAvailable = objectAvailable;
        }
    }

    public static class PreScreenElement {
            public String propertyTitle;
            public int propertyId;

            public List<Object> propertyIdList;
            public List<PreScreenCustomObject> jobPostElementList;
            public List<PreScreenCustomObject> candidateElementList;
            public boolean isMatching;
            public boolean isSingleEntity;
            public boolean isMinReq;

            // used with single entity
            public PreScreenCustomObject jobPostElement;
            public PreScreenCustomObject candidateElement;

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

            public int getPropertyId() {
                return propertyId;
            }

            public void setPropertyId(int propertyId) {
                this.propertyId = propertyId;
            }

            public List<Object> getPropertyIdList() {
                return propertyIdList;
            }

            public void setPropertyIdList(List<Object> propertyIdList) {
                this.propertyIdList = propertyIdList;
            }

            public List<PreScreenCustomObject> getJobPostElementList() {
                return jobPostElementList;
            }

            public void setJobPostElementList(List<PreScreenCustomObject> jobPostElementList) {
                this.jobPostElementList = jobPostElementList;
            }

            public List<PreScreenCustomObject> getCandidateElementList() {
                return candidateElementList;
            }

            public void setCandidateElementList(List<PreScreenCustomObject> candidateElementList) {
                this.candidateElementList = candidateElementList;
            }

            public boolean isMatching() {
                return isMatching;
            }

            public void setMatching(boolean matching) {
                isMatching = matching;
            }

            public boolean isSingleEntity() {
                return isSingleEntity;
            }

            public void setSingleEntity(boolean singleEntity) {
                isSingleEntity = singleEntity;
            }

            public boolean isMinReq() {
                return isMinReq;
            }

            public void setMinReq(boolean minReq) {
                isMinReq = minReq;
            }

            public PreScreenCustomObject getJobPostElement() {
                return jobPostElement;
            }

            public void setJobPostElement(PreScreenCustomObject jobPostElement) {
                this.jobPostElement = jobPostElement;
            }

            public PreScreenCustomObject getCandidateElement() {
                return candidateElement;
            }

            public void setCandidateElement(PreScreenCustomObject candidateElement) {
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
    public boolean visible;

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

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
