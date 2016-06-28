package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by zero on 21/6/16.
 */
@Entity(name = "expcategory")
@Table(name = "expcategory")
public class ExpCategory extends Model{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ExpCategoryId", columnDefinition = "int signed", unique = true)
    private int expCategoryId;

    @Column(name = "ExpCategoryName", columnDefinition = "text null")
    private String expCategoryName;

    public static Model.Finder<String, ExpCategory> find = new Model.Finder(ExpCategory.class);

    public int getExpCategoryId() {
        return expCategoryId;
    }

    public void setExpCategoryId(int expCategoryId) {
        this.expCategoryId = expCategoryId;
    }

    public String getExpCategoryName() {
        return expCategoryName;
    }

    public void setExpCategoryName(String expCategoryName) {
        this.expCategoryName = expCategoryName;
    }
}
