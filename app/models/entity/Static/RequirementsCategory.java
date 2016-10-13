package models.entity.Static;

import com.avaje.ebean.Model;
import models.entity.OM.JobPostWorkflow;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 11/10/16.
 */

@Entity(name = "requirements_category")
@Table(name = "requirements_category")
public class RequirementsCategory extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "requirements_category_id", columnDefinition = "bigint unsigned", unique = true)
    private long requirementsCategoryId;

    @Column(name = "requirements_category_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String requirementsCategoryUUId = ""; // UUID

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp creationTimestamp;

    @Column(name = "requirements_category_title", columnDefinition = "varchar(255) null")
    private String requirementsCategory;

    public RequirementsCategory(){
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Model.Finder<String, RequirementsCategory> find = new Model.Finder(RequirementsCategory.class);

    public long getRequirementsCategoryId() {
        return requirementsCategoryId;
    }

    public String getRequirementsCategoryUUId() {
        return requirementsCategoryUUId;
    }

    public void setRequirementsCategoryUUId(String requirementsCategoryUUId) {
        this.requirementsCategoryUUId = requirementsCategoryUUId;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getRequirementsCategory() {
        return requirementsCategory;
    }

    public void setRequirementsCategory(String requirementsCategory) {
        this.requirementsCategory = requirementsCategory;
    }
}
