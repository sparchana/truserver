package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by zero on 21/6/16.
 */

@Entity(name = "jobexpresponseoption")
@Table(name = "jobexpresponseoption")
public class JobExpResponseOption extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobExpResponseOptionId", columnDefinition = "int signed", unique = true)
    private int jobExpResponseOptionId;

    @JsonBackReference
    @Column(name = "ResponseGroupId", columnDefinition = "int signed null")
    private Integer responseGroupId;

    @Column(name = "JobExpResponseOptionName", columnDefinition = "varchar(255)")
    private String jobExpResponseOptionName;

    public static Model.Finder<String, JobExpResponseOption> find = new Model.Finder(JobExpResponseOption.class);

    public int getJobExpResponseOptionId() {
        return jobExpResponseOptionId;
    }

    public void setJobExpResponseOptionId(int jobExpResponseOptionId) {
        this.jobExpResponseOptionId = jobExpResponseOptionId;
    }

    public Integer getResponseGroupId() {
        return responseGroupId;
    }

    public void setResponseGroupId(Integer responseGroupId) {
        this.responseGroupId = responseGroupId;
    }

    public String getJobExpResponseOptionName() {
        return jobExpResponseOptionName;
    }

    public void setJobExpResponseOptionName(String jobExpResponseOptionName) {
        this.jobExpResponseOptionName = jobExpResponseOptionName;
    }
}
