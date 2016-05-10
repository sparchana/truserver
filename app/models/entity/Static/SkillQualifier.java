package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by zero on 10/5/16.
 */
@Entity(name = "skillqualifier")
@Table(name = "skillqualifier")
public class SkillQualifier extends Model {
    @Id
    @Column(name = "skillqualifierId", columnDefinition = "int signed", nullable = false, unique = true)
    public int skillqualifierId = 0;

    @Column(name = "Qualifier", columnDefinition = "varchar(100) null")
    public String qualifier;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    public Skill skill;

    public static Finder<String, SkillQualifier> find = new Finder(SkillQualifier.class);

}