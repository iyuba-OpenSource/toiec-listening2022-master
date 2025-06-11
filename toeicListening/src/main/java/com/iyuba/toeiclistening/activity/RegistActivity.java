package com.iyuba.toeiclistening.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;
import com.iyuba.toeiclistening.protocol.RegistRequest;
import com.iyuba.toeiclistening.protocol.RegistResponse;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;

public class RegistActivity extends Activity{
	private View backView;
	private Context mContext;
	private Button backBtn;
	private EditText userName, userPwd, reUserPwd, email;
	private Button regBtn;
	private String registError = "邮箱已注册";
	private String userNameString;
	private String userPwdString;
	private String reUserPwdString;
	private String emailString;
	private boolean send = false;
	private CustomDialog wettingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mContext = this;
		setContentView(R.layout.regist_layout);
		backView=findViewById(R.id.backlayout);
		//backView.setBackgroundColor(Constant.backgroundColor);
		wettingDialog = new WaittingDialog().wettingDialog(mContext);
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		userName = (EditText) findViewById(R.id.editText_userId);
		userPwd = (EditText) findViewById(R.id.editText_userPwd);
		reUserPwd = (EditText) findViewById(R.id.editText_reUserPwd);
		email = (EditText) findViewById(R.id.editText_email);
		regBtn = (Button) findViewById(R.id.button_regist);
		regBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (verification()) { // 验证通过
					// 开始注册
					if (!send) {
						send = true;
						wettingDialog.show();
						regist();
					} else {
						CustomToast.showToast(mContext, "操作中，请等候", 1000);
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (wettingDialog.isShowing()){
			wettingDialog.dismiss();
		}
	}

	/**
	 * 验证
	 */
	public boolean verification() {
		userNameString = userName.getText().toString();
		userPwdString = userPwd.getText().toString();
		reUserPwdString = reUserPwd.getText().toString();
		emailString = email.getText().toString();
		if (userNameString.length() == 0) {
			userName.setError(getResources().getString(
					R.string.regist_check_username_1));
			return false;
		}
		if (!checkUserId(userNameString)) {
			userName.setError(getResources().getString(
					R.string.regist_check_username_1));
			return false;
		}

		if (userPwdString.length() == 0) {
			userPwd.setError(getResources().getString(
					R.string.regist_check_userpwd_1));
			return false;
		}
		if (!checkUserPwd(userPwdString)) {
			userPwd.setError(getResources().getString(
					R.string.regist_check_userpwd_1));
			return false;
		}
		if (!reUserPwdString.equals(userPwdString)) {
			reUserPwd.setError(getResources().getString(
					R.string.regist_check_reuserpwd));
			return false;
		}
		if (emailString.length() == 0) {
			email.setError(getResources().getString(
					R.string.regist_check_email_1));
			return false;
		}
		if (!emailCheck(emailString)) {
			email.setError(getResources().getString(
					R.string.regist_check_email_2));
			return false;
		}
		return true;
	}

	/**
	 * 匹配用户名
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserId(String userId) {
		if (userId.length() < 4 || userId.length() > 10)
			return false;
		return true;
	}

	/**
	 * 匹配密码
	 * 
	 * @param userPwd
	 * @return
	 */
	public boolean checkUserPwd(String userPwd) {
		if (userPwd.length() < 6 || userPwd.length() > 12)
			return false;
		return true;
	}

	/**
	 * email格式匹配
	 * 
	 * @param email
	 * @return
	 */
	public boolean emailCheck(String email) {
		String check = "^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		return matcher.matches();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;
			case 1: // 弹出错误信息
				CustomToast.showToast(mContext, registError, 1000);
				break;
			case 2:
				CustomToast.showToast(mContext,
						R.string.net_disconected, 1000);
				break;
			case 3:
				CustomToast.showToast(mContext, "账号已存在", 1000);
			}
		}
	};

	private void regist() {
		ClientSession.Instace().asynGetResponse(
				new RegistRequest(userName.getText().toString(), userPwd
						.getText().toString(), "", email.getText().toString()),
				new IResponseReceiver() {

					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
						RegistResponse rr = (RegistResponse) response;
						send=false;
						if (wettingDialog.isShowing())
						wettingDialog.dismiss();
						if (rr.result.equals("111")) {
							Intent intent = new Intent(mContext,
									LoginActivity.class);
							intent.putExtra("userName", userNameString);
							intent.putExtra("userPwd", userPwdString);
							startActivity(intent);
							finish();
						} else if (rr.result.equals("112")) {
							handler.sendEmptyMessage(3);
						} else {
							// 注册失败
							handler.sendEmptyMessage(1);
						}
					}
				}, null, new INetStateReceiver() {

					@Override
					public void onStartConnect(BaseHttpRequest request,
							int rspCookie) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onConnected(BaseHttpRequest request,
							int rspCookie) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartSend(BaseHttpRequest request,
							int rspCookie, int totalLen) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSend(BaseHttpRequest request, int rspCookie,
							int len) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSendFinish(BaseHttpRequest request,
							int rspCookie) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartRecv(BaseHttpRequest request,
							int rspCookie, int totalLen) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRecv(BaseHttpRequest request, int rspCookie,
							int len) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onRecvFinish(BaseHttpRequest request,
							int rspCookie) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onNetError(BaseHttpRequest request,
							int rspCookie, ErrorResponse errorInfo) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(2);
					}

					@Override
					public void onCancel(BaseHttpRequest request, int rspCookie) {
						// TODO Auto-generated method stub

					}
				});
	}
}
