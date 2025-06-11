package com.iyuba.toeiclistening.adapter;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

public class FavTitleAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<TitleInfo> favTestList;
	private ViewHolder viewHolder;
	public ArrayList<EditCond> ediCondList;

	private ZDBHelper helper;
	private int state = 1;// 没有在进行批量删除

	public FavTitleAdapter(Context context, ArrayList<TitleInfo> list) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		favTestList = list;
		helper = new ZDBHelper(mContext);
		iniEdiCondList();
	}

	public void iniEdiCondList() {
		ediCondList = new ArrayList<EditCond>();
		for (int i = 0; i < favTestList.size(); i++) {
			EditCond editCond = new EditCond();
			editCond.switchState = 0;
			editCond.imageState = true;
			ediCondList.add(editCond);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return favTestList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return favTestList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final TitleInfo curTitleInfo;
		final EditCond curEditCond;
		final ViewHolder curViewHolder;
		final int curPosition;
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.collectedquestions_in, null);
			finView(arg1);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		curTitleInfo = favTestList.get(arg0);
		curEditCond = ediCondList.get(arg0);
		curPosition = arg0;
		curViewHolder = viewHolder;
		// 初始化viewHolder里面控件的值
		setView(curPosition, curEditCond, curTitleInfo);
		return arg1;
	}

	public void finView(View arg1) {
		viewHolder = new ViewHolder();
		viewHolder.testName = (TextView) arg1
				.findViewById(R.id.collectedquestions_in_title_name);
		viewHolder.testCond = (TextView) arg1
				.findViewById(R.id.collectedquestions_in_title_cond);
		viewHolder.relativeLayout = (RelativeLayout) arg1
				.findViewById(R.id.rl_collectedquestions_in);
		viewHolder.deleteImageView = (ImageView) arg1
				.findViewById(R.id.collectedquestions_in_delete);
		viewHolder.imageView = (ImageView) arg1
				.findViewById(R.id.collectedquestions_in_image);
	}

	public void setView(int curPosition, EditCond curEditCond,
			TitleInfo curTitleInfo) {
		if (curTitleInfo.TestType==102){
			viewHolder.testName.setText("2019 "+curTitleInfo.TitleName);
		}else {
			viewHolder.testName.setText(curTitleInfo.TitleName);
		}
		String testCondString = "正确比例：" + curTitleInfo.RightNum + "/"
				+ curTitleInfo.QuesNum + "-时长:"
				+ transTime(curTitleInfo.SoundTime);
		viewHolder.testCond.setText(testCondString);
		if (curPosition % 2 == 0) {
	
			viewHolder.relativeLayout.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.test_list_background));
		} else {
			viewHolder.relativeLayout.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.test_list_background1));
		}
		if (curEditCond.switchState == 0) {
			viewHolder.deleteImageView.setVisibility(View.GONE);
		} else {
			viewHolder.deleteImageView.setVisibility(View.VISIBLE);
			if (curEditCond.switchState == 1) {
				viewHolder.deleteImageView.setImageResource(R.drawable.rem_sn);
			} else {
				viewHolder.deleteImageView.setImageResource(R.drawable.rem_sns);
			}
		}
		if (curEditCond.imageState) {
			viewHolder.imageView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imageView.setVisibility(View.GONE);
		}
	}

	public String transTime(int timeInSeconds) {
		String timeString = "";
		int minute = 0;
		int second = 0;
		minute = timeInSeconds / 60;
		second = timeInSeconds % 60;
		if (minute > 0) {
			timeString = minute + "分" + second + "秒";
		} else {
			timeString = second + "秒";
		}
		return timeString;
	}

	/**
	 * 记录没一篇文章的编辑情况
	 * 
	 * @author 魏申鸿
	 * 
	 */

	public int getState() {
		return state;
	}

	/**
	 * 
	 * 设置收藏文章是否可编辑的状态,并更新删除的状态
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
		if (state == 0) {
			for (int i = 0; i < ediCondList.size(); i++) {
				ediCondList.get(i).switchState = 1;// 变为选择的界面
				ediCondList.get(i).imageState = false;
			}
		} else if (state == 1) {
			// 删除list中选定要删除的数据
			updateData();
			for (int i = 0; i < ediCondList.size(); i++) {
				ediCondList.get(i).switchState = 0;
				ediCondList.get(i).imageState = true;
			}
		}
		notifyDataSetChanged();
	}

	public ArrayList<EditCond> getEdiCondList() {
		return ediCondList;
	}

	class ViewHolder {
		public TextView testName;
		public TextView testCond;
		public RelativeLayout relativeLayout;
		public ImageView deleteImageView;
		public ImageView imageView;
	}

	class EditCond {
		public int switchState = 0;// 0不可见，2选中，1未选中
		public boolean imageState = true;
	}

	public void updateView(int position) {
		if (ediCondList.get(position).switchState == 1) {
			ediCondList.get(position).switchState = 2;
		} else if (ediCondList.get(position).switchState == 2) {
			ediCondList.get(position).switchState = 1;
		}
		notifyDataSetChanged();
	}

	public void updateData() {
		// 利用Itreator进行操作，保障数据的安全，若根据位置变化进行操作，则要同时改变数据位置的偏移
		Iterator<TitleInfo> titleInfoItreIterator = favTestList.iterator();
		Iterator<EditCond> editCondIterator = ediCondList.iterator();
		while (editCondIterator.hasNext()) {
			EditCond editCond = editCondIterator.next();
			TitleInfo titleInfo = titleInfoItreIterator.next();
			if (editCond.switchState == 2) {
				editCondIterator.remove();
				titleInfoItreIterator.remove();
				helper.setFavoriteTitle(titleInfo.TitleNum, false);
			}
		}
	}
}
