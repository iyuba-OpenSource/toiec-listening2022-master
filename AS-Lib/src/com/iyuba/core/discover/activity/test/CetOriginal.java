package com.iyuba.core.discover.activity.test;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.WordCard;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.subtitle.TextPage;
import com.iyuba.core.common.widget.subtitle.TextPageSelectTextCallBack;
import com.iyuba.core.R;

/**
 * 四六级原文界面
 * 
 * @author chentong
 * @version 1.0
 * @para "curPos" 当前题号
 */
public class CetOriginal extends BasisActivity {
	private Context mContext;
	private Button backBtn;
	private TextView number;
	private TextPage content;
	private Button sound;
	private Player mPlayer;
	private int pos;
	private WordCard card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cet_original);
		mContext = this;
		 CrashApplication.addActivity(this);
		pos = Integer.parseInt(getIntent().getStringExtra("curPos"));
		mPlayer = new Player(mContext, null);
		initWidget();
	}

	private void initWidget() {
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sound = (Button) findViewById(R.id.qsound);
		sound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPlayer.playUrl(CetDataManager.Instace().answerList.get(pos).sound);
			}
		});
		card = (WordCard) findViewById(R.id.word);
		card.setVisibility(View.GONE);
		number = (TextView) findViewById(R.id.question);
		number.setText(pos + ":");
		content = (TextPage) findViewById(R.id.original);
		content.setTextpageSelectTextCallBack(new TextPageSelectTextCallBack() {

			@Override
			public void selectTextEvent(String selectText) {
				// TODO Auto-generated method stub
				if (selectText.matches("^[a-zA-Z'-]*")) {
					card.setVisibility(View.VISIBLE);
					card.searchWord(selectText);
				} else {
					card.setVisibility(View.GONE);
					CustomToast.showToast(mContext,
							R.string.play_please_take_the_word);
				}
			}

			@Override
			public void selectParagraph(int paragraph) {
				// TODO Auto-generated method stub

			}
		});
		getOriginal();
	}

	protected void onDestory() {
		super.onDestroy();
		mPlayer.stop();
	}

	private void getOriginal() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer("");
		int size = CetDataManager.Instace().textList.size();
		for (int i = 0; i < size; i++) {
			if (CetDataManager.Instace().textList.get(i).id.equals(String
					.valueOf(pos))) {
				if (CetDataManager.Instace().textList.get(i).sex != null
						&& !CetDataManager.Instace().textList.get(i).sex
								.equals("")) {
					sb.append("\t");
					sb.append(CetDataManager.Instace().textList.get(i).sex);
					sb.append(": ");
					sb.append(CetDataManager.Instace().textList.get(i).sentence);
					sb.append("\n");
				} else {
					sb.append("\t");
					sb.append(CetDataManager.Instace().textList.get(i).sentence);
					sb.append("\n");
				}
			}
		}
		content.setText(TextAttr.ToDBC(sb.toString()));
		content.setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	@Override
	public void onBackPressed() {
		if (card.isShown()) {
			card.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}
}
