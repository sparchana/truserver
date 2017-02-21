package api.http.httpResponse.Recruiter.RMP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 13/2/17.
 */
public class PreviousRoundResponse {

    public static class PreviousRound{
        private String recruiterName;
        private String note;
        private String creationDate;

        public String getRecruiterName() {
            return recruiterName;
        }

        public void setRecruiterName(String recruiterName) {
            this.recruiterName = recruiterName;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }


        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }
    }

    private List<PreviousRound> previousRoundList;

    public PreviousRoundResponse() {
        this.previousRoundList = new ArrayList<>();
    }

    public List<PreviousRound> getPreviousRoundList() {
        return previousRoundList;
    }

    public void setPreviousRoundList(List<PreviousRound> previousRoundList) {
        this.previousRoundList = previousRoundList;
    }
}
