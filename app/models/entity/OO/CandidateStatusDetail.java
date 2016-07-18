package models.entity.OO;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by zero on 15/7/16.
 */
@Entity(name = "candidatestatusdetail")
@Table(name = "candidatestatusdetail")
public class CandidateStatusDetail extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CandidateStatusDetailId", columnDefinition = "int signed", unique = true)
    private int candidateStatusDetailId;

    @Column(name = "CreateTimeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @Column(name = "StatusExpiryDate", columnDefinition = "date null")
    private Date statusExpiryDate;

    @Column(name = "Reason", columnDefinition = "text null")
    private String reason;

    public CandidateStatusDetail(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, CandidateStatusDetail> find = new Finder(CandidateStatusDetail.class);

    /* getter */
    public int getCandidateStatusDetailId() {
        return candidateStatusDetailId;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getReason() {
        return reason;
    }

    public Date getStatusExpiryDate() {
        return statusExpiryDate;
    }

    /* setter */
    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatusExpiryDate(Date statusExpiryDate) {
        this.statusExpiryDate = statusExpiryDate;
    }

    public void setCreateTimeStamp(Timestamp createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

}
