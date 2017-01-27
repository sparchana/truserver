package models.entity.truly;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 27/1/17.
 *
 */

@Entity(name = "truly")
@Table(name = "truly")
public class Truly extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "truly_id", columnDefinition = "int unsigned", unique = true)
    private int trulyId;

    @Column(name = "longUrl", columnDefinition = "text not null", unique = true)
    private String longUrl;

    @Column(name = "shortUrl", columnDefinition = "varchar(255) not null", unique = true)
    private String shortUrl;

    @Column(name = "hash", columnDefinition = "varchar(255) not null", unique = true)
    private String hash;

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp not null")
    private Timestamp createTimestamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp")
    private Timestamp updateTimestamp;

    @Column(name = "truly_access_level", columnDefinition = "int(1) signed not null default 0")
    private int trulyAccessLevel;

    @Column(name = "hit_rate", columnDefinition = "bigint unsigned not null default 0")
    private long hitRate;

    public static Model.Finder<String, Truly> find = new Model.Finder(Truly.class);

    public int getTrulyId() {
        return trulyId;
    }

    public void setTrulyId(int trulyId) {
        this.trulyId = trulyId;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public int getTrulyAccessLevel() {
        return trulyAccessLevel;
    }

    public void setTrulyAccessLevel(int trulyAccessLevel) {
        this.trulyAccessLevel = trulyAccessLevel;
    }

    public long getHitRate() {
        return hitRate;
    }

    public void setHitRate(long hitRate) {
        this.hitRate = hitRate;
    }
}
