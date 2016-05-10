package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OO.TimeShiftPreference;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "timeshift")
@Table(name = "timeshift")
public class TimeShift extends Model{
    @Id
    @Column(name = "TimeShiftId", columnDefinition = "int signed", nullable = false, unique = true)
    public int timeShiftId = 0;

    @Column(name = "TimeShiftName", columnDefinition = "varchar(50) not null")
    public String timeShiftName = "";

    @JsonBackReference
    @OneToOne(mappedBy = "timeShift", cascade = CascadeType.REMOVE)
    public TimeShiftPreference timeShiftPreference;

    public static Finder<String, TimeShift> find = new Finder(TimeShift.class);

}
