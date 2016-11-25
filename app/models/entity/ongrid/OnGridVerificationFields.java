package models.entity.ongrid;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import models.entity.Static.JobRole;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@CacheStrategy
@Entity(name = "ongrid_verification_fields")
@Table(name = "ongrid_verification_fields")
public class OnGridVerificationFields extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "field_id", columnDefinition = "bigint signed", unique = true)
    private long fieldId;

    @Column(name = "field_name", columnDefinition = "varchar(255) null")
    private String fieldName;

    @Column(name = "field_type", columnDefinition = "varchar(255) null")
    private String fieldType;

    public static Finder<String, OnGridVerificationFields> find = new Finder(OnGridVerificationFields.class);

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String jobRole) {
        this.fieldType = fieldType;
    }
}
