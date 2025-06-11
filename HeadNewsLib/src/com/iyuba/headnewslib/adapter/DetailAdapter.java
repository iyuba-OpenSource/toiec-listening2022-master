package com.iyuba.headnewslib.adapter;

import java.util.List;

import com.iyuba.headnewslib.R;
import com.iyuba.headnewslib.model.ArticleDetail;
import com.iyuba.headnewslib.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailAdapter extends BaseAdapter {
	private static final String TAG = DetailAdapter.class.getSimpleName();
	public static final int MODE_ENG = 1;
	public static final int MODE_CHNENG = 2;
	
	private static final String IMAGEURL = "http://cms."+Constant.IYBHttpHead+"/cms/news/image/";

	private LayoutInflater mInflater;
	private List<ArticleDetail> mList;
	private int mode;

	public DetailAdapter(Context context, List<ArticleDetail> list) {
		mInflater = LayoutInflater.from(context);
		mList = list;
		mode = MODE_ENG;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public int getShowMode(){
		return mode;
	}
	
	public void setShowMode(int mode){
		this.mode = mode;
		notifyDataSetInvalidated();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.details_item, null);
		}
		ArticleDetail currentDetail = (ArticleDetail) getItem(position);
		ImageView sentencePic = ViewHolder.get(convertView, R.id.iv_sentence_pic);
		TextView sentence = ViewHolder.get(convertView, R.id.tp_Sentence);
		TextView sentence_cn = ViewHolder.get(convertView, R.id.tv_Sentence_cn);

		switch (mode) {
		case MODE_ENG:
			sentence.setVisibility(View.VISIBLE);
			sentence_cn.setVisibility(View.GONE);
			break;
		case MODE_CHNENG:
			sentence.setVisibility(View.VISIBLE);
			sentence_cn.setVisibility(View.VISIBLE);
			break;
		}
		sentence.setText(mList.get(position).getSentence());
		sentence_cn.setText(mList.get(position).getSentence_cn());
		if (currentDetail.hasNoPicture()) {
			sentencePic.setVisibility(View.GONE);
		} else {
			sentencePic.setVisibility(View.VISIBLE);
			String uri = IMAGEURL + currentDetail.getImgPath();
			ImageLoader.getInstance().displayImage(uri, sentencePic);
		}

		return convertView;
	}

}
