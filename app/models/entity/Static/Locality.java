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
    @Column(name = "LocalityId", columnDefinition = "bigint signed null", unique = true)
    public long localityId = 0;

    @Column(name = "LocalityName", columnDefinition = "varchar(255) null")
    public String localityName  = "";

    @Column(name = "City", columnDefinition = "varchar(255) null")
    public String city  = "";

    @Column(name = "State", columnDefinition = "varchar(255) null")
    public String state  = "";

    @Column(name = "Country", columnDefinition = "varchar(255) null")
    public String country  = "";

    @Column(name = "Latitude", columnDefinition = "double(10,6) null")
    public double lat  = 0;

    @Column(name = "Longitude", columnDefinition = "double(10,6) null")
    public double lng  = 0;

    @JsonBackReference
    @OneToMany(mappedBy = "locality", cascade = CascadeType.REMOVE)
    public List<LocalityPreference> localityPreferenceList;

    @JsonBackReference
    @OneToMany(mappedBy = "candidateCurrentJobLocation", cascade = CascadeType.REMOVE)
    public List<CandidateCurrentJobDetail> currentJobDetailList;

    public static Finder<String, Locality> find = new Finder(Locality.class);
}
