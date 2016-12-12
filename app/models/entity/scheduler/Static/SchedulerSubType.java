package models.entity.scheduler.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by zero on 12/12/16.
 */
@Entity
@Table(name = "scheduler_sub_type")

public class SchedulerSubType extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "scheduler_sub_type_id", columnDefinition = "bigint signed", unique = true)
    private long subTypeId;

    @Column(name = "scheduler_sub_type_title", columnDefinition = "varchar(255) not null")
    private String subTypeTitle;

    public static Model.Finder<String, SchedulerSubType> find = new Model.Finder(SchedulerSubType.class);

    public long getSubTypeId() {
        return subTypeId;
    }

    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
    }
}
