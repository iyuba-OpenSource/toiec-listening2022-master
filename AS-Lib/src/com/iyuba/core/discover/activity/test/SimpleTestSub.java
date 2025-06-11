package com.iyuba.core.discover.activity.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.test.CetRequest;
import com.iyuba.core.common.protocol.test.CetResponse;
import com.iyuba.core.common.protocol.test.CetSectionCRequest;
import com.iyuba.core.common.protocol.test.CetSectionCResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.discover.adapter.SimpleTestSubAdapter;
import com.iyuba.core.R;

/**
 * 四六级次界面
 * 
 * @author chentong
 * @version 1.0
 * @para "testUrl" 内容网址 "type" 四级 六级 "item" 标题
 */
public class SimpleTestSub extends BasisActivity {
	private Context mContext;
	private SimpleTestSubAdapter simpleTestSubAdapter;
	private Button backBtn;
	private ListView newsList;
	private TextView title;
	private String curTestType;
	private String getTestUrl;
	private String testMain, testMd5;
	private int type;
	private CustomDialog waitting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_news_sub);
		mContext = this;
		 CrashApplication.addActivity(this);
		waitting = WaittingDialog.showDialog(mContext);
		getTestUrl = getIntent().getStringExtra("testUrl");
		curTestType = getIntent().getStringExtra("item");
		type = getIntent().getIntExtra("type", 0);
		initData();
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		simpleTestSubAdapter = new SimpleTestSubAdapter(mContext);
		newsList = (ListView) findViewById(R.id.listview);
		newsList.setAdapter(simpleTestSubAdapter);
		newsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				getDataFromUrl(arg2);
			}
		});
		title = (TextView) findViewById(R.id.title);
		title.setText(curTestType);
	}

	/**
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub
		if (type == 0) {
			String[] tempString = curTestType.split("_");
			if (tempString.length == 2) {
				curTestType = tempString[0] + "年" + tempString[1] + "月";
				testMain = tempString[0] + tempString[1];
			} else {
				curTestType = tempString[0] + "年" + tempString[1] + "月第"
						+ tempString[2] + "套题";
				testMain = tempString[0] + tempString[1] + tempString[2];
			}
			testMd5 = "4" + testMain + "iyuba";
		} else if (type == 1) {
			String[] tempString = curTestType.split("_");
			if (tempString.length == 2) {
				curTestType = tempString[0] + "年" + tempString[1] + "月";
				testMain = tempString[0] + tempString[1];
			} else {
				curTestType = tempString[0] + "年" + tempString[1] + "月第"
						+ tempString[2] + "套题";
				testMain = tempString[0] + tempString[1] + tempString[2];
			}
			testMd5 = "6" + testMain + "iyuba";
		} else if (type == 2) {
			getTestUrl = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/voa/titleApi.jsp?maxid=0&pageNum=20&pages=1&category=csvoa&type=json";
			type = 2;
		} else if (type == 3) {
			getTestUrl = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/minutes/titleApi.jsp?type=android&format=json&pages=1&parentID=2&pageNum=20&maxid=0";
			type = 3;
		} else if (type == 4) {
			getTestUrl = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/minutes/titleApi.jsp?type=android&format=json&pages=1&parentID=1&pageNum=20&maxid=0";
			type = 4;
		} else if (type == 5) {
			getTestUrl = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/voa/titleApi.jsp?maxid=0&pageNum=20&pages=1&type=json&parentID=200";

			type = 5;
		} else if (type == 6) {
			getTestUrl = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/minutes/titleApi.jsp?type=android&format=json&pages=1&parentID=3&pageNum=20&maxid=0";
		}
	}

	private void getDataFromUrl(final int pos) {
		int grade = 4;
		handler.sendEmptyMessage(0);
		if (type == 0) {
			grade = 4;
		} else if (type == 1) {
			grade = 6;
		}
		if (pos != 2) {
			ExeProtocol.exe(
					new CetRequest(getTestUrl + testMain + "&testType="
							+ (pos + 1) + "&code="
							+ MD5.getMD5ofStr((pos + 1) + testMd5), grade),
					new ProtocolResponse() {

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
							CetResponse cetResponse = (CetResponse) bhr;
							CetDataManager.Instace().answerList = cetResponse.answerList;
							CetDataManager.Instace().explainList = cetResponse.explainList;
							CetDataManager.Instace().textList = cetResponse.textList;
							Intent intent = new Intent(mContext, CET.class);
							intent.putExtra("title",
									simpleTestSubAdapter.getItem(pos));
							startActivity(intent);
						}

						@Override
						public void error() {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(2);
						}
					});
		} else {
			ExeProtocol.exe(
					new CetSectionCRequest(getTestUrl + testMain + "&testType="
							+ (pos + 1) + "&code="
							+ MD5.getMD5ofStr((pos + 1) + testMd5), grade),
					new ProtocolResponse() {

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
							CetSectionCResponse cetResponse = (CetSectionCResponse) bhr;
							CetDataManager.Instace().blankList = cetResponse.answerList;
							CetDataManager.Instace().textList = cetResponse.textList;
							Intent intent = new Intent(mContext, CetBlank.class);
							intent.putExtra("title",
									simpleTestSubAdapter.getItem(pos));
							startActivity(intent);
						}

						@Override
						public void error() {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(2);
						}
					});
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				waitting.show();
				break;
			case 1:
				waitting.dismiss();
				break;
			case 2:
				CustomToast.showToast(mContext, R.string.check_network);
				break;
			}
		}
	};

}
