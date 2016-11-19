package models.util;

import api.ServerConstants;
import controllers.businessLogic.InteractionService;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static play.mvc.Controller.session;

public class Util {
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

    public static InteractionService.InteractionChannelType getChannelType(int channelId) {
        InteractionService.InteractionChannelType channelType = InteractionService.InteractionChannelType.UNKNOWN;
        switch (channelId) {
            case ServerConstants.SESSION_CHANNEL_CANDIDATE_WEBSITE:
                channelType = InteractionService.InteractionChannelType.SELF;
                break;
            case ServerConstants.SESSION_CHANNEL_CANDIDATE_ANDROID_APP:
                channelType = InteractionService.InteractionChannelType.SELF_ANDROID;
                break;
            case ServerConstants.SESSION_CHANNEL_SUPPORT_WEBSITE:
                channelType = InteractionService.InteractionChannelType.SUPPORT;
                break;
            case ServerConstants.SESSION_CHANNEL_KNOWLARITY:
                channelType = InteractionService.InteractionChannelType.KNOWLARITY;
                break;
            case ServerConstants.SESSION_CHANNEL_PARTNER_WEBSITE:
                channelType = InteractionService.InteractionChannelType.PARTNER;
                break;
            case ServerConstants.SESSION_CHANNEL_RECRUITER_WEBSITE:
                channelType = InteractionService.InteractionChannelType.RECRUITER;
                break;
            case 33: // this takes care of old support session_channel values also update the current session
                // TODO: remove this 33, 77, 111 after 30 days from deployment.
                channelType = InteractionService.InteractionChannelType.SUPPORT;
                session().put("sessionChannel", String.valueOf(ServerConstants.SESSION_CHANNEL_SUPPORT_WEBSITE));
                break;
            case 77: // this takes care of old partner session_channel values also update the current session
                channelType = InteractionService.InteractionChannelType.PARTNER;
                session().put("sessionChannel", String.valueOf(ServerConstants.SESSION_CHANNEL_PARTNER_WEBSITE));
                break;
            case 111: // this takes care of old recruiter session_channel values also update the current session
                channelType = InteractionService.InteractionChannelType.RECRUITER;
                session().put("sessionChannel", String.valueOf(ServerConstants.SESSION_CHANNEL_RECRUITER_WEBSITE));
                break;
        }
        return channelType;
    }
}
