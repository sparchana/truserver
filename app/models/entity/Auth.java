package models.entity;

import api.CandidateSignUpRequest;
import api.CandidateSignUpResponse;
import api.Util.Util;
import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Created by batcoder1 on 26/4/16.
 */

@Entity(name = "auth")
@Table(name = "auth")
public class Auth extends Model {
    @Id
    @Column(name = "AuthId", columnDefinition = "int signed not null", unique = true)
    public long authId = 0;

    @Column(name = "CandidateId", columnDefinition = "int signed not null")
    public long candidateId = 0;

    @Column(name = "PasswordMd5", columnDefinition = "char(60) not null")
    public String passwordMd5 = "";

    @Column(name = "PasswordSalt", columnDefinition = "bigint signed not null")
    public long passwordSalt = 0;

    @Column(name = "authCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp authCreateTimestamp;

    @Column(name = "authUpdateTimestamp", columnDefinition = "timestamp not null default 0")
    public Timestamp authUpdateTimestamp;

    public static Model.Finder<String, Auth> find = new Model.Finder(Auth.class);

    public static CandidateSignUpResponse addAuth(CandidateSignUpRequest candidateSignUpRequest) {
        String candidatePassword = candidateSignUpRequest.getCandidatePassword();
        String candidateAuthMobile = candidateSignUpRequest.getCandidateAuthMobile();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateAuthMobile).findUnique();
        Logger.info("Existing user mobile: " + existingCandidate.candidateMobile);
        if(existingCandidate != null) {
            if(existingCandidate.candidateMobile.equals(candidateAuthMobile)){
                Auth auth = new Auth();
                auth.authId =  (int)(Math.random()*9000)+100000;
                auth.candidateId = existingCandidate.candidateId;
                int passwordSalt = (new Random()).nextInt();
                auth.passwordMd5 = Util.md5(candidatePassword + passwordSalt);
                auth.passwordSalt = passwordSalt;
                auth.save();

                existingCandidate.candidateStatusId = 1;
                existingCandidate.update();
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                Logger.info("Auth Save Successful");
            }
            else {
                Logger.info("User Does not Exist!");
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            }
        }
        else {
            Logger.info("User Does not Exist!");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }
}