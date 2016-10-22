package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 11/10/16.
 */

@Entity(name = "profile_requirement")
@Table(name = "profile_requirement")
public class ProfileRequirement extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "profile_requirement_id", columnDefinition = "bigint unsigned", unique = true)
    private long profileRequirementId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp creationTimestamp;

    @Column(name = "profile_requirement_title", columnDefinition = "varchar(255) null")
    private String profileRequirementTitle;

    public ProfileRequirement(){
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Model.Finder<String, ProfileRequirement> find = new Model.Finder(ProfileRequirement.class);

    public long getProfileRequirementId() {
        return profileRequirementId;
    }

    public void setProfileRequirementId(long profileRequirementId) {
        this.profileRequirementId = profileRequirementId;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getProfileRequirementTitle() {
        return profileRequirementTitle;
    }

    public void setProfileRequirementTitle(String profileRequirementTitle) {
        this.profileRequirementTitle = profileRequirementTitle;
    }
}
