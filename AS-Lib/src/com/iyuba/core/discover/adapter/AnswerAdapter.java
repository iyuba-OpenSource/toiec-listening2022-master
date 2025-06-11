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
import com.iyuba.core.common.sqlite.mode.test.CetAnswer;
import com.iyuba.core.R;

/**
 * 四级选择题答案适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class AnswerAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CetAnswer> answers;

	public AnswerAdapter(Context context) {
		mContext = context;
		answers = CetDataManager.Instace().answerList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return answers.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return answers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final CetAnswer cetAnswer = answers.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_answer, null);
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
		viewHolder.number.setText(cetAnswer.id);
		viewHolder.your.setText(cetAnswer.yourAnswer);
		viewHolder.right.setText(cetAnswer.rightAnswer);
		if (cetAnswer.yourAnswer.equals(cetAnswer.rightAnswer)) {
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
