
package com.iyuba.core.common.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.R;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestAddAttention;
import com.iyuba.core.common.protocol.message.ResponseAddAttention;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.me.sqlite.mode.SearchItem;

import java.util.ArrayList;

/**
 * 查询结果 初步废弃了
 * @author 陈彤
 * 
 */
public class SearchResultAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<SearchItem> mList = new ArrayList<SearchItem>();
	private ViewHolder viewHolder;

	public SearchResultAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
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

	/*
	 * public void clearList(){ mList.clear(); }
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		final int pos = position;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.searchlist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.searchlist_username);
			viewHolder.userImageView = (ImageView) convertView
					.findViewById(R.id.searchlist_portrait);
			viewHolder.followButton = (Button) convertView
					.findViewById(R.id.follow_button);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.username.setText(mList.get(position).username);
		viewHolder.followButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				ClientSession.Instace().asynGetResponse(
						new RequestAddAttention(AccountManagerLib
								.Instace(mContext).userId, mList.get(pos).uid),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
									BaseHttpRequest request, int rspCookie) {
								// TODO 自动生成的方法存根
								ResponseAddAttention rs = (ResponseAddAttention) response;
								if (rs.result.equals("500")) {
									handler.sendEmptyMessage(2);
								} else if (rs.result.equals("502")) {
									handler.sendEmptyMessage(3);
								} else if (rs.result.equals("503")) {
									handler.sendEmptyMessage(4);
								}
							}
						});
			}
		});
		GitHubImageLoader.Instace(mContext).setCirclePic(mList.get(position).uid,
				viewHolder.userImageView);
		return convertView;
	}

	public void addList(ArrayList<SearchItem> list) {
		// TODO Auto-generated method stub
		mList.clear();
		mList.addAll(list);
	}

	class ViewHolder {
		TextView username;
		ImageView userImageView;
		Button followButton;
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
				CustomToast.showToast(mContext,
						R.string.social_success_attention);
				break;
			case 3:
				CustomToast.showToast(mContext, R.string.social_attentioned);
				break;
			case 4:
				CustomToast.showToast(mContext,
						R.string.social_failed_attention);
				break;
			}
		}
	};

}
