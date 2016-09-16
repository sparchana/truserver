package controllers.businessLogic;

/**
 * Created by zero on 29/7/16.
 */

import api.ServerConstants;
import com.avaje.ebean.Query;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import play.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static api.ServerConstants.*;
import static com.avaje.ebean.Expr.eq;

/**
 * Matching Engine Service receives a {latitude, longitude} pair along with jobRoleId list
 * and try to determine list of jobPost available within a defined radius rad.
 * List is ordered by its distance from center.
 * Distance between two co-ordinates in a spherical surface is calculate using Haversine formula.
 * Haversine formula is used in getDistanceFromCenter method to get distance from
 * given center co-ordinates {in KiloMeter}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
 */
public class MatchingEngineService {

    private static Double radius = DEFAULT_MATCHING_ENGINE_RADIUS;
    private static int SORT_DEFAULT = ServerConstants.SORT_DEFAULT;

    /**
     * fetchMatchingJobPostForLatLng takes candidate's home locality latitude/longitude and list of preferred job's
     * jobRole Id and generates a List<JobPost> lying within DEFAULT_MATCHING_ENGINE_RADIUS and JobPostToLocalityList
     * are ordered by distance of each JobPostToLocality from candidate's home locality.
     */
    public static List<JobPost> fetchMatchingJobPostForLatLng(Double lat, Double lng, Double r,
                                                              List<Long> jobRoleIds, Integer sortOrder)
    {
        Logger.info("[Matching Engine] for lat/lng "+lat+"/"+lng);
        if (r != null && r > 0) {
            radius = r;
        }
        if (sortOrder == null) {
            sortOrder = SORT_DEFAULT;
        }
        Query<JobPost> query = JobPost.find.query();

/*
        query = query
                .where()
                .eq("jobPostIsHot", IS_HOT)
                .query();
*/

/*        *//* TODO: until trudroid code is unable to handle diff source jobpost, filter out all the internal jobs *//*
        query = query
                .where()
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                .query();
*/

        Logger.info("JobPostIdList Size: "+ jobRoleIds.size());
        if(jobRoleIds != null && !jobRoleIds.isEmpty() ) {
            Logger.info("Matching JobPosts for: "+ jobRoleIds.toString()+" JobRoleIds");
            query = query.select("*").fetch("jobRole")
                    .where()
                    .in("jobRole.jobRoleId", jobRoleIds)
                    .query();
        }
        List<JobPost> jobPostList = query.findList();
        Logger.info("[ME] init jobpost size:"+jobPostList.size() +" within : " + radius);
        if (lat != null && lng != null) {
            List<JobPost> jobPostsResponseList = new ArrayList<>();
            for (JobPost jobPost : jobPostList) {
                boolean shouldAdd = false;
                List<JobPostToLocality> jobPostToLocalityList = new ArrayList<>();
               /* TODO: entity manager should ignore it for other transactions */
                JobPost tempJobPost = new JobPost(jobPost);
                if (jobPost.getJobPostToLocalityList() != null) {
                    for (JobPostToLocality jobPostToLocality : jobPost.getJobPostToLocalityList()) {
                                /* finds distance of this jobPost location from candidate's home location */
                        Double distance = getDistanceFromCenter(lat, lng,
                                jobPostToLocality.getLatitude(), jobPostToLocality.getLongitude());
                        if (distance != null && distance <= radius) {
                            shouldAdd = true;
                                    /* creates distance wise ordered list for
                                    *  this jobPost
                                    */
                            jobPostToLocality.setDistance(distance);
                            jobPostToLocalityList.add(jobPostToLocality);
                        }
                    }
                    Collections.sort(jobPostToLocalityList, (a, b) -> a.getDistance().compareTo(b.getDistance()));
                            /* add ordered jobPostToLocalityList to temp jobPost */
                    tempJobPost.setJobPostToLocalityList(jobPostToLocalityList);
                }

               /* add jobPost to response list if it satisfies the match/sort criteria */
                if (shouldAdd) {
                    jobPostsResponseList.add(new JobPost(tempJobPost));
                }
            }
            sortJobPostList(jobPostsResponseList, sortOrder, true);
            Logger.info("jobPostResponseList:" + jobPostsResponseList);
            return jobPostsResponseList;
        } else {
            /* Don't handle the exception just throw it to the calling method */
            throw new IllegalArgumentException();
        }
    }

    /**
     * getDistanceFromCenter takes center and point {lat, lng} value & returns distance
     * between the two co-ordinates {in kilometers}
     * for testing run MatchingEngineServiceTest.class
     */
    public static Double getDistanceFromCenter(Double centerLat, Double centerLng, Double pointLat, Double pointLng) {
        if (centerLat == null || centerLng == null || pointLat == null || pointLng == null) {
            return null;
        }
        double earthRadius = EARTH_RADIUS;
        double dLat = Math.toRadians(pointLat - centerLat);
        double dLng = Math.toRadians(pointLng - centerLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(pointLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * earthRadius;
    }

    public static void sortJobPostList(List<JobPost> jobPostsResponseList, Integer sortOrder, boolean doDefaultSort) {
        switch (sortOrder) {
            case SORT_BY_SALARY:
                Logger.info("In mGetAllJobPostsRaw : sorting on Salary");
                Collections.sort(jobPostsResponseList, (a, b) -> b.getJobPostMinSalary()
                        .compareTo(a.getJobPostMinSalary()));
                break;
            case SORT_BY_DATE_POSTED:
                Logger.info("In mGetAllJobPostsRaw : sorting on date posted");
                Collections.sort(jobPostsResponseList, (a, b) -> b.getJobPostCreateTimestamp()
                        .compareTo(a.getJobPostCreateTimestamp()));
                break;
            default:
                if(doDefaultSort) {
                    Logger.info("default sort triggered");
                    Collections.sort(jobPostsResponseList, (a, b) -> a.getJobPostToLocalityList().get(0).getDistance()
                            .compareTo(b.getJobPostToLocalityList().get(0).getDistance()));
                } else {
                    Logger.info("no default sorting if candidate not logged in");
                }
                break;
        }
    }
}
