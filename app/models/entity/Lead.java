package models.entity;

import api.ServerConstants;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.LeadSource;
import models.util.Util;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 23/4/16.
 */

@Entity(name = "lead")
@Table(name = "lead")
public class Lead extends Model {

    @Id
    @Column(name = "LeadId", columnDefinition = "bigint signed null", nullable = false, unique = true)
    public Long leadId;

    @Column(name = "LeadUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String leadUUId = "";

    @Column(name = "LeadStatus", columnDefinition = "int signed not null", nullable = false)
    public Integer leadStatus; // new, TryingToConvert

    @Column(name = "LeadName", columnDefinition = "varchar(50) not null", nullable = false)
    public String leadName = "";

    @Column(name = "LeadMobile", columnDefinition = "varchar(13) not null ", nullable = false)
    public String leadMobile = "";

    @Column(name = "LeadChannel", columnDefinition = "int signed not null", nullable = false)
    public Integer leadChannel;

    @Column(name = "LeadType", columnDefinition = "int signed not null", nullable = false)
    public Integer leadType; // recruiter, candidate

    @Column(name = "LeadInterest", columnDefinition = "varchar(30)")
    public String leadInterest = "";

    @Column(name = "LeadCreationTimestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    public Timestamp leadCreationTimestamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "LeadSourceId", referencedColumnName = "leadSourceId")
    public LeadSource leadSource;

    public static Finder<String, Lead> find = new Finder(Lead.class);

    public Lead(){
        this.leadId = Util.randomLong();
        this.leadUUId = UUID.randomUUID().toString();
        this.leadStatus = ServerConstants.LEAD_STATUS_NEW;
    }

    public Lead(String leadName, String leadMobile, int leadChannel, int leadType, int leadSourceId) {
        LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", leadSourceId).findUnique();
        this.leadId = Util.randomLong();
        this.leadUUId = UUID.randomUUID().toString();
        this.leadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.leadInterest = ServerConstants.LEAD_INTEREST_UNKNOWN; // TODO: tobe Deprecated
        this.leadName = leadName;
        this.leadMobile = leadMobile;
        this.leadChannel = leadChannel;
        this.leadType = leadType;
        this.setLeadSource(leadSource);
        if(leadSource != null) {
            Logger.info("LeadSourceId set to "+this.leadSource.leadSourceId);
        } else {
            // leadsouce saved is null
            Logger.info("LeadSource Static Table doesn't have entry for LeadSourceId: " + leadSourceId);
        }

    }
    public static void addLead(Lead lead) {
        Logger.info("inside addLead model member method ");
        lead.save();
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = leadMobile;
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
    public int getLeadType() {

        return leadType;
    }
    public Timestamp getLeadCreationTimestamp() {
        return leadCreationTimestamp;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }
    public void setLeadType(int leadType) {
        this.leadType = leadType;
    }
    public void setLeadStatus(int leadStatus) {
        if(this.leadStatus < leadStatus){
            this.leadStatus = leadStatus;
        }
    }

    public void setLeadCreationTimestamp(Timestamp leadCreationTimestamp) {
        this.leadCreationTimestamp = leadCreationTimestamp;
    }


    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

}
