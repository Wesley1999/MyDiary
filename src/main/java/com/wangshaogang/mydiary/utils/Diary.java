package bean;

import lombok.Data;

import java.util.List;

@Data
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
