package models.entity.ongrid.transactional;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.ongrid.OnGridVerificationFields;
import models.entity.ongrid.OnGridVerificationStatus;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by archana on 11/19/16.
 */
@Entity(name = "ongrid_verification_results")
@Table(name = "ongrid_verification_results")
public class OngridVerificationResults extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", columnDefinition = "bigint signed", unique = true)
    private long resultId = 0;

    @Column(name = "result_uuid", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String resultUUId;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimestamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp")
    private Timestamp updateTimestamp;

    @Column(name = "ongrid_id", columnDefinition = "int signed null")
    private Long ongridId;

    @Column(name = "ongrid_community_id", columnDefinition = "int signed null")
    private Long ongridCommunityId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "ongrid_field", referencedColumnName = "field_id")
    private OnGridVerificationFields ongridField;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "ongrid_verification_status", referencedColumnName = "status_id")
    private OnGridVerificationStatus ongridVerificationStatus;

    public OngridVerificationResults(Candidate candidate,
                                     OnGridVerificationFields field,
                                     OnGridVerificationStatus status,
                                     Long communitId,
                                     Long ongridId)
    {
        this.candidate = candidate;
        this.ongridField = field;
        this.ongridVerificationStatus = status;
        this.ongridCommunityId = communitId;
        this.ongridId = ongridId;
        this.updateTimestamp = new Timestamp(System.currentTimeMillis());
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
        this.resultUUId = UUID.randomUUID().toString();
    }

    public static Model.Finder<String, OngridVerificationResults> find = new Model.Finder(OngridVerificationResults.class);

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public String getResultUUId() {
        return resultUUId;
    }

    public void setResultUUId(String resultUUId) {
        this.resultUUId = resultUUId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Long getOngridId() {
        return ongridId;
    }

    public void setOngridId(Long ongridId) {
        this.ongridId = ongridId;
    }

    public Long getOngridCommunityId() {
        return ongridCommunityId;
    }

    public void setOngridCommunityId(Long ongridCommunityId) {
        this.ongridCommunityId = ongridCommunityId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public OnGridVerificationFields getOngridField() {
        return ongridField;
    }

    public void setOngridField(OnGridVerificationFields ongridField) {
        this.ongridField = ongridField;
    }

    public OnGridVerificationStatus getOngridVerificationStatus() {
        return ongridVerificationStatus;
    }

    public void setOngridVerificationStatus(OnGridVerificationStatus ongridVerificationStatus) {
        this.ongridVerificationStatus = ongridVerificationStatus;
    }
}
