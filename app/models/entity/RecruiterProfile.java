package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.RecruiterStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "recruiterprofile")
@Table(name = "recruiterprofile")
public class RecruiterProfile extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "RecruiterProfileId", columnDefinition = "bigint signed", unique = true)
    private Long recruiterProfileId;

    @Column(name = "RecruiterProfileUUId", columnDefinition = "varchar(255) not null")
    private String recruiterProfileUUId;

    @Column(name = "RecruiterProfileName", columnDefinition = "varchar(50) not null")
    private String recruiterProfileName;

    @Column(name = "RecruiterProfileMobile", columnDefinition = "varchar(13) not null")
    private String recruiterProfileMobile;

    @Column(name = "RecruiterProfileLandline", columnDefinition = "varchar(13) not null")
    private String recruiterProfileLandline;

    @Column(name = "RecruiterProfilePin", columnDefinition = "int signed null")
    private Long recruiterProfilePin;

    @Column(name = "RecruiterProfileEmail", columnDefinition = "varchar(255) null")
    private String recruiterProfileEmail;

    @Column(name = "RecruiterProfileCreateTimestamp", columnDefinition = "timestamp not null")
    private Timestamp recruiterProfileCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "RecruiterProfileUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp recruiterProfileUpdateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "RecStatus")
    private RecruiterStatus recStatus;

    public RecruiterProfile() {
        this.recruiterProfileUUId = UUID.randomUUID().toString();
        this.recruiterProfileCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Long getRecruiterProfileId() {
        return recruiterProfileId;
    }

    public void setRecruiterProfileId(Long recruiterProfileId) {
        this.recruiterProfileId = recruiterProfileId;
    }

    public String getRecruiterProfileUUId() {
        return recruiterProfileUUId;
    }

    public void setRecruiterProfileUUId(String recruiterProfileUUId) {
        this.recruiterProfileUUId = recruiterProfileUUId;
    }

    public String getRecruiterProfileName() {
        return recruiterProfileName;
    }

    public void setRecruiterProfileName(String recruiterProfileName) {
        this.recruiterProfileName = recruiterProfileName;
    }

    public String getRecruiterProfileMobile() {
        return recruiterProfileMobile;
    }

    public void setRecruiterProfileMobile(String recruiterProfileMobile) {
        this.recruiterProfileMobile = recruiterProfileMobile;
    }

    public String getRecruiterProfileLandline() {
        return recruiterProfileLandline;
    }

    public void setRecruiterProfileLandline(String recruiterProfileLandline) {
        this.recruiterProfileLandline = recruiterProfileLandline;
    }

    public Long getRecruiterProfilePin() {
        return recruiterProfilePin;
    }

    public void setRecruiterProfilePin(Long recruiterProfilePin) {
        this.recruiterProfilePin = recruiterProfilePin;
    }

    public String getRecruiterProfileEmail() {
        return recruiterProfileEmail;
    }

    public void setRecruiterProfileEmail(String recruiterProfileEmail) {
        this.recruiterProfileEmail = recruiterProfileEmail;
    }

    public Timestamp getRecruiterProfileCreateTimestamp() {
        return recruiterProfileCreateTimestamp;
    }

    public void setRecruiterProfileCreateTimestamp(Timestamp recruiterProfileCreateTimestamp) {
        this.recruiterProfileCreateTimestamp = recruiterProfileCreateTimestamp;
    }

    public Timestamp getRecruiterProfileUpdateTimestamp() {
        return recruiterProfileUpdateTimestamp;
    }

    public void setRecruiterProfileUpdateTimestamp(Timestamp recruiterProfileUpdateTimestamp) {
        this.recruiterProfileUpdateTimestamp = recruiterProfileUpdateTimestamp;
    }

    public RecruiterStatus getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(RecruiterStatus recStatus) {
        this.recStatus = recStatus;
    }
}
