package api.http.httpRequest;

import api.http.FormValidator;

import java.util.*;

/**
 * Created by zero on 23/5/16.
 */
public class SearchCandidateRequest {
    public String candidateFirstName;
    public String candidateMobile; //',' separated Mobile No values
    public List<Integer> candidateJobInterest;
    public List<Integer> candidateLocality;
    public Date fromThisDate;
    public Date toThisDate;
    public List<Integer> languageKnown;
    public List<Integer> idProofs;

    public Date getFromThisDate() {
        return fromThisDate;
    }

    public void setFromThisDate(Date fromThisDate) {
        this.fromThisDate = fromThisDate;
    }

    public Date getToThisDate() {

        if (toThisDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(toThisDate);
            cal.add(Calendar.DATE, 1);
            return cal.getTime();
        }

        return null;
    }

    public void setToThisDate(Date toThisDate) {
        this.toThisDate = toThisDate;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    public List<String> getCandidateMobile() {
        if(candidateMobile.isEmpty() || candidateMobile == null){
            return null;
        }
        List<String> mobileList = Arrays.asList(candidateMobile.split("\\s*,\\s*"));
        List<String> with91 = new ArrayList<>();
        for(String tempMobile : mobileList){
            tempMobile.replaceAll("\\s+", "").trim();
            tempMobile = FormValidator.convertToIndianMobileFormat(tempMobile);
            with91.add(tempMobile);
        }
        return with91;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public void setCandidateJobInterest(List<Integer> candidateJobInterest) {
        this.candidateJobInterest = candidateJobInterest;
    }

    public void setCandidateLocality(List<Integer> candidateLocality) {
        this.candidateLocality = candidateLocality;
    }

    public List<Integer> getLanguageKnownList() {
        return languageKnown;
    }

    public void setLanguageKnown(List<Integer> languageKnown) {
        this.languageKnown = languageKnown;
    }

    public List<Integer> getIdProofs() {
        return idProofs;
    }

    public void setIdProofs(List<Integer> idProofs) {
        this.idProofs = idProofs;
    }
}
