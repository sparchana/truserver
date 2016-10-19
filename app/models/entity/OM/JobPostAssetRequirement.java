package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.Asset;
import models.entity.Static.Language;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 13/10/16.
 */
@Entity(name = "job_post_asset_requirement")
@Table(name = "job_post_asset_requirement")
public class JobPostAssetRequirement extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "asset_requirement_id", columnDefinition = "bigint unsigned", unique = true)
    private long assetRequirementId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "job_post_id", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "asset_id", referencedColumnName = "asset_id")
    private Asset asset;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    public JobPostAssetRequirement(){
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, JobPostAssetRequirement> find = new Finder(JobPostAssetRequirement.class);

    public long getAssetRequirementId() {
        return assetRequirementId;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

}
