package com.iyuba.core.homepage.entity;

import com.iyuba.core.common.sqlite.mode.CommonNews;

/**
 * Created by IvoT on 2016/5/9.
 */
public class NewsInfo extends CommonNews {
    public String itemId;//取得的信息是哪个app的，1voa慢速，2BBC英语，3听歌学英语，4英语头条，5voa常速
    public String url;
    public String titleEn;
    public String readTimes;
    public String singer;
    public String announcer;
    public String source;
    public String category;
    public String topicId;
    public String flag;
    public String wordCount;
    public String hardWeight;

}
