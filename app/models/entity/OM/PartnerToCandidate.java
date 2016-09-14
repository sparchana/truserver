package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Partner;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by adarsh on 14/9/16.
 */

@Entity(name = "partner_to_candidate")
@Table(name = "partner_to_candidate")
public class PartnerToCandidate extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "partner_to_candidate_id", columnDefinition = "bigint signed", unique = true)
    private Long partnerToCandidateId;

    @Column(name = "partner_to_candidate_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp partnerToCandidateCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "partner_to_candidate_update_timestamp", columnDefinition = "timestamp")
    private Timestamp partnerToCandidateUpdateTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "partner_id", referencedColumnName= "partner_id")
    private Partner partner;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Candidate candidate;

    public PartnerToCandidate(){
        this.partnerToCandidateCreateTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, PartnerToCandidate> find = new Finder(PartnerToCandidate.class);

    public Long getPartnerToCandidateId() {
        return partnerToCandidateId;
    }

    public void setPartnerToCandidateId(Long partnerToCandidateId) {
        this.partnerToCandidateId = partnerToCandidateId;
    }

    public Timestamp getPartnerToCandidateCreateTimeStamp() {
        return partnerToCandidateCreateTimeStamp;
    }

    public void setPartnerToCandidateCreateTimeStamp(Timestamp partnerToCandidateCreateTimeStamp) {
        this.partnerToCandidateCreateTimeStamp = partnerToCandidateCreateTimeStamp;
    }

    public Timestamp getPartnerToCandidateUpdateTimestamp() {
        return partnerToCandidateUpdateTimestamp;
    }

    public void setPartnerToCandidateUpdateTimestamp(Timestamp partnerToCandidateUpdateTimestamp) {
        this.partnerToCandidateUpdateTimestamp = partnerToCandidateUpdateTimestamp;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}

