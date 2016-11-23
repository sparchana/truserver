package api.http.httpResponse;

/**
 * Created by zero on 23/11/16.
 */
public class CandidateScoreData {
        public double score; // raw score (in range of 0-1)
        public int band; // the bracket in which an item falls under
        public String reason;

        public CandidateScoreData(double score, int band, String reason) {
            this.score = score;
            this.band = band;
            this.reason = reason;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getBand() {
            return band;
        }

        public void setRank(int band) {
            this.band = band;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
}
