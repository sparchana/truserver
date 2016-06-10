package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "transportationmodes")
@Table(name = "transportationmodes")
public class TransportationMode extends Model{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "TransportationModeId", columnDefinition = "int signed", unique = true)
    private int transportationModeId;

    @Column(name = "TransportationModeName", columnDefinition = "varchar(255) null")
    private String transportationModeName;

    public static Finder<String, TransportationMode> find = new Finder(TransportationMode.class);

    public int getTransportationModeId() {
        return transportationModeId;
    }

    public void setTransportationModeId(int transportationModeId) {
        this.transportationModeId = transportationModeId;
    }

    public String getTransportationModeName() {
        return transportationModeName;
    }

    public void setTransportationModeName(String transportationModeName) {
        this.transportationModeName = transportationModeName;
    }
}
