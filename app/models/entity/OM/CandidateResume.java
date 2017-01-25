package models.entity.OM;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.WhoCreated;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Candidate;
import models.entity.Recruiter.RecruiterLead;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Expr.eq;
import static play.mvc.Controller.session;

/**
 * Created by User on 24-12-2016.
 */

@Entity(name = "candidate_resume")
@Table(name = "candidate_resume")

public class CandidateResume extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "candidate_resume_id", columnDefinition = "bigint unsigned", unique = true)
    private long candidateResumeId;

    @CreatedTimestamp
    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @Column(name = "created_by", columnDefinition = "varchar(255)", nullable = false)
    private String createdBy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "candidateId")
    private Candidate candidate;

    @Column(name = "file_path", columnDefinition = "varchar(512)", nullable = false)
    private String filePath;

    @Column(name = "external_key", columnDefinition = "varchar(255)", nullable = false)
    private String externalKey;

    @Column(name = "parsed_resume", columnDefinition = "text")
    private String parsedResume;

    public static Finder<String, CandidateResume> find = new Finder(CandidateResume.class);

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        if(createdBy != null && createdBy.length() > 0) this.createdBy = createdBy;
        else{ this.createdBy = "Unknown"; }
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public long getCandidateResumeId() {
        return candidateResumeId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public String getParsedResume() {
        return parsedResume;
    }

    public void setParsedResume(String parsedResume) {
        this.parsedResume = parsedResume;
    }

    public List<CandidateResume> readById(List<Long> ids) {
        return CandidateResume.find.where().idIn(ids).setUseCache(Boolean.TRUE).findList();
    }

    public ExpressionList<CandidateResume> getQuery(){return find.where();}

}

