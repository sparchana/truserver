package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.JobPostToLocality;
import models.entity.OM.RecruiterLeadToLocality;
import models.entity.OO.FollowUp;
import models.entity.Static.LeadSource;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

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
    private String recruiter_lead_name = "";

    @Column(name = "recruiter_lead_mobile", columnDefinition = "varchar(13) not null")
    private String recruiterLeadMobile;

    @Column(name = "recruiter_lead_channel", columnDefinition = "int signed not null")
    private int recruiter_lead_channel;

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

    public String getRecruiter_lead_name() {
        return recruiter_lead_name;
    }

    public void setRecruiter_lead_name(String recruiter_lead_name) {
        this.recruiter_lead_name = recruiter_lead_name;
    }

    public String getRecruiterLeadMobile() {
        return recruiterLeadMobile;
    }

    public void setRecruiterLeadMobile(String recruiterLeadMobile) {
        this.recruiterLeadMobile = recruiterLeadMobile;
    }

    public int getRecruiter_lead_channel() {
        return recruiter_lead_channel;
    }

    public void setRecruiter_lead_channel(int recruiter_lead_channel) {
        this.recruiter_lead_channel = recruiter_lead_channel;
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
