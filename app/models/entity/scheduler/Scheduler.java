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

@Entity
@Table(name = "scheduler")
public class Scheduler extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint signed")
    private long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "scheduler_type_id", referencedColumnName = "scheduler_type_id")
    private SchedulerType schedulerTypeId;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "scheduler_sub_type_id", referencedColumnName = "scheduler_sub_type_id")
    private SchedulerSubType schedulerSubTypeId;

    @Column(name = "recipient", columnDefinition = "varchar(255)", nullable = false)
    private String recipient;

    @Column(name = "message", columnDefinition = "text null")
    private String message;

    @Column(name = "completion_status", columnDefinition = "tinyint(1)", nullable = false)
    private boolean completionStatus;

    @Column(name = "start_timestamp", columnDefinition = "timestamp null")
    private Timestamp startTimestamp;

    @Column(name = "end_timestamp", columnDefinition = "timestamp null")
    private Timestamp endTimestamp;

    public static Model.Finder<String, Scheduler> find = new Model.Finder(Scheduler.class);

    public long getId() {
        return id;
    }

    public SchedulerType getSchedulerTypeId() {
        return schedulerTypeId;
    }

    public void setSchedulerTypeId(SchedulerType schedulerTypeId) {
        this.schedulerTypeId = schedulerTypeId;
    }

    public SchedulerSubType getSchedulerSubTypeId() {
        return schedulerSubTypeId;
    }

    public void setSchedulerSubTypeId(SchedulerSubType schedulerSubTypeId) {
        this.schedulerSubTypeId = schedulerSubTypeId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
