package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Candidate;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 5/5/16.
 */
@Entity(name = "candidateProfileStatus")
@Table(name = "candidateprofilestatus")
public class CandidateProfileStatus  extends Model {
    @Id
    @Column(name = "ProfileStatusId", columnDefinition = "int signed null", unique = true)
    public int  profileStatusId = 0;

    @Column(name = "ProfileStatusName", columnDefinition = "varchar(255) null")
    public String profileStatusName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "candidateprofilestatus")
    public List<Candidate> candidateList;

    public void setProfileStatusId(int profileStatusId) {
        this.profileStatusId = profileStatusId;
    }

    public void setProfileStatusName(String profileStatusName) {
        this.profileStatusName = profileStatusName;
    }

    public static Model.Finder<String, CandidateProfileStatus> find = new Model.Finder(CandidateProfileStatus.class);
}
