package com.iyuba.core.me.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.PayManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.MyGridView;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.MyGridAdapter;
import com.iyuba.core.microclass.protocol.CheckAmountRequest;
import com.iyuba.core.microclass.protocol.CheckAmountResponse;
import com.iyuba.core.R;

public class NewVipCenterActivity extends BasisActivity {
	private Context mContext;
	private TextView tv_username;
	private TextView tv_iyucoin;
	private RelativeLayout rl_buyforevervip;
	private MyGridView gv_tequan;
	private String username;
	private String userid;
	private String iyubi;
	private CustomDialog wettingDialog;
	private Button quarter;
	private Button month;
	private Button half_year;
	private Button year;
	private RelativeLayout lifelong;
	private Button iv_back;
	private UserInfo userInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_vip);
		 CrashApplication.addActivity(this);
		mContext = this;
		Bundle extra = getIntent().getExtras();
		username = extra.getString("username");
		userid = extra.getString("userid");
		iyubi = extra.getString("iyubi");
		initView();
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getIyubi();
	}



	private void initView() {
		wettingDialog = WaittingDialog.showDialog(mContext);
		iv_back = (Button) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NewVipCenterActivity.this.finish();
			}
		});
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_username.setText(username);
		tv_iyucoin = (TextView) findViewById(R.id.tv_iyucoin);
		tv_iyucoin.setText(iyubi);
		btn_buyiyuba = (Button) findViewById(R.id.btn_buyiyuba);
		btn_buyiyuba.setOnClickListener(ocl);
		month = (Button) findViewById(R.id.btn_buyapp1);
		quarter = (Button) findViewById(R.id.btn_buyapp2);
		half_year = (Button) findViewById(R.id.btn_buyapp3);
		year = (Button) findViewById(R.id.btn_buyapp4);
		gv_tequan = (MyGridView) findViewById(R.id.gv_tequan);
		lifelong = (RelativeLayout) findViewById(R.id.rl_buyforevervip);
		month.setOnClickListener(ocl);
		quarter.setOnClickListener(ocl);
		half_year.setOnClickListener(ocl);
		year.setOnClickListener(ocl);
		lifelong.setOnClickListener(ocl);
		gv_tequan.setAdapter(new MyGridAdapter(mContext));
	}

	private void buyVip(int month) {
		handler.sendEmptyMessage(3);
		if (month != 0) {
			PayManager.Instance(mContext).payAmount(
					AccountManagerLib.Instace(mContext).userId, getSpend(month),
					month, new OperateCallBack() {

						@Override
						public void success(String message) {
							// TODO Auto-generated method stub
							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
							handler.sendMessage(hmsg);
							ConfigManager.Instance().putInt("isvip", 1);
						}

						@Override
						public void fail(String message) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
						}
					});
		} else {
			PayManager.Instance(mContext).payAmount(
					AccountManagerLib.Instace(mContext).userId, Constant.price,
					new OperateCallBack() {

						@Override
						public void success(String message) {
							// TODO Auto-generated method stub
							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
							handler.sendMessage(hmsg);
							ConfigManager.Instance().putInt("isvip",1);
						}

						@Override
						public void fail(String message) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
						}
					});
		}
	}

	public int getSpend(int month) {
		int result = 0;
		switch (month) {
		case 1:
			result = 200;
			break;
		case 3:
			result = 600;
			break;
		case 6:
			result = 1000;
			break;
		case 12:
			result = 2000;
			break;
		}
		return result;
	}
	
	private void getIyubi() {
		userInfo = AccountManagerLib.Instace(mContext).userInfo;
		ExeProtocol.exe(
				new CheckAmountRequest(
						AccountManagerLib.Instace(mContext).userId),
				new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						Looper.prepare();
						CheckAmountResponse res = (CheckAmountResponse) bhr;
						userInfo.iyubi = res.amount;
						handler.sendEmptyMessage(5);
						Looper.loop();
						//Test
					}

					@Override
					public void error() {
						// TODO Auto-generated method stub
						Log.d("CheckAmountResponse", "Response error");
					}
				});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			Dialog dialog;
			switch (msg.what) {
			case 0:
				final int month = msg.arg1;
				String content1 = mContext
						.getString(R.string.alert_buy_content1);
				String content2 = mContext
						.getString(R.string.alert_buy_content2);
				StringBuffer sb = new StringBuffer();
				if (month == 0) {
					sb.append(content1).append(Constant.price).append(content2);
				} else {
					sb.append(content1).append(getSpend(month))
							.append(content2);
				}
				dialog = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.alert_title)
						.setMessage(sb.toString())
						.setPositiveButton(R.string.alert_btn_buy,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										buyVip(month);
									}
								})
						.setNeutralButton(R.string.alert_btn_cancel, null)
						.create();
				dialog.show();// 如果要显示对话框，一定要加上这句
				break;
			case 1:
				wettingDialog.dismiss();
				dialog = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.alert_title)
						.setMessage(R.string.alert_recharge_content)
						.setPositiveButton(R.string.alert_btn_recharge,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										buyIyubi();
									}
								})
						.setNeutralButton(R.string.alert_btn_cancel, null)
						.create();
				dialog.show();// 如果要显示对话框，一定要加上这句
				break;
			case 2:
				wettingDialog.dismiss();
				CustomToast.showToast(mContext, R.string.buy_success);
				tv_iyucoin.setText(msg.obj.toString());
				break;
			case 3:
				wettingDialog.show();
				break;
			case 4:
				wettingDialog.dismiss();
				dialog = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.alert_title)
						.setMessage(R.string.alert_all_life_vip)
						.setPositiveButton(R.string.alert_btn_recharge,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										buyIyubi();
									}
								})
						.setNeutralButton(R.string.alert_btn_cancel, null)
						.create();
				dialog.show();// 如果要显示对话框，一定要加上这句
				break;
			case 5:
				if(userInfo != null){
					tv_iyucoin.setText(userInfo.iyubi);
				}
				break;
			default:
				break;
			}
		}
	};
	private OnClickListener ocl = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*
			 * if (AccountManagerLib.Instace(mContext).userInfo.deadline
			 * .equals("终身VIP")) { handler.sendEmptyMessage(4); } else
			 */if (v == month) {
				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 200) {
					handler.obtainMessage(0, 1, 0).sendToTarget();
				} else {
					handler.sendEmptyMessage(1);
				}

			} else if (v == quarter) {
				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 600) {
					handler.obtainMessage(0, 3, 0).sendToTarget();
				} else {
					handler.sendEmptyMessage(1);
				}
			} else if (v == half_year) {
				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 1000) {
					handler.obtainMessage(0, 6, 0).sendToTarget();
				} else {
					handler.sendEmptyMessage(1);
				}
			} else if (v == year) {
				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 2000) {
					handler.obtainMessage(0, 12, 0).sendToTarget();
				} else {
					handler.sendEmptyMessage(1);
				}
			} else if (v == lifelong) {
				int amount = Constant.price;
				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= amount) {
					handler.obtainMessage(0, 0, 0).sendToTarget();
				} else {
					handler.sendEmptyMessage(1);
				}
			} else if (v == btn_buyiyuba) {
				buyIyubi();
			}
		}
	};
	private Button btn_buyiyuba;
	private void buyIyubi() {
		Intent intent = new Intent();
		intent.setClass(mContext, Web.class);
		intent.putExtra("url", "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
				+ AccountManagerLib.Instace(mContext).userId + "&appid="
				+ Constant.APPID);
		intent.putExtra("title", Constant.APP_NAME);
		startActivity(intent);
	}
}
