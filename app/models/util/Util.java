package models.util;

import models.entity.Company;
import org.apache.commons.lang3.StringUtils;
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
//        Boolean shouldRun = true;
//        String companyCode = "";
//        String companyName = company.getCompanyName().replaceAll("\\s+","");
//
//        String companyId;
//        if(company.getCompanyId() < 1000){
//            if(company.getCompanyId() < 100){
//                if(company.getCompanyId() < 10){
//                    companyId = "000" + company.getCompanyId();
//                } else{
//                    companyId = "00" + company.getCompanyId();
//                }
//            } else {
//                companyId = "0" + company.getCompanyId();
//            }
//        } else{
//            companyId = company.getCompanyId() + "";
//        }
//
//        while(shouldRun){
//            int randomCode = (int) (Math.random()*90);
//            if(randomCode < 10){
//                randomCode += 10;
//            }
//
//            if(companyName.length() > 4){
//                companyCode = (companyName.substring(0, 4)).toUpperCase() + companyId + randomCode;
//            } else{
//                companyCode = (companyName).toUpperCase() + companyId + randomCode;
//            }
//
//            // query heavy
//            // TODO convert it to use idToCode method, then this check for existing code will not be required
//            Company existingCompany = CompanyDAO.getCompaniesByCompanyCode(companyCode);
//            if(existingCompany == null){
//                shouldRun = false;
//            }
//        }
        return genCompanyCode(company);
    }

    public static String idToCode(long id) {
        // no company id will exceed 2^31 :P
        return Base36.fromBase10((int) id);
    }

    public static int codeToId(String code) {
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
}
