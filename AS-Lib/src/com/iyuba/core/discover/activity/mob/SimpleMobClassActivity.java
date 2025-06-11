package com.iyuba.core.discover.activity.mob;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.mob.SimpleCourseContentRequest;
import com.iyuba.core.common.protocol.mob.SimpleCourseContentResponse;
import com.iyuba.core.common.protocol.mob.SimpleCourseMbTextRequest;
import com.iyuba.core.common.protocol.mob.SimpleCourseMbTextResponse;
import com.iyuba.core.common.protocol.mob.SimpleGetPayedCourseRequest;
import com.iyuba.core.common.protocol.mob.SimpleGetPayedCourseResponse;
import com.iyuba.core.common.protocol.mob.SimplePayCourseAmountRequest;
import com.iyuba.core.common.protocol.mob.SimplePayCourseAmountResponse;
import com.iyuba.core.common.sqlite.mode.mob.CourseContent;
import com.iyuba.core.common.sqlite.mode.mob.MbText;
import com.iyuba.core.common.sqlite.mode.mob.PayedCourseRecord;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.discover.adapter.SimpleMobClassContentAdapter;
import com.iyuba.core.R;

public class SimpleMobClassActivity extends BasisActivity {

	private Context mContext;
	private ProgressDialog wettingDialog;
	private Button backBtn;
	private Button buyPackBtn;

	private ListView mobclassContentListView;
	private ArrayList<CourseContent> courseContentList = new ArrayList<CourseContent>();
	private ArrayList<PayedCourseRecord> payedCourseRecord = new ArrayList<PayedCourseRecord>();
	private int PackId;
	private String appId;
	private double packPrice;
	private String MobClassBuyPackUrl;

	private String MobClassContentUrl;
	private String MobClassPayedRecordUrl;
	private SimpleMobClassContentAdapter simpleMobClassContentAdapter;

	private String UserAmount;
	private String UserBalanceAmount;
	private double CostedPrice = 0; // 购买课程已经花的钱
	private double curCourseCost;

	private ArrayList<MbText> mbTextList = new ArrayList<MbText>();
	private String MobClassMbTextUrl;

