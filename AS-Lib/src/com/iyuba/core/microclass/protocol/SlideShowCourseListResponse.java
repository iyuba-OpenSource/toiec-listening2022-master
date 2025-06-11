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
import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;


/**
 * @author yao
 *
 */
public class SlideShowCourseListResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public String result;// 返回代码
	public JSONArray data;
	public ArrayList<SlideShowCourse> ssCourseList;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		Log.e("解析轮播图返回的内容：", "开始解析！！！");
		
		responseString=bodyElement.toString().trim();
		ssCourseList=new ArrayList<SlideShowCourse>();
		try {
			jsonBody=new JSONObject(responseString.substring(
					responseString.indexOf("{"), responseString.lastIndexOf("}") + 1));
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
		
		if(result.equals("1")){
			try{				
				data=jsonBody.getJSONArray("data");				
				
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(data!=null&&data.length()!=0){
				for(int i=0;i<data.length();i++){
					try{
					SlideShowCourse ssCourse=new SlideShowCourse();
					JSONObject jsonObject= ((JSONObject)data.opt(i));
					ssCourse.id=jsonObject.getInt("id");
					ssCourse.price=jsonObject.getDouble("price");	
					ssCourse.name=jsonObject.getString("name");	
					ssCourse.ownerid=jsonObject.getInt("ownerid");
					ssCourse.pic=jsonObject.getString("pic");	
					ssCourse.desc1=jsonObject.getString("desc1");	
					ssCourseList.add(ssCourse);
					}catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
					
			}

			
		}
		return true;
	}
}

