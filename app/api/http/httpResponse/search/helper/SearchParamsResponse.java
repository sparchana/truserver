package api.http.httpResponse.search.helper;

import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.Locality;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class SearchParamsResponse {
    public List<String> searchKeywords;
    public List<String> positiveKeywords;
    public List<String> negativeKeywords;
    public Locality locality;
    public Education education;
    public Experience experience;

    public SearchParamsResponse() {
        this.searchKeywords = new ArrayList<>();
        this.positiveKeywords = new ArrayList<>();
        this.negativeKeywords = new ArrayList<>();
    }

    public SearchParamsResponse(List<String> searchKeywords, Locality locality) {
        this.searchKeywords = searchKeywords;
        this.locality = locality;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public List<String> getPositiveKeywords() {
        return positiveKeywords;
    }

    public void setPositiveKeywords(List<String> positiveKeywords) {
        this.positiveKeywords = positiveKeywords;
    }

    public List<String> getNegativeKeywords() {
        return negativeKeywords;
    }

    public void setNegativeKeywords(List<String> negativeKeywords) {
        this.negativeKeywords = negativeKeywords;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }
}
