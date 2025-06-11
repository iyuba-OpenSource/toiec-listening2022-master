/**
 * 
 */
package com.iyuba.toeiclistening.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.entity.BlogContent;
import com.iyuba.toeiclistening.entity.BlogInfo;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;


/**
 * @author yao
 *
 */
public class BlogListResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public String result;// 返回代码
	public String message;// 返回信息
	public BlogInfo blogInfo = new BlogInfo();
	public JSONArray data;
	public ArrayList<BlogContent> blogList;
	public String firstPage;
	public String prevPage;
	public String nextPage;
	public String lastPage;
	public int blogCounts;
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		responseString=bodyElement.toString().trim();
		blogList=new ArrayList<BlogContent>();
		try {
			jsonBody=new JSONObject(responseString.substring(
					responseString.indexOf("{"), responseString.lastIndexOf("}") + 1));
			Log.e("jsonBody", ""+jsonBody);
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
			message = jsonBody.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			firstPage = jsonBody.getString("firstPage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			blogCounts = jsonBody.getInt("blogCounts");
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
				System.out.println("blogInfo.blogCounts"+jsonBody.getString("blogCounts"));
				blogInfo.blogCounts=jsonBody.getString("blogCounts");
				
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				blogInfo.pageNumber=jsonBody.getString("pageNumber");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				blogInfo.firstPage=jsonBody.getString("firstPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				blogInfo.prevPage=jsonBody.getString("prevPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				blogInfo.nextPage=jsonBody.getString("nextPage");
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				blogInfo.lastPage=jsonBody.getString("lastPage");
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
					BlogContent blogContent=new BlogContent();
					JSONObject jsonObject= ((JSONObject)data.opt(i));
					blogContent.replynum="";
					blogContent.viewnum=jsonObject.getString("viewnum");		//日志的浏览数
					blogContent.blogid=jsonObject.getString("blogid");			//日志的ID
					blogContent.dateline=jsonObject.getString("dateline");		//日志的发布时间
					blogContent.favtimes="";
					blogContent.friend="";
					blogContent.ids="";
					blogContent.message="";
					blogContent.noreply="";
					blogContent.password="";
					blogContent.sharetimes="";
					blogContent.subject=jsonObject.getString("subject");		//日志的标题
					blogList.add(blogContent);
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
