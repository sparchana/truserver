package models.entity.Static;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.entity.Lead;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zero on 2/6/16.
 */

@Entity(name = "leadsource")
@Table(name = "leadsource")
public class LeadSource extends Model {
    @Id
    @Column(name = "LeadSourceId", columnDefinition = "int signed null", nullable = false, unique = true)
    public int  leadSourceId;

    @Column(name = "LeadSourceName", columnDefinition = "varchar(255) null")
    public String leadSourceName;

    @JsonBackReference
    @OneToMany(mappedBy = "leadSource")
    public List<Lead> leadList;

    public static Model.Finder<String, LeadSource> find = new Model.Finder(LeadSource.class);
}
