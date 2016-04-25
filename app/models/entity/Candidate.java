package models.entity;

import api.CandidateSignUpRequest;
import api.CandidateSignUpResponse;
import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 25/4/16.
 */

@Entity(name = "candidate")
@Table(name = "candidate")
public class Candidate extends Model {
    @Id
    @Column(name = "CandidateId", columnDefinition = "int signed not null", unique = true)
    public long candidateId = 0;

    @Column(name = "CandidateStatusId", columnDefinition = "int signed not null default 0")
    public long candidateStatusId = 0;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null default 0")
    public String candidateName = "";

    @Column(name = "CandidateMobile", columnDefinition = "varchar(10) not null default 0")
    public String candidateMobile = "";

    @Column(name = "CandidateAge", columnDefinition = "int signed not null default 0")
    public int candidateAge = 0;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp;

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp not null default 0")
    public Timestamp candidateUpdateTimestamp;

    @Column(name = "CandidateotpID", columnDefinition = "int signed not null default 1234", length = 4)
    public String candidateOtpID = "";

    public static Model.Finder<String, Candidate> find = new Model.Finder(Candidate.class);

    public static CandidateSignUpResponse candidateSignUp(CandidateSignUpRequest candidateSignUpRequest) {
        String mobile = candidateSignUpRequest.getCandidateMobile();
        Logger.info("inside signup method");

        Candidate candidate = new Candidate();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();
        if(existingCandidate == null) {
            candidate.candidateId = (int)(Math.random()*9000)+100000;
            candidate.candidateName = candidateSignUpRequest.getCandidateName();
            candidate.candidateMobile = candidateSignUpRequest.getCandidateMobile();
            candidate.candidateAge = 23;
            candidate.candidateStatusId = 1;

            int randomPIN = (int)(Math.random()*9000)+1000;
            String otpCode = String.valueOf(randomPIN);
            candidate.candidateOtpID = otpCode;

            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            candidate.save();

            Logger.info("saved candidate " + candidate);
        } else {
            Logger.info("Candidate already exists");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
        }
        return candidateSignUpResponse;
    }
}
