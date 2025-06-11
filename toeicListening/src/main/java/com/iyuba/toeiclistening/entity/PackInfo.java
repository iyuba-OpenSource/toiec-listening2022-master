package com.iyuba.toeiclistening.entity;

/**
 * PackInfo表的数据库模式
 */
public class PackInfo {
    public String PackName;//包的名称

    public boolean IsVip;
    public boolean IsDownload;//是否下载过了
    public float Progress;//下载的进度
    public int id;//包的id
    public boolean IsFree;//与IsVip字段的关系
    public String ProductId;//
    public int TestType;
    public int DownloadState;//当前试题包的下载状态：    =====XXXX 0为未下载；1为已下载；2为正在下载  3下载暂停


    public int TitleSum;//每一个包里面试题数目
    public int QuestionSum;//每一个包里面问题的总数
    public int RightSum;//每一个包答对的题目总数

    public boolean pause = false;

    public int downloadStatus=0;//下载状态 0未下载 1正在下载 2下载暂停 3下载完成


}
