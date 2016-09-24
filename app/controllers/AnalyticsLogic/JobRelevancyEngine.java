package controllers.AnalyticsLogic;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import models.entity.OM.JobPreference;
import models.entity.Static.JobRole;
import play.Logger;

import static play.libs.Json.toJson;

import java.util.*;

/**
 * Created by zero on 12/7/16.
 */
public class JobRelevancyEngine {

    public static List<Long> getRelatedJobRoleIds(List<Long> jobRoleIds) {

        Map<Long, TreeMap<Long, Integer>> jobRoleToRelatedJobRolesByCount = updateRelevantJobCategories(jobRoleIds);

        // fetch all related job role ids

        List<Long> relatedJobRoleIds = new ArrayList<>();

        for (Map.Entry<Long, TreeMap<Long, Integer>> parentEntry: jobRoleToRelatedJobRolesByCount.entrySet()) {
            for (Map.Entry<Long, Integer> childEntry : parentEntry.getValue().entrySet()) {
                relatedJobRoleIds.add(childEntry.getKey());
            }
        }

        return relatedJobRoleIds;
    }

    public static Map<Long, TreeMap<Long, Integer>> updateAllRelevantJobCategories() {

        List<Long> allJobRoleids = new ArrayList<Long>();

        for (JobRole jobRole : JobRole.find.select("jobRoleId").findList()) {
            allJobRoleids.add(jobRole.getJobRoleId());
        }

        return updateRelevantJobCategories(allJobRoleids);
    }

    public static Map<Long, TreeMap<Long, Integer>> updateRelevantJobCategories(List<Long> jobRoleIds) {

        List<JobPreference> allJobPrefs = JobPreference.find.all();

        Long candidateId;
        Map<Long, TreeMap<Long, Integer>> jobRoleToRelatedJobRolesByCount = new HashMap<Long, TreeMap<Long, Integer>>();
        Map<Long, List<Long>> allCandidateToJobPrefs = new HashMap<Long, List<Long>>();

        for (JobPreference jobPref : allJobPrefs) {

            candidateId = jobPref.getCandidate().getCandidateId();

            List jobPrefsList = allCandidateToJobPrefs.get(candidateId);

            if (jobPrefsList == null) {
                jobPrefsList = new ArrayList<Long>();
                allCandidateToJobPrefs.put(candidateId, jobPrefsList);
            }

            jobPrefsList.add(jobPref.getJobRole().getJobRoleId());
        }

        int c = 0;

        // iterate on all job roles available in static table
        for (Long jobRoleId : jobRoleIds) {


            // check if we have a map for this job role already that counts related job role entries
            TreeMap<Long, Integer> relatedJobrolesToCount = jobRoleToRelatedJobRolesByCount.get(jobRoleId);

            // create a map if it doesnt exist
            if (relatedJobrolesToCount == null) {
                relatedJobrolesToCount = new TreeMap<Long, Integer>();
                jobRoleToRelatedJobRolesByCount.put(jobRoleId, relatedJobrolesToCount);
            }

            // Get the iterator on the job role preferences per candidate fetched from database
            Iterator<Map.Entry<Long, List<Long>>> itr = allCandidateToJobPrefs.entrySet().iterator();

            // iterate on entries fetched from database
            while (itr.hasNext()) {

                Map.Entry<Long, List<Long>> candidateToJobRoleList = itr.next();

                ArrayList<Long> jobRolePrefIds = (ArrayList<Long>) candidateToJobRoleList.getValue();

                if (! jobRolePrefIds.contains(jobRoleId)) {
                    continue;
                }

                // iterate on a group of preferences
                // (a group of jobpref corresponds to all job prefs given by a  single candidate)
                for (Long jobPrefRoleId : jobRolePrefIds) {

                    // we are forming a map from each job role to its associated job roles
                    // so lets ignore mapping a job role onto itself
                    if (jobPrefRoleId == jobRoleId) {
                        continue;
                    }

                    // see if we already have a count against this job role as a related job role
                    // increment the count if so
                    Integer count = relatedJobrolesToCount.get(jobPrefRoleId);

                    if (count == null) {
                        count = 1;
                    } else {
                        count++;
                    }

                    relatedJobrolesToCount.put(jobPrefRoleId, count);
                }
            }

            TreeMap<Long, Integer> sortedMap = sortMapByValue(relatedJobrolesToCount);
            jobRoleToRelatedJobRolesByCount.put(jobRoleId, sortedMap);
        }

        return jobRoleToRelatedJobRolesByCount;
    }

   public static TreeMap<Long, Integer> sortMapByValue(TreeMap<Long, Integer> map)
    {
        int total = 0;
        for (Integer value : map.values()) {
            total += value;
        }

        Comparator<Long> comparator = new ValueComparator(map);

        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by values.
        TreeMap<Long, Integer> result = new TreeMap<Long, Integer>(comparator);
        result.putAll(map);

        int cumCount = 0;
        TreeMap<Long, Integer> truncatedMap = new TreeMap<Long, Integer>(comparator);

        for (Map.Entry<Long, Integer> entry : result.entrySet()) {
            cumCount += entry.getValue();

            if (cumCount < (0.7)*total) {
                truncatedMap.put(entry.getKey(), entry.getValue());
            }
        }

        return truncatedMap;
    }

    private static class ValueComparator implements Comparator<Long>{

        TreeMap<Long, Integer> map = new TreeMap<Long, Integer>();
        int size = 0;

        public ValueComparator(TreeMap<Long, Integer> map){
            this.map.putAll(map);
        }

        @Override
        public int compare(Long i1, Long i2) {
            if(map.get(i1) >= map.get(i2)){
                return -1;
            } else{
                return 1;
            }
        }
    }
}
