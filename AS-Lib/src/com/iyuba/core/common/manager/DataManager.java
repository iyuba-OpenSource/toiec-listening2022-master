package com.iyuba.core.common.manager;

import java.util.ArrayList;

import org.w3c.dom.Text;

import com.iyuba.core.discover.sqlite.mode.BlogContent;
import com.iyuba.core.iyumooc.microclass.bean.CoursePackListBean;
import com.iyuba.core.iyumooc.microclass.bean.CourseTypeListBean;
import com.iyuba.core.iyumooc.microclass.bean.SlideShowListBean;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
import com.iyuba.core.microclass.sqlite.mode.MbText;
import com.iyuba.core.microclass.sqlite.mode.OwnedCourse;
import com.iyuba.core.microclass.sqlite.mode.PackInfo;
import com.iyuba.core.microclass.sqlite.mode.PayedCourseRecord;
import com.iyuba.core.microclass.sqlite.mode.User;

/**
 * 个人习惯在进入下一个activity的时候  先初始化数据 
 * 用于各个页面之间的数据共享与传递
 * @author 魏申鸿
 *
 */
public class DataManager {
	private static DataManager instance;
    public static DataManager Instance() {
      if (instance == null) {
        instance = new DataManager();
      }
      return instance;
    }
    
//	public ArrayList<Answer> anwserList=null;
	public ArrayList<Text> textList=null;
//	public ArrayList<TitleInfo> titleInfoList=null;
	public ArrayList<PackInfo> packInfoList=null;
//	public ArrayList<FavoriteWord> favWordList=null;//收藏的单词的列表 
//	public ArrayList<TitleInfo> favTitleInfoList=null;//收藏的文章列表
//	public ArrayList<BlogContent> blogList=new ArrayList<BlogContent>();//资讯列表
	public ArrayList<CoursePackListBean.CoursePackDataBean> courseList = new ArrayList<>();//移动课堂课程列表
	public ArrayList<CourseTypeListBean.CourseTypeDataBean> courseTypeList = new ArrayList<>();
	public User user=null;
	public ArrayList<CourseContent> courseContentList = new ArrayList<CourseContent>();//移动课堂内容列表
	public ArrayList<CourseContent> downloadCourseContentList = new ArrayList<CourseContent>();//某个包中已下载的移动课堂内容列表
	public ArrayList<MbText> mbTextList = new ArrayList<MbText>();//移动课堂每个课程的相关资源信息
//	public BlogContent blogContent=new BlogContent();
//	public Explain explain=null;	
	public ArrayList<OwnedCourse> ownedCourseList = new ArrayList<OwnedCourse>();//已购买的课程信息
	public ArrayList<PayedCourseRecord> payedCourseRecordList = new ArrayList<PayedCourseRecord>();
//	public FeedInfo feed=new FeedInfo();
	public BlogContent blogContent;

	public ArrayList<SlideShowListBean.SlideShowDataBean> slideShowList = new ArrayList<>();
	public ArrayList<String> imageUrls = new ArrayList<>();
	public ArrayList<String> titleList = new ArrayList<>();
}

