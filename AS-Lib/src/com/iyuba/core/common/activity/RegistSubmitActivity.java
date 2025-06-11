package com.iyuba.core.common.activity;

/**
 * 手机注册完善内容界面
 * 
 * @author czf
 * @version 1.0
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.iyuba.core.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestPhoneNumRegister;
import com.iyuba.core.common.protocol.message.ResponsePhoneNumRegister;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;

public class RegistSubmitActivity extends BasisActivity {
	private Context mContext;
	private EditText userNameEditText, passWordEditText;
	private Button submitButton, backButton;
	private String phonenumb, userName, passWord;
	private CustomDialog wettingDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		 CrashApplication.addActivity(this);
		setContentView(R.layout.regist_layout_phone_regist);
		backButton = (Button) findViewById(R.id.button_back);
		userNameEditText = (EditText) findViewById(R.id.regist_phone_username);
		passWordEditText = (EditText) findViewById(R.id.regist_phone_paswd);
		submitButton = (Button) findViewById(R.id.regist_phone_submit);
		phonenumb = getIntent().getExtras().getString("phoneNumb");
		wettingDialog = WaittingDialog.showDialog(mContext);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO 自动生成的方法存根
				if (verification()) {// 验证通过
					// 开始注册
					handler.sendEmptyMessage(0);// 在handler中注册
				}
			}

		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				finish();
			}
		});
	}

	/**
	 * 验证
	 */
	public boolean verification() {
		userName = userNameEditText.getText().toString();
		passWord = passWordEditText.getText().toString();
		if (!checkUserId(userName)) {
			userNameEditText.setError(mContext
					.getString(R.string.regist_check_username_1));
			return false;
		}
		if (!checkUserName(userName)) {
			userNameEditText.setError(mContext
					.getString(R.string.regist_check_username_2));
			return false;
		}
		if (!checkUserPwd(passWord)) {
			passWordEditText.setError(mContext
					.getString(R.string.regist_check_userpwd_1));
			return false;
		}
		return true;
	}

	/**
	 * 匹配用户名1
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserId(String userId) {
		if (userId.length() < 3 || userId.length() > 20)
			return false;
		return true;
	}

	/**
	 * 匹配用户名2 验证非手机号 邮箱号
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserName(String userId) {
		if (userId
				.matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
			return false;
		}
		if (userId.matches("^(1)\\d{10}$")) {
			return false;
		}

		return true;
	}

	/**
	 * 匹配密码
	 * 
	 * @param userPwd
	 * @return
	 */
	public boolean checkUserPwd(String userPwd) {
		if (userPwd.length() < 6 || userPwd.length() > 20)
			return false;
		return true;
	}
	private void regist() {
		ExeProtocol.exe(new RequestPhoneNumRegister(userName, passWord,
				phonenumb), new ProtocolResponse() {
			@Override
			public void finish(BaseHttpResponse bhr) {
				ResponsePhoneNumRegister rr = (ResponsePhoneNumRegister) bhr;
				if (rr.isRegSuccess) {
					AccountManagerLib.Instace(mContext)
							.saveUserNameAndPwd(userName, passWord);
					SettingConfig.Instance().setAutoLogin(true);
					handler.sendEmptyMessage(4);
					handler.sendEmptyMessage(6);
				} else if (rr.resultCode.equals("112")) {
					// 提示用户已存在
					handler.sendEmptyMessage(3);
				} else {
					handler.sendEmptyMessage(1);// 弹出错误提示
				}
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub

			}
		});
	}

	private void gotoLogin() {
		AccountManagerLib.Instace(mContext).login(userName, passWord,
				new OperateCallBack() {
					@Override
					public void success(String result) {
						// TODO Auto-generated method stub
						if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
							AccountManagerLib.Instace(mContext)
									.saveUserNameAndPwd(userName, passWord);
							SettingConfig.Instance().setAutoLogin(true);
						} else {
							AccountManagerLib.Instace(mContext)
									.saveUserNameAndPwd("", "");
						}
/*						Intent intent = new Intent(mContext, UpLoadImageActivity.class);
						intent.putExtra("regist", true);
						startActivity(intent);*/
						finish();
					}

					@Override
					public void fail(String message) {

					}
				});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				handler.sendEmptyMessage(5);
				regist();
				break;
			case 1:
				handler.sendEmptyMessage(4);
				CustomToast.showToast(mContext, R.string.regist_fail);
				break;
			case 2:
				CustomToast.showToast(mContext, R.string.regist_success);
				break;
			case 3:
				handler.sendEmptyMessage(4);
				CustomToast.showToast(mContext, R.string.regist_userid_exist);
				break;
			case 4:
				wettingDialog.dismiss();
				break;
			case 5:
				wettingDialog.show();
				break;
			case 6:
				gotoLogin();
				break;
			default:
				break;
			}
		}
	};
}
