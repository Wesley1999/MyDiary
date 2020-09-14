package com.wangshaogang.mydiary.utils;

import org.springframework.util.DigestUtils;

/**
 * @author 王少刚
 * @create 2018-12-22 19:55
 */

public class MD5Utils {

    public static void main(String[] args) {
        System.out.println(getMd5());
        System.out.println(getMd5("ads"));
    }

    // 加入一个盐值，用于混淆
    private final static String salt = "bd87b7cx8hnawetb83432";

    public static String getMd5(String string) {
        String base = string + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        md5 = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16)
                + "-" + md5.substring(16, 20) + "-" + md5.substring(20, 32);
        return md5;
    }

    public static String getMd5() {
        return getMd5(Math.random() + "");
    }

}
