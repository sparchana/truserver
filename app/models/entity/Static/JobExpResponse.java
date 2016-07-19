package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

/**
 * Created by zero on 20/6/16.
 */

@Entity(name = "jobexpresponse")
@Table(name = "jobexpresponse")
public class JobExpResponse extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobExpResponseId", columnDefinition = "int signed", unique = true)
    private int jobExpResponseId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    @JoinColumn(name = "JobExpQuestionId", referencedColumnName = "JobExpQuestionId")
    private JobExpQuestion jobExpQuestion;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "JobExpResponseOptionId", referencedColumnName = "JobExpResponseOptionId")
    private JobExpResponseOption jobExpResponseOption;

    public static Model.Finder<String, JobExpResponse> find = new Model.Finder(JobExpResponse.class);

    public int getJobExpResponseId() {
        return jobExpResponseId;
    }

    public void setJobExpResponseId(int jobExpResponseId) {
        this.jobExpResponseId = jobExpResponseId;
    }

    public JobExpQuestion getJobExpQuestion() {
        return jobExpQuestion;
    }

    public void setJobExpQuestion(JobExpQuestion jobExpQuestion) {
        this.jobExpQuestion = jobExpQuestion;
    }

    public JobExpResponseOption getJobExpResponseOption() {
        return jobExpResponseOption;
    }

    public void setJobExpResponseOption(JobExpResponseOption jobExpResponseOption) {
        this.jobExpResponseOption = jobExpResponseOption;
    }
}
