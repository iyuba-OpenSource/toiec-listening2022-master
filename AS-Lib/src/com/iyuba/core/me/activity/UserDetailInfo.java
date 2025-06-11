package com.iyuba.core.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.R;
/**
 * 详细信息
 * 
 * @author ct
 * @version 1.0
 * @para "currentuid" "currentname"
 */
public class UserDetailInfo extends BasisActivity {
	private TextView tvUserName, tvGender, tvResideLocation, tvBirthday,
			tvConstellation, tvZodiac, tvOccupation,tvEducation,tvGraduatesSchool, tvCompany,
			tvAffectivestatus, tvLookingfor, tvIntro, tvInterest;
	private ResponseUserDetailInfo userDetailInfo;
	private Button back;
	private String currentuid, currentname;
	private CustomDialog waitting;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userdetailinfo);
		mContext = this;
		 CrashApplication.addActivity(this);
		waitting = WaittingDialog.showDialog(mContext);
		Intent intent = getIntent();
		currentuid = intent.getStringExtra("currentuid");
		currentname = intent.getStringExtra("currentname");
		initWidget();
		handler.sendEmptyMessage(0);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				waitting.show();
				handler.sendEmptyMessage(1);
				break;
			case 1:
				ExeProtocol.exe(new RequestUserDetailInfo(currentuid),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								ResponseUserDetailInfo responseUserDetailInfo = (ResponseUserDetailInfo) bhr;
								if (responseUserDetailInfo.result.equals("211")) {
									userDetailInfo = responseUserDetailInfo;
								}
								handler.sendEmptyMessage(2);
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub

							}
						});
				break;
			case 2:
				waitting.dismiss();
				setText();
			default:
				break;
			}
		}
	};

	private void setText() {
		// TODO Auto-generated method stub
		tvUserName.setText(currentname);
		if (userDetailInfo.gender.equals("1")) {
			tvGender.setText("男");
		} else if (userDetailInfo.gender.equals("2")) {
			tvGender.setText("女");
		} else if (userDetailInfo.gender.equals("0")) {
			tvGender.setText("保密");
		}
		tvResideLocation.setText(userDetailInfo.resideLocation);
		tvBirthday.setText(userDetailInfo.birthday);
		tvConstellation.setText(userDetailInfo.constellation);
		tvZodiac.setText(userDetailInfo.zodiac);
		tvOccupation.setText(userDetailInfo.occupation);
		tvEducation.setText(userDetailInfo.education);
		tvGraduatesSchool.setText(userDetailInfo.graduateschool);
		tvCompany.setText(userDetailInfo.company);
		tvAffectivestatus.setText(userDetailInfo.affectivestatus);
		tvLookingfor.setText(userDetailInfo.lookingfor);
		tvIntro.setText(userDetailInfo.bio);
		tvInterest.setText(userDetailInfo.interest);
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvGender = (TextView) findViewById(R.id.tvGender);
		tvResideLocation = (TextView) findViewById(R.id.tvResideLocation);
		tvBirthday = (TextView) findViewById(R.id.tvBirthday);
		tvConstellation = (TextView) findViewById(R.id.tvConstellation);
		tvZodiac = (TextView) findViewById(R.id.tvZodiac);
		tvOccupation = (TextView) findViewById(R.id.tvOccupation);
		tvEducation = (TextView) findViewById(R.id.tvEducation);
		tvGraduatesSchool = (TextView) findViewById(R.id.tvGraduatesSchool);
		tvCompany = (TextView) findViewById(R.id.tvCompany);
		tvAffectivestatus = (TextView) findViewById(R.id.tvAffectivestatus);
		tvLookingfor = (TextView) findViewById(R.id.tvLookingfor);
		tvIntro = (TextView) findViewById(R.id.tvBio);
		tvInterest = (TextView) findViewById(R.id.tvInterest);
		back = (Button) findViewById(R.id.button_back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
}
