package api.http.httpResponse.search.helper;

import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.Language;

/**
 * Created by zero on 24/12/16.
 */
public class FilterParamsResponse {
    public int gender;
    public Language language;
    public Education education;
    public Experience experience;

    public FilterParamsResponse(int gender, Language language, Education education, Experience experience) {
        this.gender = gender;
        this.language = language;
        this.education = education;
        this.experience = experience;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
