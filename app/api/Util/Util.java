package api.Util;

import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by batcoder1 on 25/4/16.
 */
public class Util {
    private Util() {}
    public static String md5(String input) {
        String md5 = "";
        if(input == null) {
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
}
