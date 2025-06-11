package com.iyuba.core.microclass.sqlite.mode;

import java.io.Serializable;

/*
 * 移动课堂中MobClassRes表的数据库模式
 * 
 * */
public class MbText implements Serializable{
	public int id;
	public int seconds;
	public String imageName;
	
	public int answer;
	public int number;
	public int type;
	
	public int TitleId;
	public int PackId;
}

