package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "SupportUserSearchPermissions")
@Table(name = "support_user_search_permissions")
public class SupportUserSearchPermissions extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "support_user_search_permissions_id", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    private long supportUserSearchPermissionsId = 0;

    @Column(name = "single_query_limit", columnDefinition = "bigint signed null")
    private Integer singleQueryLimit;

    public Integer getDailyQueryLimit() {
        return dailyQueryLimit;
    }

    public void setDailyQueryLimit(Integer dailyQueryLimit) {
        this.dailyQueryLimit = dailyQueryLimit;
    }

    public Integer getSingleQueryLimit() {
        return singleQueryLimit;
    }

    public void setSingleQueryLimit(Integer singleQueryLimit) {
        this.singleQueryLimit = singleQueryLimit;
    }

    public long getSupportUserSearchPermissionsId() {
        return supportUserSearchPermissionsId;
    }

    public void setSupportUserSearchPermissionsId(long supportUserSearchPermissionsId) {
        this.supportUserSearchPermissionsId = supportUserSearchPermissionsId;
    }

    @Column(name = "daily_query_limit", columnDefinition = "bigint signed null")
    private Integer dailyQueryLimit;

    public static Finder<String, SupportUserSearchPermissions> find = new Finder(SupportUserSearchPermissions.class);

}
