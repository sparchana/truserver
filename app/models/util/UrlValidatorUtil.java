package models.util;

import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import play.mvc.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static play.mvc.Results.ok;

/**
 * Created by hawk on 23/12/16.
 */
public class UrlValidatorUtil {
    public String jobPostTitle;
    public String jobCompany;
    public String jobLocation;
    public Long jobPostId;
    public String jobRoleName;
    public Long jobRoleId;

    public Result URLValidator(String urlString){

        //Pattern Check for JobRole-jobs-in-location-at-company-JobPostID

        Pattern patternAllWithPostId = Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(-)(\\d*)");
        Matcher mAllWithPostId = patternAllWithPostId.matcher(urlString);
        if(mAllWithPostId.find()){
            jobLocation = mAllWithPostId.group(5);
            String[] splitJobCompany = mAllWithPostId.group(7).split("-");//to get first element of the array after split which is Company Name
            jobCompany = splitJobCompany[0];
            jobPostTitle = mAllWithPostId.group(1);
            jobPostId = Long.valueOf(mAllWithPostId.group(9));
            return ok(views.html.Fragment.posted_job_details.render(jobLocation,jobCompany,jobPostTitle,jobPostId));
        }

        //Pattern Check for JobRole-jobs-in-locaiton-at-company

        Pattern patternAll = Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-at-)(.*)(\\D)");
        Matcher mAll = patternAll.matcher(urlString);
        if(mAll.find()){
            return ok("Job Post in Location at Company");
        }

        //Pattern Check for JobRole-jobs-at-Company

        Pattern patternJobRoleCompany = Pattern.compile("(.*)(-jobs)(.*)(-at-)(.*)");
        Matcher mJobRoleCompany = patternJobRoleCompany.matcher(urlString);
        if(mJobRoleCompany.find()){
            return ok("Job Post at Company");
        }

        //Pattern Check for JobRole-jobs-in-Location

        Pattern patternJobRoleLocation = Pattern.compile("(.*)(-jobs)(.*)(-in-)(.*)(-)(\\d*)");
        Matcher mJobRoleLocation = patternJobRoleLocation.matcher(urlString);
        if(mJobRoleLocation.find()){
            jobRoleName = mJobRoleLocation.group(1);
            jobRoleId = Long.valueOf(mJobRoleLocation.group(7));
            return ok(views.html.Fragment.job_role_page.render(jobRoleName,jobRoleId));
        }

        //Pattern Check for All-jobs-in-Location-at-company

        Pattern patternLocationCompany = Pattern.compile("(^)(jobs)(.*)(-in-)(.*)(-at-)(.*)");
        Matcher mLocationCompany = patternLocationCompany.matcher(urlString);
        if(mLocationCompany.find()){
            return ok("All Jobs in Location at Company");
        }

        //Pattern Check for All-jobs-at-Company

        Pattern patternCompany = Pattern.compile("(^)(jobs-at-)(.*)");
        Matcher mCompany = patternCompany.matcher(urlString);
        if(mCompany.find()){
            return ok("All Jobs at Company");
        }

        //Pattern Check for All-jobs-in-Location

        Pattern patternLocation = Pattern.compile("(^)(jobs-in-)(.*)");
        Matcher mLocation = patternLocation.matcher(urlString);
        if(mLocation.find()){
            return ok(views.html.Fragment.show_all_jobs_page.render());
        }

        //Pattern Check for All-jobs-JobRoleID

        Pattern patternJobRole = Pattern.compile("(.*)(-jobs-)(\\d*)");
        Matcher mJobRole = patternJobRole.matcher(urlString);
        if(mJobRole.find()){
            jobRoleName = mJobRole.group(1);
            jobRoleId = Long.valueOf(mJobRole.group(3));
            return ok(views.html.Fragment.job_role_page.render(jobRoleName,jobRoleId));
        }
        return ok("Error");
    }


}
