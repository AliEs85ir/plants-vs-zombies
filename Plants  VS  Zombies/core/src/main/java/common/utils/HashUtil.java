package common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {
    public static String hash(String input) {
        if (input == null) return null;
        byte[] encodedHash = null;
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            encodedHash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            return "";
        }

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);

        for (byte b : encodedHash) {
            int mask = 0xff & b;
            String hex = Integer.toHexString(mask);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean verify(String password, String passwordHash) {
        String hash = hash(password);
        return hash.equals(passwordHash);
    }

}
