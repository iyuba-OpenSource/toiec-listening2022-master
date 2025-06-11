/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.discover.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.sqlite.mode.test.CetFillInBlank;
import com.iyuba.core.R;

/**
 * 四级填空题答案适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class BlankAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CetFillInBlank> blanks;

	public BlankAdapter(Context context) {
		mContext = context;
		blanks = CetDataManager.Instace().blankList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return blanks.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return blanks.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final CetFillInBlank cetFillInBlank = blanks.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_blank, null);
			viewHolder = new ViewHolder();
			viewHolder.number = (TextView) convertView
					.findViewById(R.id.number);
			viewHolder.your = (TextView) convertView
					.findViewById(R.id.your_answer);
			viewHolder.right = (TextView) convertView
					.findViewById(R.id.right_answer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.number.setText(cetFillInBlank.id);
		viewHolder.your.setText(cetFillInBlank.yourAnswer);
		viewHolder.right.setText(cetFillInBlank.answer);
		if (cetFillInBlank.yourAnswer.equals(cetFillInBlank.answer)) {
			viewHolder.your.setTextColor(Color.BLACK);
			viewHolder.right.setTextColor(Color.BLACK);
		} else {
			viewHolder.your.setTextColor(Color.RED);
			viewHolder.right.setTextColor(Color.RED);
		}
		return convertView;
	}

	public class ViewHolder {
		TextView your;
		TextView right;
		TextView number;
	}
}
