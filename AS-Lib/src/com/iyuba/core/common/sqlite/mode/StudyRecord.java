/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.sqlite.mode;

/**
 * 学习记录
 * 
 * @author 陈彤
 */
public class StudyRecord {
	public String lesson;
	public String voaid;
	public String starttime;
	public String endtime;
	public String flag;

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson=lesson;
	}

	public String getVoaid() {
		return voaid;
	}

	public void setVoaid(String voaid) {
		this.voaid=voaid;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime=starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime=endtime;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag=flag;
	}
}
