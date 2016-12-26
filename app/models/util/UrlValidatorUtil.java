package models.util;

import api.http.httpRequest.UrlParameters;
import controllers.businessLogic.JobSearchService;
import models.entity.JobPost;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import play.Logger;
import play.mvc.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Created by hawk on 23/12/16.
 */
public class UrlValidatorUtil {

    public static Pattern PATTERN_ALL_WITH_POST_ID =  Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(-)(\\d*)($)");
    public static Pattern PATTERN_ALL = Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(\\D)($)");
    public static Pattern PATTERN_JOB_ROLE_COMPANY = Pattern.compile("(.*)(-jobs-at-)(.*)");
    public static Pattern PATTERN_JOB_ROLE_LOCATION = Pattern.compile("(.*)(-jobs-in-)(.*)(-)(\\d*)($)");
    public static Pattern PATTERN_COMPANY= Pattern.compile("(^)(jobs-at-)(.*)");
    public static Pattern PATTERN_LOCATION_COMPANY= Pattern.compile("(^)(jobs-in-)(.*)(-at-)(.*)");
    public static Pattern PATTERN_LOCATION= Pattern.compile("(^)(jobs-in-bangalore)($)");
    public static Pattern PATTERN_JOB_ROLE= Pattern.compile("(.*)(-jobs)(-)(\\d*)($)");
    public static Pattern PATTERN_JOB_DETAILS= Pattern.compile("(.*)(-jobs)(-in-)(.*)(-at-)(.*)(-)(\\d*)($)");
    public static Pattern PATTERN_JOB_ROLE_WISE_JOB_POST= Pattern.compile("(.*)(-jobs-)(\\d*)($)");


    public UrlParameters parseURL(String urlString){
        UrlParameters urlParameters = new UrlParameters();

        //Pattern Check for JobRole-jobs-in-location-at-company-JobPostID

        Matcher mAllWithPostId = PATTERN_ALL_WITH_POST_ID.matcher(urlString);
        Matcher mAll = PATTERN_ALL.matcher(urlString);
        Matcher mJobRoleCompany = PATTERN_JOB_ROLE_COMPANY.matcher(urlString);
        Matcher mJobRoleLocation = PATTERN_JOB_ROLE_LOCATION.matcher(urlString);
        Matcher mLocationCompany = PATTERN_LOCATION_COMPANY.matcher(urlString);
        Matcher mCompany = PATTERN_COMPANY.matcher(urlString);
        Matcher mJobRole = PATTERN_JOB_ROLE.matcher(urlString);
        Matcher mLocation = PATTERN_LOCATION.matcher(urlString);


        if (mAllWithPostId.find()) {
            //Pattern Check for JobRole-jobs-in-location-at-company-jobPostId
            try{
                urlParameters.setUrlType(UrlParameters.TYPE.jobsRoleInAtWithJobPostId);
                urlParameters.setJobLocation(mAllWithPostId.group(5));
                String[] splitJobCompany = mAllWithPostId.group(7).split("-");//to get first element of the array after split which is Company Name
                urlParameters.setJobCompany(splitJobCompany[0]);
                urlParameters.setJobPostTitle(splitJobCompany[0]);
                urlParameters.setJobCompany(mAllWithPostId.group(1));
                urlParameters.setJobPostId(Long.valueOf(mAllWithPostId.group(9)));
            }
            catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);
            }
        } else if (mAll.find()) {

            //Pattern Check for JobRole-jobs-in-location-at-company
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);

        } else if (mJobRoleCompany.find()) {

            //Pattern Check for JobRole-jobs-at-Company
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);

        } else if (mJobRoleLocation.find()) {
            try{
                //Pattern Check for JobRole-jobs-in-Location
                urlParameters.setUrlType(UrlParameters.TYPE.jobRoleIn);
                urlParameters.setJobRoleName(mJobRoleLocation.group(1));
                urlParameters.setJobRoleId(Long.valueOf(mJobRoleLocation.group(5)));
            }catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);
            }


        } else if (mLocationCompany.find()) {

            //Pattern Check for All-jobs-in-Location-at-company;
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);

        } else if (mCompany.find()) {

            //Pattern Check for All-jobs-at-Company ;
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);

        } else if(mLocation.find()) {

            //Pattern Check for All-jobs-in-Location
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.allJobsIn);

        } else if(mJobRole.find()) {
            try{
                //Pattern Check for All-jobs-JobRoleID
                urlParameters.setUrlType(UrlParameters.TYPE.allJobsWithJobRoleId);
                urlParameters.setJobRoleId(Long.valueOf(mJobRole.group(4)));
                urlParameters.setJobRoleName(mJobRole.group(1));
            } catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InvalidRequest);
            }
        }
        return urlParameters;
    }

    public UrlParameters parseJobsContentPageUrl(String jobString) {
        Matcher mJobDetails = PATTERN_JOB_DETAILS.matcher(jobString);
        Matcher mJobRoleWiseJobPosts = PATTERN_JOB_ROLE_WISE_JOB_POST.matcher(jobString);

        UrlParameters urlParameters = new UrlParameters();

        if(mJobDetails.find()) {
            urlParameters.setUrlType(UrlParameters.TYPE.getJobDetailsWithJobPostId);
            urlParameters.setJobPostId(Long.valueOf(mJobDetails.group(8)));
        }
        else if(mJobRoleWiseJobPosts.find()) {
            urlParameters.setUrlType(UrlParameters.TYPE.getJobPostWithJobRoleId);
            urlParameters.setJobRoleId(Long.valueOf(mJobRoleWiseJobPosts.group(3)));
        }
        return urlParameters;
    }
}
