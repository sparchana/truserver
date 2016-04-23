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

@Entity(name = "candidateleads")
@Table(name = "candidateleads")
public class CandidateLeads extends Model {
    @Id
    @Column(name = "CandidateLeadId", columnDefinition = "int signed not null", unique = true)
    public long candidateLeadId = 0;

    @Column(name = "CandidateLeadName", columnDefinition = "varchar(50) not null default 0")
    public String candidateLeadName = "";

    @Column(name = "CandidateLeadMobile", columnDefinition = "int signed not null default 0")
    public int candidateLeadMobile = 0;

    @Column(name = "CandidateLeadType", columnDefinition = "int signed not null default 0")
    public int candidateLeadType = 0;

    @Column(name = "CandidateLeadChannel", columnDefinition = "int signed not null default 0")
    public int candidateLeadChannel = 0;

    @Column(name = "CandidateLeadCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateLeadCreateTimestamp;

    @Column(name = "CandidateLeadUpdateTimestamp", columnDefinition = "timestamp not null")
    public Timestamp candidateLeadUpdateTimestamp;

    public static Finder<String, CandidateLeads> find = new Finder(CandidateLeads.class);

}


