package com.wangshaogang.mydiary.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Diary {
    private String uuid;

    private Date createtime;

    private Date updatetime;

    private Date diarytime;

    private Boolean favorite;

    private String title;

    private String content;

}