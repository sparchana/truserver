package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by batcoder1 on 28/4/16.
 */
@Entity(name = "locality")
@Table(name = "locality")
public class Locality extends Model {
    @Id
    @Column(name = "LocalityId", columnDefinition = "int signed not null", unique = true)
    public int localityId = 0;

    @Column(name = "LocalityName", columnDefinition = "varchar(50) not null")
    public String localityName = "";

    public static Model.Finder<String, Locality> find = new Model.Finder(Locality.class);

}