package com.wangshaogang.mydiary.utils;

import bean.Diary;
import lombok.Data;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DiaryUtils {

    WebDAVUtil webDAVUtil;

    String SECRET_KEY;

    // 登录失败会返回false
    public DiaryUtils(String WEBDAV_URL, String WEBDAV_USERNAME, String WEBDAV_PASSWORD, String SECRET_KEY) {
        webDAVUtil = new WebDAVUtil(WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD);
        boolean test = webDAVUtil.test();
        if (test) {
            this.SECRET_KEY = SECRET_KEY;
        } else {
            throw new RuntimeException("密码错误");
        }
    }

    /**
     * 此方法通用性不强
     * @return 所有的日记文件名称
     * @throws IOException 异常
     * @throws DavException 异常
     */
    public List<Integer> getYears() throws IOException, DavException {
        // 尝试创建文件夹MyDiary
        webDAVUtil.mkdir("MyDiary");
        //得到属性
        String dirUrl = webDAVUtil.getUrl() + "MyDiary";
        DavMethod find = new PropFindMethod(dirUrl, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
        HttpClient client = webDAVUtil.getClient();
        client.executeMethod(find);

        MultiStatus multiStatus = find.getResponseBodyAsMultiStatus();
        MultiStatusResponse[] responses = multiStatus.getResponses();

        List<Integer> years = new ArrayList<>();

        for (int i=0; i<responses.length; i++) {
            MultiStatusResponse content = responses[i];
            DavPropertySet properys = content.getProperties(200);

            String docHref = content.getHref();
            String sourceName = properys.get("displayname").getValue().toString();

            if (!docHref.endsWith("/") && sourceName.startsWith("diary")) {
                years.add(Integer.valueOf(sourceName.substring(5, 9)));
            }

        }
        Collections.reverse(years);
        return years;
    }

    // 包含加密
    public void uploadDiaries(List<Diary> diaries, int year) {
        try {
            // 上传前排序
            for (int i = 0; i < diaries.size(); i++) {
                for (int j = i+1; j < diaries.size(); j++) {
                    if (diaries.get(i).getDiaryTime() < diaries.get(j).getDiaryTime()) {
                        Diary temp = diaries.get(i);
                        diaries.set(i, diaries.get(j));
                        diaries.set(j, temp);
                    }
                }
            }
            String diariesToString = JsonUtils.diariesToJsonString(diaries);

            // 加密
            AES des = new AES(SECRET_KEY); //自定义密钥
            diariesToString = des.encrypt(diariesToString);

            String fileName = UUID.randomUUID().toString();
            FileWriter writer = new FileWriter(fileName);
            writer.write(diariesToString);
            writer.flush();
            writer.close();
            File file = new File(fileName);
            webDAVUtil.upload(file, "MyDiary", "diary" + year);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 包含解密
    public List<Diary> getDiariesByYear(int year) throws Exception {
        String fileName = "diary" + year;
        String stringDiary = webDAVUtil.get("MyDiary", fileName);

        // 解密
        AES des = new AES(SECRET_KEY); //自定义密钥
        stringDiary = des.decrypt(stringDiary);

        List<Diary> diaries = JsonUtils.JsonStringToDiary(stringDiary);
        return diaries;
    }

    // 包含解密
    public List<Diary> getAllDiaries() throws IOException, DavException {
        List<Integer> years = getYears();
        List<Diary> allDiaries = new ArrayList<Diary>();
        for (Integer year : years) {
            String stringDiary = null;
            try {
                stringDiary = webDAVUtil.get("MyDiary", "diary" + year);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 解密
            AES des = new AES(SECRET_KEY); //自定义密钥
            try {
                stringDiary = des.decrypt(stringDiary);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Diary> diaries = JsonUtils.JsonStringToDiary(stringDiary);
            allDiaries.addAll(diaries);
        }
        return allDiaries;
    }

    public List<Diary> getDiariesByDate(Date date) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        List<Diary> diariesByYear = getDiariesByYear(year);
        List<Diary> diariesByDate = new ArrayList<Diary>();
        for (Diary diary : diariesByYear) {
            calendar.setTime(new Date(diary.getDiaryTime()));
            if (calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                diariesByDate.add(diary);
            }
        }
        return diariesByDate;
    }

    public Diary getDiaryByUUid(String uuid) throws Exception {
        List<Diary> allDiaries = getAllDiaries();
        for (Diary diary : allDiaries) {
            if (diary.getUuid().equals(uuid)) {
                return diary;
            }
        }
        return null;
    }

    public Diary getDiaryByUUid(String uuid, int year) throws Exception {
        List<Diary> diariesByYear = getDiariesByYear(year);
        for (Diary diary : diariesByYear) {
            if (diary.getUuid().equals(uuid)) {
                return diary;
            }
        }
        return null;
    }

    public int getYearByDiary(Diary diary) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(diary.getDiaryTime()));
        return calendar.get(Calendar.YEAR);
    }

    public int getYearByUUid(String uuid) throws Exception {
        List<Diary> allDiaries = getAllDiaries();
        for (Diary diary : allDiaries) {
            if (diary.getUuid().equals(uuid)) {
                return getYearByDiary(diary);
            }
        }
        return 0;
    }

    // uuid在这里生成
    // createTime、updateTime前端生成
    public boolean addDiary(Diary diary) {
        diary.setUuid(UUID.randomUUID().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(diary.getDiaryTime()));
        int year = calendar.get(Calendar.YEAR);
        List<Diary> diariesByYear = new ArrayList<>();
        // 可能会出现 提交前 这一年没有日记的情况
        try {
            diariesByYear = getDiariesByYear(year);
        } catch (Exception e) {}
        diariesByYear.add(diary);
        uploadDiaries(diariesByYear, year);

        return false;
    }

    public boolean deleteDiary(String uuid, int year) throws Exception {
        List<Diary> diariesByYear = getDiariesByYear(year);
        for (Diary diary : diariesByYear) {
            if (diary.getUuid().equals(uuid)) {
                diariesByYear.remove(diary);
                break;
            }
        }
        // 如果删除后为空，要删除文件
        if (diariesByYear.isEmpty()) {
            webDAVUtil.delete("MyDiary", "diary" + year);
        } else {
            uploadDiaries(diariesByYear, year);
        }
        return true;
    }

    // updateTime前端生成
    public boolean updateDiary(Diary diary, int oldYear) throws Exception {
        if (getYearByDiary(diary) == oldYear) {
            // 如果年份没有修改
            List<Diary> diariesByYear = getDiariesByYear(oldYear);
            // 从diariesByYear中找到对应的diary并修改
            for (Diary diary1 : diariesByYear) {
                if (diary1.getUuid().equals(diary.getUuid())) {
                    diariesByYear.remove(diary1);
                    diariesByYear.add(diary);
                    break;
                }
            }

            uploadDiaries(diariesByYear, oldYear);

        } else {
            // 如果年份修改，则调用删除和添加，但uuid不变
            addDiary(diary);
            deleteDiary(diary.getUuid(), oldYear);
        }
        return true;
    }

    public void printDiaries(List<Diary> diaries) {
        for (Diary diary : diaries) {
            System.out.println(diary);
        }
    }

    public static void main(String[] args) throws Exception {
//        Diary diaryByUUid = getDiaryByUUid("d58b6405-55d0-4e7c-8dab-97c04e37ecdd", 2020);
//        diaryByUUid.setContent(new String("测试修改日记"));
//        updateDiary(diaryByUUid, 2020);

//        Diary diary = new Diary();
//        diary.setContent("测试2021的日记");
//        diary.setTitle("标题");
//        diary.setMarkdown(false);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, 2021);
//        diary.setDiaryTime(calendar.getTimeInMillis());
//        diary.setUpdateTime(Calendar.getInstance().getTimeInMillis());
//        diary.setCreateTime(Calendar.getInstance().getTimeInMillis());
//        addDiary(diary);

//        deleteDiary("526685d8-41f4-479d-806a-11cdf902fafc", 2020);

//        List<Diary> diariesByYear = getAllDiaries();
//        printDiaries(diariesByYear);
        DiaryUtils diaryUtils = new DiaryUtils("https://dav.jianguoyun.com/dav/", "1095151731@qq.com", "anraiq5kn54wq5ie", "wsg727");
        diaryUtils.uploadDiaries(diaryUtils.getDiariesByYear(2017), 2017);
        diaryUtils.uploadDiaries(diaryUtils.getDiariesByYear(2018), 2018);
        diaryUtils.uploadDiaries(diaryUtils.getDiariesByYear(2019), 2019);
        diaryUtils.uploadDiaries(diaryUtils.getDiariesByYear(2020), 2020);

//        System.out.println(getYears());

    }
}
