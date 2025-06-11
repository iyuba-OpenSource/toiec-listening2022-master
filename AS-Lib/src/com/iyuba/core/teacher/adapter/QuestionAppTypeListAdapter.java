package com.iyuba.core.teacher.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.teacher.sqlite.mode.QuestionAppType;
import com.iyuba.core.R;

public class QuestionAppTypeListAdapter extends BaseAdapter {
	private Context mContext;
	public List<QuestionAppType> mList = new ArrayList<QuestionAppType>();

	public QuestionAppTypeListAdapter(Context context, List<QuestionAppType> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public QuestionAppType getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final QuestionAppType qat = mList.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lib_list_item_qapptype,
					null);
			viewHolder = new ViewHolder();
			viewHolder.select = (ImageView) convertView
					.findViewById(R.id.select);
			viewHolder.selectContent = (TextView) convertView
					.findViewById(R.id.selectContent);
			viewHolder.pall = convertView.findViewById(R.id.pall);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.selectContent.setText(qat.type);
		if (qat.isSelect) {
			viewHolder.select.setBackgroundResource(R.drawable.selected);
			viewHolder.pall.setBackgroundColor(0xffeeeeee);
		} else {
			viewHolder.select.setBackgroundResource(R.drawable.select);
			viewHolder.pall.setBackgroundColor(0xffffffff);
		}
		
		return convertView;
	}

	public class ViewHolder {

		ImageView select;

		TextView selectContent;
		View pall;
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

			case 1:
				notifyDataSetChanged();
				break;
			case 3:
				CustomToast.showToast(mContext, R.string.agree_already);
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.comment_agree);
				break;

			}

		}
	};

}
