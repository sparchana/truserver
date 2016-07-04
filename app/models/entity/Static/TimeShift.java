package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@CacheStrategy
@Entity(name = "timeshift")
@Table(name = "timeshift")
public class TimeShift extends Model{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "TimeShiftId", columnDefinition = "int signed", unique = true)
    private int timeShiftId;

    @Column(name = "TimeShiftName", columnDefinition = "varchar(50) null")
    private String timeShiftName;

    public static Finder<String, TimeShift> find = new Finder(TimeShift.class);

    public int getTimeShiftId() {
        return timeShiftId;
    }

    public void setTimeShiftId(int timeShiftId) {
        this.timeShiftId = timeShiftId;
    }

    public String getTimeShiftName() {
        return timeShiftName;
    }

    public void setTimeShiftName(String timeShiftName) {
        this.timeShiftName = timeShiftName;
    }

}
