package com.iyuba.headnewslib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.WebConstant;
import com.iyuba.headnewslib.R;
import com.iyuba.headnewslib.model.Article;
import com.iyuba.headnewslib.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

public class ArticleListAdapter extends BaseAdapter {
	private static final String TAG = ArticleListAdapter.class.getSimpleName();
	private static final String IMAGE = WebConstant.HTTP_STATIC+
			Constant.IYBHttpHead+"/cms/news/image/";
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat before = new SimpleDateFormat("yyyy.MM.dd");
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat today = new SimpleDateFormat("HH:mm");

	private LayoutInflater mInflater;
	private List<Article> mDatas;

	public ArticleListAdapter(Context context, List<Article> list) {
		mInflater = LayoutInflater.from(context);
		mDatas = list;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}
	
	public void addData(List<Article> newData){
		mDatas.addAll(newData);
	}
	
	public void replaceData(List<Article> newData){
		mDatas.clear();
		mDatas.addAll(newData);
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}
	
	public Article getFirstItem() {
		if(mDatas.size() > 0){
			return mDatas.get(0);
		}
		return new Article(); // Null Object
	}
	
	public Article getLastItem() {
		if(mDatas.size() > 0){
			return mDatas.get(mDatas.size() - 1);
		}
		return new Article(); // Null Object
	}

	@Override
	public long getItemId(int position) {
		return mDatas.get(position).getNewsId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.news_item, null);
		}
		Article currentArticle = (Article) getItem(position);
		TextView tv_ReadCount = ViewHolder.get(convertView, R.id.tv_ReadCount);
		TextView tv_Title = ViewHolder.get(convertView, R.id.tv_Title);
		TextView tv_Title_cn = ViewHolder.get(convertView, R.id.tv_Title_cn);
		TextView tv_source = ViewHolder.get(convertView, R.id.tv_article_source);
		TextView tv_createtime = ViewHolder.get(convertView, R.id.tv_createtime);
		ImageView ntImageView = ViewHolder.get(convertView, R.id.iv_Pic);

		tv_ReadCount.setText(currentArticle.getReadCount() + "");
		tv_Title.setText(currentArticle.getTitle());
		tv_Title_cn.setText(currentArticle.getTitle_cn());
		tv_source.setText(currentArticle.getSource());
		tv_createtime.setText(currentArticle.getCreatTimeInFormat(today, before));

		String url = IMAGE + currentArticle.getSmallPic();
		ImageLoader.getInstance().displayImage(url, ntImageView);

		return convertView;
	}

}
