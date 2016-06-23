package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "pricingplantype")
@Table(name = "pricingplantype")
public class PricingPlanType extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "PricingPlanTypeId", columnDefinition = "bigint signed", unique = true)
    private Integer pricingPlanTypeId;

    @Column(name = "PricingPlanTypeName", columnDefinition = "varchar(20) not null")
    private String pricingPlanTypeName;

    public Integer getPricingPlanTypeId() {
        return pricingPlanTypeId;
    }

    public void setPricingPlanTypeId(Integer pricingPlanTypeId) {
        this.pricingPlanTypeId = pricingPlanTypeId;
    }

    public String getPricingPlanTypeName() {
        return pricingPlanTypeName;
    }

    public void setPricingPlanTypeName(String pricingPlanTypeName) {
        this.pricingPlanTypeName = pricingPlanTypeName;
    }
}
