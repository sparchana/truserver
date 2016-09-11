package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import controllers.businessLogic.InteractionService;
import models.util.Util;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by adarsh on 9/9/16.
 */

@Entity(name = "partner_auth")
@Table(name = "partner_auth")
public class PartnerAuth extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_auth_id", columnDefinition = "bigint signed")
    private long partnerAuthId;

    @Column(name = "partner_id", columnDefinition = "bigint signed not null")
    private long partnerId;

    @Column(name = "partner_auth_status", columnDefinition = "int signed not null", nullable = false)
    private int partnerAuthStatus; // verified, Not-Yet-Verified

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

    public static Model.Finder<String, PartnerAuth> find = new Model.Finder(PartnerAuth.class);

    public PartnerAuth() {
        this.authCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authUpdateTimestamp = new Timestamp(System.currentTimeMillis());
        this.authSessionId = UUID.randomUUID().toString();
        this.passwordSalt = Util.randomInt();
    }

    public static void savePassword(PartnerAuth auth) {
        auth.save();
        Logger.info("Password Saved!");

    }

    public static void updatePassword(PartnerAuth auth) {
        Logger.info("inside Auth Update method" );
        auth.update();
    }


    public long getPartnerAuthId() {
        return partnerAuthId;
    }

    public void setPartnerAuthId(long partnerAuthId) {
        this.partnerAuthId = partnerAuthId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public int getPartnerAuthStatus() {
        return partnerAuthStatus;
    }

    public void setPartnerAuthStatus(int partnerAuthStatus) {
        this.partnerAuthStatus = partnerAuthStatus;
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

}
