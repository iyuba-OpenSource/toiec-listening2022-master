/**
 * 
 */
package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.mob.CourseResponseInfo;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.CoursePackDescInfo;
import com.iyuba.core.microclass.sqlite.mode.TeacherInfo;


/**
 * @author yao
 *
 */
public class ContentListResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public JSONObject jsonTeacherBody;
	public JSONObject jsonRecommendBody;
	public String result;// 返回代码
	public String detail;//介绍
	public int btnum;    //一级标题的个数
	public String condition;  //本课程面向的人群
	public String distination;
	public int viewCount;
	public String qq;
	
	public String timg;
	public String tdes;
	public String tname;
	public int tid;

	public CourseResponseInfo courseResponseInfo = new CourseResponseInfo();
	public JSONArray data;
	public JSONArray btlist;
	public ArrayList<CourseContent> courseList;
	public CourseContent recommendCourse=new CourseContent();
	public CoursePackDescInfo cpdi = new CoursePackDescInfo();
	public TeacherInfo teacherInfo = new TeacherInfo();
	
	
	public String PackId;
	
	public ContentListResponse(String packId) {
		super();
		PackId = packId;
	}

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		
		courseList=new ArrayList<CourseContent>();
		
		try {
			jsonBody = new JSONObject(bodyElement);
//			Log.e("ContentListResponse jsonBody", ""+jsonBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			result=jsonBody.getString("result");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			detail=jsonBody.getString("detail");
			cpdi.id = Integer.parseInt(PackId);
			cpdi.detail = jsonBody.getString("detail");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			btnum=jsonBody.getInt("btnum");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			condition=jsonBody.getString("condition");
			cpdi.condition = jsonBody.getString("condition");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			qq=jsonBody.getString("qq");
			cpdi.qq = jsonBody.getString("qq");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			viewCount=jsonBody.getInt("viewCount");
			cpdi.viewCount = jsonBody.getInt("viewCount");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		//获取教师信息和推荐的课程信息
		try{				
			jsonTeacherBody=jsonBody.getJSONObject("teacher");				
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{				
			timg=jsonTeacherBody.getString("timg");		
			teacherInfo.timg = jsonTeacherBody.getString("timg");
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{				
			tdes=jsonTeacherBody.getString("tdes");			
			teacherInfo.tdes = jsonTeacherBody.getString("tdes");	
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{				
			tname=jsonTeacherBody.getString("tname");	
			teacherInfo.tname = jsonTeacherBody.getString("tname");	
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{				
			tid=jsonTeacherBody.getInt("tid");			
			cpdi.tid = jsonTeacherBody.getInt("tid");
			teacherInfo.tid = jsonTeacherBody.getInt("tid");
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{				
			jsonRecommendBody=jsonTeacherBody.getJSONObject("recommend");				
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
//			recommendCourse.btid=jsonRecommendBody.getInt("btid");		
//			recommendCourse.btname=jsonRecommendBody.getString("btname");
			
			cpdi.recommendId = jsonRecommendBody.getInt("id");
			
			recommendCourse.id=jsonRecommendBody.getInt("id");
			recommendCourse.lesson = jsonRecommendBody.getString("lesson");
			recommendCourse.cost=jsonRecommendBody.getDouble("cost");	
			recommendCourse.titleName=jsonRecommendBody.getString("titleName");
			recommendCourse.video=jsonRecommendBody.getInt("video");	
			
			recommendCourse.IsFree=false;
			
			recommendCourse.AllProgress = 0f;
			recommendCourse.IsAllDownload=0;
			
			recommendCourse.AudioProgress = 0f;
			recommendCourse.IsAudioDownload=0;
			
			recommendCourse.VideoProgress = 0f;
			recommendCourse.IsVideoDownload=0;
			
			recommendCourse.PackId = Integer.parseInt(PackId);
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		if(result.equals("1")){
			try{				
				data=jsonBody.getJSONArray("data");				
				
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(data!=null&&data.length()!=0){
				for(int i=0;i<data.length();i++){
					
					try {
						JSONObject jsonFirstTitle = ((JSONObject)data.opt(i));
						try{
							btlist = jsonFirstTitle.getJSONArray("btlist");
							if(btlist!=null&&btlist.length()!=0){
								for(int j = 0;j<btlist.length();j++){
									JSONObject jsonSecondTitle = ((JSONObject)btlist.opt(j));
									CourseContent courseContent=new CourseContent();
									courseContent.btid=jsonFirstTitle.getInt("btid");		
									courseContent.btname=jsonFirstTitle.getString("btname");
									courseContent.id = jsonSecondTitle.getInt("id");
									courseContent.lesson = jsonSecondTitle.getString("lesson");
									courseContent.titleName = jsonSecondTitle.getString("titleName");
									courseContent.totaltime = jsonSecondTitle.getString("totaltime");
									courseContent.video=jsonSecondTitle.getInt("video");	
									courseContent.cost = jsonSecondTitle.getDouble("cost");
									
									courseContent.IsFree=false;
									
									courseContent.AllProgress = 0f;
									courseContent.IsAllDownload=0;
									
									courseContent.AudioProgress = 0f;
									courseContent.IsAudioDownload=0;
									
									courseContent.VideoProgress = 0f;
									courseContent.IsVideoDownload=0;
									
									
									courseContent.PackId = Integer.parseInt(PackId);
									courseList.add(courseContent);
								}
							}
							
						}catch (JSONException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
				
			}
		}
		return true;
	}
}

