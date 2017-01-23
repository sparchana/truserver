package models.util;

import api.InteractionConstants;
import api.ServerConstants;
import controllers.businessLogic.InteractionService;
import dao.CompanyDAO;
import models.entity.Company;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Util {
    private Util() {
    }

    public static final String ACTION_CREATE = "C";
    public static final String ACTION_UPDATE = "U";
    public static final String ACTION_DELETE = "D";
    public static final String ACTION_READ = "R";

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
        Boolean shouldRun = true;
        String companyCode = "";
        String companyName = company.getCompanyName().replaceAll("\\s+","");

        String companyId;
        if(company.getCompanyId() < 1000){
            if(company.getCompanyId() < 100){
                if(company.getCompanyId() < 10){
                    companyId = "000" + company.getCompanyId();
                } else{
                    companyId = "00" + company.getCompanyId();
                }
            } else {
                companyId = "0" + company.getCompanyId();
            }
        } else{
            companyId = company.getCompanyId() + "";
        }

        while(shouldRun){
            int randomCode = (int) (Math.random()*90);
            if(randomCode < 10){
                randomCode += 10;
            }

            if(companyName.length() > 4){
                companyCode = (companyName.substring(0, 4)).toUpperCase() + companyId + randomCode;
            } else{
                companyCode = (companyName).toUpperCase() + companyId + randomCode;
            }

            Company existingCompany = CompanyDAO.getCompaniesByCompanyCode(companyCode);
            if(existingCompany == null){
                shouldRun = false;
            }
        }
        return companyCode;
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
}
