package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.microclass.sqlite.mode.Comment;

/**
 * 获取评论的Response
 * 
 */

public class CommentResponse extends BaseJSONResponse {
	public ArrayList<Comment> Comments = new ArrayList<Comment>();
	public int total;
	public String resultCode;
	public String pageNumber;
	public String totalPage;
	public String firstPage;
	public String prevPage;
	public String nextPage;
	public String lastPage;
	public String counts;
	public String message;
	private Comment tempComment;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			
			Log.d("Lib库中的CommentResponse:", jsonObjectRoot.toString());
			
			resultCode = jsonObjectRoot.getString("ResultCode");
			message = jsonObjectRoot.getString("Message");
			if (resultCode != null && resultCode.equals("511")) {
				pageNumber = jsonObjectRoot.getString("PageNumber");
				totalPage = jsonObjectRoot.getString("TotalPage");
				firstPage = jsonObjectRoot.getString("FirstPage");
				prevPage = jsonObjectRoot.getString("PrevPage");
				nextPage = jsonObjectRoot.getString("NextPage");
				lastPage = jsonObjectRoot.getString("LastPage");
				counts = jsonObjectRoot.getString("Counts");
				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
				JSONObject jsonObjectData;
				int size = JsonArrayData.length();
				for (int i = 0; i < size; i++) {
					tempComment = new Comment();
					jsonObjectData = JsonArrayData.getJSONObject(i);
					tempComment.id = jsonObjectData.getInt("id");
					tempComment.ImgSrc = jsonObjectData.getString("ImgSrc");
					tempComment.Userid = jsonObjectData.getInt("Userid");
					tempComment.ShuoShuo = jsonObjectData.getString("ShuoShuo");
					tempComment.ShuoShuoType = jsonObjectData
							.getInt("ShuoShuoType");
					tempComment.userName = jsonObjectData.getString("UserName");
					tempComment.star = jsonObjectData.getInt("star");
					tempComment.CreateDate = jsonObjectData
							.getString("CreateDate");
					if (tempComment.ShuoShuoType == 0
							|| (tempComment.ShuoShuoType == 1 && !tempComment.ShuoShuo
									.contains(".aac"))) {
						Comments.add(tempComment);
//						Log.d("Comments -------" + i+":", Comments.get(i).id+","+Comments.get(i).ShuoShuo);
					}
				}
			} else if (resultCode != null && resultCode.equals("510")) {
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return true;
	}
}
