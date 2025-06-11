package com.iyuba.core.me.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestNewDoingsInfo;
import com.iyuba.core.common.protocol.message.ResponseNewDoingsInfo;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.NewDoingsListAdapter;
import com.iyuba.core.me.sqlite.mode.NewDoingsInfo;
import com.iyuba.core.R;

public class PersonalSignatureList extends BasisActivity implements OnScrollListener{

	private Context mContext;
	private Button backBtn,editBtn;
	private ListView lvPersonalSignature;
	
	private String currentuid;
	private int currDoingPage;
	private CustomDialog waiting;
	
	private ArrayList<NewDoingsInfo> doingsArrayList = new ArrayList<NewDoingsInfo>();
	private NewDoingsListAdapter doingsListAdapter;
	private Boolean isLastPage = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_signature_list);
		 CrashApplication.addActivity(this);
		mContext = this;
		
		currentuid = AccountManagerLib.Instace(mContext).userId;
		
		backBtn = (Button) findViewById(R.id.button_back);
		editBtn = (Button) findViewById(R.id.button_edit);
		lvPersonalSignature = (ListView) findViewById(R.id.lv_personal_signature_list);
		waiting = WaittingDialog.showDialog(mContext);
		
		doingsListAdapter = new NewDoingsListAdapter(mContext);
		
		doingsListAdapter.clearList();
		
		setClickListener();
		handler.sendEmptyMessage(0);
		setAdapter();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		handler.sendEmptyMessage(0);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void setClickListener() {
		// TODO Auto-generated method stub
		backBtn.setOnClickListener(ocl);
		editBtn.setOnClickListener(ocl);
		if (doingsListAdapter != null) {
			lvPersonalSignature.setAdapter(doingsListAdapter);
		}
	}
	
	private void setAdapter() {
		lvPersonalSignature.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			int id = v.getId();
			if (id == R.id.button_back) {
				onBackPressed();
			} else if(id == R.id.button_edit){
				intent = new Intent(mContext, WriteState.class);
				startActivity(intent);
			}
		}
	};
	
	private void showAlertDialog() {
		// TODO Auto-generated method stub
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(R.string.alert_title);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setMessage(mContext
				.getString(R.string.person_attention_relieve_alert));
		alert.setButton(AlertDialog.BUTTON_POSITIVE,
				mContext.getString(R.string.alert_btn_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessage(40);
					}
				});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE,
				mContext.getString(R.string.alert_btn_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
				});
		alert.show();
	}
	
	private Handler binderAdapterDataHandler = new Handler();
	private Runnable binderAdapterDataRunnable = new Runnable() {  
        public void run() {  
        	doingsListAdapter.clearList();
			doingsListAdapter.addList(doingsArrayList);
			doingsListAdapter.notifyDataSetChanged();
        }  
    }; 
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				currDoingPage = 1;
				ExeProtocol.exe(
						new RequestNewDoingsInfo(currentuid,"doing", currDoingPage),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								ResponseNewDoingsInfo responseDoingsInfo = (ResponseNewDoingsInfo) bhr;
								if (responseDoingsInfo.result.equals("391")) {
									if (responseDoingsInfo.counts.equals("0")) {
									} else {
										
										if (responseDoingsInfo.newDoingslist.size() > 0) {
											doingsArrayList.clear();
											doingsArrayList.addAll(responseDoingsInfo.newDoingslist);
											
											binderAdapterDataHandler.post(binderAdapterDataRunnable);
											
//											doingsListAdapter.clearList();
//											doingsListAdapter.addList(responseDoingsInfo.newDoingslist);
//											doingsListAdapter.notifyDataSetChanged();
											
										}
										
//										handler.sendEmptyMessage(10);
									}
								}else if(responseDoingsInfo.result.equals("392")){
									handler.sendEmptyMessage(12);
									handler.sendEmptyMessage(10);
								} else {
									handler.sendEmptyMessage(9);
								}
								currDoingPage += 1;
//								handler.sendEmptyMessage(10);
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub
								handler.sendEmptyMessage(4);
							}
						});
				break;
			case 1:
				ExeProtocol.exe(
						new RequestNewDoingsInfo(currentuid, "doing", currDoingPage),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								ResponseNewDoingsInfo responseDoingsInfo = (ResponseNewDoingsInfo) bhr;
								if (responseDoingsInfo.result.equals("391")) {
									if (responseDoingsInfo.counts.equals("0")) {
									} else {
										
										if (responseDoingsInfo.newDoingslist.size() > 0) {
											
											doingsArrayList.addAll(responseDoingsInfo.newDoingslist);
											binderAdapterDataHandler.post(binderAdapterDataRunnable);
//											doingsListAdapter.addList(responseDoingsInfo.newDoingslist);
//											doingsListAdapter.notifyDataSetChanged();
										}
//										handler.sendEmptyMessage(10);
									}
								}else if(responseDoingsInfo.result.equals("392")){
									handler.sendEmptyMessage(12);
									handler.sendEmptyMessage(10);
								} else {
									handler.sendEmptyMessage(9);
								}
								currDoingPage += 1;
//								handler.sendEmptyMessage(10);
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub
								handler.sendEmptyMessage(4);
							}
						});
				break;
			case 2:
				waiting.show();
				break;
			case 3:
				waiting.dismiss();
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.check_network);
				break;
			case 5:
				CustomToast.showToast(mContext,
						R.string.social_success_attention);
				break;
			case 6:
				CustomToast.showToast(mContext,
						R.string.social_failed_attention);
				break;
			case 7:
				CustomToast.showToast(mContext,
						R.string.social_success_cancle_attention);
				break;
			case 8:
				CustomToast.showToast(mContext,
						R.string.social_failed_cancle_attention);
				break;
			case 9:
				CustomToast.showToast(mContext, R.string.action_fail);
				break;
			case 10:
				doingsListAdapter.notifyDataSetChanged();
				break;
			case 12:
				CustomToast.showToast(mContext, R.string.action_no_more);
				break;
			default:
				break;
			}
		}

	};



	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				if (!isLastPage) {
					handler.sendEmptyMessage(1);
				}
			}
			break;
		}
	}
	
}
