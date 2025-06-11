package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.microclass.sqlite.mode.CoursePackInfo;
import com.iyuba.core.microclass.sqlite.mode.MbText;

public class MbTextResponse extends BaseJSONResponse{

	public String responseString;
	public JSONObject jsonBody;
	public String result;// 返回代码
	public int total;	 //返回总记录数
	public int titleid; //返回所属title
	public String titleId;
	public String packId;
	public CoursePackInfo cpInfo = new CoursePackInfo();

	public MbText mbText = new MbText();
	public JSONArray data;
	public ArrayList<MbText> mbTextList;
	
	public MbTextResponse(String titleid, String packId) {
		super();
		this.titleId = titleid;
		this.packId = packId;
	}



	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		responseString=bodyElement.toString().trim();
		mbTextList=new ArrayList<MbText>();
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
			Log.d("MbTextResponse result", result);
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			total = jsonBody.getInt("total");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			titleid = jsonBody.getInt("titleid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result.equals("1")){
			try{
				System.out.println("cpInfo.total"+jsonBody.getInt("total"));
				cpInfo.total=jsonBody.getInt("total");
				
			}catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try{
				cpInfo.titleid=jsonBody.getInt("titleid");
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
						MbText mbText=new MbText();
						JSONObject jsonObject= ((JSONObject)data.opt(i));
						mbText.id=jsonObject.getInt("id");
						mbText.seconds=jsonObject.getInt("seconds");
						mbText.imageName=jsonObject.getString("imageName");	
						mbText.answer=jsonObject.getInt("answer");
						mbText.number=jsonObject.getInt("number");
						mbText.type=jsonObject.getInt("type");
						mbText.TitleId = Integer.parseInt(titleId);
						mbText.PackId = Integer.parseInt(packId);
					
					mbTextList.add(mbText);
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

