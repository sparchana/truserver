package models.entity.OO;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Timeshift;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "timeshiftpreference")
@Table(name = "timeshiftpreference")
public class TimeShiftPreference extends Model {
    @Id
    @Column(name = "TimeShiftPreferenceId", columnDefinition = "int signed", nullable = false, unique = true)
    public int timeShiftPreferenceId = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "TimeShiftId", referencedColumnName = "TimeShiftId")
    public Timeshift timeshift;

    public static Finder<String, TimeShiftPreference> find = new Finder(TimeShiftPreference.class);

}
