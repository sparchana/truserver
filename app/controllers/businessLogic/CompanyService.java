package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.AddCompanyRequest;
import api.http.httpResponse.AddCompanyResponse;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import models.entity.Company;
import models.entity.Static.CompanyStatus;
import models.entity.Static.CompanyType;
import models.entity.Static.Locality;
import play.Logger;

import java.io.File;

/**
 * Created by batcoder1 on 22/6/16.
 */
public class CompanyService {
    public static void uploadCompanyLogo(File newFile, String imgName){
        String SUFFIX = ServerConstants.LOGO_UPLOAD_SUFFIX;
        try{
            AWSCredentials credentials = new BasicAWSCredentials(
                    play.Play.application().configuration().getString("aws.accesskey"),
                    play.Play.application().configuration().getString("aws.secretAccesskey"));

            AmazonS3 s3client = new AmazonS3Client(credentials);
            String bucketName = ServerConstants.AWS_S3_BUCKET_NAME;

            String folderName = ServerConstants.AWS_S3_COMPANY_LOGO_FOLDER;

            String fileName = folderName + SUFFIX + imgName;
            s3client.putObject(new PutObjectRequest(bucketName, fileName, newFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            Logger.info("Logo updated successfully");
        } catch (Exception e){
            Logger.info("Exception while uploading logo " + e);
        }
    }

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
                addCompanyResponse.setCompanyId(existingCompanyWithName.getCompanyId());
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

    private static Company getAndAddCompanyValues(AddCompanyRequest addCompanyRequest, Company newCompany){
        if(addCompanyRequest.getCompanyName() != null){
            newCompany.setCompanyName(addCompanyRequest.getCompanyName());
        }
        if(addCompanyRequest.getCompanyEmployeeCount() != null){
            newCompany.setCompanyEmployeeCount(addCompanyRequest.getCompanyEmployeeCount());
        }
        if(addCompanyRequest.getCompanyWebsite() != null){
            newCompany.setCompanyWebsite(addCompanyRequest.getCompanyWebsite());
        }
        if(addCompanyRequest.getCompanyDescription() != null){
            newCompany.setCompanyDescription(addCompanyRequest.getCompanyDescription());
        }
        if(addCompanyRequest.getCompanyAddress() != null){
            newCompany.setCompanyAddress(addCompanyRequest.getCompanyAddress());
        }
        if(addCompanyRequest.getCompanyPinCode() != null){
            newCompany.setCompanyPinCode(addCompanyRequest.getCompanyPinCode());
        }
        if(addCompanyRequest.getCompanyLogo() != null){
            newCompany.setCompanyLogo(addCompanyRequest.getCompanyLogo());
        } else{
            newCompany.setCompanyLogo(ServerConstants.DEFAULT_COMPANY_LOGO);
        }
        if(addCompanyRequest.getCompanyStatus() != null){
            CompanyStatus companyStatus = CompanyStatus.find.where().eq("companyStatusId", addCompanyRequest.getCompanyStatus()).findUnique();
            newCompany.setCompStatus(companyStatus);
        }

        if(addCompanyRequest.getCompanyType() != null){
            CompanyType companyType = CompanyType.find.where().eq("companyTypeId", addCompanyRequest.getCompanyType()).findUnique();
            newCompany.setCompType(companyType);
        }

        if(addCompanyRequest.getCompanyLocality() != null){
            Locality locality = Locality.find.where().eq("localityId", addCompanyRequest.getCompanyLocality()).findUnique();
            newCompany.setCompanyLocality(locality);
        }
        return newCompany;
    }
}