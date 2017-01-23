package models.entity.Recruiter.MO;

import api.http.httpRequest.Recruiter.CompanyLeadRequest;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.CompanyType;
import models.util.Message;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static models.util.Util.ACTION_CREATE;
import static models.util.Util.ACTION_UPDATE;

/**
 * Created by User on 17-11-2016.
 */

@Entity(name = "recruiter_lead_to_company")
@Table(name = "recruiter_lead_to_company")

public class CompanyLead extends Model{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_lead_id", columnDefinition = "bigint ", unique = true)
    private Long companyLeadId;

    @Column(name = "company_lead_uuid", columnDefinition = "varchar(255) not null", unique = true)
    private String companyLeadUUId;

    @CreatedTimestamp
    @Column(name = "company_lead_create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp companyLeadCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "company_lead_update_timeStamp", columnDefinition = "timestamp null")
    private Timestamp companyLeadUpdateTimeStamp;

    @JsonBackReference
    @PrivateOwned
    @OneToMany(mappedBy = "companyLead", cascade = CascadeType.ALL)
    private List<RecruiterLead> recruiterLeadList;

    @Column(name = "company_lead_name", columnDefinition = "varchar(255)")
    private String companyLeadName;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "company_lead_type", referencedColumnName = "companyTypeId")
    private CompanyType companyLeadType;

    //@Column(name = "company_lead_type", columnDefinition = "bigint")
    //private Long companyLeadType;

    @Column(name = "company_lead_website", columnDefinition = "varchar(255)")
    private String companyLeadWebsite;

    @Column(name = "company_lead_industry", columnDefinition = "bigint")
    private Long companyLeadIndustry;

    public CompanyLead() {
        this.companyLeadUUId = UUID.randomUUID().toString();
    }

    public Long getCompanyLeadId() {
        return companyLeadId;
    }

    public void setCompanyLeadId(Long companyLeadId) {
        this.companyLeadId = companyLeadId;
    }

    public Timestamp getCompanyLeadCreateTimeStamp() {
        return companyLeadCreateTimeStamp;
    }

    public void setCompanyLeadCreateTimeStamp(Timestamp companyLeadCreateTimeStamp) {
        this.companyLeadCreateTimeStamp = companyLeadCreateTimeStamp;
    }

    public Timestamp getCompanyLeadUpdateTimeStamp() {
        return companyLeadUpdateTimeStamp;
    }

    public void setCompanyLeadUpdateTimeStamp(Timestamp companyLeadUpdateTimeStamp) {
        this.companyLeadUpdateTimeStamp = companyLeadUpdateTimeStamp;
    }

    public List<RecruiterLead> getRecruiterLeadList() {
        return recruiterLeadList;
    }

    public void setRecruiterLeadList(List<RecruiterLead> recruiterLeadList) {
        this.recruiterLeadList = recruiterLeadList;
    }

    public String getCompanyLeadName() {
        return companyLeadName;
    }

    public void setCompanyLeadName(String companyLeadName) {
        this.companyLeadName = companyLeadName;
    }

    public CompanyType getCompanyLeadType() {
        return companyLeadType;
    }

    public void setCompanyLeadType(CompanyType companyLeadType) {
        this.companyLeadType = companyLeadType;
    }

    /*
    public Long getCompanyLeadType() {
        return companyLeadType;
    }

    public void setCompanyLeadType(Long companyLeadType) {
        this.companyLeadType = companyLeadType;
    }
*/

    public String getCompanyLeadWebsite() {
        return companyLeadWebsite;
    }

    public void setCompanyLeadWebsite(String companyLeadWebsite) {
        this.companyLeadWebsite = companyLeadWebsite;
    }

    public Long getCompanyLeadIndustry() {
        return companyLeadIndustry;
    }

    public void setCompanyLeadIndustry(Long companyLeadIndustry) {
        this.companyLeadIndustry = companyLeadIndustry;
    }

    public static Finder<String, CompanyLead> find = new Finder(CompanyLead.class);

    public List<CompanyLead> readById(List<Long> ids) {
        return CompanyLead.find.where().idIn(ids).setUseCache(Boolean.TRUE).findList();
    }

    public List<CompanyLead> readByUUID(List<String> uuids) {
        return CompanyLead.find.where().in("company_lead_uuid",uuids).setUseCache(Boolean.TRUE).findList();
    }

    public List<Message> validateCompanyLeadWebsite(CompanyLeadRequest request, String action, CompanyLead entity) {

        List<Message> messageList = new ArrayList<Message>();

        switch (action) {
            case ACTION_CREATE:
            case ACTION_UPDATE:
                if(request.getCompanyLeadWebsite().toString().length() > 0) {
                    UrlValidator validator = new UrlValidator(new String[]{ "http", "https" });
                    if(!validator.isValid(request.getCompanyLeadWebsite().trim())) {
                        try {
                            messageList.add(new Message(Message.MESSAGE_ERROR,"Company Website URL is incorrect"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }

        return messageList;
    }

}
