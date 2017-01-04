package api.http.httpResponse.search.helper;

import api.ServerConstants;
import models.entity.Static.Language;

import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class FilterParamsResponse {
    public int gender;
    public long salary;
    public List<Language> languageList;

    public FilterParamsResponse() {
        this.gender = ServerConstants.GENDER_ANY;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }
}
