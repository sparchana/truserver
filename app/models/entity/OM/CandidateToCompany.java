package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Company;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 17/1/17.
 */
@Entity(name = "candidate_to_company")
@Table(name = "candidate_to_company")
public class CandidateToCompany extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_to_company_id", columnDefinition = "int signed", unique = true)
    private Integer candidateToCompanyId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompanyId", referencedColumnName = "CompanyId")
    private Company company;

    public static Model.Finder<String, CandidateToCompany> find = new Model.Finder(CandidateToCompany.class);

    public CandidateToCompany() {
        this.creationTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public Integer getCandidateToCompanyId() {
        return candidateToCompanyId;
    }

    public void setCandidateToCompanyId(Integer candidateToCompanyId) {
        this.candidateToCompanyId = candidateToCompanyId;
    }

    public Timestamp getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Timestamp creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}