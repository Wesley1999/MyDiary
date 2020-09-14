package com.wangshaogang.mydiary.mapper;

import com.wangshaogang.mydiary.pojo.Diary;
import com.wangshaogang.mydiary.pojo.DiaryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiaryMapper {
    int countByExample(DiaryExample example);

    int deleteByExample(DiaryExample example);

    int deleteByPrimaryKey(String uuid);

    int insert(Diary record);

    int insertSelective(Diary record);

    List<Diary> selectByExample(DiaryExample example);

    Diary selectByPrimaryKey(String uuid);

    int updateByExampleSelective(@Param("record") Diary record, @Param("example") DiaryExample example);

    int updateByExample(@Param("record") Diary record, @Param("example") DiaryExample example);

    int updateByPrimaryKeySelective(Diary record);

    int updateByPrimaryKey(Diary record);
}