package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.Language;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "languagepreference")
@Table(name = "languagepreference")
public class LanguagePreference extends Model {
    @Id
    @Column(name = "LanguagePreference", columnDefinition = "int signed not null", unique = true)
    public int languagePreference = 0;

    @Column(name = "CandidateId", columnDefinition = "bigint signed not null")
    public long candidateId = 0;

    @Column(name = "LanguageId", columnDefinition = "int signed not null")
    public int languageId = 0;

    @Column(name = "VerbalAbility", columnDefinition = "int signed null")
    public int verbalAbility = 0; // 0/1

    @Column(name = "ReadingAbility", columnDefinition = "int signed null")
    public int readingAbility = 0; // 0/1

    @Column(name = "WritingAbility", columnDefinition = "int signed null")
    public int writingAbility = 0; // 0/1

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LanguageId", referencedColumnName = "LanguageId")
    public Language language;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    public Candidate candidate;

   public static Finder<String, LanguagePreference> find = new Finder(LanguagePreference.class);

}
