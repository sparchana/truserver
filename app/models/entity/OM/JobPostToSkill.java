package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
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

    @Column(name = "JobPostToSkillCreateTimeStamp", columnDefinition = "timestamp not null")
    private Timestamp jobPostToSkillCreateTimeStamp;

    @UpdatedTimestamp
    @Column(name = "JobPostToSkillUpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobPostToSkillUpdateTimeStamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName= "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SkillId", referencedColumnName = "SkillId")
    private Skill skill;

    public JobPostToSkill(){
        this.jobPostToSkillCreateTimeStamp = new Timestamp(System.currentTimeMillis());
    }
}
