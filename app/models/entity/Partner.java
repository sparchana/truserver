package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by adarsh on 9/9/16.
 */

@Entity(name = "partner")
@Table(name = "partner")
public class Partner extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id", columnDefinition = "bigint signed", unique = true)
    private long partnerId = 0;

    @Column(name = "partner_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String partnerUUId;

    @Column(name = "partner_name", columnDefinition = "varchar(50) not null")
    private String partnerFirstName;

    @Column(name = "partner_last_name", columnDefinition = "varchar(50) null")
    private String partnerLastName;

    @Column(name = "partner_mobile", columnDefinition = "varchar(13) not null")
    private String partnerMobile;

    @Column(name = "partner_email", columnDefinition = "varchar(255) null")
    private String partnerEmail;

    @Column(name = "partner_company", columnDefinition = "varchar(255) null")
    private String partnerCompany;

    @Column(name = "partner_create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp partnerCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "partner_update_timestamp", columnDefinition = "timestamp")
    private Timestamp partnerUpdateTimestamp;

    public static Finder<String, Partner> find = new Finder(Partner.class);

    public Partner() {
        this.partnerUUId = UUID.randomUUID().toString();
        this.partnerCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public long getPartnerId() {
        return partnerId;
    }

    public String getPartnerUUId() {
        return partnerUUId;
    }

    public void setPartnerUUId(String partnerUUId) {
        this.partnerUUId = partnerUUId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerFirstName() {
        return partnerFirstName;
    }

    public void setPartnerFirstName(String partnerFirstName) {
        this.partnerFirstName = partnerFirstName;
    }

    public String getPartnerLastName() {
        return partnerLastName;
    }

    public void setPartnerLastName(String partnerLastName) {
        this.partnerLastName = partnerLastName;
    }

    public String getPartnerMobile() {
        return partnerMobile;
    }

    public void setPartnerMobile(String partnerMobile) {
        this.partnerMobile = partnerMobile;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getPartnerCompany() {
        return partnerCompany;
    }

    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }
}