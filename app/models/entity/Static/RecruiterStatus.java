package models.entity.Static;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "recruiterstatus")
@Table(name = "recruiterstatus")
public class RecruiterStatus extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "RecruiterStatusId", columnDefinition = "bigint signed", unique = true)
    private Integer recruiterStatusId;

    @Column(name = "RecruiterStatusName", columnDefinition = "varchar(20) not null")
    private String recruiterStatusName;
}
