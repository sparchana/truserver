package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by adarsh on 9/9/16.
 */

@Entity(name = "city")
@Table(name = "city")
public class City extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "city_id", columnDefinition = "bigint signed not null", nullable = false, unique = true)
    private long cityId = 0;

    @Column(name = "city_name", columnDefinition = "varchar(255) null")
    private Timestamp cityName;

    public static Finder<String, City> find = new Finder(City.class);

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public Timestamp getCityName() {
        return cityName;
    }

    public void setCityName(Timestamp cityName) {
        this.cityName = cityName;
    }
}
