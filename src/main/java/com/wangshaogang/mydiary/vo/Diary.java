package com.wangshaogang.mydiary.vo;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Diary {

    private String uuid;

    private String provinceCity;

    private String location;

    private double longitude;

    private double latitude;

    private String weather;

    private Long createTime;

    private Long updateTime;

    private Long diaryTime;

    private String device;

    private boolean favorite;

    private boolean markdown;

    private List<String> imgUUids;

    private List<String> labels;

    private String title;

    private String content;

}
