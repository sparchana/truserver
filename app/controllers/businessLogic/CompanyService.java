package controllers.businessLogic;

import api.http.httpRequest.AddCompanyRequest;
import api.http.httpResponse.AddCompanyResponse;
import models.entity.Company;
import models.entity.Static.CompanyStatus;
import models.entity.Static.CompanyType;
import models.entity.Static.Locality;
import play.Logger;

/**
 * Created by batcoder1 on 22/6/16.
 */
public class CompanyService {
    public static AddCompanyResponse addCompany(AddCompanyRequest addCompanyRequest) {
        AddCompanyResponse addCompanyResponse = new AddCompanyResponse();
        Company existingCompany = Company.find.where().eq("companyId", addCompanyRequest.getCompanyId()).findUnique();
        if(existingCompany == null){
            Company existingCompanyWithName = Company.find.where().eq("companyName", addCompanyRequest.getCompanyName()).findUnique();
            if(existingCompanyWithName == null){
                Logger.info("Company does not exists. Creating a new Company");
                Company newCompany = new Company();
                newCompany  = getAndAddCompanyValues(addCompanyRequest,newCompany);
                newCompany.save();
                addCompanyResponse.setCompanyId(newCompany.getCompanyId());
                addCompanyResponse.setStatus(AddCompanyResponse.STATUS_SUCCESS);
                Logger.info("Company: " + newCompany.getCompanyName() + " successfully created");
            } else{
                addCompanyResponse.setStatus(AddCompanyResponse.STATUS_EXISTS);
                Logger.info("Company already exists");
            }
        } else {
            Logger.info("Company already exists. Updating existing Company");
            existingCompany = getAndAddCompanyValues(addCompanyRequest, existingCompany);
            existingCompany.update();
            addCompanyResponse.setStatus(AddCompanyResponse.STATUS_UPDATE_SUCCESS);
            Logger.info("Company: " + existingCompany.getCompanyName() + " successfully updated");
        }
        return addCompanyResponse;
    }

    public static Company getAndAddCompanyValues(AddCompanyRequest addCompanyRequest, Company newCompany){
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

        return newCompany;
    }
}

