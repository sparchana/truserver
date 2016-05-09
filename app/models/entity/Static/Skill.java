package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.JobToSkill;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 6/5/16.
 */
@Entity(name = "skill")
@Table(name = "skill")
public class Skill extends Model{
    @Id
    @Column(name = "SkillId", columnDefinition = "int signed not null", unique = true)
    public int skillId = 0;

    @Column(name = "skillName", columnDefinition = "varchar(100) null")
    public String skillName = "";

    @Column(name = "skillDescription", columnDefinition = "varchar(255) null")
    public String skillDescription = "";

    @JsonManagedReference
    @OneToMany(mappedBy = "skill", cascade = CascadeType.REMOVE)
    public List<JobToSkill> jobToSkillList;

    public static Model.Finder<String, Skill> find = new Model.Finder(Skill.class);
}
