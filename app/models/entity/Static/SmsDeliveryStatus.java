package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 19/1/17.
 */

@Entity(name = "sms_delivery_status")
@Table(name = "sms_delivery_status")
public class SmsDeliveryStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "status_id", columnDefinition = "int signed", unique = true)
    private int statusId;

    @Column(name = "status_name", columnDefinition = "varchar(50) null")
    private String statusName;

    public static Finder<String, SmsDeliveryStatus> find = new Finder(SmsDeliveryStatus.class);

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}