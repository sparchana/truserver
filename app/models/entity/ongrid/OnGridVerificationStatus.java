package models.entity.ongrid;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import models.entity.Static.JobRole;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@CacheStrategy
@Entity(name = "ongrid_verification_status")
@Table(name = "ongrid_verification_status")
public class OnGridVerificationStatus extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "status_id", columnDefinition = "bigint signed", unique = true)
    private long statusId;

    @Column(name = "status_name", columnDefinition = "varchar(255) null")
    private String statusName;

    public static Finder<String, OnGridVerificationStatus> find = new Finder(OnGridVerificationStatus.class);

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
