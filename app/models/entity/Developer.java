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

    @Column(name = "DeveloperApiKey", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String developerApiKey =  UUID.randomUUID().toString();

    public static Finder<String, Developer> find = new Finder(Developer.class);

}
