package com.iyuba.core.microclass.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.microclass.manager.VersionManager;
import com.iyuba.core.microclass.protocol.FeedBackJsonRequest;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 意见反馈Activity
 * 
 * @author chentong
 * 
 */

public class Feedback extends BasisActivity {
	private CustomDialog wettingDialog;
	private Button backBtn;
	private View submit;
	private EditText context, email;
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback);
		 CrashApplication.addActivity(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		wettingDialog = WaittingDialog.showDialog(this);
		context = (EditText) findViewById(R.id.editText_info);
		email = (EditText) findViewById(R.id.editText_Contact);
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		submit = findViewById(R.id.ImageView_submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!content.equals(context.getText().toString())) {
					content = context.getText().toString();
					wettingDialog.show();
					String content = makeContent();
					String uid = AccountManagerLib.Instace(Feedback.this).userId;
					if (verification()) {
						ExeProtocol.exe(new FeedBackJsonRequest(content, email
								.getText().toString(), uid),
								new ProtocolResponse() {

									@Override
									public void finish(BaseHttpResponse bhr) {
										// TODO Auto-generated method stub
										wettingDialog.dismiss();
										handler.sendEmptyMessage(0);
										onBackPressed();
									}

									@Override
									public void error() {
										// TODO Auto-generated method stub
										handler.sendEmptyMessage(1);
										wettingDialog.dismiss();
									}
								});
					}
				} else {
					handler.sendEmptyMessage(2);
				}
			}
		});
	}

	/**
	 * @return
	 * 
	 */
	private String makeContent() {
		// TODO Auto-generated method stub
		String content = context.getText().toString() + "  appversion:["
				+ VersionManager.VERSION_CODE + "]versionCode:["
				+ VersionManager.version + "]phone:[" + android.os.Build.BRAND
				+ android.os.Build.MODEL + android.os.Build.DEVICE + "]sdk:["
				+ android.os.Build.VERSION.SDK_INT + "]sysversion:["
				+ android.os.Build.VERSION.RELEASE + "]";
		return content;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				CustomToast.showToast(Feedback.this,
						R.string.feedback_submit_success);
				break;
			case 1:
				CustomToast.showToast(Feedback.this,
						R.string.feedback_network_error);
				break;
			case 2:
				CustomToast.showToast(Feedback.this,
						R.string.feedback_submitting);
				break;
			}
		}
	};

	/**
	 * 验证
	 */
	public boolean verification() {
		String contextString = context.getText().toString();
		String emailString = email.getText().toString();

		if (contextString.length() == 0) {
			context.setError(getResources().getString(
					R.string.feedback_info_null));
			return false;
		}

		if (emailString.length() != 0) {
			if (!emailCheck(emailString)) {
				email.setError(getResources().getString(
						R.string.feedback_effective_email));
				return false;
			}
		} else {
			if (!AccountManagerLib.Instace(Feedback.this).checkUserLogin()) {
				email.setError(getResources().getString(
						R.string.feedback_email_null));
				return false;
			}
		}

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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		content = new String();
	}
}
