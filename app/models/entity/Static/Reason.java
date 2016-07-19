package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by zero on 18/7/16.
 */
@Entity(name = "reason")
@Table(name = "reason")
public class Reason extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ReasonId", columnDefinition = "int unsigned", unique = true)
    private long reasonId;

    @Column(name = "ReasonName", columnDefinition = "text null")
    private String reasonName;

    public static Finder<String, Reason> find = new Finder(Reason.class);

    public long getReasonId() {
        return reasonId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
}
