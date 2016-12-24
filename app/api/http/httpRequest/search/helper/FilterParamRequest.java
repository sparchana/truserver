package api.http.httpRequest.search.helper;

/**
 * Created by zero on 24/12/16.
 */
public class FilterParamRequest {
    int gender;
    long languageId;

    public FilterParamRequest() {
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }
}
