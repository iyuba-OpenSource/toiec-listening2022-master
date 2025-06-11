package com.iyuba.toeiclistening.entity;

import java.io.Serializable;

/**
 * TitleInfo表的数据库模式
 */
public class TitleInfo implements Serializable {
    public int TestType;
    public int PartType;

    public int TitleNum;//文章的编号
    public String PackName = ""; //文章所属的考试题包的编号
    public int QuesNum;//该文章包含的问题的个数
    public int SoundTime;//文章总的语音时间长
    public boolean Vip;//bool则该文章可用，false则为不可用，此值可以修改

    public boolean EnText;
    public boolean CnText;
    public boolean JpText;//此文章是否有对应语言的版本

    public boolean EnExplain;
    public boolean CnExplain;
    public boolean JpExplain;//此文章是否有对应语言的解析
    public String TitleName = "";//文章的名称

    public int Handle;
    public boolean Favorite;//文章是否收藏
    public int RightNum;
    public int StudyTime;//暂时不用


    public String songPath = "";
    public String webSongPath = "";

    public int downloadStatus = 0;//下载状态 1未下载 2正在下载 3下载暂停 4下载完成
}
