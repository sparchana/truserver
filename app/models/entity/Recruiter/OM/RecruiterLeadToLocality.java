package models.entity.Recruiter.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.Locality;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by dodo on 5/10/16.
 */

@Entity(name = "recruiterleadtolocality")
@Table(name = "recruiterleadtolocality")

public class RecruiterLeadToLocality extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_lead_to_locality_id", columnDefinition = "bigint signed", unique = true)
    private Long recruiterLeadToLocalityId;

    @Column(name = "recruiter_lead_to_locality_uuid", columnDefinition = "varchar(255) not null", unique = true)
    private String recruiterLeadToLocalityUUID;

    @CreatedTimestamp
    @Column(name = "recruiter_lead_to_locality_create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterLeadToLocalityCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "recruiter_lead_to_locality_update_timeStamp", columnDefinition = "timestamp")
    private Timestamp recruiterLeadToLocalityUpdateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    private Locality locality;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recruiter_lead_to_job_role_id", referencedColumnName = "recruiter_lead_to_job_role_id")
    private RecruiterLeadToJobRole recruiterLeadToJobRole;

    public static Finder<String, RecruiterLeadToLocality> find = new Finder(RecruiterLeadToLocality.class);

    public RecruiterLeadToLocality(){
        this.recruiterLeadToLocalityUUID = UUID.randomUUID().toString();
    }

    public Long getRecruiterLeadToLocalityId() {
        return recruiterLeadToLocalityId;
    }

    public void setRecruiterLeadToLocalityId(Long recruiterLeadToLocalityId) {
        this.recruiterLeadToLocalityId = recruiterLeadToLocalityId;
    }

    public Timestamp getRecruiterLeadToLocalityCreateTimeStamp() {
        return recruiterLeadToLocalityCreateTimeStamp;
    }

    public void setRecruiterLeadToLocalityCreateTimeStamp(Timestamp recruiterLeadToLocalityCreateTimeStamp) {
        this.recruiterLeadToLocalityCreateTimeStamp = recruiterLeadToLocalityCreateTimeStamp;
    }

    public Timestamp getRecruiterLeadToLocalityUpdateTimeStamp() {
        return recruiterLeadToLocalityUpdateTimeStamp;
    }

    public void setRecruiterLeadToLocalityUpdateTimeStamp(Timestamp recruiterLeadToLocalityUpdateTimeStamp) {
        this.recruiterLeadToLocalityUpdateTimeStamp = recruiterLeadToLocalityUpdateTimeStamp;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public String getRecruiterLeadToLocalityUUID() {
        return recruiterLeadToLocalityUUID;
    }

    public RecruiterLeadToJobRole getRecruiterLeadToJobRole() {
        return recruiterLeadToJobRole;
    }

    public void setRecruiterLeadToJobRole(RecruiterLeadToJobRole recruiterLeadToJobRole) {
        this.recruiterLeadToJobRole = recruiterLeadToJobRole;
    }

    public List<RecruiterLeadToLocality> readById(List<Long> ids) {
        return RecruiterLeadToLocality.find.where().idIn(ids).setUseCache(Boolean.TRUE).findList();
    }

    public List<RecruiterLeadToLocality> readByUUID(List<Long> uuids) {
        return RecruiterLeadToLocality.find.where().in("recruiter_lead_to_locality_uuid",uuids).setUseCache(Boolean.TRUE).findList();
    }

}

