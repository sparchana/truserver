package models.entity;


import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "candidate")
@Table(name = "candidate")
public class Candidate extends Model {
    @Id
    @Column(name = "CandidateId", columnDefinition = "int signed not null", unique = true)
    public long candidateId = 0;

    @Column(name = "candidateUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String candidateUUId = "";

    @Column(name = "LeadId", columnDefinition = "int signed not null", unique = true)
    public long leadId = 0;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null")
    public String candidateName = "";

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    public String candidateMobile = "";

    @Column(name = "CandidateType", columnDefinition = "int signed not null default 0")
    public int candidateState = 0;

    @Column(name = "CandidateChannel", columnDefinition = "int signed not null default 0")
    public int candidateChannel = 0;

    @Column(name = "CandidateJobInterest", columnDefinition = "varchar(255) null ")
    public String candidateJobIntereset = ""; // separated by , in case of multiple jobs

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp;

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp not null")
    public Timestamp candidateUpdateTimestamp;

    public static Finder<String, Candidate> find = new Finder(Candidate.class);

}


