package models.entity;

import api.ServerConstants;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OO.FollowUp;
import models.entity.Static.LeadSource;
import org.apache.commons.lang3.text.WordUtils;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

import static play.libs.Json.toJson;

/**
 * Created by zero on 23/4/16.
 */

@Entity(name = "lead")
@Table(name = "lead")
public class Lead extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LeadId", columnDefinition = "bigint signed", unique = true)
    private long leadId;

    @Column(name = "LeadUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String leadUUId;

    @Column(name = "LeadStatus", columnDefinition = "int signed not null", nullable = false)
    private int leadStatus;

    @Column(name = "LeadName", columnDefinition = "varchar(50) not null", nullable = false)
    private String leadName = "";

    @Column(name = "LeadMobile", columnDefinition = "varchar(13) not null ", nullable = false)
    private String leadMobile;

    @Column(name = "LeadChannel", columnDefinition = "int signed not null", nullable = false)
    private int leadChannel;

    @Column(name = "LeadType", columnDefinition = "int signed not null", nullable = false)
    private int leadType; // recruiter, candidate

    @Column(name = "LeadInterest", columnDefinition = "varchar(30)")
    private String leadInterest;

    @Column(name = "LeadCreationTimestamp", columnDefinition = "timestamp not null", nullable = false)
    private Timestamp leadCreationTimestamp;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "LeadSourceId", referencedColumnName = "leadSourceId")
    private LeadSource leadSource;

    @JsonManagedReference
    @JoinColumn(name = "followUpId", referencedColumnName = "followUpId")
    @OneToOne(cascade = CascadeType.ALL)
    private FollowUp followUp;

    public static Finder<String, Lead> find = new Finder(Lead.class);

    public Lead(){
        this.leadUUId = UUID.randomUUID().toString();
        this.leadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.leadCreationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Lead(String leadName, String leadMobile, int leadChannel, int leadType, int leadSourceId) {
        LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", leadSourceId).findUnique();
        this.leadUUId = UUID.randomUUID().toString();
        this.leadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.leadCreationTimestamp = new Timestamp(System.currentTimeMillis());
        this.leadInterest = ServerConstants.LEAD_INTEREST_UNKNOWN; // TODO: tobe Deprecated
        this.leadName = leadName;
        this.leadMobile = leadMobile;
        this.leadChannel = leadChannel;
        this.leadType = leadType;
        this.setLeadSource(leadSource);
        if(leadSource != null) {
            Logger.info("LeadSourceId set to "+this.leadSource.getLeadSourceId());
        } else {
            // leadsouce saved is null
            Logger.info("LeadSource Static Table doesn't have entry for LeadSourceId: " + leadSourceId);
        }

    }
    public static void addLead(Lead lead) {
        Logger.info("inside addLead model member method mobile: " + lead);
        lead.save();
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = leadMobile;
    }

    public String getLeadName(){
        return WordUtils.capitalize(this.leadName);
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

    public void setLeadUUId(String leadUUId) {
        this.leadUUId = leadUUId;
    }

    public int getLeadStatus() {
        return leadStatus;
    }

    public int getLeadChannel() {
        return leadChannel;
    }

    public void setLeadChannel(int leadChannel) {
        this.leadChannel = leadChannel;
    }

    public String getLeadInterest() {
        return leadInterest;
    }

    public void setLeadInterest(String leadInterest) {
        this.leadInterest = leadInterest;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public FollowUp getFollowUp() {
        return followUp;
    }

    public void setFollowUp(FollowUp followUp) {
        this.followUp = followUp;
    }
}
