package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 19/11/16.
 */

@Entity(name = "reject_reason")
@Table(name = "reject_reason")
public class RejectReason extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ReasonId", columnDefinition = "int unsigned", unique = true)
    private long reasonId;

    @Column(name = "ReasonName", columnDefinition = "text null")
    private String reasonName;

    public static Finder<String, RejectReason> find = new Finder(RejectReason.class);

    public long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
}
