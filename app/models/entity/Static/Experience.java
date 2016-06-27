package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 16/6/16.
 */
@Entity(name = "experience")
@Table(name = "experience")
public class Experience extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ExperienceId", columnDefinition = "bigint signed", unique = true)
    private Integer experienceId;

    @Column(name = "ExperienceType", columnDefinition = "varchar(20) null")
    private String experienceType;

    public static Model.Finder<String, Experience> find = new Model.Finder(Experience.class);

    public Integer getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Integer experienceId) {
        this.experienceId = experienceId;
    }

    public String getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
    }
}
