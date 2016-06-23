package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "screeningstatus")
@Table(name = "screeningstatus")
public class ScreeningStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ScreeningStatusId", columnDefinition = "bigint signed", unique = true)
    private Integer secruiterStatusId;

    @Column(name = "ScreeningStatusName", columnDefinition = "varchar(20) not null")
    private String screeningStatusName;
}
