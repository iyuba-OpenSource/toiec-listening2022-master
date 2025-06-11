/**
 * 
 */
package com.iyuba.core.discover.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.R;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestAddAttention;
import com.iyuba.core.common.protocol.message.RequestCancelAttention;
import com.iyuba.core.common.protocol.message.ResponseAddAttention;
import com.iyuba.core.common.protocol.message.ResponseCancelAttention;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.me.sqlite.mode.MutualAttention;

import java.util.ArrayList;

/**
 * 相互关注适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class MutualAttentionAdapter extends BaseAdapter {
	private Context mContext;
	public ArrayList<MutualAttention> mList = new ArrayList<MutualAttention>();
	private ViewHolder viewHolder;
	private String fansId;

	/**
	 * @param mContext
	 * @param mList
	 */
	public MutualAttentionAdapter(Context mContext,
			ArrayList<MutualAttention> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	/**
	 * @param mContext
	 */
	public MutualAttentionAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public void addList(ArrayList<MutualAttention> fansList) {
		mList.addAll(fansList);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void clearList() {
		mList.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final MutualAttention curFans = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.mutualattentionlist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.mutualattentionlist_username);
			viewHolder.userImageView = (ImageView) convertView
					.findViewById(R.id.mutualattentionlist_portrait);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.username.setText(curFans.fusername);
		GitHubImageLoader.Instace(mContext).setCirclePic(curFans.followuid,
				viewHolder.userImageView);
		return convertView;
	}

	class ViewHolder {
		TextView username;
		ImageView userImageView;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				notifyDataSetChanged();
				break;
			case 2:
				// 加关注
				ClientSession.Instace().asynGetResponse(
						new RequestAddAttention("10", fansId),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
									BaseHttpRequest request, int rspCookie) {
								// TODO 自动生成的方法存根
								// TODO Auto-generated method stub
								ResponseAddAttention res = (ResponseAddAttention) response;
								if (res.result.equals("501")) {
									handler.sendEmptyMessage(5);
									handler.sendEmptyMessage(0);// 修改为已关注
								} else {
									handler.sendEmptyMessage(6);
								}
								handler.sendEmptyMessage(4);
							}

						});
				break;
			case 3:
				// 取消关注
				ClientSession.Instace().asynGetResponse(
						new RequestCancelAttention("10", fansId),
						new IResponseReceiver() {

							@Override
							public void onResponse(BaseHttpResponse response,
									BaseHttpRequest request, int rspCookie) {
								// TODO 自动生成的方法存根
								ResponseCancelAttention responseFansList = (ResponseCancelAttention) response;
								System.out.println(responseFansList.result
										+ "!!!!!!!!!!!!");
								if (responseFansList.result.equals("510")) {
									handler.sendEmptyMessage(5);
									handler.sendEmptyMessage(0);// 修改为已关注
								} else {
									handler.sendEmptyMessage(5);
								}
								handler.sendEmptyMessage(4);
							}

						});
				break;
			case 4:
				//

				break;
			case 5:
				Toast.makeText(mContext, R.string.social_success_attention,
						Toast.LENGTH_SHORT).show();
				break;
			case 6:
				Toast.makeText(mContext, R.string.social_cancle_attention,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
