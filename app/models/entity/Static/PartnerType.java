package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by adarsh on 12/9/16.
 */
@Entity(name = "partner_type")
@Table(name = "partner_type")
public class PartnerType extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "partner_type_id", columnDefinition = "bigint signed", unique = true)
    private Integer partnerTypeId;

    @Column(name = "partner_type_name", columnDefinition = "varchar(100) not null")
    private String partnerTypeName;

    public static Model.Finder<String, PartnerType> find = new Model.Finder(PartnerType.class);

    public Integer getPartnerTypeId() {
        return partnerTypeId;
    }

    public void setPartnerTypeId(Integer partnerTypeId) {
        this.partnerTypeId = partnerTypeId;
    }

    public String getPartnerTypeName() {
        return partnerTypeName;
    }

    public void setPartnerTypeName(String partnerTypeName) {
        this.partnerTypeName = partnerTypeName;
    }
}
