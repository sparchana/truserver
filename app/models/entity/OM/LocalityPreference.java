package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Locality;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "localitypreference")
@Table(name = "localitypreference")
public class LocalityPreference extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LocalityPreferenceId", columnDefinition = "bigint signed not null", unique = true)
    private long localityPreferenceId;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    private Locality locality;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    private Candidate candidate;

    public static Finder<String, LocalityPreference> find = new Finder(LocalityPreference.class);

    public long getLocalityPreferenceId() {
        return localityPreferenceId;
    }

    public void setLocalityPreferenceId(long localityPreferenceId) {
        this.localityPreferenceId = localityPreferenceId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
