package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "leadtype")
@Table(name = "leadtype")
public class LeadType extends Model{
    @Id
    @Column(name = "LeadTypeId", columnDefinition = "int signed not null", nullable = false, unique = true)
    public long leadTypeId = 0;

    @Column(name = "LeadTypeName", columnDefinition = "varchar(50) not null default 0", nullable = false)
    public String leadTypeName = "";
}
