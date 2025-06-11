package com.iyuba.core.teacher.activity;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.teacher.adapter.IyuTeacherListAdapter;
import com.iyuba.core.teacher.protocol.GetTeacherListRequest;
import com.iyuba.core.teacher.protocol.GetTeacherListResponse;
import com.iyuba.core.teacher.sqlite.mode.IyuTeacher;
import com.iyuba.core.R;

public class FindTeacherActivity extends ListActivity implements OnHeaderRefreshListener, OnFooterRefreshListener{
	private Context mContext;
	
	private ImageView btnBack;
	private PullToRefreshView refreshView;// 刷新列表
	private ListView quesListview;
	private IyuTeacherListAdapter quesAdapter;
	private CustomDialog waitDialog;
	public int pageNum=1;
	boolean isLast=false;
	private ArrayList<IyuTeacher> quesList = new ArrayList<IyuTeacher>();
	public  int catgory=1;
	public  TextView tt1,tt2,tt3,tt4;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lib_findteacher_list);
        mContext = this;
        waitDialog = WaittingDialog.showDialog(mContext);
        initWidget();
    }
	
	public void initWidget() {
		tt1=(TextView)findViewById(R.id.tt1);
		tt2=(TextView)findViewById(R.id.tt2);
		tt3=(TextView)findViewById(R.id.tt3);
		tt4=(TextView)findViewById(R.id.tt4);
		
		tt1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				catgory=1;
				tt1.setTextColor(0xff000000);
				tt2.setTextColor(0xffdcdcdc);
				tt3.setTextColor(0xffdcdcdc);
				tt4.setTextColor(0xffdcdcdc);
				getHeaderData();
			}
		});
		
		
		tt2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						catgory=2;
						tt2.setTextColor(0xff000000);
						tt1.setTextColor(0xffdcdcdc);
						tt3.setTextColor(0xffdcdcdc);
						tt4.setTextColor(0xffdcdcdc);
						getHeaderData();
					}
				});
		
		tt3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				catgory=3;
				tt3.setTextColor(0xff000000);
				tt2.setTextColor(0xffdcdcdc);
				tt1.setTextColor(0xffdcdcdc);
				tt4.setTextColor(0xffdcdcdc);
				getHeaderData();
			}
		});		
				
		tt4.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						catgory=4;
						tt4.setTextColor(0xff000000);
						tt2.setTextColor(0xffdcdcdc);
						tt3.setTextColor(0xffdcdcdc);
						tt1.setTextColor(0xffdcdcdc);
						getHeaderData();
					}
				});		
		btnBack = (ImageView) findViewById(R.id.btn_back);
		
		quesListview = getListView();//(ListView) findViewById(R.id.ques_listview);
		refreshView = (PullToRefreshView) findViewById(R.id.tll_queslist_content);
		refreshView.setOnHeaderRefreshListener(this);
		refreshView.setOnFooterRefreshListener(this);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		quesAdapter = new IyuTeacherListAdapter(mContext, quesList);
		quesListview.setAdapter(quesAdapter);
		quesListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			//	QuestionManager.getInstance().question.qid  = quesList.get(arg2).qid;
				
				Intent intent = new Intent();
				intent.setClass(mContext, TeacherQuesListActivity.class);
				QuestionManager.getInstance().teacher=quesList.get(arg2);
				startActivity(intent);
			}
		});
		
		getHeaderData();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;
			case 1:
				quesAdapter.notifyDataSetChanged();
				break;
			case 2:
				waitDialog.show();
				break;
			case 3:
				waitDialog.dismiss();
				break;
			case 4:
				refreshView.onHeaderRefreshComplete();
				break;
			case 5:
				refreshView.onFooterRefreshComplete();
				break;
			case 6:
				CustomToast.showToast(mContext, R.string.no_data);
				break;
				
			case 7:
				CustomToast.showToast(mContext, "已是最后一页");
				break;
			}
		}
	};
	
	public void getHeaderData() {
		handler.sendEmptyMessage(2);
		 
		ExeProtocol.exe(new GetTeacherListRequest(1,catgory), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				// TODO Auto-generated method stub
				GetTeacherListResponse tr = (GetTeacherListResponse) bhr;
				if (tr.list != null && tr.list.size() != 0) {
					 pageNum=2;
					quesList.clear();
					quesList.addAll(tr.list);
					
					handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(3);
					handler.sendEmptyMessage(4);
					if(tr.list.size()<20) isLast=true;
					else  isLast=false;
				} else {
					handler.sendEmptyMessage(3);
					handler.sendEmptyMessage(6);
				}
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(3);
				handler.sendEmptyMessage(6);
			}
		});
	}
	
	public void getFooterData() {
	
		if(isLast){
			handler.sendEmptyMessage(5);
			handler.sendEmptyMessage(7);
			return;
		}
		handler.sendEmptyMessage(2);
		ExeProtocol.exe(new GetTeacherListRequest(pageNum,catgory), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				// TODO Auto-generated method stub
				GetTeacherListResponse tr = (GetTeacherListResponse) bhr;
				if (tr.list != null && tr.list.size() != 0) {
					pageNum++;
					quesList.addAll(tr.list);
					handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(3);
					handler.sendEmptyMessage(5);
					if(tr.list.size()<20) isLast=true;
					else  isLast=false;
				} else {
					handler.sendEmptyMessage(1);
				}
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(1);
			}
		});
	}

	private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
			handler.sendEmptyMessage(2);
			getHeaderData();
			return null;
		}
	}
	
	private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
			getFooterData();
			return null;
		}
	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		new GetFooterDataTask().execute();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		Log.e("onHeaderRefresh", "onHeaderRefresh");
		refreshView.setLastUpdated(ExeRefreshTime
				.lastRefreshTime("NewPostListUpdateTime"));
		new GetHeaderDataTask().execute();
	}
	
}
