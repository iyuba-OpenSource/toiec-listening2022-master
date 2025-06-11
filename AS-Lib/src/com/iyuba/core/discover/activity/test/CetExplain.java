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
 * 四六级解释界面
 * 
 * @author chentong
 * @version 1.0
 * @para "curPos" 当前题号
 */
public class CetExplain extends BasisActivity {
	private Context mContext;
	private Button backBtn;
	private TextView number, title;
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

	@Override
	protected void onPause() {
		super.onPause();
		mPlayer.pause();
	}

	private void initWidget() {
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.explain);
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		card = (WordCard) findViewById(R.id.word);
		card.setVisibility(View.GONE);
		sound = (Button) findViewById(R.id.qsound);
		sound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPlayer.playUrl(CetDataManager.Instace().answerList.get(pos).sound);
			}
		});
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
		getExplain();
	}

	private void getExplain() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer("");
		int size = CetDataManager.Instace().explainList.size();
		for (int i = 0; i < size; i++) {
			if (CetDataManager.Instace().explainList.get(i).id.equals(String
					.valueOf(pos))) {
				sb.append("\t");
				sb.append(mContext.getString(R.string.keys));
				sb.append(CetDataManager.Instace().explainList.get(i).keys);
				sb.append("\n\n\t");
				sb.append(mContext.getString(R.string.explains));
				sb.append(CetDataManager.Instace().explainList.get(i).explain);
				sb.append("\n\n\t");
				sb.append(mContext.getString(R.string.knowledges));
				sb.append(CetDataManager.Instace().explainList.get(i).knowledge);
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
