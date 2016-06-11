package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "developer")
@Table(name = "developer")
public class Developer extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "DeveloperId", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    private long developerId = 0;

    @Column(name = "DeveloperName", columnDefinition = "varchar(50) not null", nullable = false)
    private String developerName = "";

    @Column(name = "DeveloperAccessLevel", columnDefinition = "int not null", nullable = false)
    private int developerAccessLevel = 0;

    @Column(name = "DeveloperPasswordSalt", columnDefinition = "bigint signed not null", nullable = false)
    private long developerPasswordSalt = 0;

    @Column(name = "DeveloperPasswordMd5", columnDefinition = "char(32) not null", nullable = false)
    private String developerPasswordMd5 = "";

    @Column(name = "DeveloperSessionId", columnDefinition = "varchar(50) null")
    private String developerSessionId;

    @Column(name = "DeveloperSessionIdExpiryMillis", columnDefinition = "bigint signed")
    private Long developerSessionIdExpiryMillis;

    @Column(name = "DeveloperApiKey", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String developerApiKey =  UUID.randomUUID().toString();

    public static Finder<String, Developer> find = new Finder(Developer.class);

    public void setDeveloperSessionIdExpiryMillis(Long developerSessionIdExpiryMillis) {
        this.developerSessionIdExpiryMillis = developerSessionIdExpiryMillis;
    }

    public void setDeveloperSessionId(String developerSessionId) {
        this.developerSessionId = developerSessionId;
    }

    public long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(long developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public int getDeveloperAccessLevel() {
        return developerAccessLevel;
    }

    public void setDeveloperAccessLevel(int developerAccessLevel) {
        this.developerAccessLevel = developerAccessLevel;
    }

    public long getDeveloperPasswordSalt() {
        return developerPasswordSalt;
    }

    public void setDeveloperPasswordSalt(long developerPasswordSalt) {
        this.developerPasswordSalt = developerPasswordSalt;
    }

    public String getDeveloperPasswordMd5() {
        return developerPasswordMd5;
    }

    public void setDeveloperPasswordMd5(String developerPasswordMd5) {
        this.developerPasswordMd5 = developerPasswordMd5;
    }

    public String getDeveloperSessionId() {
        return developerSessionId;
    }

    public Long getDeveloperSessionIdExpiryMillis() {
        return developerSessionIdExpiryMillis;
    }

    public String getDeveloperApiKey() {
        return developerApiKey;
    }

    public void setDeveloperApiKey(String developerApiKey) {
        this.developerApiKey = developerApiKey;
    }
}
