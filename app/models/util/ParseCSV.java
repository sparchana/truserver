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
import java.util.UUID;

/**
 * Created by zero on 3/5/16.
 */
public class ParseCSV {
    public static int parseCSV(File file) {
        String[] nextLine;
        ArrayList<Lead> leads = new ArrayList<>();
        int totalUniqueLead = 0;
        int overLappingRecordCount = 0;
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

                    // fix mobile no
                    if(!nextLine[1].contains("+")) {
                        lead.leadMobile = "+"+nextLine[2];
                    } else {
                        lead.leadMobile = nextLine[2];
                    }

                    // check if lead already exists
                    Lead existingLead = Lead.find.where().eq("leadMobile", lead.leadMobile).findUnique();

                    if (existingLead == null) {
                        lead.leadUUId = UUID.randomUUID().toString();
                        lead.leadId = Util.randomLong();
                        lead.leadCreationTimestamp = new Timestamp(knowlarityInBoundDate.getTime());
                        lead.leadType = ServerConstants.TYPE_LEAD;
                        lead.leadChannel = ServerConstants.LEAD_CHANNEL_KNOWLARITY;
                        lead.leadStatus = ServerConstants.LEAD_STATUS_NEW;
                        lead.save();
                        leads.add(lead);
                        totalUniqueLead++;

                        interaction.objectAType = lead.leadType;
                        interaction.objectAUUId= lead.leadUUId;
                        interaction.note = "First Inbound Call";

                    } else {
                        if(existingLead.getLeadCreationTimestamp().getTime() > lead.leadCreationTimestamp.getTime()) {
                            // recording the first inbound of a lead
                            existingLead.setLeadCreationTimestamp(lead.leadCreationTimestamp);
                            existingLead.update();
                        }
                        overLappingRecordCount++;
                        interaction.objectAType = existingLead.leadType;
                        interaction.objectAUUId= existingLead.leadUUId;
                        interaction.note = "Existing Lead Called Back";
                    }

                    // gives total no of old leads
                    List<Interaction> existingInteraction = Interaction.find.where()
                            .eq("objectAUUID", interaction.objectAUUId)
                            .eq("creationTimestamp", new Timestamp(knowlarityInBoundDate.getTime()))
                            .findList();
                    Logger.info("TimeStamp Matching : " + new Timestamp(knowlarityInBoundDate.getTime()));
                    if(existingInteraction == null || existingInteraction.isEmpty()){
                        // save all inbound calls to interaction
                        Logger.info("CSV Interaction saved");
                        interaction.createdBy = "System - Knowlarity";
                        interaction.creationTimestamp = new Timestamp(knowlarityInBoundDate.getTime());
                        interaction.interactionType = ServerConstants.INTERACTION_TYPE_CALL_IN;
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
