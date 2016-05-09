package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Locality;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "localitypreference")
@Table(name = "localitypreference")
public class LocalityPreference extends Model {
    @Id
    @Column(name = "LocalityPreferenceId", columnDefinition = "bigint signed not null", unique = true)
    public long localityPreferenceId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    public Locality locality;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    public Candidate candidate;

    public static Finder<String, LocalityPreference> find = new Finder(LocalityPreference.class);

    public long getLocalityPreferenceId() {
        return localityPreferenceId;
    }

    public void setLocalityPreferenceId(long localityPreferenceId) {
        this.localityPreferenceId = localityPreferenceId;
    }

    public long getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }
}
