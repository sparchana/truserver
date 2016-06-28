package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 20/6/16.
 */
@Entity(name = "jobexpquestion")
@Table(name = "jobexpquestion")
public class JobExpQuestion extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobExpQuestionId", columnDefinition = "int signed", unique = true)
    private int jobExpQuestionId;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "ExpCategoryId", referencedColumnName = "ExpCategoryId")
    private ExpCategory expCategory;

    @Column(name = "JobExpQuestion", columnDefinition = "text null")
    private String jobExpQuestion;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobExpQuestion", cascade = CascadeType.ALL)
    private List<JobExpResponse> jobExpResponseList;

    public static Model.Finder<String, JobExpQuestion> find = new Model.Finder(JobExpQuestion.class);

    public int getJobExpQuestionId() {
        return jobExpQuestionId;
    }

    public void setJobExpQuestionId(int jobExpQuestionId) {
        this.jobExpQuestionId = jobExpQuestionId;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public ExpCategory getExpCategory() {
        return expCategory;
    }

    public void setExpCategory(ExpCategory expCategory) {
        this.expCategory = expCategory;
    }

    public String getJobExpQuestion() {
        return jobExpQuestion;
    }

    public void setJobExpQuestion(String jobExpQuestion) {
        this.jobExpQuestion = jobExpQuestion;
    }

    public List<JobExpResponse> getJobExpResponseList() {
        return jobExpResponseList;
    }

    public void setJobExpResponseList(List<JobExpResponse> jobExpResponseList) {
        this.jobExpResponseList = jobExpResponseList;
    }
}
