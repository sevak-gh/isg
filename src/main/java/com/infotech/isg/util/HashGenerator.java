package com.infotech.isg.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * utility class for generating hash
 *
 * @author Sevak Gharibian
 */
public class HashGenerator {

    public static String getSHA512(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 digest error", e);
        }
        byte[] hash = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (Byte b : hash) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getMD5(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 digest error", e);
        }
        byte[] hash = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (Byte b : hash) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
