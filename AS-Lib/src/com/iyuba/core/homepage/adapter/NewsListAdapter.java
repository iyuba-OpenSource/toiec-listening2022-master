package com.iyuba.core.homepage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.homepage.entity.NewsInfo;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class NewsListAdapter extends BaseAdapter {

    private List<NewsInfo> mList = new ArrayList<NewsInfo>();
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder curViewHolder;
    private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

    public NewsListAdapter(Context mContext, List<NewsInfo> mList) {
        this.mList = mList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(this.mContext);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NewsInfo curDetail = mList.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_info, null);

            curViewHolder = new ViewHolder();
            curViewHolder.image = (ImageView) convertView
                    .findViewById(R.id.news_pic);
            curViewHolder.newsEn = (TextView) convertView
                    .findViewById(R.id.news_en);
            curViewHolder.newsCn = (TextView) convertView
                    .findViewById(R.id.news_cn);
            curViewHolder.newsTime = (TextView) convertView
                    .findViewById(R.id.news_time);
            curViewHolder.newsReadTimes = (TextView) convertView
                    .findViewById(R.id.news_read_times);

            convertView.setTag(curViewHolder);
        } else {
            curViewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(curDetail.picUrl,
                        curViewHolder.image,options);
                curViewHolder.newsCn.setTextColor(Color.GRAY);
                curViewHolder.newsCn.setText(curDetail.title);
                curViewHolder.newsEn.setText(curDetail.titleEn);
                curViewHolder.newsTime.setText(curDetail.time.substring(0, curDetail.time.indexOf(" ")));
                curViewHolder.newsReadTimes.setText(curDetail.readTimes);
//        switch (curDetail.itemId) {
//            case "3":
//
//                ImageLoader.getInstance().displayImage(curDetail.picUrl,
//                        curViewHolder.image,options);
//                curViewHolder.newsCn.setTextColor(Color.GRAY);
//                curViewHolder.newsCn.setText(curDetail.title);
//                curViewHolder.newsEn.setText(curDetail.titleEn);
//                curViewHolder.newsTime.setText(curDetail.time.substring(0, curDetail.time.indexOf(" ")));
//                curViewHolder.newsReadTimes.setText(curDetail.readTimes);
//                break;
//            case "4":
//                ImageLoader.getInstance().displayImage(curDetail.picUrl,
//                        curViewHolder.image,options);
//                curViewHolder.newsCn.setText(curDetail.title);
//                curViewHolder.newsEn.setText(curDetail.titleEn);
//                curViewHolder.newsTime.setText(curDetail.time.substring(0, curDetail.time.indexOf(" ")));
//                curViewHolder.newsReadTimes.setText(curDetail.readTimes);
//                break;
//            default:
//                ImageLoader.getInstance().displayImage(curDetail.picUrl,
//                        curViewHolder.image,options);
//                curViewHolder.newsCn.setText(curDetail.title);
//                curViewHolder.newsEn.setText(curDetail.titleEn);
//                curViewHolder.newsTime.setText(curDetail.time.substring(0, curDetail.time.indexOf(" ")));
//                curViewHolder.newsReadTimes.setText(curDetail.readTimes);
//                break;
//        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView image;
        TextView newsCn;
        TextView newsEn;
        TextView newsTime;
        TextView newsReadTimes;
    }
}
