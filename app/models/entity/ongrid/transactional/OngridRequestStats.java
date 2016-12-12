package models.entity.ongrid.transactional;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.ongrid.OnGridVerificationFields;
import models.entity.ongrid.OnGridVerificationStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by archana on 11/19/16.
 */
@Entity(name = "ongrid_request_stats")
@Table(name = "ongrid_request_stats")
public class OngridRequestStats extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", columnDefinition = "bigint signed", unique = true)
    private long resultId = 0;

    @Column(name = "create_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimestamp;

    @UpdatedTimestamp
    @Column(name = "update_timestamp", columnDefinition = "timestamp")
    private Timestamp updateTimestamp;

    @Column(name = "verification_type", columnDefinition = "varchar(500) not null")
    private String verificationType;

    @Column(name = "request_url", columnDefinition = "text not null")
    private String requestURL;

    @Column(name = "request_text", columnDefinition = "text null")
    private String requestText;

    @Column(name = "response_text", columnDefinition = "text null")
    private String responseText;

    @Column(name = "response_status", columnDefinition = "text null")
    private String responseStatus;

    public OngridRequestStats(String type, String url, String req, String response, String status) {
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
        this.updateTimestamp = new Timestamp(System.currentTimeMillis());
        verificationType = type;
        requestURL = url;
        requestText = req;
        responseText = response;
        responseStatus = status;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
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

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
}
