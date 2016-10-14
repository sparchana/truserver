package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Partner;

import javax.persistence.*;
import java.util.List;

/**
 * Created by adarsh on 10/9/16.
 */
@Entity(name = "partner_profile_status")
@Table(name = "partner_profile_status")
public class PartnerProfileStatus  extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "profile_status_id", columnDefinition = "int signed", unique = true)
    private int  profileStatusId;

    @Column(name = "profile_status_name", columnDefinition = "varchar(255) null")
    private String profileStatusName;

    @JsonBackReference
    @OneToMany(mappedBy = "partnerprofilestatus")
    private List<Partner> partnerList;

    public static Model.Finder<String, PartnerProfileStatus> find = new Model.Finder(PartnerProfileStatus.class);

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

    public List<Partner> getPartnerList() {
        return partnerList;
    }

    public void setPartnerList(List<Partner> partnerList) {
        this.partnerList = partnerList;
    }
}
