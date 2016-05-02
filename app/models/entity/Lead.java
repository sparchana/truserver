package models.entity;

import api.http.AddLeadRequest;
import api.http.AddLeadResponse;
import com.avaje.ebean.Model;
import models.util.Util;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 23/4/16.
 */

@Entity(name = "lead")
@Table(name = "lead")
public class Lead extends Model {

    @Id
    @Column(name = "LeadId", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    public long leadId = 0;

    @Column(name = "LeadUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String leadUUId = "";

    @Column(name = "LeadStatus", columnDefinition = "int signed not null", nullable = false)
    public int leadStatus = 0; // new, TryingToConvert

    @Column(name = "LeadName", columnDefinition = "varchar(50) not null", nullable = false)
    public String leadName = "";

    @Column(name = "LeadMobile", columnDefinition = "varchar(13) not null ", nullable = false)
    public String leadMobile = "";

    @Column(name = "LeadChannel", columnDefinition = "int signed not null", nullable = false)
    public int leadChannel = 0;

    @Column(name = "LeadType", columnDefinition = "int signed not null", nullable = false)
    public int leadType = 0; // recruter, candidate

    @Column(name = "LeadInterest", columnDefinition = "varchar(30)")
    public String leadInterest = "";

    @Column(name = "LeadCreationTimestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    public Timestamp leadCreationTimestamp;

    public static Finder<String, Lead> find = new Finder(Lead.class);

    public static AddLeadResponse addLead(AddLeadRequest addLeadRequest) {
        String mobile = addLeadRequest.getleadMobile();
        Logger.info("inside addLead method");

        Lead lead = new Lead();
        Lead existingLead = Lead.find.where().eq("leadMobile", mobile).findUnique();
        AddLeadResponse addLeadResponse = new AddLeadResponse();
        if(existingLead == null) {
            lead.leadId = Util.randomLong();
            lead.leadUUId = UUID.randomUUID().toString();
            lead.leadName = addLeadRequest.getleadName();
            lead.leadMobile = addLeadRequest.getleadMobile();
            addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
            lead.save();
            Logger.info("saved data " + lead);
        } else {
            Logger.info("Lead already exists");
            addLeadResponse.setStatus(AddLeadResponse.STATUS_FAILURE);
        }
        return addLeadResponse;
    }



    public String getLeadName(){
        return this.leadName;
    }
    public String getLeadMobile(){
        return this.leadMobile;
    }
    public String getLeadUUId() {
        return leadUUId;
    }
    public long getLeadId() {
        return leadId;

    }
    public int getLeadSource() {
        return leadChannel;
    }
    public int getLeadType() {

        return leadType;
    }
    public Timestamp getLeadCreationTimestamp() {
        return leadCreationTimestamp;
    }
    public int getLeadStatus() {
        return leadStatus;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }
    public void setLeadType(int leadType) {
        this.leadType = leadType;
    }
    public void setLeadStatus(int leadStatus) {
        this.leadStatus = leadStatus;
    }
}
