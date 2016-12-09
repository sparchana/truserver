package dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Company;

import java.util.List;

/**
 * Created by dodo on 8/12/16.
 */
public class CompanyDAO {
    public static List<Company> getHiringCompanyLogos() {
        List<Company> hiringCompanyLogo;
        String companySql = "SELECT companylogo from company c where (select count(*) from jobpost where companyid = " +
                "jobpost.companyid and jobstatus = '2') > 0 and c.companylogo != 'https://s3.amazonaws.com/trujobs.in/companyLogos/default_company_logo.png'";

        RawSql rawSql = RawSqlBuilder.parse(companySql)
                .tableAliasMapping("c", "company")
                .columnMapping("companylogo", "companyLogo")
                .create();

        hiringCompanyLogo = Ebean.find(Company.class)
                .setRawSql(rawSql)
                .findList();

        return hiringCompanyLogo;
    }
}
