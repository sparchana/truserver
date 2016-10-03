package controllers.AnalyticsLogic;

import models.entity.Intelligence.RelatedJobRole;
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
        List<RelatedJobRole> relatedJobRoles = RelatedJobRole.find.where().in("job_role_id", jobRoleIds).findList();

        List<Long> relatedJobRoleIds = new ArrayList<>();

        for (RelatedJobRole relatedJobRole : relatedJobRoles) {
            relatedJobRoleIds.add(relatedJobRole.getRelatedJobRole().getJobRoleId());
        }

        return relatedJobRoleIds;
    }

    public static List<Long> updateRelevantJobCategories(List<Long> jobRoleIds) {
        Map<Long, TreeMap<Long, Double>> jobRoleToRelatedJobRolesByCount = computeRelatedJobRoleIds(jobRoleIds);

        // fetch all related job role ids

        List<Long> relatedJobRoleIds = new ArrayList<>();

        List<Long> relatedJobRoleTableId = new ArrayList<>();

        for (Map.Entry<Long, TreeMap<Long, Double>> parentEntry: jobRoleToRelatedJobRolesByCount.entrySet()) {
            for (Map.Entry<Long, Double> childEntry : parentEntry.getValue().entrySet()) {
                relatedJobRoleIds.add(childEntry.getKey());

                // make entry in db

                RelatedJobRole relatedJobRole = RelatedJobRole.find
                        .where()
                        .eq("job_role_id", parentEntry.getKey())
                        .eq("related_job_role_id", childEntry.getKey())
                        .findUnique();

                if (relatedJobRole == null) {
                    relatedJobRole = new RelatedJobRole();
                }
                relatedJobRole.setJobRole(JobRole.find.where().eq("jobRoleId", parentEntry.getKey()).findUnique());
                relatedJobRole.setRelatedJobRole(JobRole.find.where().eq("jobRoleId", childEntry.getKey()).findUnique());
                relatedJobRole.setWeight(childEntry.getValue());

                relatedJobRole.save();

                relatedJobRoleTableId.add(relatedJobRole.getId());
            }
        }

        // remove old orphan rows
        List<RelatedJobRole> relatedJobRoleList = RelatedJobRole.find.all();

        for ( RelatedJobRole relatedJobRole: relatedJobRoleList) {
            if (! relatedJobRoleTableId.contains(relatedJobRole.getId())) relatedJobRole.delete();
        }

        return relatedJobRoleIds;
    }

    public static List<Long> updateAllRelevantJobCategories() {

        List<Long> allJobRoleids = new ArrayList<Long>();

        for (JobRole jobRole : JobRole.find.select("jobRoleId").findList()) {
            allJobRoleids.add(jobRole.getJobRoleId());
        }

        return updateRelevantJobCategories(allJobRoleids);
    }

    public static Map<Long, TreeMap<Long, Double>> computeRelatedJobRoleIds(List<Long> jobRoleIds) {

        List<JobPreference> allJobPrefs = JobPreference.find.all();

        Long candidateId;
        Map<Long, TreeMap<Long, Double>> jobRoleToRelatedJobRolesByCount = new HashMap<Long, TreeMap<Long, Double>>();
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
            TreeMap<Long, Double> relatedJobrolesToCount = jobRoleToRelatedJobRolesByCount.get(jobRoleId);

            // create a map if it doesnt exist
            if (relatedJobrolesToCount == null) {
                relatedJobrolesToCount = new TreeMap<Long, Double>();
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
                    Double count =  relatedJobrolesToCount.get(jobPrefRoleId);

                    if (count == null) {
                        count = 1D;
                    } else {
                        count++;
                    }

                    relatedJobrolesToCount.put(jobPrefRoleId, count);
                }
            }

            TreeMap<Long, Double> sortedMap = sortMapByValue(relatedJobrolesToCount);
            jobRoleToRelatedJobRolesByCount.put(jobRoleId, sortedMap);
        }

        return jobRoleToRelatedJobRolesByCount;
    }

   public static TreeMap<Long, Double> sortMapByValue(TreeMap<Long, Double> map)
    {
        int total = 0;
        for (Double value : map.values()) {
            total += value;
        }

        Comparator<Long> comparator = new ValueComparator(map);

        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by values.
        TreeMap<Long, Double> result = new TreeMap<Long, Double>(comparator);
        result.putAll(map);

        int cumCount = 0;
        TreeMap<Long, Double> truncatedMap = new TreeMap<Long, Double>(comparator);

        for (Map.Entry<Long, Double> entry : result.entrySet()) {
            cumCount += entry.getValue();

            if (cumCount < (0.7)*total) {
                truncatedMap.put(entry.getKey(), entry.getValue()/total);
            }
        }

        return truncatedMap;
    }

    private static class ValueComparator implements Comparator<Long>{

        TreeMap<Long, Double> map = new TreeMap<Long, Double>();
        int size = 0;

        public ValueComparator(TreeMap<Long, Double> map){
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
