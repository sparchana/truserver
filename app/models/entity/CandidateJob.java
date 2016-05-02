package models.entity;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by batcoder1 on 30/4/16.
 */
@Entity(name = "candidatejob")
@Table(name = "candidatejob")
public class CandidateJob extends Model {
    @Id
    @Column(name = "CandidateJobId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateJobId = 0;

    @Column(name = "CandidateJobCandidateId", columnDefinition = "bigint not null default 0")
    public long candidateJobCandidateId = 0;

    @Column(name = "CandidateJobJobId", columnDefinition = "varchar(4) not null default 0")
    public String candidateJobJobId = "";

    public static Model.Finder<String, CandidateJob> find = new Model.Finder(CandidateJob.class);

}
