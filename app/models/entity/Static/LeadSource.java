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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "LeadSourceId", columnDefinition = "int signed", unique = true)
    private int  leadSourceId;

    @Column(name = "LeadSourceName", columnDefinition = "varchar(255) null")
    private String leadSourceName;

    @JsonBackReference
    @OneToMany(mappedBy = "leadSource")
    private List<Lead> leadList;

    public static Model.Finder<String, LeadSource> find = new Model.Finder(LeadSource.class);

    public int getLeadSourceId() {
        return leadSourceId;
    }

    public void setLeadSourceId(int leadSourceId) {
        this.leadSourceId = leadSourceId;
    }

    public String getLeadSourceName() {
        return leadSourceName;
    }

    public void setLeadSourceName(String leadSourceName) {
        this.leadSourceName = leadSourceName;
    }

    public List<Lead> getLeadList() {
        return leadList;
    }

    public void setLeadList(List<Lead> leadList) {
        this.leadList = leadList;
    }
}
