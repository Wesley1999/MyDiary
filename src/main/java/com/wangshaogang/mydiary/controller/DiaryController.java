package com.wangshaogang.mydiary.controller;

import com.google.gson.Gson;
import com.wangshaogang.mydiary.response.PageInfo;
import com.wangshaogang.mydiary.response.ServerResponse;
import com.wangshaogang.mydiary.response.consts.ResponseCode;
import com.wangshaogang.mydiary.mapper.DiaryMapper;
import com.wangshaogang.mydiary.mapper.SettingMapper;
import com.wangshaogang.mydiary.pojo.Diary;
import com.wangshaogang.mydiary.service.DiaryService;
import com.wangshaogang.mydiary.utils.AESUtils;
import com.wangshaogang.mydiary.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Controller
@ResponseBody
@RequestMapping("api")
public class DiaryController {

    private static final String SESSION_KEY_PASSWORD = "PASSWORD";

    @Autowired
    DiaryService diaryService;

    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    SettingMapper settingMapper;

    @RequestMapping("get_diaries.action")
    public ServerResponse<PageInfo> getDiaries(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                               @RequestParam(required = false, defaultValue = "1") int pageNum,
                                               @RequestParam(required = false, defaultValue = "") String keyWord,
                                               @RequestParam(required = false, defaultValue = "-62167248343000") long startDate,
                                               @RequestParam(required = false, defaultValue = "253402271999000") long endDate,
                                               HttpSession session) {
        if (session.getAttribute(SESSION_KEY_PASSWORD) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NOT_LOGIN);
        }
        if (startDate < -62167248343000L || endDate > 253402271999000L) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_DATE_TIME);
        }
        String password = (String) session.getAttribute(SESSION_KEY_PASSWORD);
        return diaryService.getDiaries(pageSize, pageNum, keyWord, new Date(startDate), new Date(endDate), password);
    }

    @RequestMapping("add_diary.action")
    public ServerResponse<String> addDiary(@RequestParam Long diaryTime,
                                           @RequestParam String content,
                                           HttpSession session) throws Exception {
        if (session.getAttribute(SESSION_KEY_PASSWORD) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NOT_LOGIN);
        }
        if (diaryTime < -62167248343000L || diaryTime > 253402271999000L) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_DATE_TIME);
        }
        String password = (String) session.getAttribute(SESSION_KEY_PASSWORD);
        return diaryService.addDiary(new Date(diaryTime), content, password);
    }

    @RequestMapping("delete_diary.action")
    public ServerResponse<String> deleteDiary(@RequestParam String uuid,
                                              HttpSession session) {
        if (session.getAttribute(SESSION_KEY_PASSWORD) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NOT_LOGIN);
        }
        return diaryService.deleteDiary(uuid);
    }

    // 密码为空表示不加密
    @RequestMapping("login.action")
    public ServerResponse<String> login(@RequestParam(required = false, defaultValue = "") String SECRET_KEY, HttpSession session) throws Exception {
        String password = SECRET_KEY;
        AESUtils.encrypt("test", password);
        if (settingMapper.selectByPrimaryKey("password").getValue().equals(MD5Utils.getMd5(password))) {
            session.setAttribute("PASSWORD", password);
            return ServerResponse.createSuccessResponse();
        } else {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_PASSWORD);
        }
    }

    // 检查是否已登录，也可以调用此接口为session续命
    @RequestMapping("whether_login.action")
    public ServerResponse<Boolean> whetherLogin(HttpSession session) {
        return ServerResponse.createSuccessResponse(session.getAttribute("PASSWORD") != null);
    }

    @RequestMapping("logout.action")
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute("PASSWORD");
        return ServerResponse.createSuccessResponse();
    }

    // 要修改所有日记的密码
    // 前端在调用此接口前会先用旧密码登录，所以这里不需要传入旧密码
    @RequestMapping("change_secret_key.action")
    public ServerResponse<String> changePassword(@RequestParam(required = false, defaultValue = "") String newSECRET_KEY,
                                                 HttpSession session) throws Exception {
        String password = (String) session.getAttribute(SESSION_KEY_PASSWORD);
        AESUtils.encrypt("test", password);
        if (!settingMapper.selectByPrimaryKey("password").getValue().equals(MD5Utils.getMd5(password))) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_PASSWORD);
        }
        return diaryService.changePassword((String) session.getAttribute("PASSWORD"), newSECRET_KEY);
    }


    @RequestMapping("update_diary.action")
    public ServerResponse<String> updateDiary(@RequestParam String uuid,
                                              @RequestParam Long diaryTime,
                                              @RequestParam String content,
                                              HttpSession session) throws Exception {
        if (session.getAttribute(SESSION_KEY_PASSWORD) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NOT_LOGIN);
        }
        if (diaryTime < -62167248343000L || diaryTime > 253402271999000L) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_DATE_TIME);
        }
        String password = (String) session.getAttribute(SESSION_KEY_PASSWORD);
        return diaryService.updateDiary(uuid, new Date(diaryTime), content, password);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("D:\\diary.txt"));
        String str;
        String aaa = "";
        while ((str = in.readLine()) != null) {
            aaa += str;
        }
        System.out.println(aaa);
        Gson gson = new Gson();
        List<Map> maps = gson.fromJson(aaa, List.class);
        for (Map map : maps) {
            Diary diary = new Diary();
            diary.setUuid((String) map.get("uuid"));
            diary.setCreatetime(new Date((String) map.get("createtime")));
            diary.setUpdatetime(new Date((String) map.get("updatetime")));
            diary.setDiarytime(new Date((String) map.get("diarytime")));
            diary.setFavorite(false);
            diary.setTitle("");
            diary.setContent((String) map.get("content"));

        }
    }

}
