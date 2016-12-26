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

    public static Pattern PATTERN_ALL_WITH_POSTID =  Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(-)(\\d*)($)");
    public static Pattern patternAll = Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(\\D)($)");
    public static Pattern patternJobRoleCompany = Pattern.compile("(.*)(-jobs-at-)(.*)");
    public static Pattern patternJobRoleLocation = Pattern.compile("(.*)(-jobs-in-)(.*)(-)(\\d*)($)");
    public static Pattern patternCompany = Pattern.compile("(^)(jobs-at-)(.*)");
    public static Pattern patternLocationCompany = Pattern.compile("(^)(jobs-in-)(.*)(-at-)(.*)");
    public static Pattern patternLocation = Pattern.compile("(^)(jobs-in-)(.*)");
    public static Pattern patternJobRole = Pattern.compile("(.*)(-jobs)(-)(\\d*)($)");
    public static Pattern patternJobDetails = Pattern.compile("(.*)(-jobs)(-in-)(.*)(-at-)(.*)(-)(\\d*)($)");
    public static Pattern patternJobRoleWiseJobPosts = Pattern.compile("(.*)(-jobs-)(\\d*)($)");


    public UrlParameters parseURL(String urlString){
        UrlParameters urlParameters = new UrlParameters();

        //Pattern Check for JobRole-jobs-in-location-at-company-JobPostID

        Matcher mAllWithPostId = PATTERN_ALL_WITH_POSTID.matcher(urlString);
        Matcher mAll = patternAll.matcher(urlString);
        Matcher mJobRoleCompany = patternJobRoleCompany.matcher(urlString);
        Matcher mJobRoleLocation = patternJobRoleLocation.matcher(urlString);
        Matcher mLocationCompany = patternLocationCompany.matcher(urlString);
        Matcher mCompany = patternCompany.matcher(urlString);
        Matcher mJobRole = patternJobRole.matcher(urlString);
        Matcher mLocation = patternLocation.matcher(urlString);


        if (mAllWithPostId.find()) {
            try{
                urlParameters.setUrlType(UrlParameters.TYPE.A);
                urlParameters.setJobLocation(mAllWithPostId.group(5));
                String[] splitJobCompany = mAllWithPostId.group(7).split("-");//to get first element of the array after split which is Company Name
                urlParameters.setJobCompany(splitJobCompany[0]);
                urlParameters.setJobPostTitle(splitJobCompany[0]);
                urlParameters.setJobCompany(mAllWithPostId.group(1));
                urlParameters.setJobPostId(Long.valueOf(mAllWithPostId.group(9)));
            }
            catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);
            }
        } else if (mAll.find()) {

            //Pattern Check for JobRole-jobs-in-location-at-company
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);

        } else if (mJobRoleCompany.find()) {

            //Pattern Check for JobRole-jobs-at-Company
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);

        } else if (mJobRoleLocation.find()) {
            try{
                //Pattern Check for JobRole-jobs-in-Location
                urlParameters.setUrlType(UrlParameters.TYPE.H);
                urlParameters.setJobRoleName(mJobRoleLocation.group(1));
                urlParameters.setJobRoleId(Long.valueOf(mJobRoleLocation.group(5)));
            }catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);
            }


        } else if (mLocationCompany.find()) {

            //Pattern Check for All-jobs-in-Location-at-company;
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);

        } else if (mCompany.find()) {

            //Pattern Check for All-jobs-at-Company ;
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);

        } else if(mLocation.find()) {

            //Pattern Check for All-jobs-in-Location
            // We are not handling this url pattern for now
            urlParameters.setUrlType(UrlParameters.TYPE.G);

        } else if(mJobRole.find()) {
            try{
                //Pattern Check for All-jobs-JobRoleID
                Logger.info("Value "+mJobRole.group(4));
                urlParameters.setUrlType(UrlParameters.TYPE.H);
                urlParameters.setJobRoleId(Long.valueOf(mJobRole.group(4)));
                urlParameters.setJobRoleName(mJobRole.group(1));
            } catch(NumberFormatException ex){
                Logger.info("Invalid Url !! " + ex);
                urlParameters.setUrlType(UrlParameters.TYPE.InValidRequest);
            }
        }

        return urlParameters;
    }

    public UrlParameters jobCommonDetailsRequest(String jobString) {
        Matcher mJobDetails = patternJobDetails.matcher(jobString);
        Matcher mJobRoleWiseJobPosts = patternJobRoleWiseJobPosts.matcher(jobString);

        UrlParameters urlParameters = new UrlParameters();

        if(mJobDetails.find()) {
            urlParameters.setUrlType(UrlParameters.TYPE.A);
            urlParameters.setJobPostId(Long.valueOf(mJobDetails.group(8)));
        }
        else if(mJobRoleWiseJobPosts.find()) {
            urlParameters.setUrlType(UrlParameters.TYPE.B);
            urlParameters.setJobRoleId(Long.valueOf(mJobRoleWiseJobPosts.group(3)));
        }
        return urlParameters;
    }
}
