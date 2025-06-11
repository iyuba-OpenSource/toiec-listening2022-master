package com.iyuba.core.discover.activity.mob;

/**
 * 移动课堂界面
 * 
 * @author rzy
 * @version 1.0
 */
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.mob.SimpleCoursePackRequest;
import com.iyuba.core.common.protocol.mob.SimpleCoursePackResponse;
import com.iyuba.core.common.sqlite.mode.mob.CoursePack;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.discover.adapter.SimpleMobClassListAdapter;
import com.iyuba.core.R;

public class SimpleMobClassList extends BasisActivity {
	private Context mContext;
	private SimpleMobClassListAdapter simpleMobClassListAdapter;
	private ArrayList<CoursePack> coursePackArrayList = new ArrayList<CoursePack>();
	private Button backBtn;
	private ListView mobClassListView;
	private CustomDialog wettingDialog;
	private int curPackId;
	private double curPackPrice;
	private String getAllMobPacksUrl = "http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10004&sign=7dfa531f79338a7c565fe03e516bea5c";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_mobclass_list);
		mContext = this;
		wettingDialog = WaittingDialog.showDialog(mContext);
		handler.sendEmptyMessage(0);
		mobClassListView = (ListView) findViewById(R.id.mobclass_listview);
		backBtn = (Button) findViewById(R.id.mobclass_button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		simpleMobClassListAdapter = new SimpleMobClassListAdapter(mContext,
				coursePackArrayList);
		mobClassListView.setAdapter(simpleMobClassListAdapter);
		mobClassListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
					curPackId = coursePackArrayList.get(position).id;
					curPackPrice = coursePackArrayList.get(position).price;

					MobManager.Instance().packid = curPackId;
					MobManager.Instance().ownerid = coursePackArrayList
							.get(position).ownerid;
					MobManager.Instance().appId = getAppId(MobManager.Instance().ownerid);
					MobManager.Instance().desc = coursePackArrayList
							.get(position).desc;
					MobManager.Instance().curPackPrice = curPackPrice;
					Intent intent = new Intent();
					intent.setClass(mContext, SimpleMobClassActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}

		});
		handler.sendEmptyMessage(4);
	}

	public String getAppId(int ownerid) {
		// TODO Auto-generated method stub
		String appId = "";
		if (ownerid == 1) {
			appId = "205";
		} else if (ownerid == 5) {
			appId = "206";
		} else if (ownerid == 6) {
			appId = "203";
		} else if (ownerid == 28) {
			appId = "28";
		}
		return appId;
	}

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
				CustomToast.showToast(mContext, R.string.check_network);
				break;
			case 3:
				if (simpleMobClassListAdapter == null) {
					simpleMobClassListAdapter = new SimpleMobClassListAdapter(
							mContext, coursePackArrayList);
					mobClassListView.setAdapter(simpleMobClassListAdapter);
				} else {
					simpleMobClassListAdapter.setList(coursePackArrayList);
					simpleMobClassListAdapter.notifyDataSetChanged();
				}
				break;
			case 4:
				ExeProtocol.exe(new SimpleCoursePackRequest(getAllMobPacksUrl),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								coursePackArrayList = ((SimpleCoursePackResponse) bhr).coursePacks;
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
}
