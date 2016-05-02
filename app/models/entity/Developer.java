package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "developer")
@Table(name = "developer")
public class Developer extends Model {
    @Id
    @Column(name = "DeveloperId", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    public long developerId = 0;

    @Column(name = "DeveloperName", columnDefinition = "varchar(50) not null", nullable = false)
    public String developerName = "";

    @Column(name = "DeveloperAccessLevel", columnDefinition = "int not null", nullable = false)
    public int developerAccessLevel = 0;

    @Column(name = "DeveloperPasswordSalt", columnDefinition = "bigint signed not null", nullable = false)
    public long developerPasswordSalt = 0;

    @Column(name = "DeveloperPasswordMd5", columnDefinition = "char(32) not null", nullable = false)
    public String developerPasswordMd5 = "";

    @Column(name = "DeveloperSessionId", columnDefinition = "varchar(50) not null", nullable = false)
    public String developerSessionId = "";

    @Column(name = "DeveloperSessionIdExpiryMillis", columnDefinition = "bigint signed not null", nullable = false)
    public long developerSessionIdExpiryMillis = 0;

    @Column(name = "DeveloperApiKey", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String developerApiKey =  UUID.randomUUID().toString();

    public static Finder<String, Developer> find = new Finder(Developer.class);

    public void setDeveloperSessionIdExpiryMillis(long developerSessionIdExpiryMillis) {
        this.developerSessionIdExpiryMillis = developerSessionIdExpiryMillis;
    }

    public void setDeveloperSessionId(String developerSessionId) {
        this.developerSessionId = developerSessionId;
    }
}
