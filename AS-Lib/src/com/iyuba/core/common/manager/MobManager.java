/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.manager;

import java.util.ArrayList;

import com.iyuba.core.common.sqlite.mode.mob.MbText;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
import com.iyuba.core.microclass.sqlite.mode.OwnedCourse;
import com.iyuba.core.microclass.sqlite.mode.PayedCourseRecord;
import com.iyuba.core.microclass.sqlite.mode.User;


/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class MobManager {
	public int packid;
	public String appId;
	public int ownerid;
	public String desc;
	public double curPackPrice;
	public ArrayList<MbText> mbList;
	public int CourseNum;
	private static MobManager instance;
	
	public ArrayList<CoursePack> courseList = new ArrayList<CoursePack>();//移动课堂课程列表
	public ArrayList<CoursePackType> courseTypeList = new ArrayList<CoursePackType>();
	public User user=null;
	public ArrayList<CourseContent> courseContentList = new ArrayList<CourseContent>();//移动课堂内容列表
	public ArrayList<MbText> mbTextList = new ArrayList<MbText>();//移动课堂每个课程的相关资源信息
	public ArrayList<OwnedCourse> ownedCourseList = new ArrayList<OwnedCourse>();//已购买的课程信息
	public ArrayList<PayedCourseRecord> payedCourseRecordList = new ArrayList<PayedCourseRecord>();
	

	private MobManager() {
	};

	public static synchronized MobManager Instance() {
		if (instance == null) {
			instance = new MobManager();
		}
		return instance;
	}
}
