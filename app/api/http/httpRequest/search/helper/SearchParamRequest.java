package api.http.httpRequest.search.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class SearchParamRequest {
    List<String> keywordList;
    String locationName;
    String educationText;
    String experienceText;

    public SearchParamRequest() {
        this.keywordList = new ArrayList<>();
    }

    public SearchParamRequest(SearchParamRequest searchParamRequest) {
    }

    public List<String> getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getEducationText() {
        return educationText;
    }

    public void setEducationText(String educationText) {
        this.educationText = educationText;
    }

    public String getExperienceText() {
        return experienceText;
    }

    public void setExperienceText(String experienceText) {
        this.experienceText = experienceText;
    }
}
