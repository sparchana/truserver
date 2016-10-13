package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 10/10/16.
 */

@CacheStrategy
@Entity(name = "job_post_workflow_status")
@Table(name = "job_post_workflow_status")
public class JobPostWorkflowStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "status_id", columnDefinition = "int unsigned", unique = true)
    private int statusId;

    @Column(name = "status_title", columnDefinition = "varchar(255) null")
    private String statusTitle;

    public static Model.Finder<String, JobPostWorkflowStatus> find = new Model.Finder(JobPostWorkflowStatus.class);

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }
}
