package models.entity;

import api.*;
import api.http.CandidateSignUpRequest;
import api.http.CandidateSignUpResponse;
import api.http.ResetPasswordResponse;
import api.http.ResetPasswordResquest;
import com.avaje.ebean.Model;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 26/4/16.
 */

@Entity(name = "auth")
@Table(name = "auth")
public class Auth extends Model {
    @Id
    @Column(name = "AuthId", columnDefinition = "bigint signed not null")
    public long authId = 0;

    @Column(name = "CandidateId", columnDefinition = "bigint signed not null")
    public long candidateId = 0;

    @Column(name = "PasswordMd5", columnDefinition = "char(60) not null")
    public String passwordMd5 = "";

    @Column(name = "PasswordSalt", columnDefinition = "bigint signed not null")
    public long passwordSalt = 0;

    @Column(name = "AuthSessionId", columnDefinition = "varchar(50) not null", nullable = false)
    public String authSessionId = "";

    @Column(name = "AuthSessionIdExpiryMillis", columnDefinition = "bigint signed not null", nullable = false)
    public long authSessionIdExpiryMillis = 0;

    @Column(name = "authCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp authCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "authUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp authUpdateTimestamp;

    public static Model.Finder<String, Auth> find = new Model.Finder(Auth.class);

    public static CandidateSignUpResponse addAuth(CandidateSignUpRequest candidateSignUpRequest) {
        String candidatePassword = candidateSignUpRequest.getCandidatePassword();
        String candidateAuthMobile = "+91" + candidateSignUpRequest.getCandidateAuthMobile();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateAuthMobile).findUnique();
        Logger.info("getting : " + candidateAuthMobile + " got : " + existingCandidate.candidateMobile);
        if(existingCandidate != null) {
            Logger.info("Existing user mobile: " + existingCandidate.candidateMobile);
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.candidateId).findUnique();
            if(existingAuth != null){
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
            }
            else{
                Auth auth = new Auth();
                auth.authId =  (int)(Math.random()*9000)+100000;
                auth.candidateId = existingCandidate.candidateId;
                int passwordSalt = (new Random()).nextInt();
                auth.passwordMd5 = Util.md5(candidatePassword + passwordSalt);
                auth.passwordSalt = passwordSalt;
                auth.authSessionId = UUID.randomUUID().toString();
                auth.authSessionIdExpiryMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
                session("sessionId", auth.authSessionId);
                session("sessionExpiry", String.valueOf(auth.authSessionIdExpiryMillis));
                auth.save();
                Logger.info("Password saved");

                Interaction interaction = new Interaction();
                interaction.objectAUUId = existingCandidate.candidateUUId;
                interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                interaction.result = "New Candidate Added";
                interaction.save();
                Logger.info("Interaction added");

                existingCandidate.candidateStatusId = ServerConstants.CANDIDATE_STATUS_VERIFIED;
                existingCandidate.update();
                Logger.info("candidate status confirmed");

                Lead existingLead = Lead.find.where().eq("leadId", existingCandidate.leadId).findUnique();
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.update();
                Logger.info("Lead converted in candidate");

                String msg = "Hey " + existingCandidate.candidateName +
                        "! Welcome to Trujobs.in. Complete our skill assessment today and find your right job. Take assessment now: bit.ly/trujobstest";
                SmsUtil.sendSms(existingCandidate.candidateMobile,msg);

                candidateSignUpResponse.setCandidateId(existingCandidate.candidateId);
                candidateSignUpResponse.setCandidateName(existingCandidate.candidateName);
                candidateSignUpResponse.setAccountStatus(existingCandidate.candidateStatusId);
                candidateSignUpResponse.setCandidateEmail(existingCandidate.candidateEmail);
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            }
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("User Does not Exist!");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    public static ResetPasswordResponse savePassword(ResetPasswordResquest resetPasswordResquest) {
        String candidatePassword = resetPasswordResquest.getCandidateNewPassword();
        String candidateAuthMobile = resetPasswordResquest.getForgotPasswordNewMobile();
        ResetPasswordResponse resetPasswordResponse= new ResetPasswordResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + candidateAuthMobile).findUnique();

        Logger.info("Existing user mobile: " + existingCandidate.candidateMobile);

        if(existingCandidate != null) {
            Auth auth = Auth.find.where().eq("candidateId", existingCandidate.candidateId).findUnique();
            int passwordSalt = (new Random()).nextInt();
            auth.passwordMd5 = Util.md5(candidatePassword + passwordSalt);
            auth.passwordSalt = passwordSalt;
            auth.authSessionId = UUID.randomUUID().toString();
            auth.authSessionIdExpiryMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
            session("sessionId", auth.authSessionId);
            session("sessionExpiry", String.valueOf(auth.authSessionIdExpiryMillis));
            auth.update();

            resetPasswordResponse.setCandidateId(existingCandidate.candidateId);
            resetPasswordResponse.setCandidateName(existingCandidate.candidateName);
            resetPasswordResponse.setCandidateMobile(existingCandidate.candidateMobile);

            existingCandidate.candidateStatusId = ServerConstants.CANDIDATE_STATUS_VERIFIED;
            existingCandidate.update();
            resetPasswordResponse.setStatus(ResetPasswordResponse.STATUS_SUCCESS);
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("User Does not Exist!");
            resetPasswordResponse.setStatus(ResetPasswordResponse.STATUS_FAILURE);
        }
        return resetPasswordResponse;
    }
}