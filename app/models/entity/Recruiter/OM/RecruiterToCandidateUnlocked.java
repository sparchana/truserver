package models.entity.Recruiter.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Recruiter.RecruiterProfile;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 12/10/16.
 */

@Entity(name = "recruiter_to_candidate_unlocked")
@Table(name = "recruiter_to_candidate_unlocked")
public class RecruiterToCandidateUnlocked extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_to_candidate_unlocked_id", columnDefinition = "int signed", unique = true)
    private Integer recruiterToCandidateUnlockedId;

    @Column(name = "recruiter_to_candidate_unlocked_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String recruiterToCandidateUnlockedUUID;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimestamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "recruiterProfileId", referencedColumnName = "recruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    public static Model.Finder<String, RecruiterToCandidateUnlocked> find = new Model.Finder(RecruiterToCandidateUnlocked.class);

    public RecruiterToCandidateUnlocked(){
        this.recruiterToCandidateUnlockedUUID = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Integer getRecruiterToCandidateUnlockedId() {
        return recruiterToCandidateUnlockedId;
    }

    public void setRecruiterToCandidateUnlockedId(Integer recruiterToCandidateUnlockedId) {
        this.recruiterToCandidateUnlockedId = recruiterToCandidateUnlockedId;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String getRecruiterToCandidateUnlockedUUID() {
        return recruiterToCandidateUnlockedUUID;
    }

    public void setRecruiterToCandidateUnlockedUUID(String recruiterToCandidateUnlockedUUID) {
        this.recruiterToCandidateUnlockedUUID = recruiterToCandidateUnlockedUUID;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}