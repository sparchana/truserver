package models.entity;

import api.http.CandidateSignUpResponse;
import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "candidate")
@Table(name = "candidate")
public class Candidate extends Model {
    @Id
    @Column(name = "CandidateId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateId = 0;

    @Column(name = "candidateUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String candidateUUId = "";

    @Column(name = "LeadId", columnDefinition = "bigint signed not null", unique = true)
    public long leadId = 0;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null")
    public String candidateName = "";

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    public String candidateMobile = "";

    @Column(name = "CandidateType", columnDefinition = "int signed not null default 0")
    public int candidateState = 0;

    @Column(name = "CandidateChannel", columnDefinition = "int signed not null default 0")
    public int candidateChannel = 0;

    @Column(name = "CandidateStatusId", columnDefinition = "int signed not null default 0")
    public long candidateStatusId = 0;

    @Column(name = "CandidateEmail", columnDefinition = "varchar(50) not null")
    public String candidateEmail = "";

    @Column(name = "CandidateAge", columnDefinition = "int signed not null default 0")
    public int candidateAge = 0;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp candidateUpdateTimestamp;

    public static Finder<String, Candidate> find = new Finder(Candidate.class);


    public static CandidateSignUpResponse candidateSignUp(Candidate candidate) {
        Logger.info("inside signup method" );
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        candidate.save();
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setCandidateId(candidate.candidateId);
        candidateSignUpResponse.setCandidateName(candidate.candidateName);

        return candidateSignUpResponse;
    }

    public static CandidateSignUpResponse candidateUpdate(Candidate eCandidate, Candidate candidate) {
        Logger.info("inside Candidate Update method" );
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", eCandidate.candidateMobile).findUnique();

        existingCandidate.candidateName = candidate.candidateName;
        existingCandidate.candidateMobile = candidate.candidateMobile;
        existingCandidate.update();

        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setCandidateId(candidate.candidateId);
        candidateSignUpResponse.setCandidateName(candidate.candidateName);
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        return candidateSignUpResponse;
    }
}


