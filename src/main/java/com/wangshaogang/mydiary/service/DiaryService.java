package com.wangshaogang.mydiary.service;

import com.wangshaogang.mydiary.response.PageInfo;
import com.wangshaogang.mydiary.response.ServerResponse;
import com.wangshaogang.mydiary.response.consts.ResponseCode;
import com.wangshaogang.mydiary.mapper.DiaryMapper;
import com.wangshaogang.mydiary.mapper.SettingMapper;
import com.wangshaogang.mydiary.pojo.Diary;
import com.wangshaogang.mydiary.pojo.DiaryExample;
import com.wangshaogang.mydiary.pojo.Setting;
import com.wangshaogang.mydiary.utils.DiaryAESUtils;
import com.wangshaogang.mydiary.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class DiaryService {

    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    SettingMapper settingMapper;

    public ServerResponse<PageInfo> getDiaries(int pageSize, int pageNum, String keyWord, Date startDate, Date endDate, String password) {
        DiaryExample diaryExample = new DiaryExample();
        diaryExample.setOrderByClause("diaryTime desc");
        diaryExample.createCriteria().andDiarytimeGreaterThanOrEqualTo(startDate).andDiarytimeLessThanOrEqualTo(endDate);
        List<Diary> diaryList = diaryMapper.selectByExample(diaryExample);
        // 解密 移除不包含keyword的数据
        for (int i = 0; i < diaryList.size(); i++) {
            Diary diary = diaryList.get(i);
            diary.setContent(DiaryAESUtils.diaryDecrypt(diary.getContent(), password));
            if (!diary.getContent().toLowerCase().contains(keyWord.toLowerCase())) {
                diaryList.remove(i--);
            }
        }

        PageInfo<Diary> diaryPageInfo = new PageInfo<>();
        diaryPageInfo.setFirstPage(pageNum == 1);
        diaryPageInfo.setLastPage(diaryList.size() <= pageSize * (pageNum));
        diaryPageInfo.setHasPreviousPage(pageNum > 1);
        diaryPageInfo.setHasNextPage(!diaryPageInfo.isLastPage());
        diaryPageInfo.setPageNum(pageNum);
        diaryPageInfo.setTotal(diaryList.size());
        diaryPageInfo.setPages((diaryList.size() - 1) / pageSize + 1);
        // 分页
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, diaryList.size());
        List<Diary> diaries = diaryList.subList(startIndex, endIndex);
        diaryPageInfo.setPageSize(diaries.size());
        diaryPageInfo.setList(diaries);
        return ServerResponse.createSuccessResponse(diaryPageInfo);
    }

    public ServerResponse<String> updateDiary(String uuid, Date diaryTime, String content, String password) throws Exception {
        if (content.trim().equals("")) {
            ServerResponse.createErrorResponse(ResponseCode.CONTENT_CANNOT_BE_EMPTY);
        }
        if (diaryMapper.selectByPrimaryKey(uuid) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_UUID);
        }
        Diary diary = new Diary();
        diary.setUuid(uuid);
        diary.setDiarytime(diaryTime);
        diary.setUpdatetime(new Date());
        diary.setContent(DiaryAESUtils.diaryEncrypt(content, password));
        diaryMapper.updateByPrimaryKeySelective(diary);
        return ServerResponse.createSuccessResponse();
    }

    public ServerResponse<String> deleteDiary(String uuid) {
        if (diaryMapper.selectByPrimaryKey(uuid) == null) {
            return ServerResponse.createErrorResponse(ResponseCode.ERROR_UUID);
        }
        diaryMapper.deleteByPrimaryKey(uuid);
        return ServerResponse.createSuccessResponse();
    }

    public ServerResponse<String> addDiary(Date diaryTime, String content, String password) throws Exception {
        if (content.trim().equals("")) {
            ServerResponse.createErrorResponse(ResponseCode.CONTENT_CANNOT_BE_EMPTY);
        }
        Date nowDate = new Date();
        Diary diary = new Diary();
        diary.setUuid(MD5Utils.getMd5());
        diary.setDiarytime(diaryTime);
        diary.setCreatetime(nowDate);
        diary.setUpdatetime(nowDate);
        diary.setContent(DiaryAESUtils.diaryEncrypt(content, password));
        diaryMapper.insertSelective(diary);
        return ServerResponse.createSuccessResponse();
    }

    public ServerResponse<String> changePassword(String oldPassword, String newPassword) throws Exception {
        List<Diary> diaries = diaryMapper.selectByExample(null);
        for (Diary diary : diaries) {
            String oldContent = DiaryAESUtils.diaryDecrypt(diary.getContent(), oldPassword);
            String newContent = DiaryAESUtils.diaryEncrypt(oldContent, newPassword);
            diary.setContent(newContent);
            diaryMapper.updateByPrimaryKeySelective(diary);
        }
        Setting setting = new Setting();
        setting.setKey("password");
        setting.setValue(MD5Utils.getMd5(newPassword));
        settingMapper.updateByPrimaryKey(setting);
        return ServerResponse.createSuccessResponse();
    }

}
