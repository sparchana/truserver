package models.entity.Recruiter.Static;

import com.avaje.ebean.Model;
import models.entity.Recruiter.RecruiterLead;

import javax.persistence.*;
import java.util.List;

import static javax.imageio.ImageIO.setUseCache;

/**
 * Created by User on 29-11-2016.
 */
@Entity(name = "recruiter_lead_status")
@Table(name = "recruiter_lead_status")
public class RecruiterLeadStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "RecruiterLeadStatusId", columnDefinition = "bigint", unique = true)
    private Integer recruiterLeadStatusId;

    @Column(name = "RecruiterLeadStatusName", columnDefinition = "varchar(20) not null")
    private String recruiterLeadStatusName;

    public static Finder<String, RecruiterLeadStatus> find = new Finder(RecruiterLeadStatus.class);

    public List<RecruiterLeadStatus> readAll() {
        return RecruiterLeadStatus.find.all();
    }

    public Integer getRecruiterLeadStatusId() {
        return recruiterLeadStatusId;
    }

    public void setRecruiterLeadStatusId(Integer recruiterLeadStatusId) {
        this.recruiterLeadStatusId = recruiterLeadStatusId;
    }

    public String getRecruiterLeadStatusName() {
        return recruiterLeadStatusName;
    }

    public void setRecruiterLeadStatusName(String recruiterLeadStatusName) {
        this.recruiterLeadStatusName = recruiterLeadStatusName;
    }
}
