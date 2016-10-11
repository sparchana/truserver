package models.entity.Recruiter.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Recruiter.RecruiterProfile;

import javax.persistence.*;
import java.util.List;

/**
 * Created by dodo on 5/10/16.
 */

@Entity(name = "recruiter_profile_status")
@Table(name = "recruiter_profile_status")
public class RecruiterProfileStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "profile_status_id", columnDefinition = "int signed", unique = true)
    private int  profileStatusId;

    @Column(name = "profile_status_name", columnDefinition = "varchar(255) null")
    private String profileStatusName;

    @JsonBackReference
    @OneToMany(mappedBy = "recruiterprofilestatus")
    private List<RecruiterProfile> recruiterList;

    public static Model.Finder<String, RecruiterProfileStatus> find = new Model.Finder(RecruiterProfileStatus.class);

    public int getProfileStatusId() {
        return profileStatusId;
    }

    public void setProfileStatusId(int profileStatusId) {
        this.profileStatusId = profileStatusId;
    }

    public String getProfileStatusName() {
        return profileStatusName;
    }

    public void setProfileStatusName(String profileStatusName) {
        this.profileStatusName = profileStatusName;
    }

    public List<RecruiterProfile> getRecruiterList() {
        return recruiterList;
    }

    public void setRecruiterList(List<RecruiterProfile> recruiterList) {
        this.recruiterList = recruiterList;
    }
}

