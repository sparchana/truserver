package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.OM.LanguageKnown;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "language")
@Table(name = "language")
public class Language extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LanguageId", columnDefinition = "int signed", unique = true)
    private int languageId;

    @Column(name = "LanguageName", columnDefinition = "varchar(255) null")
    private String languageName;

    @JsonBackReference
    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    private List<LanguageKnown> languageKnownList;

    public static Finder<String, Language> find = new Finder(Language.class);

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public List<LanguageKnown> getLanguageKnownList() {
        return languageKnownList;
    }

    public void setLanguageKnownList(List<LanguageKnown> languageKnownList) {
        this.languageKnownList = languageKnownList;
    }
}
