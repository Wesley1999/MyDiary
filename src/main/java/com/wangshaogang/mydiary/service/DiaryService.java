package com.wangshaogang.mydiary.service;

import com.wangshaogang.mydiary.utils.AES;
import com.wangshaogang.mydiary.utils.JsonUtils;
import com.wangshaogang.mydiary.utils.WebDAVUtil;
import com.wangshaogang.mydiary.vo.Diary;
import com.wangshaogang.mydiary.vo.DiaryPage;
import org.apache.commons.httpclient.HttpClient;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

import static com.wangshaogang.mydiary.utils.StringUtils.getEncoding;
import sun.nio.cs.ext.GBK;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.wangshaogang.mydiary.utils.StringUtils.isMessyCode;

public class DiaryService {

    WebDAVUtil webDAVUtil;

    String SECRET_KEY;

    public DiaryService(String WEBDAV_URL, String WEBDAV_USERNAME, String WEBDAV_PASSWORD, String SECRET_KEY) {
        webDAVUtil = new WebDAVUtil(WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD);
        this.SECRET_KEY = SECRET_KEY;
    }

    public boolean whetherInitialization() {
        // 尝试创建文件夹MyDiary
        webDAVUtil.mkdir("MyDiary");
        webDAVUtil.mkdir("MyDiary/images");
        try {
            webDAVUtil.get("MyDiary", "CheckSecretKey");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void initialization() {
        try {
            String fileName = UUID.randomUUID().toString();
            AES aes = new AES(SECRET_KEY);
            String string = aes.encrypt("Ly0MODtoYjHSFm5V");
            FileWriter writer = new FileWriter(fileName);
            writer.write(string);
            writer.flush();
            writer.close();
            File file = new File(fileName);
            webDAVUtil.upload(file, "MyDiary", "CheckSecretKey");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkWebDAVPassword() {
        return webDAVUtil.test();
    }

    // 必须先验证checkWebDAVPassword()
    public boolean checkSecretKey() {
        try {
            String s = webDAVUtil.get("MyDiary", "CheckSecretKey");
            AES aes = new AES(SECRET_KEY);
            return aes.decrypt(s).equals("Ly0MODtoYjHSFm5V");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void changeSecretKey(String newSECRET_KEY) {
        // 修改日记文件密码
        try {
            List<Integer> years = getYears();
            for (Integer year : years) {
                List<Diary> diariesByYear = getDiariesByYear(year);
                String diariesToString = JsonUtils.diariesToJsonString(diariesByYear);
                // 加密
                AES des = new AES(newSECRET_KEY); //自定义密钥
                diariesToString = des.encrypt(diariesToString);
                String fileName = UUID.randomUUID().toString();
                FileWriter writer = new FileWriter(fileName);
                writer.write(diariesToString);
                writer.flush();
                writer.close();
                File file = new File(fileName);
                webDAVUtil.upload(file, "MyDiary", "diary" + year);
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 修改校验文件
        try {
            String fileName = UUID.randomUUID().toString();
            AES aes = new AES(newSECRET_KEY);
            String string = aes.encrypt("Ly0MODtoYjHSFm5V");
            FileWriter writer = new FileWriter(fileName);
            writer.write(string);
            writer.flush();
            writer.close();
            File file = new File(fileName);
            webDAVUtil.upload(file, "MyDiary", "CheckSecretKey");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getYears() throws IOException, DavException {
        //得到属性
        String dirUrl = webDAVUtil.getUrl() + "MyDiary";
        DavMethod find = new PropFindMethod(dirUrl, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
        HttpClient client = webDAVUtil.getClient();
        client.executeMethod(find);

        MultiStatus multiStatus = find.getResponseBodyAsMultiStatus();
        MultiStatusResponse[] responses = multiStatus.getResponses();

        List<Integer> years = new ArrayList<>();

        for (int i = 0; i < responses.length; i++) {
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
                for (int j = i + 1; j < diaries.size(); j++) {
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

    // 时间只精确到天
    public DiaryPage getAllDiaries(long startDate, long endDate, String keyWord, int startIndex, int pageSize) throws IOException, DavException {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(startDate));
//        startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH) + 1);
        startCalendar.set(Calendar.HOUR, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date(endDate));
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH) + 1);   // 置为下一天
        endCalendar.set(Calendar.HOUR, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        List<Diary> allDiaries = getAllDiaries();
        // 过滤
        for (int i = 0; i < allDiaries.size(); i++) {
            Diary diary = allDiaries.get(i);
            String content = diary.getContent();
            // 注意是否带等号
            if (!content.contains(keyWord) || diary.getDiaryTime() < startCalendar.getTimeInMillis() || diary.getDiaryTime() >= endCalendar.getTimeInMillis()) {
                allDiaries.remove(i--);
            }
        }
        // 分页
        int endIndex = Math.min(startIndex + pageSize, allDiaries.size());
        int pages = allDiaries.size() / pageSize + (allDiaries.size() % pageSize == 0 ? 0 : 1);
        List<Diary> diaries = allDiaries.subList(startIndex, endIndex);

        DiaryPage diaryPage = new DiaryPage();
        diaryPage.setPages(pages);
        diaryPage.setStartIndex(startIndex);
        diaryPage.setEndIndex(endIndex);
        diaryPage.setTotalItems(allDiaries.size());
        diaryPage.setDiaryList(diaries);
        return diaryPage;
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
    // createTime、updateTime上层生成
    public boolean addDiary(Diary diary) {
        diary.setUuid(UUID.randomUUID().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(diary.getDiaryTime()));
        int year = calendar.get(Calendar.YEAR);
        List<Diary> diariesByYear = new ArrayList<>();
        // 可能会出现 提交前 这一年没有日记的情况
        try {
            diariesByYear = getDiariesByYear(year);
        } catch (Exception e) {
        }
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

}
