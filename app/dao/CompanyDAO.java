package dao;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Company;
import models.entity.JobPost;

import java.util.*;

/**
 * Created by dodo on 8/12/16.
 */
public class CompanyDAO {
    public static List<Company> getHiringCompanyLogos() {
        List<JobPost> activeJobPostList = JobPost.find.where()
                .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                .eq("Source", ServerConstants.SOURCE_INTERNAL)
                .orderBy().asc("source")
                .orderBy().desc("jobPostIsHot")
                .orderBy().desc("jobPostUpdateTimestamp")
                .findList();

        Map<Company, Integer> companyMap = new HashMap<Company, Integer>();

        for(JobPost jobPost : activeJobPostList){

            if(jobPost.getCompany() != null){
                Company company = jobPost.getCompany();

                Integer count = companyMap.get(company);
                companyMap.put(company, (count == null) ? 1 : count + 1);
            }
        }

        List<Company> hiringCompanyLogo = new ArrayList<>();
        for (Map.Entry<Company, Integer> entry : companyMap.entrySet()) {

            //getting key and value
            Company company = entry.getKey();
            if(!Objects.equals(company.getCompanyLogo(), ServerConstants.DEFAULT_COMPANY_LOGO)){
                hiringCompanyLogo.add(company);
            }
        }

        return hiringCompanyLogo;
    }

    public static List<Company> getCompaniesWithoutCompanyCode() {
        return Company.find.where()
                .isNull("CompanyCode")
                .findList();
    }

    public static Company getCompaniesByCompanyCode(int code) {
        return Company.find.where()
                .eq("CompanyCode", code)
                .findUnique();
    }

}
