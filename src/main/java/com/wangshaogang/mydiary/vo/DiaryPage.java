package com.wangshaogang.mydiary.vo;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiaryPage {

    private int pages;

    private int startIndex;

    private int endIndex;

    private int totalItems;

    private List<Diary> diaryList;

}
