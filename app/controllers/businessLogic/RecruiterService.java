package controllers.businessLogic;

import api.http.httpRequest.Recruiter.AddRecruiterRequest;
import api.http.httpResponse.AddRecruiterResponse;
import models.entity.Company;
import models.entity.RecruiterProfile;
import play.Logger;

/**
 * Created by batcoder1 on 7/7/16.
 */
public class RecruiterService {
    public static AddRecruiterResponse addRecruiter(AddRecruiterRequest addRecruiterRequest) {
        AddRecruiterResponse addRecruiterResponse = new AddRecruiterResponse();
        Company existingCompany = Company.find.where().eq("companyId", addRecruiterRequest.getRecruiterCompany()).findUnique();
        if(existingCompany != null){
            RecruiterProfile existingRecruiter = RecruiterProfile.find.where().eq("recruiterProfileMobile", addRecruiterRequest.getRecruiterMobile()).findUnique();
            if(existingRecruiter == null){
                RecruiterProfile newRecruiter = new RecruiterProfile();
                newRecruiter = getAndSetRecruiterValues(addRecruiterRequest, newRecruiter, existingCompany);
                newRecruiter.save();
                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                addRecruiterResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());
                Logger.info("Recruiter successfully saved");
            } else{
                existingRecruiter = getAndSetRecruiterValues(addRecruiterRequest, existingRecruiter, existingCompany);
                existingRecruiter.update();
                addRecruiterResponse.setRecruiterId(existingRecruiter.getRecruiterProfileId());
                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_UPDATE);
                Logger.info("Recruiter already exists. Updated The recruiter");
            }
        } else{
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
            Logger.info("Company Does not exists");
        }
        return addRecruiterResponse;
    }

    public static RecruiterProfile getAndSetRecruiterValues(AddRecruiterRequest addRecruiterRequest, RecruiterProfile newRecruiter, Company existingCompany){
        newRecruiter.setRecCompany(existingCompany);
        newRecruiter.setRecruiterProfileName(addRecruiterRequest.getRecruiterName());
        newRecruiter.setRecruiterProfileMobile(addRecruiterRequest.getRecruiterMobile());
        newRecruiter.setRecruiterProfileLandline(addRecruiterRequest.getRecruiterLandline());
        newRecruiter.setRecruiterProfileEmail(addRecruiterRequest.getRecruiterEmail());

        return newRecruiter;
    }
}
