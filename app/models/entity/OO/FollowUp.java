package models.entity.OO;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 16/6/16.
 */
@Entity
@Table(name="followup")
public class FollowUp extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "followUpId", columnDefinition = "int signed", unique = true)
    private int followUpId;

    @Column(name = "followUpMobile", columnDefinition = "varchar(13)", nullable = false)
    private String followUpMobile;

    @Column(name = "followUpStatus", columnDefinition = "tinyint(1)", nullable = false)
    private boolean followUpStatus;

    @Column(name = "FollowUpTimeStamp", columnDefinition = "timestamp null")
    private Timestamp followUpTimeStamp;

    @Column(name = "followUpCreationTimeStamp", columnDefinition = "timestamp not null")
    private Timestamp followUpCreationTimeStamp;

    @Column(name = "followUpUpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp followUpUpdateTimeStamp;

    public static Finder<String, FollowUp> find = new Finder(FollowUp.class);

    public FollowUp(){
        this.followUpCreationTimeStamp = new Timestamp(System.currentTimeMillis());
        this.followUpStatus = false;
        this.followUpUpdateTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public int getFollowUpId() {
        return followUpId;
    }

    public void setFollowUpId(int followUpId) {
        this.followUpId = followUpId;
    }

    public String getFollowUpMobile() {
        return followUpMobile;
    }

    public void setFollowUpMobile(String followUpMobile) {
        this.followUpMobile = followUpMobile;
    }

    public boolean isFollowUpStatusRequired() {
        return followUpStatus;
    }

    public void setFollowUpStatus(boolean followUpStatus) {
        this.followUpStatus = followUpStatus;
    }

    public Timestamp getFollowUpTimeStamp() {
        return followUpTimeStamp;
    }

    public void setFollowUpTimeStamp(Timestamp followUpTimeStamp) {
        this.followUpTimeStamp = followUpTimeStamp;
    }

    public Timestamp getFollowUpCreationTimeStamp() {
        return followUpCreationTimeStamp;
    }

    public void setFollowUpCreationTimeStamp(Timestamp followUpCreationTimeStamp) {
        this.followUpCreationTimeStamp = followUpCreationTimeStamp;
    }

    public boolean isFollowUpStatus() {
        return followUpStatus;
    }

    public Timestamp getFollowUpUpdateTimeStamp() {
        return followUpUpdateTimeStamp;
    }

    public void setFollowUpUpdateTimeStamp(Timestamp followUpUpdateTimeStamp) {
        this.followUpUpdateTimeStamp = followUpUpdateTimeStamp;
    }


}
