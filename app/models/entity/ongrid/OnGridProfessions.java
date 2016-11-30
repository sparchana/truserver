package models.entity.ongrid;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.JobRole;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@CacheStrategy
@Entity(name = "ongrid_professions")
@Table(name = "ongrid_professions")
public class OnGridProfessions extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "profession_id", columnDefinition = "bigint signed", unique = true)
    private long professionId;

    @Column(name = "profession_name", columnDefinition = "varchar(255) null")
    private String professionName;

    @OneToMany
    @JsonManagedReference
    @JoinColumn(name = "job_role_id", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    public static Finder<String, OnGridProfessions> find = new Finder(OnGridProfessions.class);

    public long getProfessionId() {
        return professionId;
    }

    public void setProfessionId(long professionId) {
        this.professionId = professionId;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }
}
