package com.wangshaogang.mydiary.controller;

import com.wangshaogang.mydiary.base.ServerResponse;
import com.wangshaogang.mydiary.base.consts.DefaultAccount;
import com.wangshaogang.mydiary.base.consts.ResponseCode;
import com.wangshaogang.mydiary.base.consts.SessionKey;
import com.wangshaogang.mydiary.service.DiaryService;
import com.wangshaogang.mydiary.vo.Diary;
import com.wangshaogang.mydiary.vo.DiaryPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Controller
@ResponseBody
@RequestMapping("api")
public class DiaryController {

    DiaryService getDiaryUtilsObj(HttpSession session) {
        return new DiaryService(
                (String) session.getAttribute(SessionKey.WEBDAV_URL),
                (String) session.getAttribute(SessionKey.WEBDAV_USERNAME),
                (String) session.getAttribute(SessionKey.WEBDAV_PASSWORD),
                (String) session.getAttribute(SessionKey.SECRET_KEY));
    }

    @RequestMapping("initialization.action")
    public ServerResponse<String> initialization(@RequestParam(defaultValue = "https://dav.jianguoyun.com/dav/") String WEBDAV_URL, @RequestParam String WEBDAV_USERNAME,
                                                 @RequestParam String WEBDAV_PASSWORD, @RequestParam String SECRET_KEY) {
        DiaryService diaryService = new DiaryService(WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD, SECRET_KEY);
        if (!diaryService.checkWebDAVPassword()) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_WEBDAV_PASSWORD);
        }
        if (diaryService.whetherInitialization()) {
            return ServerResponse.createErrorResponse(ResponseCode.HAVE_INITIALIZED);
        }
        diaryService.initialization();
        return ServerResponse.createSuccessResponse();
    }

    @RequestMapping("login.action")
    public ServerResponse<String> login(@RequestParam(defaultValue = DefaultAccount.DEFAULT_WEBDAV_URL)  String WEBDAV_URL,
                                        @RequestParam(defaultValue = DefaultAccount.DEFAULT_WEBDAV_USERNAME) String WEBDAV_USERNAME,
                                        @RequestParam(defaultValue = DefaultAccount.DEFAULT_WEBDAV_PASSWORD) String WEBDAV_PASSWORD,
                                        @RequestParam(defaultValue = DefaultAccount.DEFAULT_SECRET_KEY) String SECRET_KEY, HttpSession session) {
        try {
            DiaryService diaryService = new DiaryService(WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD, SECRET_KEY);

            // 如果默认账号未初始化，将其初始化
            if (WEBDAV_URL.equals(DefaultAccount.DEFAULT_WEBDAV_URL) && WEBDAV_USERNAME.equals(DefaultAccount.DEFAULT_WEBDAV_USERNAME) &&
                    WEBDAV_PASSWORD.equals(DefaultAccount.DEFAULT_WEBDAV_PASSWORD) && SECRET_KEY.equals(DefaultAccount.DEFAULT_SECRET_KEY) &&
                    !diaryService.whetherInitialization()) {
                diaryService.initialization();
            }

            if (!diaryService.checkWebDAVPassword()) {
                return ServerResponse.createErrorResponse(ResponseCode.ERROR_WEBDAV_PASSWORD);
            } else if (!diaryService.whetherInitialization()) {
                return ServerResponse.createErrorResponse(ResponseCode.HAVE_NOT_INITIALIZED);
            } else if (!diaryService.checkSecretKey()) {
                return ServerResponse.createErrorResponse(ResponseCode.ERROR_SECRET_KEY);
            }
            // 登录成功
            session.setAttribute(SessionKey.WEBDAV_URL, WEBDAV_URL);
            session.setAttribute(SessionKey.WEBDAV_USERNAME, WEBDAV_USERNAME);
            session.setAttribute(SessionKey.WEBDAV_PASSWORD, WEBDAV_PASSWORD);
            session.setAttribute(SessionKey.SECRET_KEY, SECRET_KEY);
            return ServerResponse.createSuccessResponse();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ServerResponse.createErrorResponse(ResponseCode.LOGIN_FAIL);
        }
    }

    @RequestMapping("whether_login.action")
    public ServerResponse<Boolean> whetherLogin(HttpSession session) {
        return ServerResponse.createSuccessResponse(session.getAttribute(SessionKey.WEBDAV_URL ) != null);
    }

    @RequestMapping("logout.action")
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(SessionKey.WEBDAV_URL);
        session.removeAttribute(SessionKey.WEBDAV_USERNAME);
        session.removeAttribute(SessionKey.WEBDAV_PASSWORD);
        session.removeAttribute(SessionKey.SECRET_KEY);
        return ServerResponse.createSuccessResponse();
    }

    // 要修改所有日记的密码
    @RequestMapping("change_secret_key.action")
    public ServerResponse<String> changeSecretKey(@RequestParam String newSECRET_KEY, HttpSession session) {
        DiaryService diaryService = getDiaryUtilsObj(session);
        try {
            diaryService.changeSecretKey(newSECRET_KEY);
            session.setAttribute(SessionKey.SECRET_KEY, newSECRET_KEY);
            return ServerResponse.createSuccessResponse();
        } catch (Exception e) {
            e.printStackTrace();;
            return ServerResponse.createErrorResponse(ResponseCode.CHANGE_SECRET_KEY_FAIL);
        }
    }

    @RequestMapping("get_diaries.action")
    // startTime和endTime只需精确到天
    public ServerResponse<DiaryPage> getDiaries(@RequestParam(defaultValue = "-62167248343000") long startDate,
                                                @RequestParam(defaultValue = "253402271999000") long endDate,
                                                @RequestParam(defaultValue = "") String keyWord,
                                                @RequestParam(defaultValue = "0") int startIndex,
                                                @RequestParam(defaultValue = "10") int pageSize, HttpSession session) {
        try {
            DiaryService diaryService = getDiaryUtilsObj(session);
            return ServerResponse.createSuccessResponse(diaryService.getAllDiaries(startDate, endDate, keyWord, startIndex, pageSize));
        } catch (Exception e) {
            return ServerResponse.createErrorResponse(ResponseCode.PROLONGED_INACTIVITY);
        }
    }

    @RequestMapping("get_diary.action")
    public ServerResponse<Diary> getDiary(@RequestParam String uuid, @RequestParam int year, HttpSession session) {
        DiaryService diaryUtils = getDiaryUtilsObj(session);
        try {
            return ServerResponse.createSuccessResponse(diaryUtils.getDiaryByUUid(uuid, year));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createErrorResponse(ResponseCode.ERROR_104);
    }

    @RequestMapping("delete_diary.action")
    public ServerResponse<String> deleteDiary(@RequestParam String uuid, @RequestParam int year, HttpSession session) {
        DiaryService diaryUtils = getDiaryUtilsObj(session);
        try {
            diaryUtils.deleteDiary(uuid, year);
            return ServerResponse.createSuccessResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createErrorResponse(ResponseCode.ERROR_105);
    }

    @RequestMapping("add_diary.action")
    public ServerResponse<String> addDiary(@RequestParam(defaultValue = "") String provinceCity,
                                           @RequestParam(defaultValue = "") String location,
                                           @RequestParam(defaultValue = "0") double longitude,
                                           @RequestParam(defaultValue = "0") double latitude,
                                           @RequestParam(defaultValue = "") String weather,
                                           @RequestParam Long diaryTime,
                                           @RequestParam (defaultValue = "")String device,
//                                           @RequestParam boolean favorite,
//                                           @RequestParam boolean markdown,
                                           @RequestParam(defaultValue = "") String title,
                                           @RequestParam String content,
                                           HttpSession session) {
        if (content.trim().equals("")) {
            ServerResponse.createErrorResponse(ResponseCode.CONTENT_CANNOT_BE_EMPTY);
        }

        DiaryService diaryUtilsObj = getDiaryUtilsObj(session);

        Diary diary = new Diary();
        diary.setUuid(UUID.randomUUID().toString());
        diary.setProvinceCity(provinceCity);
        diary.setLocation(location);
        diary.setLongitude(longitude);
        diary.setLatitude(latitude);
        diary.setWeather(weather);
        diary.setCreateTime(Calendar.getInstance().getTimeInMillis());
        diary.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        diary.setDiaryTime(diaryTime);
        diary.setDevice(device);
        diary.setFavorite(false);
        diary.setMarkdown(false);
        diary.setTitle(title);
        diary.setContent(content);

        diaryUtilsObj.addDiary(diary);
        return ServerResponse.createSuccessResponse();
    }

    @RequestMapping("update_diary.action")
    public ServerResponse<String> updateDiary(@RequestParam String uuid,
                                              @RequestParam(defaultValue = "") String provinceCity,
                                              @RequestParam(defaultValue = "") String location,
                                              @RequestParam(defaultValue = "0") double longitude,
                                              @RequestParam(defaultValue = "0") double latitude,
                                              @RequestParam(defaultValue = "") String weather,
                                              @RequestParam Long diaryTime,
                                              @RequestParam (defaultValue = "")String device,
//                                            @RequestParam boolean favorite,
//                                            @RequestParam boolean markdown,
                                              @RequestParam(defaultValue = "") String title,
                                              @RequestParam String content,
                                              HttpSession session) throws Exception {
        if (content.trim().equals("")) {
            ServerResponse.createErrorResponse(ResponseCode.CONTENT_CANNOT_BE_EMPTY);
        }

        DiaryService diaryUtilsObj = getDiaryUtilsObj(session);
        Diary oldDiary = diaryUtilsObj.getDiaryByUUid(uuid);

        Diary diary = new Diary();
        diary.setUuid(uuid);
        diary.setProvinceCity(provinceCity);
        diary.setLocation(location);
        diary.setLongitude(longitude);
        diary.setLatitude(latitude);
        diary.setWeather(weather);
        diary.setCreateTime(oldDiary.getCreateTime());
        diary.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        diary.setDiaryTime(diaryTime);
        diary.setDevice(device);
        diary.setFavorite(false);
        diary.setMarkdown(false);
        diary.setTitle(title);
        diary.setContent(content);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(oldDiary.getDiaryTime()));
        diaryUtilsObj.updateDiary(diary, calendar.get(Calendar.YEAR));
        return ServerResponse.createSuccessResponse();
    }

}
