package models.entity;

import api.CandidateSignUpRequest;
import api.CandidateSignUpResponse;
import api.Util.SmsUtil;
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

    @Column(name = "CandidateOtp", columnDefinition = "int signed not null default 1234", length = 4)
    public int candidateOtp = 1234;

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
            candidate.candidateAge = 0;
            candidate.candidateStatusId = 0;
            int randomPIN = (int)(Math.random()*9000)+1000;
            String otpCode = String.valueOf(randomPIN);
            candidate.candidateOtp = randomPIN;

            candidate.save();

            SmsUtil.sendSms(candidate.candidateMobile,otpCode);

            Logger.info("Candidate successfully registered " + candidate);
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        } else {
            Logger.info("Candidate already exists");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
        }
        return candidateSignUpResponse;
    }

    public static CandidateSignUpResponse verifyOtp(CandidateSignUpRequest candidateSignUpRequest) {
        int candidateOtp = candidateSignUpRequest.getCandidateOtp();
        String candidateMobile = candidateSignUpRequest.getAutoCandidateMobile();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
        if(existingCandidate != null){
            if(existingCandidate.candidateOtp == candidateOtp){
                existingCandidate.candidateStatusId = 1;
                existingCandidate.update();
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                Logger.info("OTP CORRECT!");
            }
            else{
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_INCORRECT_OTP);
                Logger.info("OTP INCORRECT!");
            }
        }

        else{
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Wrong otp!");
        }
        return candidateSignUpResponse;
    }
}
