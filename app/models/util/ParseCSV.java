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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zero on 3/5/16.
 */
public class ParseCSV {
    public static int parseCSV(File file) {
        String[] nextLine;
        ArrayList<Lead> leads = new ArrayList<>();
        int count = 0;
        int overLappingRecordCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssXXX");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            reader.readNext();// skip title row
            while ((nextLine = reader.readNext()) != null) {
                Lead lead = new Lead();
                Interaction kwObject = new Interaction();
                // Read all InBound Calls
                if (nextLine != null && nextLine[0].equals("0")) {
                    lead.leadUUId = UUID.randomUUID().toString();
                    lead.leadId = Util.randomLong();
                    if(!nextLine[1].contains("+")) {
                        lead.leadMobile = "+"+nextLine[2];
                    } else {
                        lead.leadMobile = nextLine[2];
                    }
                    Date parsedDate;
                    try {
                        parsedDate = sdf.parse(nextLine[4]);
                    } catch (ParseException e) {
                        parsedDate = sdf2.parse(nextLine[4]);
                    }
                    lead.leadCreationTimestamp = new Timestamp(parsedDate.getTime());
                    Lead existingLead = Lead.find.where()
                            .eq("leadMobile", lead.leadMobile)
                            .findUnique();
                    lead.leadType = ServerConstants.TYPE_LEAD;
                    lead.leadChannel = ServerConstants.LEAD_CHANNEL_KNOWLARITY;
                    lead.leadStatus = ServerConstants.LEAD_STATUS_NEW;

                    kwObject.objectAType = lead.leadType;
                    kwObject.objectAUUId= lead.leadUUId;
                    if (existingLead == null) {
                        lead.save();
                        count++;
                        leads.add(lead);
                    } else {
                        if(existingLead.getLeadCreationTimestamp().getTime() > lead.leadCreationTimestamp.getTime()) {
                            // recording the first inbound of a lead
                            existingLead.setLeadCreationTimestamp(lead.leadCreationTimestamp);
                            existingLead.update();
                        }
                        overLappingRecordCount++;
                        kwObject.objectAType = existingLead.leadType;
                        kwObject.objectAUUId= existingLead.leadUUId;
                        Logger.info("compared DateTime: KwVer." + lead.leadCreationTimestamp.getTime() + "  OurDbVer. " + existingLead.leadCreationTimestamp.getTime());
                    }
                    // gives total no of old leads
                    Logger.info("Total OverLapping records : " + overLappingRecordCount);

                    // save all inbound calls to interaction
                    kwObject.createdBy = "System";
                    kwObject.creationTimestamp = new Timestamp(parsedDate.getTime());
                    kwObject.interactionType = ServerConstants.INTERACTION_TYPE_CALL_IN;
                    kwObject.save();
                }
            }
            Logger.info("Csv File Parsed and stored in db!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    return count;
    }
}
