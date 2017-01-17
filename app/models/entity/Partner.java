package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.IDProofReference;
import models.entity.OM.JobApplication;
import models.entity.OM.PartnerToCandidate;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.Locality;
import models.entity.Static.PartnerProfileStatus;
import models.entity.Static.PartnerType;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by adarsh on 9/9/16.
 */

@Entity(name = "partner")
@Table(name = "partner")
public class Partner extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id", columnDefinition = "bigint signed", unique = true)
    private long partnerId = 0;

    @Column(name = "partner_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String partnerUUId;

    @Column(name = "partner_name", columnDefinition = "varchar(50) not null")
    private String partnerFirstName;

    @Column(name = "partner_last_name", columnDefinition = "varchar(50) null")
    private String partnerLastName;

    @Column(name = "partner_mobile", columnDefinition = "varchar(13) not null")
    private String partnerMobile;

    @Column(name = "partner_email", columnDefinition = "varchar(255) null")
    private String partnerEmail;

    @Column(name = "partner_company", columnDefinition = "varchar(255) null")
    private String partnerCompany;

    @Column(name = "partner_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp partnerCreateTimestamp;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "partner_locality")
    private Locality locality;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "partner_type")
    private PartnerType partnerType;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Lead lead;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "partner_status_id", referencedColumnName = "profile_status_id")
    private PartnerProfileStatus partnerprofilestatus;

    @UpdatedTimestamp
    @Column(name = "partner_update_timestamp", columnDefinition = "timestamp")
    private Timestamp partnerUpdateTimestamp;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<PartnerToCandidate> partnerToCandidateList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplicationList;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "CompanyId", referencedColumnName = "CompanyId")
    private Company company;

    public static Finder<String, Partner> find = new Finder(Partner.class);

    public Partner() {
        this.partnerUUId = UUID.randomUUID().toString();
        this.partnerCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public void registerPartner() {
        Logger.info("inside registerPartner(), Partner registered/saved" );
        this.save();
    }
    public void partnerUpdate() {
        Logger.info("inside partnerUpdate(), partner updated" );
        this.partnerUpdateTimestamp = new Timestamp(System.currentTimeMillis());
        this.update();
    }

    public PartnerType getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public PartnerProfileStatus getPartnerprofilestatus() {
        return partnerprofilestatus;
    }

    public void setPartnerprofilestatus(PartnerProfileStatus partnerprofilestatus) {
        this.partnerprofilestatus = partnerprofilestatus;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public String getPartnerUUId() {
        return partnerUUId;
    }

    public void setPartnerUUId(String partnerUUId) {
        this.partnerUUId = partnerUUId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerFirstName() {
        return partnerFirstName;
    }

    public void setPartnerFirstName(String partnerFirstName) {
        this.partnerFirstName = partnerFirstName;
    }

    public String getPartnerLastName() {
        return partnerLastName;
    }

    public void setPartnerLastName(String partnerLastName) {
        this.partnerLastName = partnerLastName;
    }

    public String getPartnerMobile() {
        return partnerMobile;
    }

    public void setPartnerMobile(String partnerMobile) {
        this.partnerMobile = partnerMobile;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getPartnerCompany() {
        return partnerCompany;
    }

    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }

    public List<JobApplication> getJobApplicationList() {
        return jobApplicationList;
    }

    public void setJobApplicationList(List<JobApplication> jobApplicationList) {
        this.jobApplicationList = jobApplicationList;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}