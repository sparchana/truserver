package models.util;

import controllers.businessLogic.AuthService;
import controllers.truly.TrulyService;
import dao.CandidateDAO;
import dao.JobPostDAO;
import models.entity.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

import static api.ServerConstants.BASE_URL;

public class Util {

    private Util() {
    }

    public static final String ACTION_CREATE = "C";
    public static final String ACTION_UPDATE = "U";
    public static final String ACTION_DELETE = "D";
    public static final String ACTION_READ = "R";
    public static final int COMPANY_NAME_KEY_LENGTH = 3;
    public static final int COMPANY_CODE_LENGTH = 8;

    public static long randomLong() {
        long random = new Random().nextLong();
        if (random < 0) {
            random = -(random);
        }
        random = random % 100000000;
        return random;
    }

    public static int generateOtp() {
        int otpCode = (int) ((Math.random()*9000)+1000);
        return otpCode;
    }

    public static String generateCompanyCode(Company company) {
        return genCompanyCode(company);
    }

    public static String idToCode(long id) {
        return Base36.fromBase10(id);
    }

    public static long codeToId(String code) {
        return Base36.toBase10(code);
    }

    public static String genCompanyCode(Company company) {
        String companyName = company.getCompanyName().replaceAll("[^A-Za-z]","");
        if(companyName.length() > COMPANY_NAME_KEY_LENGTH) {
            companyName = (companyName.substring(0, COMPANY_NAME_KEY_LENGTH)).toUpperCase();
        } else {
            companyName =  companyName.toUpperCase();
        }
        String code = idToCode(company.getCompanyId());
        // return company code, decide the no of trailing zeros need to be added to make the total length 8
        String formatted = StringUtils.leftPad(code, COMPANY_CODE_LENGTH - companyName.length(), "0");
        return companyName + formatted;
    }

    public static int randomInt() {
        return new Random().nextInt();
    }

    public static String md5(String input) {
        String md5 = "";
        if (input == null) {
            input = "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {
            Logger.info(e.toString());
        }
        return md5;
    }

    public static int getAge(Date dateOfBirth){
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }
    public static Double RoundTo6Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.######");
        return Double.valueOf(df2.format(val));
    }
    public static Double RoundTo1Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#.#");
        return Double.valueOf(df2.format(val));
    }
    public static Double RoundTo2Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("##.##");
        return Double.valueOf(df2.format(val));
    }

    public static String generateApplyInShortUrl(Candidate candidate, JobPost jobPost, Auth auth){
        StringBuilder stringBuilder = new StringBuilder();

        if(candidate != null && jobPost != null) {
            stringBuilder.append(BASE_URL);
            stringBuilder.append("/apply/inshort/");
            stringBuilder.append(jobPost.getJobPostTitle().replaceAll("[\\s]","-").toLowerCase());
            stringBuilder.append("-jobs-in-bangalore-at-");
            stringBuilder.append(jobPost.getCompany().getCompanyName().replaceAll("[\\s]","-").toLowerCase());
            stringBuilder.append("-"+jobPost.getJobPostId());
            stringBuilder.append("?cid="+candidate.getCandidateId());
            stringBuilder.append("&key="+md5(String.valueOf(auth.getOtp())));
//            stringBuilder.append("&key="+(auth.getOtp()));
        }

        // trulyfy this url
        TrulyService trulyService = new TrulyService();

        return trulyService.generateShortURL(stringBuilder.toString());
    }


    public static String generateApplyInShortUrl(Long candidateId, Long jobPostId){
        if(candidateId != null && jobPostId != null) {
            Candidate candidate = CandidateDAO.getById(candidateId);

            Auth auth = AuthService.isAuthExists(candidateId);
            auth.setOtp(generateOtp());
            auth.save();

            JobPost jobPost = JobPostDAO.findById(jobPostId);

            return generateApplyInShortUrl(candidate, jobPost, auth);
        }
        return null;
    }

    public static String generateApplyInShortUrl(Candidate candidate, JobPost jobPost){
        if(candidate != null && jobPost != null) {

            Auth auth = AuthService.isAuthExists(candidate.getCandidateId());
            auth.setOtp(generateOtp());
            auth.save();


            return generateApplyInShortUrl(candidate, jobPost, auth);
        }
        return null;
    }


    public static String generateReferralInShortUrl(Partner partner, List<JobPost> jobPostList, PartnerAuth auth){
        StringBuilder stringBuilder = new StringBuilder();
        List<String> jobPostIdList = new ArrayList<>();

        if(partner != null && !jobPostList.isEmpty()) {
            stringBuilder.append(BASE_URL);
            stringBuilder.append("/employee/refer/");

            // prep job post id list
            for(JobPost jobPost: jobPostList) {
                jobPostIdList.add(String.valueOf(jobPost.getJobPostId()));
            }
            stringBuilder.append("?jpId="+String.join(",", jobPostIdList));

            // commented out for now since bulk uploaded partner don't have auth
//            stringBuilder.append("&key="+md5(String.valueOf(auth.getAuthSessionId())));
        }

        // trulyfy/simplify this url
        TrulyService trulyService = new TrulyService();

        return trulyService.generateShortURL(stringBuilder.toString());
    }

    public static String generateReferralInShortUrl(Partner partner, List<JobPost> jobPostList) {
        if(partner != null && !jobPostList.isEmpty()) {

            // update session id
//            PartnerAuth auth = PartnerAuth.find.where().eq("partner_id",partner.getPartnerId()).findUnique();
//            if(auth != null) {
//                auth.setAuthSessionId(UUID.randomUUID().toString());
//                auth.save();
//
//            }

            return generateReferralInShortUrl(partner, jobPostList, null);
        }
        return null;
    }
}
