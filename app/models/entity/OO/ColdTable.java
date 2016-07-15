package models.entity.OO;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 15/7/16.
 */
@Entity(name = "coldtable")
@Table(name = "coldtable")
public class ColdTable  extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ColdTableId", columnDefinition = "int signed", unique = true)
    private int coldTableId;

    @Column(name = "CreateTimeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @Column(name = "Reason", columnDefinition = "text null")
    private String reason;

    @Column(name = "Duration", columnDefinition = "int signed null")
    private Integer duration; // in days

    public ColdTable(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, ColdTable> find = new Finder(ColdTable.class);

    /* getter */
    public int getColdTableId() {
        return coldTableId;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getReason() {
        return reason;
    }

    public Integer getDuration() {
        return duration;
    }

    /* setter */
    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
