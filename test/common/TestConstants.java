package common;

import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zero on 7/6/16.
 */
public class TestConstants {
    public static final int TEST_SERVER_PORT = 9001;
    public static final String BASE_URL = "http://localhost:" + TEST_SERVER_PORT;

    // defaults for creating candidate object
    public static final Long    testCandidateId = (long) 100002;
    public static final String  testCandidateUUId = "4079cb54-34b1-4b68-a479-ede86c6f9c58";
    public static final String  testCandidateName= "TEST CANDIDATE";
    public static final String  testCandidateLastName = "LAST NAME";
    public static final String  testCandidateMobile = "+919019672209";
    public static final String  testCandidatePhoneType = "TESTPhoneType";
    public static final String  testCandidateEmail = "test@localhost.com";
    public static final Integer testCandidateGender = 1;
    public static final Integer testCandidateMaritalStatus = 1;
    public static final Integer testCandidateAge = 99;
    public static final Integer testCandidateSalarySlip = 1;
    public static final Integer testCandidateAppointmentLetter = 1;
    public static final List<Integer> testCandidateJobInterest = new ArrayList<Integer>() {{
        add(1);add(2);add(3);
    }};
    public static final List<Integer> testCandidateLocalityPreference = new ArrayList<Integer>() {{
        add(3);add(2);add(1);
    }};
    public static final Integer testLeadSource = 1;
    public static final String  testAdminId = "2209";
    public static final String  testAdminPassword = "TruJobs7";
    public static final String  testCandidateTimeShiftPref = "1";
    public static final Date testCandidateDob = Calendar.getInstance().getTime();
    public static final Integer testCandidateIsEmployed = 1;

    // Skill Profile start
    public static final Integer testCandidateTotalExperience = 1;  // data in months
    public static final String testCandidateCurrentCompany = "Test Current Company";
    public static final Integer testCandidateMotherTongue = 1;  //

    //CurrentJobDetails
    public static final String testCandidateCurrentJobDesignation = "TEST DESIGN";
    public static final Long testCandidateCurrentSalary = 12500L;
    public static final Integer testCandidateCurrentJobDuration = 1;
    public static final Integer testCandidateCurrentWorkShift = 1;
    public static final Integer testCandidateCurrentJobRole = 1;
    public static final Integer testCandidateCurrentJobLocation = 1;
    public static final Integer testCandidateTransportation = 1;

    //Skills
    public static final List<CandidateSkills> testCandidateSkillList = new ArrayList<CandidateSkills>() {{
        CandidateSkills candidateSkill1 = new CandidateSkills();
        candidateSkill1.setId("7");
        candidateSkill1.setQualifier("Yes");

        CandidateSkills candidateSkill2 = new CandidateSkills();
        candidateSkill2.setId("8");
        candidateSkill2.setQualifier("No");

        add(candidateSkill1);
        add(candidateSkill2);
    }};

    //Language Known
    public static final List<CandidateKnownLanguage> testCandidateLanguageKnownList = new ArrayList<CandidateKnownLanguage>() {{
        CandidateKnownLanguage candidateKnownLanguage1 = new CandidateKnownLanguage();
        candidateKnownLanguage1.setId("1");
        candidateKnownLanguage1.setR(1);
        candidateKnownLanguage1.setS(1);
        candidateKnownLanguage1.setW(1);

        add(candidateKnownLanguage1);
    }};

    //education
    public static final String testCandidateEducationInstitute = "Test Education Institute";
    public static final Integer testCandidateDegree = 1;
    public static final Integer testCandidateEducationLevel = 1;
    // other informations
    public static final Integer testCandidateHomeLocality = 1;

    //id proof preference
    public static final List<Integer> testCandidateIdProof = new ArrayList<Integer>(){{
        add(1); add(2);
    }};
    public static final String testCandidatePastCompany = "Test Past Company";
    public static final Long testCandidatePastJobSalary = 11000L;
    public static final Integer testCandidatePastJobRole = 1;

    public static final List<JobRole> testCandidateJobInterestResult = new ArrayList<JobRole>() {{
        JobRole jobRole = new JobRole();
        jobRole.setJobRoleId(1);
        jobRole.setJobName("TestA");
        add(jobRole);
        jobRole.setJobRoleId(2);
        jobRole.setJobName("TestB");
        jobRole.setJobRoleId(3);
        jobRole.setJobName("TestC");
        add(jobRole);
        add(jobRole);
    }};
    public static final List<Locality> testCandidateLocalityPreferenceResult = new ArrayList<Locality>() {{
        Locality locality = new Locality();
        locality.setLocalityId(1);
        add(locality);
        locality.setLocalityId(2);
        add(locality);
        locality.setLocalityId(3);
        add(locality);
    }};

}
