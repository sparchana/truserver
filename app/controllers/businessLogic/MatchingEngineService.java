package controllers.businessLogic;

/**
 * Created by zero on 29/7/16.
 */

import api.ServerConstants;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import play.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static Double radius = ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS;

    /**
     * fetchMatchingJobPostForLatLng takes candidate's home locality latitude/longitude and list of preferred job's
     * jobRole Id and generates a List<JobPost> lying within DEFAULT_MATCHING_ENGINE_RADIUS and JobPostToLocalityList
     * are ordered by distance of each JobPostToLocality from candidate's home locality.
     */
    public static List<JobPost> fetchMatchingJobPostForLatLng(Double lat, Double lng, Double r, List<Long> jobRoleIds){
        if(r!=null && r>0){
            radius = r;
        }
        List<JobPost> jobPostList = JobPost.find.where()
                .eq("jobPostIsHot", ServerConstants.IS_HOT)
                .in("jobRole.jobRoleId", jobRoleIds)
                .findList();
        if(lat != null && lng != null) {
           List<JobPost> jobPostsResponseList = new ArrayList<>();
           for (JobPost jobPost : jobPostList){
               boolean shouldAdd = false;
               List<JobPostToLocality> jobPostToLocalityList = new ArrayList<>();
               /* TODO: entity manager should ignore it for other transactions */
               JobPost tempJobPost = new JobPost(jobPost);
               if(jobPost.getJobPostToLocalityList() != null){
                  for(JobPostToLocality jobPostToLocality : jobPost.getJobPostToLocalityList()){
                      /* finds distance of this jobPost location from candidate's home location */
                      Double distance = getDistanceFromCenter(lat, lng,
                              jobPostToLocality.getLatitude(), jobPostToLocality.getLongitude());
                      if(distance!= null && distance <= radius){
                          shouldAdd = true;
                          /* creates distance wise ordered list for
                          *  this jobPost
                          */
                          jobPostToLocality.setDistance(distance);
                          jobPostToLocalityList.add(jobPostToLocality);
                      }
                  }
                   Collections.sort(jobPostToLocalityList, (a,b)->a.getDistance().compareTo(b.getDistance()));
                   /* add ordered jobPostToLocalityList to temp jobPost */
                   tempJobPost.setJobPostToLocalityList(jobPostToLocalityList);
              }
               /* add jobPost to response list if it satisfies the match criteria */
               if(shouldAdd){
                   jobPostsResponseList.add(new JobPost(tempJobPost));
               }
           }
            Collections.sort(jobPostsResponseList, (a,b) -> a.getJobPostToLocalityList().get(0).getDistance()
                    .compareTo(b.getJobPostToLocalityList().get(0).getDistance()));
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
    public static Double getDistanceFromCenter(Double centerLat, Double centerLng, Double pointLat, Double pointLng){
        if(centerLat == null || centerLng == null || pointLat == null || pointLng == null){
            return null;
        }
        double earthRadius = 6371.0 ; // kilometers (or 3958.75 in miles)
        double dLat = Math.toRadians(pointLat-centerLat);
        double dLng = Math.toRadians(pointLng-centerLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(pointLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * earthRadius;
    }
}
