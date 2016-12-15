package models.entity.scheduler;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 12/12/16.
 */

@Entity(name = "scheduler_stats")
@Table(name = "scheduler_stats")
public class SchedulerStats extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint signed")
    private long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "scheduler_type_id", referencedColumnName = "scheduler_type_id")
    private SchedulerType schedulerType;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "scheduler_sub_type_id", referencedColumnName = "scheduler_sub_type_id")
    private SchedulerSubType schedulerSubType;

    @Column(name = "note", columnDefinition = "text null")
    private String note;

    @Column(name = "completion_status", columnDefinition = "tinyint(1)", nullable = false)
    private boolean completionStatus;

    @Column(name = "start_timestamp", columnDefinition = "timestamp null")
    private Timestamp startTimestamp;

    @Column(name = "end_timestamp", columnDefinition = "timestamp null")
    private Timestamp endTimestamp;

    public static Model.Finder<String, SchedulerStats> find = new Model.Finder(SchedulerStats.class);

    public long getId() {
        return id;
    }

    public SchedulerType getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(SchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    public SchedulerSubType getSchedulerSubType() {
        return schedulerSubType;
    }

    public void setSchedulerSubType(SchedulerSubType schedulerSubType) {
        this.schedulerSubType = schedulerSubType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(boolean completionStatus) {
        this.completionStatus = completionStatus;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
