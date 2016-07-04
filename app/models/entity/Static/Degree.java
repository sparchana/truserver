package models.entity.Static;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

/**
 * Created by zero on 13/5/16.
 */
@CacheStrategy
@Entity(name = "degree")
@Table(name = "degree")
public class Degree extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "DegreeId", columnDefinition = "int signed", unique = true)
    private int degreeId = 0;

    @Column(name = "DegreeName", columnDefinition = "varchar(100) null")
    private String degreeName = "";

    public static Model.Finder<String, Degree> find = new Model.Finder(Degree.class);

    public int getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(int degreeId) {
        this.degreeId = degreeId;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }
}
