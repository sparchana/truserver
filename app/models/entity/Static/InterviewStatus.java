package models.entity.Static;

import com.avaje.ebean.Model;
import javax.persistence.*;

/**
 * Created by dodo on 8/11/16.
 */
@Entity(name = "interview_status")
@Table(name = "interview_status")
public class InterviewStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "interview_status_id", columnDefinition = "bigint signed", unique = true)
    private Integer interviewStatusId;

    @Column(name = "interview_status_name", columnDefinition = "varchar(100) not null")
    private String interviewStatusName;

    public static Model.Finder<String, InterviewStatus> find = new Model.Finder(InterviewStatus.class);

    public Integer getInterviewStatusId() {
        return interviewStatusId;
    }

    public void setInterviewStatusId(Integer interviewStatusId) {
        this.interviewStatusId = interviewStatusId;
    }

    public String getInterviewStatusName() {
        return interviewStatusName;
    }

    public void setInterviewStatusName(String interviewStatusName) {
        this.interviewStatusName = interviewStatusName;
    }
}

