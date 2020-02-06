package com.wangshaogang.mydiary.controller;

import bean.Diary;
import com.wangshaogang.mydiary.ServerResponse;
import com.wangshaogang.mydiary.utils.DiaryUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@ResponseBody
public class DiaryController {

    DiaryUtils getDiaryUtilsObj(HttpSession session) {
        return new DiaryUtils(
                (String) session.getAttribute("WEBDAV_URL"),
                (String) session.getAttribute("WEBDAV_USERNAME"),
                (String) session.getAttribute("WEBDAV_PASSWORD"),
                (String) session.getAttribute("SECRET_KEY"));
    }

    @RequestMapping("login")
    public ServerResponse<String> login(String WEBDAV_URL, String WEBDAV_USERNAME, String WEBDAV_PASSWORD, String SECRET_KEY, HttpSession session) {
        new DiaryUtils(WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD, SECRET_KEY);
        session.setAttribute("WEBDAV_URL", WEBDAV_URL);
        session.setAttribute("WEBDAV_USERNAME", WEBDAV_USERNAME);
        session.setAttribute("WEBDAV_PASSWORD", WEBDAV_PASSWORD);
        session.setAttribute("SECRET_KEY", SECRET_KEY);
        return ServerResponse.createSuccessResponse();
    }

    @RequestMapping("get_diaries")
    public ServerResponse<List> getDiaries(HttpSession session) throws Exception {
        DiaryUtils diaryUtils = getDiaryUtilsObj(session);
        return ServerResponse.createSuccessResponse(diaryUtils.getAllDiaries());
    }

}
