package models.util;

import api.ServerConstants;
import au.com.bytecode.opencsv.CSVReader;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                            Logger.info("updating leadCreationTimeStamp");
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
                    Logger.info("TimeStamp Matching : " + new Timestamp(knowlarityInBoundDate.getTime()));
                    if(existingInteraction == null || existingInteraction.isEmpty()){
                        // save all inbound calls to interaction
                        Logger.info("CSV Interaction saved");
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
}
