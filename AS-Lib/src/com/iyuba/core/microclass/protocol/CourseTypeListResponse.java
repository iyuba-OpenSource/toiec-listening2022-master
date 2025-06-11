/**
 * 
 */
package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
import com.iyuba.core.microclass.sqlite.mode.CourseResponseInfo;


/**
 * @author yao
 *
 */
public class CourseTypeListResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public String total;
	public String result;// 返回代码
	public CourseResponseInfo courseResponseInfo = new CourseResponseInfo();
	public JSONArray data;
	public ArrayList<CoursePackType> courseTypeList;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		responseString=bodyElement.toString().trim();
		courseTypeList=new ArrayList<CoursePackType>();
		
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
			total = jsonBody.getString("total");
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
					CoursePackType coursePackType=new CoursePackType();
					JSONObject jsonObject= ((JSONObject)data.opt(i));
					coursePackType.id=jsonObject.getInt("id");
					coursePackType.desc=jsonObject.getString("desc");	
					coursePackType.condition=jsonObject.getString("condition");	
					coursePackType.name=jsonObject.getString("name");	
					coursePackType.type=jsonObject.getInt("type");
					coursePackType.destination=jsonObject.getString("destination");	
					courseTypeList.add(coursePackType);
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

