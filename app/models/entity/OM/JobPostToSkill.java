package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.JobPost;
import models.entity.Static.Skill;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 16/6/16.
 */
@Entity(name = "jobposttoskill")
@Table(name = "jobposttoskill")
public class JobPostToSkill extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobPostToSkillId", columnDefinition = "bigint signed not null", unique = true)
    private Long jobPostToSkillId;

    @Column(name = "JobPostToSkillUpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToSkillUpdateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName= "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;
}
