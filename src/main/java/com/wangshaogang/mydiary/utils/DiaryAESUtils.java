package com.wangshaogang.mydiary.utils;

public class DiaryAESUtils {
    public static String diaryEncrypt(String string, String password) throws Exception {
        if (password.equals("")) {
            return string;
        } else {
            return AESUtils.encrypt(string, password);
        }
    }

    public static String diaryDecrypt(String string, String password) {
        if (password.equals("")) {
            return string;
        } else {
            return AESUtils.decrypt(string, password);
        }
    }
}
