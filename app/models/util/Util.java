package models.util;

import dao.CandidateDAO;
import dao.JobPostDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Util {
    public static String BASE_URL = "https://trujobs.in/";

    private Util() {
    }

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

    public static String generateApplyInShortUrl(Candidate candidate, JobPost jobPost){
        StringBuilder stringBuilder = new StringBuilder();
        if(candidate != null && jobPost != null) {
            stringBuilder.append(BASE_URL);
            stringBuilder.append("apply/inshort/");
            stringBuilder.append(jobPost.getJobPostTitle().replaceAll("[^\\w\\s]","-").toLowerCase());
            stringBuilder.append("-jobs-in-bangalore-at-");
            stringBuilder.append(jobPost.getCompany().getCompanyName().replaceAll("[^\\w\\s]","-").toLowerCase());
        }
        return stringBuilder.toString();
    }


    public static String generateApplyInShortUrl(Long candidateId, Long jobPostId){
        if(candidateId != null && jobPostId != null) {
            Candidate candidate = CandidateDAO.getById(candidateId);
            JobPost jobPost = JobPostDAO.findById(jobPostId);

            return generateApplyInShortUrl(candidate, jobPost);
        }
        return null;
    }
}
