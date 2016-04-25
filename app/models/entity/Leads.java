package models.entity;

import api.AddLeadRequest;
import api.AddLeadResponse;
import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by zero on 23/4/16.
 */

@Entity(name = "leads")
@Table(name = "leads")
public class Leads extends Model {
    @Id
    @Column(name = "LeadId", columnDefinition = "int signed not null", unique = true)
    public long leadId = 0;

    @Column(name = "LeadName", columnDefinition = "varchar(50) not null")
    public String leadName = "";

    @Column(name = "LeadMobile", columnDefinition = "varchar(10) not null ")
    public String leadMobile = "";

    @Column(name = "LeadChannel", columnDefinition = "int signed not null")
    public long leadChannel = 0;

    @Column(name = "LeadType", columnDefinition = "int signed not null")
    public long leadType = 0;

    @Column(name = "LeadInterest", columnDefinition = "varchar(30) not null ")
    public String leadInterest = "";

    @Column(name = "LeadCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp LeadCreateTimestamp;

    public static Finder<String, Leads> find = new Finder(Leads.class);

    public static AddLeadResponse addLead(AddLeadRequest addLeadRequest) {
        String mobile = addLeadRequest.getLeadMobile();
        Logger.info("inside signup method");

        Leads lead = new Leads();
        Leads existingLead = Leads.find.where().eq("leadMobile", mobile).findUnique();
        AddLeadResponse addLeadResponse = new AddLeadResponse();
        if(existingLead == null) {
            lead.leadId = (int)(Math.random()*9000)+1000;
            lead.leadName = addLeadRequest.getLeadName();
            lead.leadMobile = addLeadRequest.getLeadMobile();
            lead.leadChannel = addLeadRequest.getLeadChannel();
            lead.leadType = addLeadRequest.getLeadType();
            lead.leadInterest = addLeadRequest.getLeadInterest();
            addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
            lead.save();
            Logger.info("saved data " + lead);
        } else {
            Logger.info("Lead already exists");
            addLeadResponse.setStatus(AddLeadResponse.STATUS_EXISTS);
        }
        return addLeadResponse;
    }
}
