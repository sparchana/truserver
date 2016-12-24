package models.entity.Recruiter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.MO.CompanyLead;
import models.entity.Recruiter.OM.RecruiterLeadToJobRole;
import models.util.Message;
import play.Logger;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static models.util.Util.ACTION_CREATE;
import static models.util.Util.ACTION_UPDATE;

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

    @Column(name = "recruiter_lead_name", columnDefinition = "varchar(50) null")
    private String recruiterLeadName = "";

    @Column(name = "recruiter_lead_mobile", columnDefinition = "varchar(13) null")
    private String recruiterLeadMobile;

    @Column(name = "recruiter_lead_alt_number", columnDefinition = "varchar(13) null")
    private String recruiterLeadAltNumber = "";

    @Column(name = "recruiter_lead_email", columnDefinition = "varchar(255) null")
    private String recruiterLeadEmail = "";

    @Column(name = "recruiter_lead_channel", columnDefinition = "int signed not null")
    private int recruiterLeadChannel;

    @CreatedTimestamp
    @Column(name = "recruiter_lead_creation_timestamp", columnDefinition = "timestamp not null")
    private Timestamp recruiterLeadCreationTimestamp;

    @UpdatedTimestamp
    @Column(name = "recruiter_lead_update_timeStamp", columnDefinition = "timestamp")
    private Timestamp recruiterLeadUpdateTimeStamp;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterLead", cascade = CascadeType.ALL)
    private List<RecruiterLeadToJobRole> recruiterLeadToJobRoleList;

    @Column(name = "recruiter_lead_requirement", columnDefinition = "varchar(255)")
    private String recruiterLeadRequirement = "";

    @Column(name = "recruiter_lead_source_type", columnDefinition = "int not null")
    private Integer recruiterLeadSourceType;

    @Column(name = "recruiter_lead_source_name", columnDefinition = "int not null")
    private Integer recruiterLeadSourceName;

    @Column(name = "recruiter_lead_source_date", columnDefinition = "date null")
    private Date recruiterLeadSourceDate;

    @JsonManagedReference
    @PrivateOwned
    @ManyToOne
    @JoinColumn(name = "company_lead_id", referencedColumnName = "company_lead_id")
    private CompanyLead companyLead;

    public static Finder<String, RecruiterLead> find = new Finder(RecruiterLead.class);

    public String getRecruiterLeadAltNumber() {
        return recruiterLeadAltNumber;
    }

    public void setRecruiterLeadAltNumber(String recruiterLeadAltNumber) {
        this.recruiterLeadAltNumber = recruiterLeadAltNumber;
    }

    public String getRecruiterLeadEmail() {
        return recruiterLeadEmail;
    }

    public void setRecruiterLeadEmail(String recruiterLeadEmail) {
        this.recruiterLeadEmail = recruiterLeadEmail;
    }

    public Integer getRecruiterLeadSourceType() {
        return recruiterLeadSourceType;
    }

    public void setRecruiterLeadSourceType(Integer recruiterLeadSourceType) {
        this.recruiterLeadSourceType = recruiterLeadSourceType;
    }

    public Integer  getRecruiterLeadSourceName() {
        return recruiterLeadSourceName;
    }

    public void setRecruiterLeadSourceName(Integer recruiterLeadSourceName) {
        this.recruiterLeadSourceName = recruiterLeadSourceName;
    }

    public Date getRecruiterLeadSourceDate() {
        return recruiterLeadSourceDate;
    }

    public void setRecruiterLeadSourceDate(Date recruiterLeadSourceDate) {
        this.recruiterLeadSourceDate = recruiterLeadSourceDate;
    }

    public CompanyLead getCompanyLead() {
        return companyLead;
    }

    public void setCompanyLead(CompanyLead companyLead) {
        this.companyLead = companyLead;
    }

    public RecruiterLead(){
        this.recruiterLeadUUId = UUID.randomUUID().toString();
        this.recruiterLeadStatus = ServerConstants.LEAD_STATUS_NEW;
        this.recruiterLeadCreationTimestamp = new Timestamp(System.currentTimeMillis());
        this.recruiterLeadSourceType = 0;
        this.recruiterLeadSourceName = 0;
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
        this.recruiterLeadMobile = FormValidator.convertToIndianMobileFormat(recruiterLeadMobile);
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

    public List<RecruiterLeadToJobRole> getRecruiterLeadToJobRoleList() {
        return recruiterLeadToJobRoleList;
    }

    public void setRecruiterLeadToJobRoleList(List<RecruiterLeadToJobRole> recruiterLeadToJobRoleList) {
        this.recruiterLeadToJobRoleList = recruiterLeadToJobRoleList;
    }

    public String getRecruiterLeadRequirement() {
        return recruiterLeadRequirement;
    }

    public void setRecruiterLeadRequirement(String recruiterLeadRequirement) {
        this.recruiterLeadRequirement = recruiterLeadRequirement;
    }

    public List<Message> validateRecruiterLeadMobile(RecruiterLeadRequest request, String action, RecruiterLead entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:
                if(request.getRecruiterLeadMobile().toString().length() == 0) {
                    try {
                        messageList.add(new Message(Message.MESSAGE_ERROR,"Mobile number is a compulsory input"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                messageList.addAll(checkMobile(request.getRecruiterLeadMobile().toString(),"Recruiter Mobile"));
                break;
        }

        return messageList;
    }

    public List<Message> validateRecruiterLeadAltNumber(RecruiterLeadRequest request, String action, RecruiterLead entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:
                messageList.addAll(checkMobile(request.getRecruiterLeadAltNumber().toString(),"Alternate Mobile Number"));
                break;
        }

        return messageList;
    }

    public List<Message> validateRecruiterLeadName(RecruiterLeadRequest request, String action, RecruiterLead entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:
                if(request.getRecruiterLeadName().toString().length() > 0 && !Pattern.matches("^[ A-z]+$",request.getRecruiterLeadName().toString())) {
                    try {
                        messageList.add(new Message(Message.MESSAGE_ERROR,"Name must contain only letters"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        return messageList;
    }

    public List<Message> validateRecruiterLeadEmail(RecruiterLeadRequest request, String action, RecruiterLead entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:

                EmailValidator ev = EmailValidator.getInstance();
                if(StringUtils.isNotBlank(request.getRecruiterLeadEmail()) && (!ev.isValid(request.getRecruiterLeadEmail().trim()))) {
                    try {
                        messageList.add(new Message(Message.MESSAGE_ERROR,request.getRecruiterLeadEmail()+" is not a valid email id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        return messageList;
    }

    public List<RecruiterLead> readById(List<Long> ids) {
        return RecruiterLead.find.where().idIn(ids).setUseCache(Boolean.TRUE).findList();
    }

    public List<RecruiterLead> readByUUID(List<String> uuids) {
        return RecruiterLead.find.where().in("recruiter_lead_uuid",uuids).setUseCache(Boolean.TRUE).findList();
    }

    public List<Message> checkMobile(String mobile, String field){

        List<Message> messageList = new ArrayList<>();

        if(!StringUtils.isNumeric(mobile)){
            try {
                messageList.add(new Message(Message.MESSAGE_ERROR,field+" must only contain numbers"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(mobile.length() < 10) {
            try {
                messageList.add(new Message(Message.MESSAGE_ERROR,field+" number must be 10 digits"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            int startNumber = Integer.parseInt(String.valueOf(mobile.charAt(mobile.length()-10)));
            if(startNumber < 7) {
                try {
                    messageList.add(new Message(Message.MESSAGE_ERROR,field+" number must start with 7 or 8 or 9"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return messageList;

    }

}
