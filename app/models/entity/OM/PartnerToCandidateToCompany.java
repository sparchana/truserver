package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Partner;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 23/1/17.
 */

@Entity(name = "partner_to_candidate_to_company")
@Table(name = "partner_to_candidate_to_company")
public class PartnerToCandidateToCompany extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_to_candidate_to_company_id", columnDefinition = "bigint unsigned", unique = true)
    private long partnerToCandidateToCompanyId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "partner_id", referencedColumnName = "partner_id")
    private Partner partner;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "partner_to_candidate_id", referencedColumnName = "partner_to_candidate_id")
    private PartnerToCandidate partnerToCandidate;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "partner_to_company_id", referencedColumnName = "partner_to_company_id")
    private PartnerToCompany partnerToCompany;

    public PartnerToCandidateToCompany() {
        this.creationTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, PartnerToCandidateToCompany> find = new Finder(PartnerToCandidateToCompany.class);

    public long getPartnerToCandidateToCompanyId() {
        return partnerToCandidateToCompanyId;
    }

    public void setPartnerToCandidateToCompanyId(long partnerToCandidateToCompanyId) {
        this.partnerToCandidateToCompanyId = partnerToCandidateToCompanyId;
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

    public PartnerToCandidate getPartnerToCandidate() {
        return partnerToCandidate;
    }

    public void setPartnerToCandidate(PartnerToCandidate partnerToCandidate) {
        this.partnerToCandidate = partnerToCandidate;
    }

    public PartnerToCompany getPartnerToCompany() {
        return partnerToCompany;
    }

    public void setPartnerToCompany(PartnerToCompany partnerToCompany) {
        this.partnerToCompany = partnerToCompany;
    }
}