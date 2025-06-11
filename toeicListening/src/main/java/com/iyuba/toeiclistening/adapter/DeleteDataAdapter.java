package com.iyuba.toeiclistening.adapter;

import java.io.File;
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
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.ListFileUtil;

public class DeleteDataAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<File> testList;
	private LayoutInflater mInflater;
	private ArrayList<EditCond> ediCondList;
	int state = 1;
	private ZDBHelper helper;

	public DeleteDataAdapter(Context context, ArrayList<File> list) {
		mContext = context;
		testList = list;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		helper = new ZDBHelper(mContext);
		iniEdiCondList();
	}

	public void iniEdiCondList() {
		ediCondList = new ArrayList<EditCond>();
		for (int i = 0; i < testList.size(); i++) {
			EditCond editCond = new EditCond();
			editCond.switchState = 0;
			ediCondList.add(editCond);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return testList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return testList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView textView = null;
		RelativeLayout layout = null;
		ImageView imageView = null;
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.delete_test_in, null);
			
		}
		textView = (TextView) arg1.findViewById(R.id.delete_test_in_title_info);
		layout = (RelativeLayout) arg1
				.findViewById(R.id.delete_test_in_relativlayout);
		imageView = (ImageView) arg1.findViewById(R.id.delete_data_in_delete);
		/*
		 * if(arg0%2==0){
		 * layout.setBackground(mContext.getResources().getDrawable
		 * (R.drawable.test_list_background)); }else{
		 * layout.setBackground(mContext
		 * .getResources().getDrawable(R.drawable.test_list_background1)); }
		 */
		textView.setText(getString(testList.get(arg0)));
		
//		Log.d("计算大小的试题为：", testList.get(arg0)+"");
		
		if (ediCondList.get(arg0).switchState == 0) {
			imageView.setVisibility(View.INVISIBLE);
		} else {
			imageView.setVisibility(View.VISIBLE);
			if (ediCondList.get(arg0).switchState == 1) {
				imageView.setImageResource(R.drawable.rem_sn);
			} else {
				imageView.setImageResource(R.drawable.rem_sns);
			}
		}
		return arg1;
	}

	public String getString(File file) {
		String string = "";
		if (file != null) {
			double length = ListFileUtil.getDirSize(file);
			length = length/1024/1024;
			//这个地方存在问题，每次返回值太小，都变成了0
			string = file.getName() + "试题所占容量:" +String.format("%.2f", length) + "M";
		}
		return string;
	}

	class EditCond {
		public int switchState = 0;// 0不可见，2选中，1未选中
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
		Iterator<File> testIterator = testList.iterator();
		Iterator<EditCond> editCondIterator = ediCondList.iterator();
		while (editCondIterator.hasNext()) {
			EditCond editCond = editCondIterator.next();
			File file = (File) testIterator.next();
			if (editCond.switchState == 2) {
				editCondIterator.remove();
				testIterator.remove();
				deleteFile(file);
				helper.setProgress(file.getName(), 0, false);
				file.delete();// 删除文件本身
			}
		}
		DataManager.Instance().packInfoList.clear();
		DataManager.Instance().packInfoList = helper.getPackInfo();
	}

	/**
	 * 删除目录下的所有文件
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {
		if (!file.isDirectory()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
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
			}
		} else if (state == 1) {
			// 删除list中选定要删除的数据
			updateData();
			for (int i = 0; i < ediCondList.size(); i++) {
				ediCondList.get(i).switchState = 0;
			}
		}
		notifyDataSetChanged();
	}

	public int getState() {
		return state;
	}
}
