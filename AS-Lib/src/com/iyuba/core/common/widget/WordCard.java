package com.iyuba.core.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.core.R;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.DictRequest;
import com.iyuba.core.common.protocol.base.DictResponse;
import com.iyuba.core.common.protocol.news.WordUpdateRequest;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordDBOp;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.discover.activity.WordContentActivity;

/**
 * 单词卡
 * 
 * @author 陈彤
 */
public class WordCard extends LinearLayout {
	private Context mContext;
	LayoutInflater layoutInflater;
	private Button add_word, close_word;
	private ProgressBar progressBar_translate;
	private String selectText;
	private TextView key, def, pron;
	private Word selectCurrWordTemp;
	private ImageView speaker;
	private View main;

	public WordCard(Context context) {
		super(context);
		mContext = context;
		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
				this);
		initGetWordMenu();
	}

	public WordCard(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
				this);
		initGetWordMenu();
	}

	private void initGetWordMenu() {
		main = findViewById(R.id.word);
		progressBar_translate = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
		key = (TextView) findViewById(R.id.word_key);
		def = (TextView) findViewById(R.id.word_def);
		pron = (TextView) findViewById(R.id.word_pron);
		speaker = (ImageView) findViewById(R.id.word_speaker);
		add_word = (Button) findViewById(R.id.word_add);
		add_word.setOnClickListener(new OnClickListener() { // 添加到生词本

			@Override
			public void onClick(View v) {
				saveNewWords();
			}
		});
		close_word = (Button) findViewById(R.id.word_close);
		close_word.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				WordCard.this.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 获取单词释义
	 */
	private void getNetworkInterpretation() {
		if (selectText != null && selectText.length() != 0) {
			WordDBOp wordDBOp = new WordDBOp(mContext);
			selectCurrWordTemp = wordDBOp.findDataByKey(selectText);
			wordDBOp.updateWord(selectText);
			if (selectCurrWordTemp != null) {
				if (selectCurrWordTemp.def != null
						&& selectCurrWordTemp.def.length() != 0) {
					handler.sendEmptyMessage(1);
				} else {
					handler.sendEmptyMessage(2);
				}
			} else {

				ExeProtocol.exe(new DictRequest(selectText),
						new ProtocolResponse() {
							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								DictResponse dictResponse = (DictResponse) bhr;
								selectCurrWordTemp = dictResponse.word;
								if (selectCurrWordTemp != null) {
									if (selectCurrWordTemp.def != null
											&& selectCurrWordTemp.def.length() != 0) {
										handler.sendEmptyMessage(1);
									} else {
										handler.sendEmptyMessage(2);
									}
								}
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub
								CustomToast.showToast(mContext,
										R.string.play_please_take_the_word);
							}
						});
			}
		}
	}

	private void showWordDefInfo() {
		key.setText(selectCurrWordTemp.key);
		def.setText(selectCurrWordTemp.def);
		if (selectCurrWordTemp.pron != null
				&& !selectCurrWordTemp.pron.equals("null")) {
			StringBuffer sb = new StringBuffer();
			sb.append('[').append(selectCurrWordTemp.pron).append(']');
			pron.setText(TextAttr.decode(sb.toString()));
		}
		if (selectCurrWordTemp.audioUrl != null
				&& selectCurrWordTemp.audioUrl.length() != 0) {
			speaker.setVisibility(View.VISIBLE);
		} else {
			speaker.setVisibility(View.GONE);
		}
		speaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Player player = new Player(mContext, null);
				String url = selectCurrWordTemp.audioUrl;
				player.playUrl(url);
			}
		});
		main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, WordContentActivity.class);
				intent.putExtra("word", selectCurrWordTemp.key);
				mContext.startActivity(intent);
			}
		});
		add_word.setVisibility(View.VISIBLE); // 选词的同事隐藏加入生词本功能
		progressBar_translate.setVisibility(View.GONE); // 显示等待
	}

	private void saveNewWords() {
		if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			mContext.startActivity(intent);
		} else {
			try {
				selectCurrWordTemp.userid = AccountManagerLib.Instace(mContext).userId;
				WordOp wo = new WordOp(mContext);
				wo.saveData(selectCurrWordTemp);
				CustomToast.showToast(mContext,
						R.string.play_ins_new_word_success);
				WordCard.this.setVisibility(View.GONE);
				addNetwordWord(selectCurrWordTemp.key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addNetwordWord(String wordTemp) {
		ClientSession.Instace().asynGetResponse(
				new WordUpdateRequest(AccountManagerLib.Instace(mContext).userId,
						WordUpdateRequest.MODE_INSERT, wordTemp),
				new IResponseReceiver() {
					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
					}
				}, null, null);
	}

	public void searchWord(String word) {
		selectText = word;
		getNetworkInterpretation();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				showWordDefInfo();
				break;
			case 2:
				CustomToast.showToast(mContext,
						R.string.play_no_word_interpretation);
				WordCard.this.setVisibility(View.GONE);
				break;
			}
		}
	};
}
