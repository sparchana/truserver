package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by batcoder1 on 30/4/16.
 */
@Entity(name = "job")
@Table(name = "job")
public class Job extends Model {
    @Id
    @Column(name = "JobId", columnDefinition = "int signed not null", unique = true)
    public int jobId = 0;

    @Column(name = "JobName", columnDefinition = "varchar(50) not null")
    public String jobName = "";

    public static Model.Finder<String, Job> find = new Model.Finder(Job.class);
}
