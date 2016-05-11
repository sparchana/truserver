package models.entity.OO;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.JobRole;

import javax.persistence.*;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "candidatecurrentjobdetail")
@Table(name = "candidatecurrentjobdetail")
public class CandidateCurrentJobDetail extends Model{
    @Id
    @Column(name = "CandidateCurrentJobId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateCurrentJobId = 0;

    @Column(name = "CandidateCurrentCompany", columnDefinition = "bigint signed null")
    public long candidateCurrentCompany = 0;

    @Column(name = "CandidateCurrentJobLocation", columnDefinition = "bigint signed null")
    public long candidateCurrentJobLocation = 0;

    @Column(name = "CandidateTransportationMode", columnDefinition = "int null")
    public int candidateTransportationMode = 0; // Inner Join to Static.TransportationMode table

    @Column(name = "CandidateCurrentWorkShift", columnDefinition = "int null")
    public int candidateCurrentWorkShift = 0;

    @Column(name = "CandidateCurrentDesignation", columnDefinition = "varchar(30) null")
    public String candidateCurrentDesignation = " ";

    @Column(name = "CandidateCurrentSalary", columnDefinition = "bigint signed null")
    public long candidateCurrentSalary = 0;

    @Column(name = "CandidateCurrentJobDuration", columnDefinition = "int signed null")
    public int candidateCurrentJobDuration = 0;

    @Column(name = "CandidateCurrentEmployerRefName", columnDefinition = "varchar(100) null")
    public String candidateCurrentEmployerRefName = "";

    @Column(name = "CandidateCurrentEmployerRefMobile", columnDefinition = "varchar(13) null")
    public String candidateCurrentEmployerRefMobile = "";

    @Column(name = "CandidateCurrentJob", columnDefinition = "bigint signed null")
    public long candidateCurrentJob = 0;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public long updateTimeStamp = 0;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    public JobRole jobRole;

    public static Finder<String, CandidateCurrentJobDetail> find = new Finder(CandidateCurrentJobDetail.class);

}
