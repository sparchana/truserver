package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by dodo on 1/2/17.
 */

@Entity(name = "sms_type")
@Table(name = "sms_type")
public class SmsType  extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "sms_type_id", columnDefinition = "int signed", unique = true)
    private int smsTypeId;

    @Column(name = "type_name", columnDefinition = "varchar(50) null")
    private String typeName;

    public static Finder<String, SmsType> find = new Finder(SmsType.class);

    public int getSmsTypeId() {
        return smsTypeId;
    }

    public void setSmsTypeId(int smsTypeId) {
        this.smsTypeId = smsTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
