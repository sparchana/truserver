package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Company;
import models.entity.Partner;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 21/1/17.
 */

@Entity(name = "partner_to_company")
@Table(name = "partner_to_company")
public class PartnerToCompany extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "partner_to_company_id", columnDefinition = "bigint signed", unique = true)
    private Long partnerToCompanyId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "partner_id", referencedColumnName= "partner_id")
    private Partner partner;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompanyId", referencedColumnName= "CompanyId")
    private Company company;

    @Column(name = "verification_status", columnDefinition = "int(2) signed not null default 0")
    private int verification_status;

    public PartnerToCompany(){
        this.creationTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, PartnerToCompany> find = new Finder(PartnerToCompany.class);

    public Long getPartnerToCompanyId() {
        return partnerToCompanyId;
    }

    public void setPartnerToCompanyId(Long partnerToCompanyId) {
        this.partnerToCompanyId = partnerToCompanyId;
    }

    public Timestamp getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Timestamp creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getVerification_status() {
        return verification_status;
    }

    public void setVerification_status(int verification_status) {
        this.verification_status = verification_status;
    }
}