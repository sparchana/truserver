package api.http.httpResponse.Recruiter.recruiterAdmin;

/**
 * Created by zero on 27/1/17.
 *
 * bundle gives x, y and x/y %
 */
public class PercentageBundle {
        private int total;
        private int selected;
        private float percentage;

        public PercentageBundle(int selected, int total, Float percentage) {

            this.total = total;
            this.selected = selected;
            this.percentage = percentage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        public float getPercentage() {
            return percentage;
        }

        public void setPercentage(Float percentage) {
            this.percentage = percentage;
        }
}
