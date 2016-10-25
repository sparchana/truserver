package models.entity.Recruiter;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.util.Util;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 4/10/16.
 */

@Entity(name = "recruiter_auth")
@Table(name = "recruiter_auth")

public class RecruiterAuth extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_auth_id", columnDefinition = "bigint signed")
    private long recruiterAuthId;

    @JsonBackReference
    @JoinColumn(name = "recruiter_id", referencedColumnName = "recruiterProfileId")
    @OneToOne
    private RecruiterProfile recruiterId;

    @Column(name = "recruiter_auth_status", columnDefinition = "int signed not null", nullable = false)
    private int recruiterAuthStatus; // verified, Not-Yet-Verified

    @Column(name = "password_md5", columnDefinition = "char(60) not null")
    private String passwordMd5;

    @Column(name = "password_salt", columnDefinition = "bigint signed not null")
    private long passwordSalt;

    @Column(name = "auth_session_id", columnDefinition = "varchar(50)", nullable = false)
    private String authSessionId;

    @Column(name = "auth_session_id_expiry_millis", columnDefinition = "bigint signed", nullable = false)
    private long authSessionIdExpiryMillis;

    @Column(name = "auth_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp authCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "auth_update_timestamp", columnDefinition = "timestamp")
    private Timestamp authUpdateTimestamp;

    public static Model.Finder<String, RecruiterAuth> find = new Model.Finder(RecruiterAuth.class);

    public RecruiterAuth() {
        this.authCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authUpdateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authSessionId = UUID.randomUUID().toString();
        this.passwordSalt = Util.randomInt();
    }

    public static void savePassword(RecruiterAuth auth) {
        auth.save();
        Logger.info("Password Saved!");

    }

    public long getRecruiterAuthId() {
        return recruiterAuthId;
    }

    public void setRecruiterAuthId(long recruiterAuthId) {
        this.recruiterAuthId = recruiterAuthId;
    }

    public RecruiterProfile getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(RecruiterProfile recruiterId) {
        this.recruiterId = recruiterId;
    }

    public int getRecruiterAuthStatus() {
        return recruiterAuthStatus;
    }

    public void setRecruiterAuthStatus(int recruiterAuthStatus) {
        this.recruiterAuthStatus = recruiterAuthStatus;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    public long getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(long passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getAuthSessionId() {
        return authSessionId;
    }

    public void setAuthSessionId(String authSessionId) {
        this.authSessionId = authSessionId;
    }

    public long getAuthSessionIdExpiryMillis() {
        return authSessionIdExpiryMillis;
    }

    public void setAuthSessionIdExpiryMillis(long authSessionIdExpiryMillis) {
        this.authSessionIdExpiryMillis = authSessionIdExpiryMillis;
    }

    public Timestamp getAuthCreateTimestamp() {
        return authCreateTimestamp;
    }

    public void setAuthCreateTimestamp(Timestamp authCreateTimestamp) {
        this.authCreateTimestamp = authCreateTimestamp;
    }

    public Timestamp getAuthUpdateTimestamp() {
        return authUpdateTimestamp;
    }

    public void setAuthUpdateTimestamp(Timestamp authUpdateTimestamp) {
        this.authUpdateTimestamp = authUpdateTimestamp;
    }
}