package models.entity;

import api.ServerConstants;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.JobPostToLocality;
import models.entity.OM.RecruiterLeadToLocality;
import models.entity.OO.FollowUp;
import models.entity.Static.LeadSource;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by dodo on 5/10/16.
 */
@Entity(name = "recruiter_lead")
@Table(name = "recruiter_lead")
public class RecruiterLead extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_lead_id", columnDefinition = "bigint signed", unique = true)
    private long recruiterLeadId;

    @Column(name = "recruiter_lead_uuid", columnDefinition = "varchar(255) not null", unique = true)
    private String recruiterLeadUUId;

    @Column(name = "recruiter_lead_status", columnDefinition = "int signed not null")
    private int recruiterLeadStatus;

    @Column(name = "recruiter_lead_name", columnDefinition = "varchar(50) not null")
    private String recruiterLeadName = "";

    @Column(name = "recruiter_lead_mobile", columnDefinition = "varchar(13) not null")
    private String recruiterLeadMobile;

    @Column(name = "recruiter_lead_channel", columnDefinition = "int signed not null")
    private int recruiterLeadChannel;

    @Column(name = "recruiter_lead_creation_timestamp", columnDefinition = "timestamp not null")
    private Timestamp recruiterLeadCreationTimestamp;

    @UpdatedTimestamp
    @Column(name = "recruiter_lead_update_timeStamp", columnDefinition = "timestamp")
    private Timestamp recruiterLeadUpdateTimeStamp;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterLead", cascade = CascadeType.ALL)
    private List<RecruiterLeadToLocality> recruiterLeadToLocalityList;

    public static Finder<String, RecruiterLead> find = new Finder(RecruiterLead.class);

    public RecruiterLead(){
        this.recruiterLeadUUId = UUID.randomUUID().toString();
        this.recruiterLeadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.recruiterLeadCreationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public RecruiterLead(String leadName, String leadMobile, int leadChannel) {
        this.recruiterLeadUUId = UUID.randomUUID().toString();
        this.recruiterLeadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.recruiterLeadCreationTimestamp = new Timestamp(System.currentTimeMillis());
        this.recruiterLeadName = leadName;
        this.recruiterLeadMobile = leadMobile;
        this.recruiterLeadChannel = leadChannel;
    }
    public static void addLead(RecruiterLead lead) {
        Logger.info("inside addLead model member method ");
        lead.save();
    }


    public long getRecruiterLeadId() {
        return recruiterLeadId;
    }

    public void setRecruiterLeadId(long recruiterLeadId) {
        this.recruiterLeadId = recruiterLeadId;
    }

    public String getRecruiterLeadUUId() {
        return recruiterLeadUUId;
    }

    public void setRecruiterLeadUUId(String recruiterLeadUUId) {
        this.recruiterLeadUUId = recruiterLeadUUId;
    }

    public int getRecruiterLeadStatus() {
        return recruiterLeadStatus;
    }

    public void setRecruiterLeadStatus(int recruiterLeadStatus) {
        this.recruiterLeadStatus = recruiterLeadStatus;
    }

    public String getRecruiterLeadName() {
        return recruiterLeadName;
    }

    public void setRecruiterLeadName(String recruiterLeadName) {
        this.recruiterLeadName = recruiterLeadName;
    }

    public String getRecruiterLeadMobile() {
        return recruiterLeadMobile;
    }

    public void setRecruiterLeadMobile(String recruiterLeadMobile) {
        this.recruiterLeadMobile = recruiterLeadMobile;
    }

    public int getRecruiterLeadChannel() {
        return recruiterLeadChannel;
    }

    public void setRecruiterLeadChannel(int recruiterLeadChannel) {
        this.recruiterLeadChannel = recruiterLeadChannel;
    }

    public Timestamp getRecruiterLeadCreationTimestamp() {
        return recruiterLeadCreationTimestamp;
    }

    public void setRecruiterLeadCreationTimestamp(Timestamp recruiterLeadCreationTimestamp) {
        this.recruiterLeadCreationTimestamp = recruiterLeadCreationTimestamp;
    }

    public Timestamp getRecruiterLeadUpdateTimeStamp() {
        return recruiterLeadUpdateTimeStamp;
    }

    public void setRecruiterLeadUpdateTimeStamp(Timestamp recruiterLeadUpdateTimeStamp) {
        this.recruiterLeadUpdateTimeStamp = recruiterLeadUpdateTimeStamp;
    }

    public List<RecruiterLeadToLocality> getRecruiterLeadToLocalityList() {
        return recruiterLeadToLocalityList;
    }

    public void setRecruiterLeadToLocalityList(List<RecruiterLeadToLocality> recruiterLeadToLocalityList) {
        this.recruiterLeadToLocalityList = recruiterLeadToLocalityList;
    }
}
