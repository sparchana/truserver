package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 12/10/16.
 */
@CacheStrategy
@Entity(name = "asset")
@Table(name = "asset")
public class Asset extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "asset_id", columnDefinition = "int unsigned", unique = true)
    private int assetId;

    @Column(name = "asset_title", columnDefinition = "varchar(255) null")
    private String assetTitle;

    @Column(name = "is_common", columnDefinition = "tinyint(1)", nullable = false)
    private boolean isCommon;

    public static Model.Finder<String, Asset> find = new Model.Finder(Asset.class);

    public int getAssetId() {
        return assetId;
    }

    public String getAssetTitle() {
        return assetTitle;
    }

    public void setAssetTitle(String assetTitle) {
        this.assetTitle = assetTitle;
    }

    public boolean isCommon() {
        return isCommon;
    }

    public void setCommon(boolean common) {
        isCommon = common;
    }
}
