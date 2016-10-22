package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.Language;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/10/16.
 */
@Entity(name = "job_post_language_requirement")
@Table(name = "job_post_language_requirement")
public class JobPostLanguageRequirement extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "LanguageRequirementId", columnDefinition = "bigint unsigned", unique = true)
    private long languageRequirementId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LanguageId", referencedColumnName = "LanguageId")
    private Language language;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    public JobPostLanguageRequirement() {
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }
    public static Finder<String, JobPostLanguageRequirement> find = new Finder(JobPostLanguageRequirement.class);

    public long getLanguageRequirementId() {
        return languageRequirementId;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

}
