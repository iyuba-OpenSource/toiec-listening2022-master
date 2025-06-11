/**
 * 
 */
package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;
import com.iyuba.core.microclass.sqlite.mode.CourseResponseInfo;


/**
 * @author yao
 *
 */
public class CourseListResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public String result;// 返回代码
	public String message;// 返回信息
	public CourseResponseInfo courseResponseInfo = new CourseResponseInfo();
	public JSONArray data;
	public ArrayList<CoursePack> courseList;
	public String firstPage;
	public String prevPage;
	public String nextPage;
	public String lastPage;
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		responseString=bodyElement.toString().trim();
		courseList=new ArrayList<CoursePack>();
		try {
			jsonBody=new JSONObject(responseString.substring(
					responseString.indexOf("{"), responseString.lastIndexOf("}") + 1));
//			Log.e("jsonBody", ""+jsonBody);
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
		try {
			firstPage = jsonBody.getString("firstPage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prevPage = jsonBody.getString("prevPage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			nextPage = jsonBody.getString("nextPage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			lastPage = jsonBody.getString("lastPage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result.equals("1")){
			try{
				courseResponseInfo.firstPage=jsonBody.getString("firstPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				courseResponseInfo.prevPage=jsonBody.getString("prevPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				courseResponseInfo.nextPage=jsonBody.getString("nextPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				courseResponseInfo.lastPage=jsonBody.getString("lastPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{				
				data=jsonBody.getJSONArray("data");				
				
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(data!=null&&data.length()!=0){
				for(int i=0;i<data.length();i++){
					try{
					CoursePack coursePack=new CoursePack();
					JSONObject jsonObject= ((JSONObject)data.opt(i));
					coursePack.id=jsonObject.getInt("id");
					coursePack.price=jsonObject.getDouble("price");	
					coursePack.realprice=jsonObject.getDouble("realprice");	
//					coursePack.price=10.00;
					coursePack.desc=jsonObject.getString("desc");	
					coursePack.name=jsonObject.getString("name");	
					coursePack.ownerid=jsonObject.getInt("ownerid");
					coursePack.viewCount=jsonObject.getInt("viewCount");
//					coursePack.picUrl=jsonObject.getString("pic");	
					coursePack.picUrl = coursePack.ownerid+"-"+coursePack.id;
					coursePack.classNum=jsonObject.getInt("classNum");
					courseList.add(coursePack);
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