	public boolean modeDelete = false;
	private String ProductId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_mobclass_activity);

		mContext = this;
		wettingDialog = new ProgressDialog(mContext);

		PackId = MobManager.Instance().packid;
		packPrice = MobManager.Instance().curPackPrice;
		appId = MobManager.Instance().appId;
		MobClassContentUrl = "http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="
				+ PackId
				+ "&type=2&sign="
				+ MD5.getMD5ofStr("10102class" + PackId);

		mobclassContentListView = (ListView) findViewById(R.id.mobclass_content_title_list);
		backBtn = (Button) findViewById(R.id.mobclass_button_back);
		buyPackBtn = (Button) findViewById(R.id.mobclass_button_buypack);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		buyPackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

					handlerRequest.sendEmptyMessage(1);
					if (packPrice == CostedPrice) {
						Log.d("用户打包购买，购买价格已经等于包价格时，提示已经购买该课程。",
								";当前购买该包课程已经花的金额:" + CostedPrice + ";当前包的总价:"
										+ packPrice);
						handlerBuy.sendEmptyMessage(5);
					} else if (CostedPrice < packPrice) {
						curCourseCost = packPrice - CostedPrice;
						Log.d("用户打包购买，购买价格仍小于包价格时，按差价支付打包价格。",
								";当前购买该包课程已经花的金额:" + CostedPrice + ";当前包的总价:"
										+ packPrice);
						ProductId = "0";
						Message message = handlerBuy.obtainMessage(0);
						handlerBuy.sendMessage(message);
					} else {
						Log.d("用户打包购买，购买错误时。", ";当前购买该包课程已经花的金额:" + CostedPrice
								+ ";当前包的总价:" + packPrice);
						handlerBuy.sendEmptyMessage(7);
					}

				} else {
					Intent intent;
					intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
				}

			}
		});

		handlerRequest.sendEmptyMessage(0);

		mobclassContentListView.setAdapter(simpleMobClassContentAdapter);

		mobclassContentListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						if (courseContentList.get(position).IsFree == false
								&& courseContentList.get(position).cost != 0.0) {

							if (AccountManagerLib.Instace(mContext)
									.checkUserLogin()) {
								if (CostedPrice
										+ courseContentList.get(position).cost >= packPrice) {

									curCourseCost = packPrice - CostedPrice;

									if (curCourseCost + CostedPrice == packPrice) {
										ProductId = "0";
									} else {
										ProductId = courseContentList
												.get(position).id + "";
									}
								} else {
									curCourseCost = (int) courseContentList
											.get(position).cost;
									ProductId = courseContentList.get(position).id
											+ "";
								}

								Message message = handlerBuy.obtainMessage(0);
								handlerBuy.sendMessage(message);

							} else {
								Intent intent;
								intent = new Intent();
								intent.setClass(mContext, LoginActivity.class);
								mContext.startActivity(intent);
							}

						} else if (courseContentList.get(position).cost == 0.0
								|| courseContentList.get(position).IsFree == true) {

							Message msg = new Message();
							msg.what = 0;
							msg.obj = position;
							handlerPlayCourse.sendMessage(msg);

						}

					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		handlerRequest.sendEmptyMessage(0);
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	Handler handlerRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				handler.sendEmptyMessage(0);

				MobClassPayedRecordUrl = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetPayRecord.jsp?userId="
						+ AccountManagerLib.Instace(mContext).userId
						+ "&appId="
						+ appId
						+ "&packageId="
						+ PackId
						+ "&sign="
						+ MD5.getMD5ofStr(appId
								+ AccountManagerLib.Instace(mContext).userId
								+ PackId + "iyuba");
				ExeProtocol.exe(new SimpleGetPayedCourseRequest(
						MobClassPayedRecordUrl), new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						payedCourseRecord.clear();
						payedCourseRecord
								.addAll(((SimpleGetPayedCourseResponse) bhr).pcrList);

						CostedPrice = 0.0;
						// 设置当前课程的是否购买的标志,循环判断所有购买记录中是否包含当前课程对应的TitleId;判断是否是打包购买的
						for (int count = 0; count < payedCourseRecord.size(); count++) {
							CostedPrice += Double.parseDouble(payedCourseRecord
									.get(count).Amount);
						}

					}

					@Override
					public void error() {
						// TODO Auto-generated method stub

					}
				});
				handlerRequest.sendEmptyMessage(1);
				break;
			case 1:
				ExeProtocol.exe(new SimpleCourseContentRequest(
						MobClassContentUrl), new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						courseContentList.clear();
						courseContentList
								.addAll(((SimpleCourseContentResponse) bhr).courseContents);

						// 设置当前课程的是否购买的标志,循环判断所有购买记录中是否包含当前课程对应的TitleId;判断是否是打包购买的
						for (int count = 0; count < payedCourseRecord.size(); count++) {
							for (int index = 0; index < courseContentList
									.size(); index++) {
								if (payedCourseRecord.get(count).ProductId
										.equals(courseContentList.get(index).id
												+ "")
										|| payedCourseRecord.get(count).ProductId
												.equals("0")) {
									courseContentList.get(index).IsFree = true;

								}

							}
						}

						handler.sendEmptyMessage(1);
						handler.sendEmptyMessage(3);

					}

					@Override
					public void error() {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(1);
						handler.sendEmptyMessage(2);
					}
				});

				break;
			}
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				wettingDialog.show();
				break;
			case 1:
				wettingDialog.dismiss();
				break;
			case 2:
				Toast.makeText(mContext, R.string.check_network, 1000).show();
				break;
			case 3:
				if (simpleMobClassContentAdapter == null) {
					simpleMobClassContentAdapter = new SimpleMobClassContentAdapter(
							mContext, courseContentList);

					mobclassContentListView
							.setAdapter(simpleMobClassContentAdapter);
					Log.d("courseContentList size:", courseContentList.size()
							+ "");

					for (int i = 0; i < courseContentList.size(); i++) {
						Log.d("courseContentList " + i,
								courseContentList.get(i).titleName);
					}

				} else {
					simpleMobClassContentAdapter.notifyDataSetChanged();
				}

				break;
			}
		}
	};

	Handler handlerBuy = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case 0:
				UserAmount = AccountManagerLib.Instace(mContext).userInfo.iyubi;
				double amount = Double.parseDouble(UserAmount);
				if (amount >= curCourseCost && curCourseCost <= packPrice
						&& curCourseCost > 0) {
					handlerBuy.sendEmptyMessage(1);
				} else if (amount < curCourseCost) {
					handlerBuy.sendEmptyMessage(3);
				} else {
					handlerBuy.sendEmptyMessage(7);
				}
				break;
			case 1:
				Dialog dialog = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.alert_title)
						.setMessage(
								"您需要支付" + curCourseCost + "爱语币购买本节课程，确定要购买么")
						.setPositiveButton(R.string.alert_btn_buy,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										CustomToast.showToast(mContext,
												"您可以购买该课程");
										handlerBuy.sendEmptyMessage(2);
									}
								})
						.setNeutralButton(R.string.alert_btn_cancel, null)
						.create();
				dialog.show();// 如果要显示对话框，一定要加上这句
				break;
			case 2:
				MobClassBuyPackUrl = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/payClassApi.jsp?userId="
						+ AccountManagerLib.Instace(mContext).userId
						+ "&amount="
						+ (int) curCourseCost
						+ "&appId="
						+ appId
						+ "&productId="
						+ ProductId
						+ "&packageId="
						+ PackId
						+ "&sign="
						+ MD5.getMD5ofStr((int) curCourseCost + appId
								+ AccountManagerLib.Instace(mContext).userId
								+ PackId + ProductId + "iyuba");

				ExeProtocol.exe(new SimplePayCourseAmountRequest(
						MobClassBuyPackUrl), new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						SimplePayCourseAmountResponse response = (SimplePayCourseAmountResponse) bhr;
						if (response.result.equals("1")) {

							handlerBuy.sendEmptyMessage(6);

							handlerBuy.sendEmptyMessage(4);
						} else {
							handlerBuy.sendEmptyMessage(7);
						}

						UserBalanceAmount = response.amount;
					}

					@Override
					public void error() {
						// TODO Auto-generated method stub

					}
				});

				break;

			case 3:
				// wettingDialog.dismiss();
				Dialog dialog1 = new AlertDialog.Builder(mContext)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("提示")
						.setMessage("您的爱语币余额不足支付，是否充值？")
						.setPositiveButton("充值",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent intent = new Intent();
										intent.setClass(mContext, Web.class);
										intent.putExtra(
												"url",
												"http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
														+ AccountManagerLib
																.Instace(mContext).userId);
										intent.putExtra("title", "购买爱语币");
										mContext.startActivity(intent);
									}
								}).setNeutralButton("取消", null).create();
				dialog1.show();// 如果要显示对话框，一定要加上这句
				break;
			case 4:
				Toast.makeText(mContext, "支付成功，您现在的余额是：" + UserBalanceAmount,
						Toast.LENGTH_SHORT).show();
				break;
			case 5:
				Toast.makeText(mContext, "您已购买本课程", Toast.LENGTH_SHORT).show();
			case 6:
				handlerRequest.sendEmptyMessage(0);
				break;
			case 7:
				Toast.makeText(mContext, "购买错误，请稍后再试！", Toast.LENGTH_SHORT)
						.show();
			default:
				break;
			}
		}
	};

	private void getMbTextInfo(int pos) {

		MobClassMbTextUrl = "http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10003&id="
				+ courseContentList.get(pos).id
				+ "&sign="
				+ MD5.getMD5ofStr("10003class" + courseContentList.get(pos).id);

		ExeProtocol.exe(new SimpleCourseMbTextRequest(MobClassMbTextUrl),
				new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						mbTextList = ((SimpleCourseMbTextResponse) bhr).mbTextList;
					}

					@Override
					public void error() {
						// TODO Auto-generated method stub

					}
				});

	}

	Handler handlerPlayCourse = new Handler() {

		int pos;
		String titleid;
		String title;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				getMbTextInfo(Integer.parseInt(msg.obj.toString()));
				pos = Integer.parseInt(msg.obj.toString());
				handlerPlayCourse.sendEmptyMessageDelayed(1, 1200);
				break;
			case 1:
				titleid = String.valueOf(courseContentList.get(pos).id);
				title = courseContentList.get(pos).titleName;

				Intent intent = new Intent();
				intent.setClass(mContext, SimpleMobClassBase.class);
				MobManager.Instance().mbList = mbTextList;
				intent.putExtra("titleid", titleid);
				intent.putExtra("title", title);
				startActivity(intent);
				break;
			case 2:

				break;
			}
		}
	};

}
