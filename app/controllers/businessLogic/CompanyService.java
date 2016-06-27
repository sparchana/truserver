package controllers.businessLogic;

import api.http.httpRequest.AddCompanyRequest;
import models.entity.Company;
import models.entity.Static.CompanyStatus;
import models.entity.Static.CompanyType;
import models.entity.Static.Locality;
import play.Logger;

/**
 * Created by batcoder1 on 22/6/16.
 */
public class CompanyService {
    public static Integer addCompany(AddCompanyRequest addCompanyRequest) {
        Company newCompany = new Company();

        newCompany.setCompanyName(addCompanyRequest.getCompanyName());
        newCompany.setCompanyEmployeeCount(addCompanyRequest.getCompanyEmployeeCount());
        newCompany.setCompanyWebsite(addCompanyRequest.getCompanyWebsite());
        newCompany.setCompanyDescription(addCompanyRequest.getCompanyDescription());
        newCompany.setCompanyAddress(addCompanyRequest.getCompanyAddress());
        newCompany.setCompanyPinCode(addCompanyRequest.getCompanyPinCode());
        newCompany.setCompanyLogo(addCompanyRequest.getCompanyLogo());

        CompanyStatus companyStatus = CompanyStatus.find.where().eq("companyStatusId", addCompanyRequest.getCompanyStatus()).findUnique();
        newCompany.setCompStatus(companyStatus);

        CompanyType companyType = CompanyType.find.where().eq("companyTypeId", addCompanyRequest.getCompanyType()).findUnique();
        newCompany.setCompType(companyType);

        Locality locality = Locality.find.where().eq("localityId", addCompanyRequest.getCompanyLocality()).findUnique();
        newCompany.setCompanyLocality(locality);
        newCompany.save();
        Logger.info("Company: " + newCompany.getCompanyName() + " successfully created");
        return 0;
    }
}
