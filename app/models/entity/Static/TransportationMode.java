package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "transportationmodes")
@Table(name = "transportationmodes")
public class TransportationMode extends Model{
    @Id
    @Column(name = "TransportationModeId", columnDefinition = "int signed null", unique = true)
    public int transportationModeId = 0;

    @Column(name = "TransportationModeName", columnDefinition = "varchar(255) null")
    public String transportationModeName = "";

    public static Finder<String, TransportationMode> find = new Finder(TransportationMode.class);

}
