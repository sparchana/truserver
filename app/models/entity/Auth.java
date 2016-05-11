package models.entity;

import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 26/4/16.
 */

@Entity(name = "auth")
@Table(name = "auth")
public class Auth extends Model {
    @Id
    @Column(name = "AuthId", columnDefinition = "bigint signed not null")
    public long authId = 0;

    @Column(name = "CandidateId", columnDefinition = "bigint signed not null")
    public long candidateId = 0;

    @Column(name = "AuthStatus", columnDefinition = "int signed not null", nullable = false)
    public int authStatus = 0; // verified, Not-Yet-Verified

    @Column(name = "PasswordMd5", columnDefinition = "char(60) not null")
    public String passwordMd5 = "";

    @Column(name = "PasswordSalt", columnDefinition = "bigint signed not null")
    public long passwordSalt = 0;

    @Column(name = "AuthSessionId", columnDefinition = "varchar(50) not null", nullable = false)
    public String authSessionId = "";

    @Column(name = "AuthSessionIdExpiryMillis", columnDefinition = "bigint signed not null", nullable = false)
    public long authSessionIdExpiryMillis = 0;

    @Column(name = "authCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp authCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "authUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp authUpdateTimestamp;

    public static Model.Finder<String, Auth> find = new Model.Finder(Auth.class);

    public static void savePassword(Auth auth) {
        auth.save();
        Logger.info("Password Saved!");

    }

    public static void updatePassword(Auth auth) {
        Logger.info("inside Auth Update method" );
        auth.update();
    }
}