package com.nuaa.art.user.common.utils;


import java.util.Random;


public class PasswdUtils {
    private static char[] hex ="0123456789ABCDEF".toCharArray();

    /**
     * 加密算法加盐
     *
     * @return {@link String}
     */
    public static String salt() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        for(int i = 0; i < sb.capacity(); i++) {
            sb.append(hex[random.nextInt(16)]);
        }
        return sb.toString();
    }

}
