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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "skillqualifierId", columnDefinition = "int signed", unique = true)
    private int skillqualifierId = 0;

    @Column(name = "Qualifier", columnDefinition = "varchar(100) null")
    private String qualifier;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;

    public static Finder<String, SkillQualifier> find = new Finder(SkillQualifier.class);

    public int getSkillqualifierId() {
        return skillqualifierId;
    }

    public void setSkillqualifierId(int skillqualifierId) {
        this.skillqualifierId = skillqualifierId;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}