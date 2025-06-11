package com.iyuba.toeiclistening.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.InfoListAdapter;
import com.iyuba.toeiclistening.entity.BlogContent;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.protocol.BlogListRequest;
import com.iyuba.toeiclistening.protocol.BlogListResponse;
import com.iyuba.toeiclistening.sqlite.BlogHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.widget.PullToRefreshListView;
import com.iyuba.toeiclistening.widget.PullToRefreshListView.OnRefreshListener;
import com.iyuba.toeiclistening.widget.TitleBar;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;

/**
 * 咨询列表，在主Activity中
 *
 */
public class InfoListActivity extends Activity implements OnScrollListener {
    private PullToRefreshListView listView;//下拉刷新列表
    private ArrayList<BlogContent> blogList = new ArrayList<BlogContent>();
    private InfoListAdapter adapter;
    private Context mContext;
//    private TitleBar titleBar;
    private Boolean isLastPage = false;
    private BlogHelper blogHelper;
    private CustomDialog dialog;
    private int startid = 0;
    private int GAP = 10;
    private int selectId = 0;

    private int maxBlogId;
    private static final String TAG = "InfoListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.information_list);
        mContext = this;
        blogHelper = new BlogHelper(mContext);
        blogList = blogHelper.findDataByAll();
//		blogList = blogHelper.getBlogs(0, 0, 1);

        //Log.d("blogList的大小为：", blogList.size() + "");

        DataManager.Instance().blogList = blogList;
        dialog = new WaittingDialog().wettingDialog(mContext);
//        titleBar = (TitleBar) findViewById(R.id.title_bar);
//        titleBar.setTitleText("资讯");
//        titleBar.setButtonVisible(View.INVISIBLE);
        initWidget();
        adapter = new InfoListAdapter(mContext, blogList);
        listView.setAdapter(adapter);
        setListener();
        listView.addFooterOnClickListener(onClickListener);
        listView.setOnScrollListener(this);
        new Thread() {
            public void run() {
                super.run();
                listView.setRefresh();
            }
        }.start();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.putExtra("position", position);
                intent.setClass(mContext, InformationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initWidget() {

        // TODO Auto-generated method stub
        listView = (PullToRefreshListView) findViewById(R.id.blogslist);
        listView.setOnRefreshListener(onRefreshListener);
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            // TODO Auto-generated method stub

            maxBlogId = blogHelper.findMaxBlogId();

            if (CheckNetWork.isNetworkAvailable(mContext)) {// 开始刷新
                LogUtil.e(TAG,"请求："+"开始刷新");
                handler.sendEmptyMessage(1);
            } else {// 刷新失败
                listView.onRefreshFail();
            }
        }

    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        adapter.notifyDataSetChanged();
        super.onResume();

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    dialog.show();
                    ClientSession.Instace().asynGetResponse(
                            // APPID pageNumber pageCounts
                            new BlogListRequest(Constant.PUBLIC_ACCOUNT_ID, maxBlogId + "",
                                    "100"), new IResponseReceiver() {

                                @Override
                                public void onResponse(BaseHttpResponse response,
                                                       BaseHttpRequest request, int rspCookie) {
                                    BlogListResponse res = (BlogListResponse) response;
                                    LogUtil.e(TAG,"请求：result"+res.result);
                                    if (res.result.equals("1")) {
                                        if (res.blogList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
                                            // 则证明没有新的资讯类容，
                                            // 无需刷新
                                            // 以后可更改接口实现高效刷新
                                            int flag = 0;
                                            if (DataManager.Instance().blogList
                                                    .size() == 0) {
                                                flag = 1;
                                            } else {
                                                if (!res.blogList.get(0).blogid.equals(DataManager
                                                        .Instance().blogList.get(0).blogid)) {
                                                    flag = 1;
                                                }
                                            }
                                            if (flag == 1) {
                                                blogList.clear();
                                                blogList.addAll(res.blogList);
                                                adapter.clearList();// 清除原来的记录
                                                adapter.addList(res.blogList);
                                                blogHelper.insertBlogs(blogList);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    handler.sendEmptyMessage(2);
                                    dialog.dismiss();
                                }
                            }, null, null);
                    LogUtil.e(TAG,"请求："+"onResponse");
                    break;
                case 2:
                    LogUtil.e(TAG,"请求："+"列表刷新成功！");
                    listView.onRefreshSuccess();
                    break;
            }
        }
    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        /*
        if (arg1 + arg2 == arg3&&!isLastPage) {
			try {
				int endid = Integer
						.parseInt(blogList.get(blogList.size() - 1).blogid) - 1;
				int startid = endid -GAP;
				ArrayList<BlogContent> blogContents = blogHelper.getBlogs(
						startid, endid, 1);
				if(blogContents.size()<GAP){
					isLastPage=true;
					
				}
				if(blogContents.size()>0){
					blogList.addAll(blogList.size(), blogContents);
					adapter.notifyDataSetChanged();
				}
			} catch (Exception exception) {
			}
		}*/
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (!isLastPage) {
                try {
                    int endid = Integer
                            .parseInt(blogList.get(blogList.size() - 1).blogid) - 1;
                    int startid = endid - GAP;
                    int selection = blogList.size();
                    Log.e("endis", endid + " ");
                    ArrayList<BlogContent> blogContents = blogHelper.getBlogs(
                            startid, endid, 2);
                    if (blogContents.size() < GAP) {
                        isLastPage = true;
                    }
                    if (blogContents.size() > 0) {
                        blogList.addAll(blogList.size(), blogContents);
                        adapter.notifyDataSetChanged();
                    }
                    listView.setSelection(selection);
                    if (isLastPage) {
                        listView.setFooterTextByState(isLastPage);
                    }
                } catch (Exception exception) {
                    LogUtil.e(TAG,"资讯"+exception);
                }
            }
        }
    };

}
