package models.util;

import api.ServerConstants;
import au.com.bytecode.opencsv.CSVReader;
import models.entity.Company;
import models.entity.Interaction;
import models.entity.JobPost;
import models.entity.Lead;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.Experience;
import models.entity.Static.JobRole;
import models.entity.Static.JobStatus;
import models.entity.Static.Locality;
import org.apache.commons.lang3.text.WordUtils;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by zero on 3/5/16.
 */
public class ParseCSV {
    public static int parseCSV(File file) {
        String[] nextLine;
        ArrayList<Lead> leads = new ArrayList<>();
        int totalUniqueLead = 0;
        int overLappingRecordCount = 0;
        /* TODO validate cell format of uploaded csv before parsing begins */
        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_ENTRY);
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            reader.readNext();// skip title row
            Logger.info("csv parsing begins");
            while ((nextLine = reader.readNext()) != null) {
                Lead lead = new Lead();
                Interaction interaction = new Interaction();
                // Read all InBound Calls
                if (nextLine != null && nextLine[0].equals("0")) {
                    Date  knowlarityInBoundDate = sdf.parse(nextLine[4]);
                    Timestamp knowlarityInBoundTimestamp = new Timestamp(knowlarityInBoundDate.getTime());

                    // fix mobile no
                    if(!nextLine[1].contains("+")) {
                        lead.setLeadMobile("+"+nextLine[2]);
                    } else {
                        lead.setLeadMobile(nextLine[2]);
                    }
                    // check if lead already exists
                    Lead existingLead = Lead.find.where().eq("leadMobile", lead.getLeadMobile()).findUnique();

                    if (existingLead == null) {
                        lead.setLeadType(ServerConstants.TYPE_LEAD);
                        lead.setLeadChannel(ServerConstants.LEAD_CHANNEL_KNOWLARITY);
                        lead.setLeadStatus(ServerConstants.LEAD_STATUS_NEW);
                        lead.setLeadCreationTimestamp(knowlarityInBoundTimestamp);
                        lead.save();
                        leads.add(lead);
                        totalUniqueLead++;

                        interaction.setObjectAType(lead.getLeadType());
                        interaction.setObjectAUUId(lead.getLeadUUId());
                        interaction.setResult(ServerConstants.INTERACTION_RESULT_FIRST_INBOUND_CALL);

                    } else {
                        if(existingLead.getLeadCreationTimestamp().getTime() > knowlarityInBoundTimestamp.getTime()) {
                            // recording the first inbound of a lead
                            existingLead.setLeadCreationTimestamp(knowlarityInBoundTimestamp);
                            existingLead.update();
                        }
                        overLappingRecordCount++;
                        interaction.setObjectAType(existingLead.getLeadType());
                        interaction.setObjectAUUId(existingLead.getLeadUUId());
                        if(existingLead.getLeadStatus() == ServerConstants.LEAD_STATUS_WON){
                            interaction.setResult(ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_CALLED_BACK);
                        } else {
                            interaction.setResult(ServerConstants.INTERACTION_RESULT_EXISTING_LEAD_CALLED_BACK);
                        }
                    }

                    // gives total no of old leads
                    List<Interaction> existingInteraction = Interaction.find.where()
                            .eq("objectAUUID", interaction.getObjectAUUId())
                            .eq("creationTimestamp", new Timestamp(knowlarityInBoundDate.getTime()))
                            .findList();
                    if(existingInteraction == null || existingInteraction.isEmpty()){
                        // save all inbound calls to interaction
                        interaction.setCreatedBy(ServerConstants.INTERACTION_CREATED_SYSTEM_KNOWLARITY);
                        interaction.setCreationTimestamp(knowlarityInBoundTimestamp);
                        interaction.setInteractionType(ServerConstants.INTERACTION_TYPE_CALL_IN);
                        interaction.save();
                    }
                }
            }
            Logger.info("Csv File Parsed and stored in db!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    return totalUniqueLead;
    }

