package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.listener.OnResultListener;
//import com.iyuba.toeiclistening.manager.AccountManagerLib;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.manager.PayManager;
import com.iyuba.toeiclistening.setting.SettingConfig;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;
import com.iyuba.toeiclistening.R;

public class BuyVipActivity extends Activity{
	private Context mContext;
	private Button buyVip;
	private Button back;
	private Button buyIyubi;
	private TextView nameTextView,amounTextView;
	private int iyuba_amount=0;//爱语吧账户的金钱
	private CustomDialog wettingDialog;
	private RelativeLayout rlLogin,reNoneLogin;
	private Button goLogin;//跳转到登录界面
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Log.d("test buy VIP!!!!!!", "跳转到BuyVipActivity");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buy_vip);
		mContext=this;
		rlLogin=(RelativeLayout)findViewById(R.id.rl);
		reNoneLogin=(RelativeLayout)findViewById(R.id.relativeLayout_noLogin);
		goLogin=(Button)findViewById(R.id.button_to_login);
		nameTextView=(TextView)findViewById(R.id.buy_username);
		amounTextView=(TextView)findViewById(R.id.buy_account);
		buyIyubi=(Button)findViewById(R.id.buy_iyubi);
		
		goLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
			}
		});	
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(AccountManagerLib.Instace(mContext).userName==null){//AccountManagerMain
			rlLogin.setVisibility(View.GONE);
			reNoneLogin.setVisibility(View.VISIBLE);
		}else{
			rlLogin.setVisibility(View.VISIBLE);
			reNoneLogin.setVisibility(View.GONE);
			ini();
		}
		
	}


	
	
	public void ini(){
		mContext=this;
		String string= ConfigManagerMain.Instance().loadString(AccountManagerLib.IYUBAAMOUNT);
		iyuba_amount=Integer.parseInt(string);
		buyVip=(Button)findViewById(R.id.buy_vip_button_buy);
		back=(Button)findViewById(R.id.button_back);
		nameTextView.setText(AccountManagerLib.Instace(mContext).userName);
		amounTextView.setText(iyuba_amount+"");
		wettingDialog = new WaittingDialog().wettingDialog(mContext);
		back.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		buyVip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AccountManagerLib.Instace(mContext).checkUserLogin()){
					if(SettingConfig.Instance().isVip()==1){
						CustomToast.showToast(mContext, "您已经是VIP", 1000);
					}else{
						Message message=handler.obtainMessage(0);
						handler.sendMessage(message);
					}
				}else{
					Intent intent=new Intent();
					intent.setClass(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		buyIyubi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext,
						AllAppActivity.class);
				intent.putExtra(
						"url",
						"http://app."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
								+ AccountManagerLib
										.Instace(mContext).userId+"&appid="+Constant.APPID);
				intent.putExtra("title", "购买爱语币");
				startActivity(intent);
			}
		});
	}
		/*
		 * 
		 * app重装以后，恢复vip
		 */
		/*
		recoveryApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wettingDialog.show();
				if(SettingConfig.Instance().isVip()==0){
					
						
						PayManager.Instance(mContext).recoverAppVip(AccountManagerLib.Instace(mContext).userId, "0",
						new OnResultListener() {
							
							@Override
							public void OnSuccessListener(String msg) {
								// TODO Auto-generated method stub
								SettingConfig.Instance().setVip(1);
								CustomToast.showToast(mContext, "您已成功恢复VIP", 1000);
								wettingDialog.dismiss();
							}
							
							@Override
							public void OnFailureListener(String msg) {
								// TODO Auto-generated method stub
								CustomToast.showToast(mContext, "您还没有购买VIP", 1000);
								wettingDialog.dismiss();
							}
						}
					
						);
					
			}else{
				handler.sendEmptyMessage(3);
			}
			}
		});
	}
	/**
	 * 购买vip
	 * 
	 */
	public void buyVip(){
		wettingDialog.show();
		PayManager.Instance(mContext).payAmount(
				AccountManagerLib.Instace(mContext).userId, Constant.PAYAMOUNT,
				new OnResultListener() {

					@Override
					public void OnSuccessListener(String msg) {
						Message hmsg = handler.obtainMessage(2, msg);// 对话框提示支付成功
						handler.sendMessage(hmsg);
						SettingConfig.Instance().setVip(1);
						SettingConfig.Instance().setIyubaAmount(msg);
						amounTextView.setText(msg);
					}
					@Override
					public void OnFailureListener(String msg) {//余额不足				
						handler.sendEmptyMessage(1);
						SettingConfig.Instance().setVip(0);
					}
				});
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Dialog dialog = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("提示")
						.setMessage("您需要支付100爱语币获得托福听力VIP，确定要购买么")
						.setPositiveButton("购买",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										
										
										if(Integer.parseInt(SettingConfig.Instance().getIyubaAmount())<100){
											handler.sendEmptyMessage(1);
										}else{
											buyVip();
										}
									}
								}).setNeutralButton("取消", null).create();
				dialog.show();// 如果要显示对话框，一定要加上这句
				break;
			case 1:
				wettingDialog.dismiss();
				Dialog dialog1 = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("提示")
						.setMessage("您的爱语币余额不足支付，是否充值？")
						.setPositiveButton("充值",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent intent = new Intent();
										intent.setClass(mContext,
												AllAppActivity.class);
										intent.putExtra(
												"url",
												"http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
														+ AccountManagerLib
																.Instace(mContext).userId);
										intent.putExtra("title", "购买爱语币");
										startActivity(intent);
									}
								}).setNeutralButton("取消", null).create();
				dialog1.show();// 如果要显示对话框，一定要加上这句
				break;
			case 2:
				wettingDialog.dismiss();
				CustomToast.showToast(mContext,
						"支付成功，您现在的余额是：" + msg.obj.toString(), 1000);
				ConfigManagerMain.Instance().putString(AccountManagerLib.IYUBAAMOUNT,
						msg.obj.toString());
				break;
			case 3:
				wettingDialog.dismiss();
				CustomToast.showToast(mContext, "您已经是VIP", 1000);
			default:
				break;
			}
		}
	};
}
