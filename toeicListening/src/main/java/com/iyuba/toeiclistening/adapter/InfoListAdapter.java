package com.iyuba.toeiclistening.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.BlogContent;



public class InfoListAdapter extends BaseAdapter{

	private Context mContext;
	public  ArrayList<BlogContent> mList=new ArrayList<BlogContent>();
	private ViewHolder viewHolder;
	
	/**
	 * @param mContext
	 * @param mList
	 */
	public InfoListAdapter(Context mContext, ArrayList<BlogContent> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	/**
	 * @param mContext
	 */
	public InfoListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	public void addList(ArrayList<BlogContent> blogList){
		mList.addAll(blogList);
		
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
	public void clearList(){
		mList.clear();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BlogContent curBlog=mList.get(position);
	//	System.out.println("position------>"+position);
	//	System.out.println("curBlog------>"+curBlog.subject);
		if(convertView==null){
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=vi.inflate(R.layout.informationlist_item, null);
			viewHolder=new ViewHolder();
			viewHolder.replyNum=(TextView)convertView.findViewById(R.id.infoReplyNum);
			//viewHolder.shareNum=(TextView)convertView.findViewById(R.id.blogShareNum);
			viewHolder.subject=(TextView)convertView.findViewById(R.id.infoSubject);
			viewHolder.time=(TextView)convertView.findViewById(R.id.infoTime);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		if (curBlog.message!=null&&!curBlog.message.equals("")) {
			convertView.findViewById(R.id.read).setVisibility(View.VISIBLE);
		}
		else {
			convertView.findViewById(R.id.read).setVisibility(View.GONE);
		}
		viewHolder.replyNum.setText(curBlog.replynum);
		//viewHolder.shareNum.setText(curBlog.sharetimes);
		viewHolder.subject.setText(curBlog.subject);
		long time=Long.parseLong(curBlog.dateline)*1000;
		
		viewHolder.time.setText(DateFormat.format("yyyy-MM-dd kk:mm:ss", time));
		return convertView;
	}
	class ViewHolder{
		TextView subject;
		TextView time;
		TextView replyNum;
		//TextView shareNum;
	}
}
