package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by zero on 14/10/16.
 */
@Entity(name = "pre_screen_response")
@Table(name = "pre_screen_response")
public class PreScreenResponse extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "pre_screen_response_id", columnDefinition = "bigint unsigned", unique = true)
    private long preScreenResponseId;

    @Column(name = "pre_screen_response_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String preScreenResponseUUId; // UUID

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "pre_screen_result_id", referencedColumnName = "pre_screen_result_id")
    private PreScreenResult preScreenResult;

    public PreScreenResponse() {
        this.preScreenResponseUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, PreScreenResponse> find = new Finder(PreScreenResponse.class);

    public long getPreScreenResponseId() {
        return preScreenResponseId;
    }

    public String getPreScreenResponseUUId() {
        return preScreenResponseUUId;
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

    public PreScreenResult getPreScreenResult() {
        return preScreenResult;
    }

    public void setPreScreenResult(PreScreenResult preScreenResult) {
        this.preScreenResult = preScreenResult;
    }
}
