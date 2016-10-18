package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by zero on 14/10/16.
 */
@Entity(name = "pre_screen_result")
@Table(name = "pre_screen_result")
public class PreScreenResult extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "pre_screen_result_id", columnDefinition = "bigint unsigned", unique = true)
    private long preScreenResultId;

    @Column(name = "pre_screen_result_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String preScreenResultUUId; // UUID

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @Column(name = "attempt_count", columnDefinition = "int null")
    private Integer attemptCount;

    @Column(name = "result_score", columnDefinition = "double(2,2) null")
    private Double resultScore;

    @Column(name = "force_set", columnDefinition = "tinyint(1) null")
    private Boolean forceSet;

    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_workflow_id", referencedColumnName = "job_post_workflow_id")
    private JobPostWorkflow jobPostWorkflow;

    /*@JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "preScreenResult", cascade = CascadeType.ALL)
    private List<PreScreenResponse> preScreenResponseList;*/


    public PreScreenResult(){
        this.preScreenResultUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, PreScreenResult> find = new Finder(PreScreenResult.class);

    public long getPreScreenResultId() {
        return preScreenResultId;
    }

    public String getPreScreenResultUUId() {
        return preScreenResultUUId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Double getResultScore() {
        return resultScore;
    }

    public void setResultScore(Double resultScore) {
        this.resultScore = resultScore;
    }

    public Boolean getForceSet() {
        return forceSet;
    }

    public void setForceSet(Boolean forceSet) {
        this.forceSet = forceSet;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public JobPostWorkflow getJobPostWorkflow() {
        return jobPostWorkflow;
    }

    public void setJobPostWorkflow(JobPostWorkflow jobPostWorkflow) {
        this.jobPostWorkflow = jobPostWorkflow;
    }

    /*public List<PreScreenResponse> getPreScreenResponseList() {
        return preScreenResponseList;
    }

    public void setPreScreenResponseList(List<PreScreenResponse> preScreenResponseList) {
        this.preScreenResponseList = preScreenResponseList;
    }*/
}
