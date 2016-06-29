package models.entity;

import api.ServerConstants;
import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "interaction")
@Table(name = "interaction")
public class Interaction extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "rowId", columnDefinition = "int signed", unique = true)
    private long id;

    @Column(name = "ObjectAUUId", columnDefinition = "varchar(255) not null", nullable = false)
    private String objectAUUId = ""; // UUID

    @Column(name = "ObjectAType", columnDefinition = "int signed not null", nullable = false)
    private int objectAType = 0; // Lead, Candidate, Recruiter

    @Column(name = "ObjectBUUId", columnDefinition = "varchar(255) not null")
    private String objectBUUId = ""; // UUID

    @Column(name = "ObjectBType", columnDefinition = "int signed null")
    private Integer objectBType; // Lead, Candidate, Recruiter

    @Column(name = "InteractionType", columnDefinition = "int signed null")
    private Integer interactionType; // CallIn, CallOut, Sms

    @Column(name = "Note", columnDefinition = "varchar(255) null")
    private String note;

    @Column(name = "Result", columnDefinition = "varchar(255) null")
    private String result;

    @Column(name = "CreationTimestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    private Timestamp creationTimestamp;

    @Column(name = "CreatedBy", columnDefinition = "varchar(255) not null default 'System'", nullable = false)
    private String createdBy = ServerConstants.INTERACTION_CREATED_SYSTEM;

    public static Finder<String, Interaction> find = new Finder(Interaction.class);

    public Interaction(){
       this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    // single object constructor
    public Interaction(String objectAUUId, int objectAType, int interactionType, String note, String result, String createdBy){
        // no need to set creationTimestamp, It is set By - default
        this.objectAUUId = objectAUUId;
        this.objectAType = objectAType;
        this.interactionType = interactionType;
        this.note = (createdBy == null) ? ServerConstants.INTERACTION_NOTE_CREATED_BY_ERROR : note;
        this.result = result;
        this.createdBy = (createdBy == null) ? ServerConstants.INTERACTION_CREATED_ERROR : createdBy;
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    // Two object constructor
    public Interaction(String objectAUUId, int objectAType, String objectBUUId, int objectBType, int interactionType, String result, String createdBy){
        // no need to set creationTimestamp, It is set By - default
        this.objectAUUId = objectAUUId;
        this.objectAType = objectAType;
        this.objectBUUId = objectBUUId;
        this.objectBType = objectBType;
        this.note = ServerConstants.INTERACTION_NOTE_BLANK;
        this.interactionType = interactionType;
        this.result = result;
        this.createdBy = (createdBy == null) ? ServerConstants.INTERACTION_CREATED_ERROR : createdBy;
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static void addInteraction(Interaction interaction){
        interaction.save();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObjectAUUId() {
        return objectAUUId;
    }

    public void setObjectAUUId(String objectAUUId) {
        this.objectAUUId = objectAUUId;
    }

    public int getObjectAType() {
        return objectAType;
    }

    public void setObjectAType(int objectAType) {
        this.objectAType = objectAType;
    }

    public String getObjectBUUId() {
        return objectBUUId;
    }

    public void setObjectBUUId(String objectBUUId) {
        this.objectBUUId = objectBUUId;
    }

    public Integer getObjectBType() {
        return objectBType;
    }

    public void setObjectBType(Integer objectBType) {
        this.objectBType = objectBType;
    }

    public Integer getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(Integer interactionType) {
        this.interactionType = interactionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

