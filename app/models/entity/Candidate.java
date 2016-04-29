package models.entity;

import api.*;
import com.avaje.ebean.Model;
import models.util.SmsUtil;
import models.util.Util;
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

    @Column(name = "CandidateEmail", columnDefinition = "varchar(50) not null default 0")
    public String candidateEmail = "";

    @Column(name = "CandidateLocality", columnDefinition = "varchar(150) not null default 0")
    public String candidateLocality = "";

    @Column(name = "CandidateJobPref", columnDefinition = "varchar(50) not null default 0")
    public String candidateJobPref = "";

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
        Logger.info("inside signup method" + candidateSignUpRequest.getCandidateLocality());

        Candidate candidate = new Candidate();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();
        if(existingCandidate == null ) {
            candidate.candidateId = (int)(Math.random()*9000)+100000;
            candidate.candidateName = candidateSignUpRequest.getCandidateName();
            candidate.candidateMobile = candidateSignUpRequest.getCandidateMobile();
            candidate.candidateAge = 0;
            candidate.candidateStatusId = 0;
            candidate.candidateJobPref = candidateSignUpRequest.getCandidateJobPref();
            candidate.candidateLocality = candidateSignUpRequest.getCandidateLocality();
            int randomPIN = (int)(Math.random()*9000)+1000;
            String otpCode = String.valueOf(randomPIN);
            candidate.candidateOtp = randomPIN;
            candidate.save();

            SmsUtil.sendSms(candidate.candidateMobile,otpCode);
            Logger.info("Candidate successfully registered " + candidate);
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        }
        else if(existingCandidate != null && existingCandidate.candidateStatusId == 0) {
            int randomPIN = (int)(Math.random()*9000)+1000;
            String otpCode = String.valueOf(randomPIN);
            existingCandidate.candidateOtp = randomPIN;
            existingCandidate.candidateName = candidateSignUpRequest.getCandidateName();
            existingCandidate.candidateMobile = candidateSignUpRequest.getCandidateMobile();
            existingCandidate.candidateAge = 0;
            existingCandidate.candidateStatusId = 0;
            existingCandidate.candidateJobPref = candidateSignUpRequest.getCandidateJobPref();
            existingCandidate.candidateLocality = candidateSignUpRequest.getCandidateLocality();
            existingCandidate.update();
            SmsUtil.sendSms(existingCandidate.candidateMobile,otpCode);
            Logger.info("Candidate successfully registered " + candidate);
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        }
        else {
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
        Logger.info("--> " + existingCandidate.candidateName + " " + existingCandidate.candidateOtp + " " + candidateOtp + "<--");
        if(existingCandidate != null){
            if(existingCandidate.candidateOtp == candidateOtp){
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                Logger.info("OTP correct!");
            }
            else{
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_INCORRECT_OTP);
                Logger.info("OTP incorrect!");
            }
        }
        else{
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Verification failed");
        }
        return candidateSignUpResponse;
    }

    public static LoginResponse login(LoginRequest loginRequest) {
        String candidateMobile = loginRequest.getCandidateLoginMobile();
        String candidatePassword = loginRequest.getCandidateLoginPassword();
        LoginResponse loginResponse = new LoginResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
        if(existingCandidate == null){

            loginResponse.setStatus(loginResponse.STATUS_NO_USER);
            Logger.info("User Does not Exists");
        }
        else {
            Logger.info(" -> Incomming " + loginRequest.getCandidateLoginMobile());
            long candidateId = existingCandidate.candidateId;
            Auth existingAuth = Auth.find.where().eq("candidateId",candidateId).findUnique();
            if (((existingAuth.passwordMd5.equals(Util.md5(candidatePassword + existingAuth.passwordSalt))) &&
                    (existingCandidate.candidateStatusId != 0))) {
                Logger.info(existingCandidate.candidateName + " " + existingCandidate.candidateStatusId);
                loginResponse.setCandidateName(existingCandidate.candidateName);
                loginResponse.setAccountStatus(existingCandidate.candidateStatusId);
                loginResponse.setCandidateEmail(existingCandidate.candidateEmail);
                loginResponse.setStatus(loginResponse.STATUS_SUCCESS);
                Logger.info("Login Successful");
            }
            else {
                loginResponse.setStatus(loginResponse.STATUS_FAILURE);
                Logger.info("User Does not Exists");
            }
        }
        return loginResponse;
    }

    public static ResetPasswordResponse checkCandidate(ResetPasswordResquest resetPasswordResquest) {
        String candidateResetMobile = resetPasswordResquest.getResetPasswordMobile();
        ResetPasswordResponse resetPasswordResponse= new ResetPasswordResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateResetMobile).findUnique();
        if(existingCandidate != null){
            int randomPIN = (int)(Math.random()*9000)+1000;
            String otpCode = String.valueOf(randomPIN);
            existingCandidate.candidateOtp = randomPIN;
            existingCandidate.update();
            SmsUtil.sendSms(existingCandidate.candidateMobile, otpCode);
            Logger.info("Reset otp sent");

            resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
        }
        else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_FAILURE);
            Logger.info("Verification failed");
        }
        return resetPasswordResponse;
    }
    public static ResetPasswordResponse checkResetOtp(ResetPasswordResquest resetPasswordResquest) {
        String candidateMobile = resetPasswordResquest.getCandidateForgotMobile();
        int candidateOtp = resetPasswordResquest.getCandidateForgotOtp();
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();

        Logger.info(resetPasswordResquest.getCandidateForgotOtp() + " ---");
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
        if(existingCandidate != null){

            Logger.info(existingCandidate.candidateOtp + " = " + candidateOtp);
            if(existingCandidate.candidateOtp == candidateOtp){
                resetPasswordResponse.setStatus(ResetPasswordResponse.STATUS_SUCCESS);
                Logger.info("Otp Correct");
            }
            else{
                resetPasswordResponse.setStatus(ResetPasswordResponse.STATUS_FAILURE);
                Logger.info("Otp Incorrect");
            }
        }
        else{
            resetPasswordResponse.setStatus(ResetPasswordResponse.STATUS_FAILURE);
            Logger.info("No User");
        }
        return resetPasswordResponse;
    }
}
