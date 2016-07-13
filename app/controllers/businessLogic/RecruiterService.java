package controllers.businessLogic;

import api.http.httpRequest.AddCompanyRequest;
import api.http.httpResponse.AddCompanyResponse;
import models.entity.Company;
import models.entity.RecruiterProfile;
import play.Logger;

/**
 * Created by batcoder1 on 7/7/16.
 */
public class RecruiterService {
    public static AddCompanyResponse addRecruiter(AddCompanyRequest addCompanyRequest, Long companyId) {
        AddCompanyResponse addCompanyResponse = new AddCompanyResponse();
        Company existingCompany = Company.find.where().eq("companyId", companyId).findUnique();
        if(existingCompany != null){
            RecruiterProfile existingRecruiter = RecruiterProfile.find.where().eq("recruiterProfileMobile", addCompanyRequest.getRecruiterMobile()).findUnique();
            if(existingRecruiter == null){
                RecruiterProfile newRecruiter = new RecruiterProfile();
                newRecruiter = getAndSetRecruiterValues(addCompanyRequest, newRecruiter, existingCompany);
                newRecruiter.save();
                addCompanyResponse.setStatus(AddCompanyResponse.RECRUITER_CREATED);
                Logger.info("Recruiter successfully saved");
            } else{
                existingRecruiter = getAndSetRecruiterValues(addCompanyRequest, existingRecruiter, existingCompany);
                existingRecruiter.update();
                addCompanyResponse.setStatus(AddCompanyResponse.RECRUITER_UPDATED);
                Logger.info("Recruiter already exists. Updated The recruiter");
            }
        } else{
            addCompanyResponse.setStatus(AddCompanyResponse.RECRUITER_NO_COMPANY);
            Logger.info("Company Does not exists");
        }
        return addCompanyResponse;
    }

    public static RecruiterProfile getAndSetRecruiterValues(AddCompanyRequest addCompanyRequest, RecruiterProfile newRecruiter, Company existingCompany){
        newRecruiter.setRecCompany(existingCompany);
        newRecruiter.setRecruiterProfileName(addCompanyRequest.getRecruiterName());
        newRecruiter.setRecruiterProfileMobile(addCompanyRequest.getRecruiterMobile());
        newRecruiter.setRecruiterProfileLandline(addCompanyRequest.getRecruiterLandline());
        newRecruiter.setRecruiterProfileEmail(addCompanyRequest.getRecruiterEmail());

        return newRecruiter;
    }
}
