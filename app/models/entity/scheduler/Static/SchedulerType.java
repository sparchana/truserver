package models.entity.scheduler.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by zero on 12/12/16.
 */

@Entity
@Table(name = "scheduler_type")
public class SchedulerType extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "scheduler_type_id", columnDefinition = "bigint signed", unique = true)
    private long typeId;

    @Column(name = "scheduler_type_title", columnDefinition = "varchar(255) not null")
    private String typeTitle;

    public static Model.Finder<String, SchedulerType> find = new Model.Finder(SchedulerType.class);

    public long getTypeId() {
        return typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }
}
