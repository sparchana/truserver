package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Language;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "languagepreference")
@Table(name = "languagepreference")
public class LanguagePreference extends Model {
    @Id
    @Column(name = "LanguagePreferenceId", columnDefinition = "int signed not null", unique = true)
    public int languagePreferenceId = 0;

    @Column(name = "VerbalAbility", columnDefinition = "int signed null")
    public int verbalAbility = 0; // 0/1

    @Column(name = "ReadingAbility", columnDefinition = "int signed null")
    public int readingAbility = 0; // 0/1

    @Column(name = "WritingAbility", columnDefinition = "int signed null")
    public int writingAbility = 0; // 0/1

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LanguageId", referencedColumnName = "LanguageId")
    public Language language;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    public Candidate candidate;


    public int getVerbalAbility() {
        return verbalAbility;
    }

    public void setVerbalAbility(int verbalAbility) {
        this.verbalAbility = verbalAbility;
    }

    public int getReadingAbility() {
        return readingAbility;
    }

    public void setReadingAbility(int readingAbility) {
        this.readingAbility = readingAbility;
    }

    public int getWritingAbility() {
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

    public static Finder<String, LanguagePreference> find = new Finder(LanguagePreference.class);

}
