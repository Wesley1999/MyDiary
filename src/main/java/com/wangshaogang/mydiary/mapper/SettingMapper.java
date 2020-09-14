package com.wangshaogang.mydiary.mapper;

import com.wangshaogang.mydiary.pojo.Setting;
import com.wangshaogang.mydiary.pojo.SettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SettingMapper {
    int countByExample(SettingExample example);

    int deleteByExample(SettingExample example);

    int deleteByPrimaryKey(String key);

    int insert(Setting record);

    int insertSelective(Setting record);

    List<Setting> selectByExample(SettingExample example);

    Setting selectByPrimaryKey(String key);

    int updateByExampleSelective(@Param("record") Setting record, @Param("example") SettingExample example);

    int updateByExample(@Param("record") Setting record, @Param("example") SettingExample example);

    int updateByPrimaryKeySelective(Setting record);

    int updateByPrimaryKey(Setting record);
}