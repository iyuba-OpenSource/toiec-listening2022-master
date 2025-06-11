package com.iyuba.core.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.WdDetailAdapter;
import com.iyuba.core.me.protocol.WordDetailRequest;
import com.iyuba.core.me.protocol.WordDetailResponse;
import com.iyuba.core.me.sqlite.mode.WordDetail;
import com.iyuba.core.R;

public class WordDetailActivity extends Activity {
	private Context mContext;
	private ListView WdDetailListView;
	private List<WordDetail> mList = new ArrayList<WordDetail>();
	WdDetailAdapter WdDetailAdapter;
	private CustomDialog waitDialog;
	private String mode = "1";
	private Button backBtn;
	private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

		@Override
		public void onStartConnect(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnected(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartSend(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSend(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSendFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartRecv(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetError(BaseHttpRequest request, int rspCookie,
				ErrorResponse errorInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intel_word_detail);
		mContext = this;
		waitDialog = WaittingDialog.showDialog(WordDetailActivity.this);
		Intent intent = getIntent();
		mode = intent.getStringExtra("testMode");
		WdDetailListView = (ListView) findViewById(R.id.detail_list);
		WdDetailAdapter = new WdDetailAdapter(mContext);
		WdDetailListView.setAdapter(WdDetailAdapter);
		backBtn = (Button) findViewById(R.id.button_back);
		waitDialog.show();
		new UpdateWdDetailThread().start();
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private class UpdateWdDetailThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String uid = ConfigManager.Instance().loadString("userId");

			ClientSession.Instace().asynGetResponse(
					new WordDetailRequest(uid,mode), new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {

							WordDetailResponse tr = (WordDetailResponse) response;

							if (tr != null && tr.result.equals("1")) {

								mList.clear();
								mList.addAll(tr.mList);
								binderAdapterDataHandler
										.post(binderAdapterDataRunnable);

							} else {

							}
						}
					}, null, mNetStateReceiver);

		}
	}
	
	
	private Handler binderAdapterDataHandler = new Handler();
	private Runnable binderAdapterDataRunnable = new Runnable() {
		public void run() {
				WdDetailAdapter.addList((ArrayList<WordDetail>) mList);
				WdDetailAdapter.notifyDataSetChanged();			
				waitDialog.dismiss();
				if(mList.size() == 0){
					Toast.makeText(mContext, "没有数据记录哦~~", 2000).show();
				}
		}
	};
	
	
	
}
