package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.LocalityPreference;
import models.entity.OO.CandidateCurrentJobDetail;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */

// static table
@Entity(name = "locality")
@Table(name = "locality")
public class Locality extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LocalityId", columnDefinition = "bigint signed", unique = true)
    private long localityId;

    @Column(name = "LocalityName", columnDefinition = "varchar(255) null")
    private String localityName;

    @Column(name = "City", columnDefinition = "varchar(255) null")
    private String city;

    @Column(name = "State", columnDefinition = "varchar(255) null")
    private String state;

    @Column(name = "Country", columnDefinition = "varchar(255) null")
    private String country;

    @Column(name = "Latitude", columnDefinition = "double(10,6) null")
    private Double lat;

    @Column(name = "Longitude", columnDefinition = "double(10,6) null")
    private Double lng;

    @JsonBackReference
    @OneToMany(mappedBy = "locality", cascade = CascadeType.REMOVE)
    private List<LocalityPreference> localityPreferenceList;

    @JsonBackReference
    @OneToMany(mappedBy = "candidateCurrentJobLocation", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CandidateCurrentJobDetail> currentJobDetailList;

    public static Finder<String, Locality> find = new Finder(Locality.class);

    public long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<LocalityPreference> getLocalityPreferenceList() {
        return localityPreferenceList;
    }

    public void setLocalityPreferenceList(List<LocalityPreference> localityPreferenceList) {
        this.localityPreferenceList = localityPreferenceList;
    }

    public List<CandidateCurrentJobDetail> getCurrentJobDetailList() {
        return currentJobDetailList;
    }

    public void setCurrentJobDetailList(List<CandidateCurrentJobDetail> currentJobDetailList) {
        this.currentJobDetailList = currentJobDetailList;
    }
}
