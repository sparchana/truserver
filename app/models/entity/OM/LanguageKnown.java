package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Language;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "languageknown")
@Table(name = "languageknown")
public class LanguageKnown extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LanguageKnownId", columnDefinition = "int signed", unique = true)
    private int languageKnownId;

    @Column(name = "VerbalAbility", columnDefinition = "int signed null")
    private Integer verbalAbility;

    @Column(name = "ReadingAbility", columnDefinition = "int signed null")
    private Integer readingAbility;

    @Column(name = "WritingAbility", columnDefinition = "int signed null")
    private Integer writingAbility;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LanguageId", referencedColumnName = "LanguageId")
    private Language language;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    private Candidate candidate;

    @Column(name = "LanguageIntel", columnDefinition = "int(1) null")
    private Integer languageIntel;

    @Column(name = "ReadWrite", columnDefinition = "int(1) null")
    private Integer readWrite;

    public static Finder<String, LanguageKnown> find = new Finder(LanguageKnown.class);

    public Integer getLanguageKnownId() {
        return languageKnownId;
    }

    public void setLanguageKnownId(int languageKnownId) {
        this.languageKnownId = languageKnownId;
    }

    public Integer getVerbalAbility() {
        return verbalAbility;
    }

    public void setVerbalAbility(int verbalAbility) {
        this.verbalAbility = verbalAbility;
    }

    public Integer getReadingAbility() {
        return readingAbility;
    }

    public void setReadingAbility(int readingAbility) {
        this.readingAbility = readingAbility;
    }

    public Integer getWritingAbility() {
        return writingAbility;
    }

    public void setWritingAbility(int writingAbility) {
        this.writingAbility = writingAbility;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Integer getLanguageIntel() {
        return languageIntel;
    }

    public void setLanguageIntel(Integer languageIntel) {
        this.languageIntel = languageIntel;
    }

    public Integer getReadWrite() {
        return readWrite;
    }

    public void setReadWrite(Integer readWrite) {
        this.readWrite = readWrite;
    }
}
