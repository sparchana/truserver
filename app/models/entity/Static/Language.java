package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Candidate;
import models.entity.OM.LanguagePreference;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 4/5/16.
 */

@Entity(name = "language")
@Table(name = "language")
public class Language extends Model {
    @Id
    @Column(name = "LanguageId", columnDefinition = "int signed null", unique = true)
    public int languageId = 0;

    @Column(name = "LanguageName", columnDefinition = "varchar(255) null")
    public String languageName = "";

    @JsonBackReference
    @OneToMany(mappedBy = "motherTongue")
    public Candidate candidate;

    @JsonBackReference
    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    public List<LanguagePreference> languagePreferenceList;

    public static Finder<String, Language> find = new Finder(Language.class);
}
