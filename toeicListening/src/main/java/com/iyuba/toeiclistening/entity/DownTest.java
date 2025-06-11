package com.iyuba.toeiclistening.entity;

/**
 * 
 * 下载听力对应的实体类
 * @author 魏申鸿
 *
 */
public class DownTest{
	public String sound="";//对应.m4a文件名
	public int titleNum;//对应的title的number
	public String packName="";//对应的包的id
	public int packNum; //对应的packInfo的number

	public int testNum;//本套题的试题数量，排除数据库查询数量多的问题

	public int testType;
}
