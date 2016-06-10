package models.entity.OO;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.TimeShift;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "timeshiftpreference")
@Table(name = "timeshiftpreference")
public class TimeShiftPreference extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "TimeShiftPreferenceId", columnDefinition = "int signed", unique = true)
    private int timeShiftPreferenceId;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());

    @JsonBackReference
    @OneToOne(mappedBy = "timeShiftPreference")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "TimeShiftId", referencedColumnName = "TimeShiftId")
    private TimeShift timeShift;

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setTimeShift(TimeShift timeShift) {
        this.timeShift = timeShift;
    }

    public static Finder<String, TimeShiftPreference> find = new Finder(TimeShiftPreference.class);

    public int getTimeShiftPreferenceId() {
        return timeShiftPreferenceId;
    }

    public void setTimeShiftPreferenceId(int timeShiftPreferenceId) {
        this.timeShiftPreferenceId = timeShiftPreferenceId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public TimeShift getTimeShift() {
        return timeShift;
    }
}
