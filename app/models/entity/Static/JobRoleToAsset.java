package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 12/10/16.
 */
@CacheStrategy
@Entity(name = "job_role_to_asset")
@Table(name = "job_role_to_asset")
public class JobRoleToAsset extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "job_role_to_asset_id", columnDefinition = "int unsigned", unique = true)
    private int jobRoleToAssetId;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_role_id", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "asset_id", referencedColumnName = "asset_id")
    private Asset asset;


    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp creationTimestamp;

    public static Model.Finder<String, JobRoleToAsset> find = new Model.Finder(JobRoleToAsset.class);

    public JobRoleToAsset() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getJobRoleToAssetId() {
        return jobRoleToAssetId;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
