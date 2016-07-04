package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 6/5/16.
 */
@CacheStrategy
@Entity(name = "education")
@Table(name = "education")
public class Education extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "EducationId", columnDefinition = "int signed", unique = true)
    private int educationId = 0;

    @Column(name = "EducationName", columnDefinition = "varchar(255) null")
    private String educationName = "";

    public static Model.Finder<String, Education> find = new Model.Finder(Education.class);

    public int getEducationId() {
        return educationId;
    }

    public void setEducationId(int educationId) {
        this.educationId = educationId;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

}