    public static int parseBabaJobsCSV(File file){

        String[] cells;
        int totalJobPostSaved = 0;
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            reader.readNext();// skip title row
            Logger.info("csv parsing begins");
            JobStatus jobStatus = JobStatus.find.where().eq("JobStatusId", 2).findUnique(); // Active
            while ((cells = reader.readNext()) != null) {
                String desc;
                JobPost jobpost = new JobPost();
                jobpost.setJobPostTitle(WordUtils.capitalize(cells[2]));
                jobpost.setGender(cells[7].trim().equalsIgnoreCase("Male")? ServerConstants.GENDER_MALE: ServerConstants.GENDER_FEMALE);
                desc = cells[6].trim();
                if(!cells[9].trim().isEmpty()){
                    desc +=" Languages: " + cells[9];
                }
                jobpost.setJobPostDescription(desc);
                jobpost.setJobPostIsHot(false);
                jobpost.setSource(ServerConstants.SOURCE_BABAJOBS);
                jobpost.setJobPostMinSalary(Long.parseLong(cells[5])); // no salary range in data set
                jobpost.setJobPostMaxSalary(0L); // no max hence default
                if(!cells[11].trim().isEmpty())jobpost.setJobPostVacancies(Integer.parseInt(cells[11]));
                jobpost.setJobPostExperience(getExperience(cells[8].trim()));
                jobpost.setCompany(getCompany(cells[4]));
                jobpost.setJobPostToLocalityList(getLocalityList(cells[10], jobpost));
                jobpost.setJobRole(JobRole.find.where().eq("jobRoleId", cells[3]).findUnique());
                jobpost.setJobPostStatus(jobStatus);

                //Logger.info(String.valueOf(toJson(jobpost)));
                if(!jobpost.getJobPostToLocalityList().isEmpty()){
                    totalJobPostSaved++;
                    jobpost.save();
                }
            }
            Logger.info("BJ2TJ !! Csv File Parsed and stored in db!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalJobPostSaved;
    }

    private static List<JobPostToLocality> getLocalityList(String localities, JobPost jobPost) {
        List<String> localityNameList = Arrays.asList(localities.split("\\s*,\\s*"));
        List<JobPostToLocality> jobPostToLocalityList = new ArrayList<>();
        for(String dirtyLocality : localityNameList) {
            String localityName = performSanitization(dirtyLocality);
            if(localityName.isEmpty()){
                continue;
            }
            JobPostToLocality jobPostToLocality = new JobPostToLocality();
            jobPostToLocality.setJobPost(jobPost);
            try{
                Locality locality = Locality.find.where()
                        .eq("localityName", localityName).findUnique();
                if(locality == null) continue;
                jobPostToLocality.setLocality(locality);
                if(locality.getLat()!=0) jobPostToLocality.setLatitude(locality.getLat());
                if(locality.getLng()!=0) jobPostToLocality.setLongitude(locality.getLng());

            } catch (NonUniqueResultException n){
                Logger.error("multiple data in db found:"+localityName);
            }
            jobPostToLocalityList.add(jobPostToLocality);
        }

        return jobPostToLocalityList;
    }

    private static String performSanitization(String dirtyLocality) {
        /* TODO: Add more sanitization conditions */
        return dirtyLocality.trim();
    }

    private static Company getCompany(String companyName) {
        /* Make sure company name is only 50 chars at max*/
        companyName = companyName.trim();
        if(companyName.isEmpty()){
            return null;
        }
        Company company = Company.find.where().eq("companyName", companyName).findUnique();
        if(company == null){
            /* create company */
            company = new Company();
            companyName = companyName.length() > 49 ? companyName.substring(0, 49) : companyName;
            company.setCompanyName(WordUtils.capitalize(companyName));
            company.setSource(ServerConstants.SOURCE_BABAJOBS);
            company.save();
        }
        return company;
    }

    private static Experience getExperience(String yr) {
        Experience experience = null;
        if(yr.trim().isEmpty()){
            experience = Experience.find.where().eq("experienceId", 5).findUnique();
        } else {
            int c = Integer.parseInt(yr);
            /* Since sheet has only these division in experience */
            switch (c){
                case 1 : experience = Experience.find.where().eq("experienceId", 2).findUnique(); break;
                case 2 : experience = Experience.find.where().eq("experienceId", 3).findUnique(); break;
                case 3 : experience = Experience.find.where().eq("experienceId", 3).findUnique(); break;
                case 4 : experience = Experience.find.where().eq("experienceId", 4).findUnique(); break;
                case 5 : experience = Experience.find.where().eq("experienceId", 4).findUnique(); break;
                default: experience = Experience.find.where().eq("experienceId", 5).findUnique(); break;
            }
        }
        return experience;
    }
}
