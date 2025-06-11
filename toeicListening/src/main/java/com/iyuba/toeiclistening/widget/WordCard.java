package com.iyuba.toeiclistening.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.protocol.DictRequest;
import com.iyuba.toeiclistening.protocol.DictResponse;
import com.iyuba.toeiclistening.protocol.WordUpdateRequest;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.DownAudio;
import com.iyuba.toeiclistening.util.Player;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;

import java.io.File;
import java.util.ArrayList;

public class WordCard extends LinearLayout {
    private Context mContext;
    private ZDBHelper helper;
    LayoutInflater layoutInflater;
    private Button add_word, close_word;
    private ProgressBar progressBar_translate;
    private String selectText;
    private TextView key, pron, def, example;
    private Typeface mFace;
    private NewWord selectCurrWordTemp;
    private ImageView speaker;
    private DownAudio downAudio;
    private Player player;

    public WordCard(Context context) {
        this(context, null);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.wordcard,
                this);
        initGetWordMenu();

    }

    public WordCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.wordcard,
                this);
        initGetWordMenu();
    }

    private void initGetWordMenu() {
        progressBar_translate = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
        key = (TextView) findViewById(R.id.word_key);
        pron = (TextView) findViewById(R.id.word_pron);
        def = (TextView) findViewById(R.id.word_def);
        example = (TextView) findViewById(R.id.example);
        mFace = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/SEGOEUI.TTF");
        speaker = (ImageView) findViewById(R.id.word_speaker);

        add_word = (Button) findViewById(R.id.word_add);
        add_word.setOnClickListener(new OnClickListener() { // 添加到生词本

            @Override
            public void onClick(View v) {
                saveNewWords(selectCurrWordTemp);
            }
        });
        close_word = (Button) findViewById(R.id.word_close); //关闭生词卡
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
            ClientSession.Instace().asynGetResponse(
                    new DictRequest(selectText), new IResponseReceiver() {

                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            DictResponse dictResponse = (DictResponse) response;
                            selectCurrWordTemp = dictResponse.word;
                            if (selectCurrWordTemp != null) {
                                if (selectCurrWordTemp.def != null
                                        && selectCurrWordTemp.def.length() != 0) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    handler.sendEmptyMessage(2);
                                }
                            } else {
                            }
                        }
                    }, null, null);
        } else {
            CustomToast.showToast(mContext, R.string.play_please_take_the_word,
                    1000);
        }
    }

    public void showWordDefInfo() {
        key.setText(selectCurrWordTemp.Word);
        pron.setTypeface(mFace);
        if (selectCurrWordTemp.pron != null
                && selectCurrWordTemp.pron.length() != 0) {
            pron.setText(Html.fromHtml("[" + selectCurrWordTemp.pron + "]"));
        }
        def.setMovementMethod(ScrollingMovementMethod.getInstance());
        def.setText(Html.fromHtml(selectCurrWordTemp.def));

        //example.setText(Html.fromHtml(selectCurrWordTemp.examples));
        example.setMovementMethod(ScrollingMovementMethod.getInstance());
        example.setText(Html.fromHtml(selectCurrWordTemp.examples));

        if (selectCurrWordTemp.audio != null && selectCurrWordTemp.audio.length() != 0) {
            speaker.setVisibility(View.VISIBLE);
        } else {
            speaker.setVisibility(View.GONE);
        }
        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                player = new Player(mContext, null);
                String url = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                        + "/" + Constant.SDCARD_FAVWORD_AUDIO_PATH + "/" + selectCurrWordTemp.Word + ".mp3";
                File file = new File(url);
                if (!file.exists()) {
                    url = selectCurrWordTemp.audio;
                }
                player.playUrl(url);
            }
        });
        add_word.setVisibility(View.VISIBLE); // 选词的同时隐藏加入生词本功能
        progressBar_translate.setVisibility(View.GONE); // 显示等待
    }

    private void saveNewWords(NewWord wordTemp) {
        LogUtil.e("保存单词");
        try {
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                //保存，更新数据
                wordTemp.userName = AccountManagerLib.Instace(mContext).userName;
                Log.e("userName", wordTemp.userName);
                helper = new ZDBHelper(mContext);
                boolean flag = helper.saveNewWord(wordTemp);
                if (DataManager.Instance().newWordList == null) {
                    ArrayList<NewWord> words = helper.getNewWords(wordTemp.userName);
                    DataManager.Instance().newWordList = words;
                } else if (flag) {
                    DataManager.Instance().newWordList.add(wordTemp);
                    CustomToast.showToast(mContext,
                            R.string.play_ins_new_word_success, 1000);
                    addNetwordWord(wordTemp.Word);
                } else if (!flag) {
                    CustomToast.showToast(mContext, R.string.play_ins_new_word_exist, 1000);
                }
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext, LoginActivity.class);
                mContext.startActivity(intent);
            }
            WordCard.this.setVisibility(View.GONE);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                    downAudio = new DownAudio(mContext, selectCurrWordTemp.audio, selectCurrWordTemp.Word + ".mp3");
                    downAudio.startDownLoadAudio();
                    break;
                case 2:
                    CustomToast.showToast(mContext,
                            R.string.play_no_word_interpretation, 1000);
                    WordCard.this.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     * 上传单词到服务器
     *
     * @param wordTemp
     */
    private void addNetwordWord(String wordTemp) {
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
}