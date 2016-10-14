package api.http.httpResponse;

import models.entity.OM.JobPreference;

import java.util.List;

/**
 * Created by zero on 23/9/16.
 */
public class CandidateJobPrefs {
    public static class JobPrefWithAssessmentBundle {
        JobPreference jobPreference;
        boolean isAssessed;

        public JobPreference getJobPreference() {
            return jobPreference;
        }

        public void setJobPreference(JobPreference jobPreference) {
            this.jobPreference = jobPreference;
        }

        public boolean isAssessed() {
            return isAssessed;
        }

        public void setAssessed(boolean isAssessed) {
            this.isAssessed = isAssessed;
        }
    }
    List<JobPrefWithAssessmentBundle> jobPrefWithAssessmentBundles;
}
