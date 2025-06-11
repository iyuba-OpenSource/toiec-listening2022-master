/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.discover.activity.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.discover.adapter.AnswerAdapter;
import com.iyuba.core.discover.adapter.BlankAdapter;
import com.iyuba.core.R;

/**
 * 四六级提交界面
 * 
 * @author chentong
 * @version 1.0
 * @para "type" 0选择 1 填空
 */
public class CetSubmit extends BasisActivity {
	private Context mContext;
	private Button backBtn;
	private ListView answer;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cet_submit);
		mContext = this;
		type = getIntent().getIntExtra("type", 0);
		 CrashApplication.addActivity(this);
		initWidget();
	}

	private void initWidget() {
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		answer = (ListView) findViewById(R.id.list);
		if (type == 0) {
			answer.setAdapter(new AnswerAdapter(mContext));
			answer.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, CetExplain.class);
					intent.putExtra("curPos",
							CetDataManager.Instace().answerList.get(arg2).id);
					startActivity(intent);
				}
			});
		} else {
			answer.setAdapter(new BlankAdapter(mContext));
		}
	}
}
