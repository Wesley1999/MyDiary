package com.wangshaogang.mydiary.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wangshaogang.mydiary.vo.Diary;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<Diary> JsonStringToDiary(String JsonString) {
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        JsonArray jsonElements = jsonParser.parse(JsonString).getAsJsonArray();    //获取JsonArray对象
        List<Diary> diaries = new ArrayList<Diary>();
        for (JsonElement bean : jsonElements) {
            Diary diary = gson.fromJson(bean, Diary.class); //解析
            diaries.add(diary);
        }
        return diaries;
    }

    public static String diariesToJsonString(List<Diary> diaries) {
        Gson gson = new Gson();
        return gson.toJson(diaries);
    }

}
