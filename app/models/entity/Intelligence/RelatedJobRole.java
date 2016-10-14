package models.entity.Intelligence;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.JobRole;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 29/9/16.
 */

@Entity(name = "related_jobrole")
@Table(name = "related_jobrole")
public class RelatedJobRole extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint signed", unique = true)
    private long id;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_role_id", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "related_job_role_id", referencedColumnName = "JobRoleId")
    private JobRole relatedJobRole;

    @Column(name = "weight", columnDefinition = "double(2, 2) null")
    private Double weight;

    public static Model.Finder<String, RelatedJobRole> find = new Model.Finder(RelatedJobRole.class);

    public long getId() {
        return id;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public JobRole getRelatedJobRole() {
        return relatedJobRole;
    }

    public void setRelatedJobRole(JobRole relatedJobRole) {
        this.relatedJobRole = relatedJobRole;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
