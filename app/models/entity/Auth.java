package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import models.util.Util;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by batcoder1 on 26/4/16.
 */

@Entity(name = "auth")
@Table(name = "auth")
public class Auth extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "AuthId", columnDefinition = "bigint signed")
    private long authId;

    @Column(name = "CandidateId", columnDefinition = "bigint signed not null")
    private long candidateId;

    @Column(name = "AuthStatus", columnDefinition = "int signed not null", nullable = false)
    private int authStatus; // verified, Not-Yet-Verified

    @Column(name = "PasswordMd5", columnDefinition = "char(60) not null")
    private String passwordMd5;

    @Column(name = "PasswordSalt", columnDefinition = "bigint signed not null")
    private long passwordSalt;

    @Column(name = "AuthSessionId", columnDefinition = "varchar(50)", nullable = false)
    private String authSessionId ;

    @Column(name = "AuthSessionIdExpiryMillis", columnDefinition = "bigint signed", nullable = false)
    private long authSessionIdExpiryMillis;

    @Column(name = "authCreateTimestamp", columnDefinition = "timestamp not null")
    private Timestamp authCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "authUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp authUpdateTimestamp;

    public static Model.Finder<String, Auth> find = new Model.Finder(Auth.class);

    public Auth(){
        this.authCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authUpdateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authSessionId = UUID.randomUUID().toString();
        this.passwordSalt = Util.randomInt();
    }

    public static void savePassword(Auth auth) {
        auth.save();
        Logger.info("Password Saved!");

    }

    public static void updatePassword(Auth auth) {
        Logger.info("inside Auth Update method" );
        auth.update();
    }

    public long getAuthId() {
        return authId;
    }

    public void setAuthId(long authId) {
        this.authId = authId;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
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

    public int getAuthIsMobileVerified() {
        return authIsMobileVerified;
    }

    public void setAuthIsMobileVerified(int authIsMobileVerified) {
        this.authIsMobileVerified = authIsMobileVerified;
    }
}