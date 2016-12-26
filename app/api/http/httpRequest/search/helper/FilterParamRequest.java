package api.http.httpRequest.search.helper;

import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class FilterParamRequest {
    Integer selectedGender;
    List<Long> selectedLanguageIdList;

    public FilterParamRequest() {
    }

    public Integer getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(Integer selectedGender) {
        this.selectedGender = selectedGender;
    }

    public List<Long> getSelectedLanguageIdList() {
        return selectedLanguageIdList;
    }

    public void setSelectedLanguageIdList(List<Long> selectedLanguageIdList) {
        this.selectedLanguageIdList = selectedLanguageIdList;
    }
}
