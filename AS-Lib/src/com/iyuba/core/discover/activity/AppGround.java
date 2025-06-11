package com.iyuba.core.discover.activity;

/**
 * 应用广场界面 兼容新闻 考试
 * 
 * @author chentong
 * @version 1.0
 * @para "title" 传入String的id值 判断哪种类型
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.discover.activity.news.SimpleNews;
import com.iyuba.core.discover.activity.test.SimpleTest;

import java.util.ArrayList;
import java.util.HashMap;

public class AppGround extends BasisActivity {
	private Context mContext;
	private Button backButton;
	private GridView gridview;
	private TextView title;
	private String titleStringId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_ground);
//		CrashApplication.getInstance().addActivity(this);
		mContext = this;
		gridview = (GridView) findViewById(R.id.app_shelf);
		backButton = (Button) findViewById(R.id.button_back);
		title = (TextView) findViewById(R.id.title);
		titleStringId = this.getIntent().getStringExtra("title");
		title.setText(titleStringId);
		init();
	}

	/**
	 * 根据类别初始化 后续可添加新内容
	 */
	private void init() {
		// TODO Auto-generated method stub
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		int[] drawable = null;
		int[] text = null;
		int[] appId = null;
		if (titleStringId .equals("听说系列应用")) {
			text = new int[] { R.string.voa_speical, R.string.voa_bbc6,
					R.string.voa_video, R.string.voa_cs, R.string.voa_bbc,
					R.string.voa_ae, R.string.voa_word, R.string.voa_bbcnews,
					R.string.voa_music, R.string.voa_ted };
			// R.string.concept1, R.string.concept2
			drawable = new int[] { R.drawable.voa, R.drawable.bbc6,
					R.drawable.voavideo, R.drawable.csvoa, R.drawable.bbc,
					R.drawable.meiyu, R.drawable.voa_word, R.drawable.bbc_news,
					R.drawable.music, R.drawable.ted };
			appId = new int[] { 201, 215, 217, 212, 231, 213, 218, 221, 209,
					229 };
		} else if (titleStringId .equals("考试系列应用")) {
			text = new int[] { R.string.cet_4, R.string.cet_6, };// R.string.toeic,
			// R.string.toelf, R.string.jlpt1, R.string.jlpt2,
			// R.string.jlpt3
			drawable = new int[] { R.drawable.cet4, R.drawable.cet6, };
			appId = new int[] { 207, 208 };
		}
		HashMap<String, Object> map;
		int currentApp = Integer.parseInt(Constant.APPID);
		for (int i = 0; i < text.length; i++) {
			if (currentApp != appId[i]) {
				map = new HashMap<String, Object>();
				map.put("ItemImage", drawable[i]);// 添加图像资源的ID
				map.put("ItemText", mContext.getString(text[i]));// 按序号做ItemText
				lstImageItem.add(map);
			}
		}
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
				R.layout.item_app, new String[] { "ItemImage", "ItemText" },
				new int[] { R.id.ItemImage, R.id.ItemText });
		gridview.setAdapter(saImageItems);
		gridview.setOnItemClickListener(new ItemClickListener());
	}

	class ItemClickListener implements OnItemClickListener {
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			HashMap<String, Object> item;
			Intent intent;
			if (titleStringId .equals("听说系列应用")) {
				item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				intent = new Intent(mContext, SimpleNews.class);
				intent.putExtra("type", (String) item.get("ItemText"));
				startActivity(intent);
			} else {
				item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				intent = new Intent(mContext, SimpleTest.class);
				intent.putExtra("type", (String) item.get("ItemText"));
				startActivity(intent);
			}
		}
	}

}
