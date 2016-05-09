package models.entity;

import api.ServerConstants;
import api.http.*;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.*;
import models.entity.OO.CandidateCurrentJobDetail;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.Education;
import models.entity.Static.Language;
import models.entity.Static.Locality;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static play.mvc.Controller.session;

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

    @Column(name = "CandidateLastName", columnDefinition = "varchar(50) not null")
    public String candidateLastName = "";

    @Column(name = "CandidateGender", columnDefinition = "int(1) null default 0")
    public int candidateGender = 0;

    @Column(name = "CandidateDOB", columnDefinition = "varchar(20) null default 0")
    public String candidateDOB = "";

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    public String candidateMobile = "";

    @Column(name = "CandidatePhoneType", columnDefinition = "varchar(100) null")
    public String candidatePhoneType = "";

    @Column(name = "CandidateMaritalStatus", columnDefinition = "int null default 0")
    public int candidateMaritalStatus = 0;

    @Column(name = "CandidateEmail", columnDefinition = "varchar(50) not null")
    public String candidateEmail = "";

    @Column(name = "CandidateIsEmployed", columnDefinition = "int not null")
    public int candidateIsEmployed = 0;

    @Column(name = "CandidateTotalExperience", columnDefinition = "decimal(3,2) signed null default 0.00")
    public float candidateTotalExperience = 0;  // data in years

    @Column(name = "CandidateType", columnDefinition = "int signed not null default 0")
    public int candidateState = 0;

    @Column(name = "CandidateChannel", columnDefinition = "int signed not null default 0")
    public int candidateChannel = 0;

    @Column(name = "CandidateAge", columnDefinition = "int signed not null default 0")
    public int candidateAge = 0;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp candidateUpdateTimestamp;

    @Column(name = "CandidateOtp", columnDefinition = "int signed not null default 1234")
    public int candidateOtp = 1234;

    @Column(name = "CandidateIsAssessed", columnDefinition = "int signed not null default 0")
    public int candidateIsAssessed = 0;

    @Column(name = "CandidateSalarySlip", columnDefinition = "int signed not null default 0")
    public int candidateSalarySlip = 0;

    @Column(name = "CandidateAppointmentLetter", columnDefinition = "int signed not null default 0")
    public int candidateAppointmentLetter = 0;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<IDProofreference> idProofreferenceList;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<JobHistory> jobHistoryList;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<JobPreference> jobPreferencesList;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<LanguagePreference> languagePreferenceList;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<LocalityPreference> localityPreferenceList;

    @OneToMany(mappedBy = "candidate")
    public List<CandidateSkill> candidateSkillList;

    @OneToOne(mappedBy = "candidate")
    public CandidateCurrentJobDetail candidateCurrentJobDetail;

    @OneToOne(mappedBy = "candidate")
    public TimeShiftPreference timeShiftPreference;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "CandidateMotherTongue", referencedColumnName = "languageId")
    public Language motherTongue;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CandidateHomeLocality")
    public Locality locality;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JsonManagedReference
    @JoinColumn(name = "CandidateStatusId", referencedColumnName = "profileStatusId")
    public CandidateProfileStatus candidateprofilestatus;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "EducationId", referencedColumnName = "EducationId")
    public Education education;


    public static Finder<String, Candidate> find = new Finder(Candidate.class);

    public static CandidateSignUpResponse candidateSignUp(CandidateSignUpRequest candidateSignUpRequest) {
        String mobile = "+91" + candidateSignUpRequest.getCandidateMobile();
        Logger.info("inside signup method" );

        Candidate candidate = new Candidate();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();
        Lead lead = new Lead();

        String otpCode = null;
        if(existingCandidate == null ) {
            Lead existingLead = Lead.find.where().eq("leadMobile", mobile).findUnique();
            if(existingLead == null) {
                lead.leadId = Util.randomLong();
                lead.leadMobile = "+91" + candidateSignUpRequest.getCandidateMobile();
                lead.leadUUId = UUID.randomUUID().toString();
                lead.leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
                lead.leadName = candidateSignUpRequest.getCandidateName();
                lead.leadType = ServerConstants.TYPE_CANDIDATE;
                lead.save();

                candidate.leadId = lead.leadId;
                candidate.candidateId = Util.randomLong();
                candidate.candidateUUId = UUID.randomUUID().toString();
                candidate.candidateName = candidateSignUpRequest.getCandidateName();
                candidate.candidateMobile = "+91" + candidateSignUpRequest.getCandidateMobile();
                candidate.candidateAge = 0;
                candidate.candidateprofilestatus.profileStatusId = 0;
                int randomPIN = (int)(Math.random()*9000)+1000;
                otpCode = String.valueOf(randomPIN);
                candidate.candidateOtp = randomPIN;
                candidate.save();
            }
            else{
                candidate.leadId = existingLead.leadId;
                candidate.candidateId = Util.randomLong();
                candidate.candidateUUId = UUID.randomUUID().toString();
                candidate.candidateName = candidateSignUpRequest.getCandidateName();
                candidate.candidateMobile = "+91" + candidateSignUpRequest.getCandidateMobile();
                candidate.candidateAge = 0;
                candidate.candidateprofilestatus.profileStatusId = 0;
                int randomPIN = (int)(Math.random()*9000)+1000;
                otpCode = String.valueOf(randomPIN);
                candidate.candidateOtp = randomPIN;
                candidate.save();
            }

                List<String> locality = Arrays.asList(candidateSignUpRequest.getCandidateLocality().split("\\s*,\\s*"));
                for(String  s : locality) {
                    CandidateLocality candidateLocality = new CandidateLocality();
                    candidateLocality.candidateLocalityId = Util.randomLong();
                    candidateLocality.candidateLocalityCandidateId = candidate.candidateId;
                    candidateLocality.candidateLocalityLocalityId = s;
                    candidateLocality.save();
                }

                List<String> jobs = Arrays.asList(candidateSignUpRequest.getCandidateJobPref().split("\\s*,\\s*"));
                for(String  s : jobs) {
                    CandidateJob candidateJob = new CandidateJob();
                    candidateJob.candidateJobId = Util.randomLong();
                    candidateJob.candidateJobCandidateId = candidate.candidateId;
                    candidateJob.candidateJobJobId = s;
                    candidateJob.save();
                }
                String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to register";

                SmsUtil.sendSms(candidate.candidateMobile,msg);
                Logger.info("Candidate successfully registered " + candidate);
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        }
        else if(existingCandidate != null && existingCandidate.candidateprofilestatus.profileStatusId == 0) {
            int randomPIN = (int)(Math.random()*9000)+1000;
            otpCode = String.valueOf(randomPIN);
            existingCandidate.candidateOtp = randomPIN;
            existingCandidate.candidateName = candidateSignUpRequest.getCandidateName();
            existingCandidate.candidateMobile = "+91" + candidateSignUpRequest.getCandidateMobile();
            existingCandidate.candidateAge = 0;
            existingCandidate.candidateprofilestatus.profileStatusId = ServerConstants.CANDIDATE_STATUS_NO_VERIFICATION;
            existingCandidate.update();

            List<CandidateLocality> allLocality = CandidateLocality.find.where().eq("candidateLocalityCandidateId", existingCandidate.candidateId).findList();
            for(CandidateLocality candidateLocality : allLocality){
                candidateLocality.delete();
            }

            List<String> locality = Arrays.asList(candidateSignUpRequest.getCandidateLocality().split("\\s*,\\s*"));
            for(String  s : locality) {
                CandidateLocality candidateLocality = new CandidateLocality();
                candidateLocality.candidateLocalityId = Util.randomLong();
                candidateLocality.candidateLocalityCandidateId = existingCandidate.candidateId;
                candidateLocality.candidateLocalityLocalityId = s;
                candidateLocality.save();
            }

            List<CandidateJob> allJob = CandidateJob.find.where().eq("candidateJobCandidateId", existingCandidate.candidateId).findList();
            for(CandidateJob candidateJobs : allJob){
                candidateJobs.delete();
            }

            List<String> jobs = Arrays.asList(candidateSignUpRequest.getCandidateJobPref().split("\\s*,\\s*"));
            for(String  s : jobs) {

                CandidateJob candidateJob = new CandidateJob();
                candidateJob.candidateJobId = Util.randomLong();
                candidateJob.candidateJobCandidateId = existingCandidate.candidateId;
                candidateJob.candidateJobJobId = s;
                candidateJob.save();
            }

            String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to register";
            SmsUtil.sendSms(existingCandidate.candidateMobile,msg);
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
        String candidateMobile = "+91" + candidateSignUpRequest.getAutoCandidateMobile();
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
        Logger.info( existingCandidate.candidateName + " " + existingCandidate.candidateOtp + " " + candidateOtp);
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
        String candidateMobile = "+91" + loginRequest.getCandidateLoginMobile();
        String candidatePassword = loginRequest.getCandidateLoginPassword();
        LoginResponse loginResponse = new LoginResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
        if(existingCandidate == null){
            loginResponse.setStatus(loginResponse.STATUS_NO_USER);
            Logger.info("User Does not Exists");
        }
        else {
            long candidateId = existingCandidate.candidateId;
            Auth existingAuth = Auth.find.where().eq("candidateId",candidateId).findUnique();
            if(existingAuth != null){
                if (((existingAuth.passwordMd5.equals(Util.md5(candidatePassword + existingAuth.passwordSalt))) &&
                        (existingCandidate.candidateprofilestatus.profileStatusId != 0))) {
                    Logger.info(existingCandidate.candidateName + " " + existingCandidate.candidateprofilestatus.profileStatusId);
                    loginResponse.setCandidateId(existingCandidate.candidateId);
                    loginResponse.setCandidateName(existingCandidate.candidateName);
                    loginResponse.setAccountStatus(existingCandidate.candidateprofilestatus.profileStatusId);
                    loginResponse.setCandidateEmail(existingCandidate.candidateEmail);
                    loginResponse.setStatus(loginResponse.STATUS_SUCCESS);

                    existingAuth.authSessionId = UUID.randomUUID().toString();
                    existingAuth.authSessionIdExpiryMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
                    session("sessionId", existingAuth.authSessionId);
                    session("sessionExpiry", String.valueOf(existingAuth.authSessionIdExpiryMillis));
                    existingAuth.update();
                    Logger.info("Login Successful");
                }
                else {
                    loginResponse.setStatus(loginResponse.STATUS_WRONG_PASSWORD);
                    Logger.info("Incorrect Password");
                }
            }
            else {
                loginResponse.setStatus(loginResponse.STATUS_NO_USER);
                Logger.info("No User");
            }
        }
        return loginResponse;
    }

    public static ResetPasswordResponse checkCandidate(ResetPasswordResquest resetPasswordResquest) {
        String candidateResetMobile = resetPasswordResquest.getResetPasswordMobile();
        ResetPasswordResponse resetPasswordResponse= new ResetPasswordResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + candidateResetMobile).findUnique();
        if(existingCandidate != null){
            if(existingCandidate.candidateprofilestatus.profileStatusId == 2){
                int randomPIN = (int)(Math.random()*9000)+1000;
                String otpCode = String.valueOf(randomPIN);
                existingCandidate.candidateOtp = randomPIN;
                existingCandidate.update();
                String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to reset password";
                SmsUtil.sendSms(existingCandidate.candidateMobile, msg);

            }
            else{
                Logger.info("Reset otp sent");
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            }
        }
        else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Verification failed");
        }
        return resetPasswordResponse;
    }
    public static ResetPasswordResponse checkResetOtp(ResetPasswordResquest resetPasswordResquest) {
        String candidateMobile = resetPasswordResquest.getCandidateForgotMobile();
        int candidateOtp = resetPasswordResquest.getCandidateForgotOtp();
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();

        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + candidateMobile).findUnique();
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


