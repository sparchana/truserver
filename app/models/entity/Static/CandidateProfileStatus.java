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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ProfileStatusId", columnDefinition = "int signed", unique = true)
    private int  profileStatusId;

    @Column(name = "ProfileStatusName", columnDefinition = "varchar(255) null")
    private String profileStatusName;

    @JsonBackReference
    @OneToMany(mappedBy = "candidateprofilestatus")
    private List<Candidate> candidateList;

    public void setProfileStatusId(int profileStatusId) {
        this.profileStatusId = profileStatusId;
    }

    public void setProfileStatusName(String profileStatusName) {
        this.profileStatusName = profileStatusName;
    }

    public static Model.Finder<String, CandidateProfileStatus> find = new Model.Finder(CandidateProfileStatus.class);

    public int getProfileStatusId() {
        return profileStatusId;
    }

    public String getProfileStatusName() {
        return profileStatusName;
    }

    public List<Candidate> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }
}
