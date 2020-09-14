package com.wangshaogang.mydiary.response.consts;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {

    public static final Integer SUCCESS = 0;
    public static final Integer LOGIN_FAIL = 101;
    public static final Integer NOT_LOGIN = 102;
    public static final Integer CHANGE_SECRET_KEY_FAIL = 103;
    public static final Integer CONTENT_CANNOT_BE_EMPTY = 104;
    public static final Integer ERROR_UUID = 105;
    public static final Integer ERROR_PASSWORD = 106;
    public static final Integer HAVE_INITIALIZED = 107;
    public static final Integer HAVE_NOT_INITIALIZED = 108;
    public static final Integer ERROR_SECRET_KEY = 109;
    public static final Integer PROLONGED_INACTIVITY = 110;
    public static final Integer ERROR_DATE_TIME = 111;


    public static Map<Integer, String> responseMessage = new HashMap<Integer, String>() {{
        put(LOGIN_FAIL, "登录失败");    // 两次输入的密码不同
        put(NOT_LOGIN, "未登录");
        put(CHANGE_SECRET_KEY_FAIL, "secretKey修改失败");
        put(CONTENT_CANNOT_BE_EMPTY, "日记内容不能为空");
        put(ERROR_UUID, "uuid有误");
        put(ERROR_PASSWORD, "密码错误");
        put(HAVE_INITIALIZED, "已初始化");
        put(HAVE_NOT_INITIALIZED, "已初始化");
        put(ERROR_SECRET_KEY, "secretKey错误");
        put(PROLONGED_INACTIVITY, "长时间未操作，请重新登录");
        put(ERROR_DATE_TIME, "时间日期有误");

    }};


    public static String getResponseMessageByResponseCode(Integer responseCode) {
        return responseMessage.get(responseCode);
    }

}
