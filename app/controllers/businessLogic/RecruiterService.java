package controllers.businessLogic;

import api.http.httpRequest.AddCompanyRequest;
import models.entity.Company;
import models.entity.RecruiterProfile;
import play.Logger;

/**
 * Created by batcoder1 on 7/7/16.
 */
public class RecruiterService {
    public static Integer addRecruiter(AddCompanyRequest addCompanyRequest, Long companyId) {
        RecruiterProfile newRecruiter = new RecruiterProfile();
        Logger.info(" == " + companyId);
        Company existingCompany = Company.find.where().eq("companyId", companyId).findUnique();
        if(existingCompany != null){
            RecruiterProfile existingRecruiter = RecruiterProfile.find.where().eq("recruiterMobile", addCompanyRequest.getRecruiterMobile()).findUnique();
            if(existingRecruiter == null){
                newRecruiter.setRecCompany(existingCompany);
                newRecruiter.setRecruiterProfileName(addCompanyRequest.getRecruiterName());
                newRecruiter.setRecruiterProfileMobile(addCompanyRequest.getRecruiterMobile());
                newRecruiter.setRecruiterProfileLandline(addCompanyRequest.getRecruiterLandline());
                newRecruiter.setRecruiterProfileEmail(addCompanyRequest.getRecruiterEmail());
                newRecruiter.save();
                Logger.info("Recruiter successfully saved");
            } else{
                Logger.info("Recruiter already exists");
            }
        } else{
            Logger.info("Company Doesnot exists");
        }
        return 0;
    }
}
