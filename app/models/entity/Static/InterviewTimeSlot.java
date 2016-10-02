package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 29/9/16.
 */
@Entity(name = "interview_time_slot")
@Table(name = "interview_time_slot")
public class InterviewTimeSlot extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "interview_time_slot_id", columnDefinition = "bigint signed", unique = true)
    private Integer interviewTimeSlotId;

    @Column(name = "interview_time_slot_name", columnDefinition = "varchar(20) null")
    private String interviewTimeSlotName;

    public static Model.Finder<String, InterviewTimeSlot> find = new Model.Finder(InterviewTimeSlot.class);

    public Integer getInterviewTimeSlotId() {
        return interviewTimeSlotId;
    }

    public void setInterviewTimeSlotId(Integer interviewTimeSlotId) {
        this.interviewTimeSlotId = interviewTimeSlotId;
    }

    public String getInterviewTimeSlotName() {
        return interviewTimeSlotName;
    }

    public void setInterviewTimeSlotName(String interviewTimeSlotName) {
        this.interviewTimeSlotName = interviewTimeSlotName;
    }
}
