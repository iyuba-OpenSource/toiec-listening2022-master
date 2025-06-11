package com.iyuba.toeiclistening.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iyuba.core.me.activity.UpLoadImageActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.protocol.RequestPhoneNumRegister;
import com.iyuba.toeiclistening.protocol.ResponsePhoneNumRegister;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;

public class SetUserNameAndPsActivity extends Activity{
	private Button back,register;
	private EditText username,password;
	private String phoneNumString;
	private CustomDialog waitingDialog;
	private String errorInfo;
	private Context mContext;
	private EditText confirmEditText;
	private String userNameString;
	private String userPwdString;
	private String reUserPwdString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setnameandpsd);
				
		mContext=this;
		Intent intent=getIntent();
		phoneNumString=intent.getStringExtra("phonenum");

		
		waitingDialog = waitingDialog();
		initWidget();
	}
	private void initWidget() {
		// TODO Auto-generated method stub
		back=(Button)findViewById(R.id.button_back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		register=(Button)findViewById(R.id.regist);
		confirmEditText=(EditText)findViewById(R.id.editText_reUserPwd);
		username=(EditText)findViewById(R.id.re_username);
		password=(EditText)findViewById(R.id.set_password);
		register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(verification()){
				handler.sendEmptyMessage(3);
				ClientSession.Instace().asynGetResponse(
						new RequestPhoneNumRegister(username.getText().toString(),password.getText().toString(),phoneNumString
							),new IResponseReceiver() {
								
								@Override
								public void onResponse(BaseHttpResponse response, BaseHttpRequest request,
										int rspCookie) {
									// TODO Auto-generated method stub
									
									ResponsePhoneNumRegister res = (ResponsePhoneNumRegister) response;
									if (res.resultCode.equals("111")) {									
										//注册成功
										Intent intent = new Intent(mContext,UpLoadImageActivity.class);
										startActivity(intent);
										handler.sendEmptyMessage(0);
									} else  if(res.resultCode.equals("105")){
										errorInfo="密码格式错误，禁止使用全部数字";
										handler.sendEmptyMessage(1);
									}else  if(res.resultCode.equals("110")){
										errorInfo="注册失败,请重新注册";
										handler.sendEmptyMessage(1);
									}else  if(res.resultCode.equals("112")){
										errorInfo="用户名存在，请重新输入用户名";
										handler.sendEmptyMessage(1);
									}
									handler.sendEmptyMessage(4);
								}
							
			},null,null);
			
			}
			}
		});
	}
	@SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, R.string.tip_regist_success_text,
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(mContext,errorInfo,Toast.LENGTH_SHORT).show();
				break;
			case 3:
				waitingDialog.show();
				break;
			case 4:
				waitingDialog.dismiss();
				break;
			}
		}
	};
	public CustomDialog waitingDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.wetting, null);
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		CustomDialog cDialog = customBuilder.setContentView(layout).create();
		return cDialog;
	}
	/**
	 * 验证
	 */
	public boolean verification() {
		userNameString = username.getText().toString();
		userPwdString = password.getText().toString();
		reUserPwdString = confirmEditText.getText().toString();		
		if (userNameString.length() == 0) {
			username.setError(getResources().getString(
					R.string.regist_check_username_1));
			return false;
		}
		if (!checkUserId(userNameString)) {
			username.setError(getResources().getString(
					R.string.regist_check_username_1));
			return false;
		}

		if (userPwdString.length() == 0) {
			password.setError(getResources().getString(
					R.string.regist_check_userpwd_1));
			return false;
		}
		if (!checkUserPwd(userPwdString)) {
			password.setError(getResources().getString(
					R.string.regist_check_userpwd_1));
			return false;
		}
		if (!reUserPwdString.equals(userPwdString)) {
			confirmEditText.setError(getResources().getString(
					R.string.regist_check_reuserpwd));
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
}
