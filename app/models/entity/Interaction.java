package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by zero on 27/4/16.
 */

@Entity(name = "interaction")
@Table(name = "interaction")
public class Interaction extends Model {
    @Id
    @Column(name = "rowId", columnDefinition = "int signed not null", nullable = false, unique = true)
    public long id = 0;

    @Column(name = "ObjectAUUId", columnDefinition = "varchar(255) not null", nullable = false)
    public String objectAUUId = ""; // UUID

    @Column(name = "ObjectAType", columnDefinition = "int signed not null", nullable = false)
    public int objectAType = 0; // Lead, Candidate, Recruiter

    @Column(name = "ObjectBUUId", columnDefinition = "varchar(255) not null")
    public String objectBUUId = ""; // UUID

    @Column(name = "ObjectBType", columnDefinition = "int signed null")
    public int objectBType = 0; // Lead, Candidate, Recruiter

    @Column(name = "InteractionType", columnDefinition = "int signed not null", nullable = false)
    public int interactionType = 0; // CallIn, CallOut, Sms

    @Column(name = "Note", columnDefinition = "varchar(255) null")
    public String note = "";

    @Column(name = "Result", columnDefinition = "varchar(255) null")
    public String result = "";

    @Column(name = "CreationTimestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    public Timestamp creationTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "CreatedBy", columnDefinition = "varchar(255) not null default 'system'", nullable = false)
    public String createdBy = "system"; // system-> website, Knowlarity Dump

    public static Finder<String, Interaction> find = new Finder(Interaction.class);

    public Interaction(){

    }
    // single object constructor
    public Interaction(String objectAUUId, int objectAType, int interactionType, String note, String result, String createdBy){
        // no need to set creationTimestamp, It is set By - default
        this.objectAUUId = objectAUUId;
        this.objectAType = objectAType;
        this.interactionType = interactionType;
        this.note = note;
        this.result = result;
        this.createdBy = createdBy;
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

    public String getObjectAId() {
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

    public String getObjectBId() {
        return objectBUUId;
    }

    public void setObjectBUUId(String objectBUUId) {
        this.objectBUUId = objectBUUId;
    }

    public int getObjectBType() {
        return objectBType;
    }

    public void setObjectBType(int objectBType) {
        this.objectBType = objectBType;
    }

    public int getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(int interactionType) {
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

