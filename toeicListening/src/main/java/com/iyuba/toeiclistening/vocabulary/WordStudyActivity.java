package com.iyuba.toeiclistening.vocabulary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.RecordManager2;
import com.iyuba.core.common.manager.WordReadEvaluatePost;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.news.WordUpdateRequest;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.entity.EvaluateBean;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.event.UpWordEvent;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.util.ResultParse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class WordStudyActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    Toolbar toolbar;
    TextView txtWord;
    CheckBox cbCollect;
    View collectFrame;
    ImageView imgSpeaker;
    TextView txtPron;
    TextView txtExplain;
    RelativeLayout rlTop;
    TextView txtSentence;
    ImageView imgSwift;
    TextView txtSentencePron;
    TextView txtSentenceCh;
    ImageView imgLowScore;
    TextView txtScore;
    LinearLayout llScore;
    TextView txtEncourage;
    ImageView imgOriginal;
    ImageView imgRecord;
    LinearLayout llRecordBg;
    ImageView imgOwn;
    LinearLayout llOwn;
    TextView txtPosHint;
    TextView txtClickHint;
    TextView last;
    TextView next;
    private String webPath;

    private int count;

    public static void start(Context context, int position) {
        Intent intent = new Intent(context, WordStudyActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void start(Context context, List<RememberWord> list, int position) {
        Intent intent = new Intent(context, WordStudyActivity.class);
        intent.putExtra("word_list", (Serializable) list);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    //CetDataBase db;

    public static final String Header = "http://static2." + WebConstant.CN_SUFFIX + "sounds/toeic/words/";
    List<RememberWord> cetRootWordList = new ArrayList<>();
    File file;
    int position;

    private String sentenceUrl;
    private String wordUrl;

    RememberWord cetRootWord;

    AnimationDrawable animation;
    AnimationDrawable animation_record;
    AnimationDrawable animation_own;
    RecordManager2 recordManager;
    private boolean isRecording;

    Context mContext;
    MediaPlayer player;

    private String playUrl = "http://dict.youdao.com/dictvoice?audio=";
    private boolean isSentence = true;
    private TEDBHelper helper;
    private WordReadEvaluatePost manager;

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        txtWord = findViewById(R.id.txt_word);
        cbCollect = findViewById(R.id.cb_collect);
        collectFrame = findViewById(R.id.collect_layout);
        imgSpeaker = findViewById(R.id.img_speaker);
        txtPron = findViewById(R.id.txt_pron);
        txtExplain = findViewById(R.id.txt_explain);
        rlTop = findViewById(R.id.rl_top);
        txtSentence = findViewById(R.id.txt_sentence);

        imgSwift = findViewById(R.id.img_swift);
        txtSentencePron = findViewById(R.id.txt_sentence_pron);
        txtSentenceCh = findViewById(R.id.txt_sentence_ch);
        imgLowScore = findViewById(R.id.img_low_score);

        txtScore = findViewById(R.id.txt_score);
        llScore = findViewById(R.id.ll_score);
        txtEncourage = findViewById(R.id.txt_encourage);
        imgOriginal = findViewById(R.id.img_original);
        imgRecord = findViewById(R.id.img_record);
        llRecordBg = findViewById(R.id.ll_record_bg);
        imgOwn = findViewById(R.id.img_own);
        llOwn = findViewById(R.id.ll_own);
        txtPosHint = findViewById(R.id.txt_pos_hint);
        txtClickHint = findViewById(R.id.click_record_hint);

        last = findViewById(R.id.iv_last);
        next = findViewById(R.id.iv_next);

        last.setOnClickListener(onViewClicked);
        next.setOnClickListener(onViewClicked);
        cbCollect.setOnClickListener(onViewClicked);
        collectFrame.setOnClickListener(onViewClicked);
        imgOriginal.setOnClickListener(onViewClicked);
        last.setOnClickListener(onViewClicked);
        last.setOnClickListener(onViewClicked);
        imgRecord.setOnClickListener(onViewClicked);
        imgSwift.setOnClickListener(onViewClicked);
        llOwn.setOnClickListener(onViewClicked);
        imgSpeaker.setOnClickListener(onViewClicked);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_study_actiivty);
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mContext = this;
        helper = new TEDBHelper(mContext);
        //db = CetDataBase.getInstance(this);
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        int stage = getIntent().getExtras().getInt("stage");
        if (null != getIntent().getExtras().getSerializable("word")) {
            RememberWord word = (RememberWord) getIntent().getExtras().getSerializable("word");
            cetRootWordList.add(word);
//            cetRootWordList.addAll(db.getCetRootWordDao().getWordsByStage(word.stage));
//            cetRootWordList.addAll(db.getCetRootWordDao().getAllRootWord());
        } else {
            //cetRootWordList = db.getCetRootWordDao().getWordsByStage(stage);
            List<RememberWord> list = (List<RememberWord>) getIntent().getSerializableExtra("word_list");
            if (list != null) {
                cetRootWordList = list;
            } else {
                cetRootWordList = helper.geWords();
            }
        }

        position = getIntent().getExtras().getInt("position", 0);

        animation = (AnimationDrawable) imgOriginal.getDrawable();
        animation.selectDrawable(2);

        animation_own = (AnimationDrawable) imgOwn.getDrawable();
        animation_own.selectDrawable(2);

        animation_record = (AnimationDrawable) imgRecord.getDrawable();

        manager = new WordReadEvaluatePost();

        refreshUI();
    }


    View.OnClickListener onViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_last:
                    if (position != 0) {
                        position--;
                        refreshUI();
                    } else {
                        ToastUtil.showToast(mContext, "不能向前了");
                    }
                    stopPlayer();
                    break;
                case R.id.iv_next:
                    if (position != cetRootWordList.size() - 1) {
                        position++;
                        refreshUI();
                    } else {
                        ToastUtil.showToast(mContext, "不能向后了");
                    }
                    stopPlayer();
                    break;
                case R.id.collect_layout:
                    if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
                        startActivity(new Intent(mContext, LoginActivity.class));
                    }

                case R.id.cb_collect:
                    //单词


                    break;
                case R.id.img_original:
                    if (isRecording) {
                        ToastUtil.showToast(mContext, "评测中...");
                        return;
                    }
                    if (!isSentence) {
                        playAudio(cetRootWord.word, false, false);
                    } else {
                        playAudio(cetRootWord.sentence, true, false);
                    }
                    break;
                case R.id.img_swift:
                    llScore.setVisibility(View.INVISIBLE);
                    imgLowScore.setVisibility(View.INVISIBLE);
                    isSentence = !isSentence;
                    setSentenceTxt();
                    break;
                case R.id.img_speaker:
                    playAudio(cetRootWord.word, false, false);
                    break;
                case R.id.ll_own://播放录音文件的啊
                    if (isRecording) {
                        ToastUtil.showToast(mContext, "评测中...");
                        return;
                    }
                    if (!isSentence) {
                        playAudio("", false, true);
                    } else {
                        playAudio(webPath, true, true);
                    }
                    break;
                case R.id.img_record:

                    if (player.isPlaying()) {
                        player.stop();
                        animation.stop();
                    }
                    if (!isRecording) {

                        new AlertDialog.Builder(WordStudyActivity.this)
                                .setTitle("权限说明")
                                .setMessage("读写权限:存储录音文件" + "\n" + "录音权限:录音后进行评测")
                                .setPositiveButton("确定", (dialog, which) -> {

                                    WordStudyActivityPermissionsDispatcher.startRecordWithPermissionCheck(WordStudyActivity.this, cetRootWord.word);
                                })
                                .show();
                    } else {
                        stopRecord();
                        showMessage("正在测评");
                        txtClickHint.setText("正在测评");
                        Map<String, String> map = buildMap(txtSentence.getText().toString());
                        manager.post(map, file.getPath(), handler, 0, 0);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void setSentenceTxt() {
        if (isSentence) {
            getSentence();
        } else {
            txtSentence.setText(cetRootWord.word);
            txtSentenceCh.setText(cetRootWord.explain);
        }
    }

    private void showMessage(String message) {
        ToastUtil.showToast(mContext, message);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 6:
                    showMessage("评测成功");
                    LogUtil.e("评测成功" + msg.obj);
                    EvaluateBean evaluateBean = (EvaluateBean) msg.obj;
                    webPath = evaluateBean.getURL();
                    requestSuccess(evaluateBean);
                    txtClickHint.setText("点击录音");
                    break;
                case 12:
                    if (msg.arg1 == 404) {
                        showMessage("评测失败 未连接到服务器，请检查网络！");
                    } else {
                        showMessage("评测失败");
                    }
                    LogUtil.e("评测失败");
                    txtClickHint.setText("点击录音");
                    break;
            }
        }
    };

    private void requestSuccess(EvaluateBean bean) {
        if (bean != null) {
            if (bean.getWords() != null) {
                if (bean.getWords().size() > 1) {
                    sentenceUrl = bean.getURL();
                    llOwn.setVisibility(View.VISIBLE);
                } else {
                    wordUrl = bean.getURL();
                    llOwn.setVisibility(View.VISIBLE);
                }
                llOwn.setVisibility(View.VISIBLE);
                int scoreF = (int) (Float.parseFloat(bean.getTotal_score()) * 20);
                if (scoreF < 50) {
                    imgLowScore.setVisibility(View.VISIBLE);
                    llScore.setVisibility(View.INVISIBLE);
                    Animation animation = new TranslateAnimation(0, 0, 300, 0);
                    animation.setDuration(1000);
                    imgLowScore.startAnimation(animation);
                } else {
                    txtScore.setText(scoreF + "");
                    imgLowScore.setVisibility(View.INVISIBLE);
                    llScore.setVisibility(View.VISIBLE);
                    Animation animation = new TranslateAnimation(0, 0, 300, 0);
                    animation.setDuration(1000);
                    llScore.startAnimation(animation);
                }
                try {
                    String[] floats = bean.textScore.split("-");
                    LogUtil.e("======" + bean.getSentence());
                    SpannableStringBuilder readResult;
                    if (bean.getSentence() != null && !bean.getSentence().equals("")) {
                        readResult = ResultParse.getSenResultLocal(floats, bean.getSentence());
                        txtSentence.setText(readResult);//变色写入
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

        } else {
            ToastUtil.showToast(mContext, "测评失败,请重试");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WordStudyActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void playAudio(String webPath, boolean isSentence, boolean isOwn) {
        if (player.isPlaying()) {
            player.pause();
            animation.stop();
            animation_own.stop();
            return;
        }
        try {
            player.reset();
            if (isSentence) {
                if (isOwn) {
                    player.setDataSource(webPath);
                    animation_own.start();//自己
                } else {
                    if (TextUtils.isEmpty(cetRootWord.sentence)) {
                        return;
                    }
                    String sence = cetRootWord.sentence.replace(" ", " ");
                    sence = sence.replace("ˌ", ",");
                    String path = playUrl + URLEncoder.encode(sence);
                    Log.e("sentence_path", path);

                    player.setDataSource(path);
                    animation.start();
                }
            } else {
                if (isOwn) {
                    player.setDataSource(Header + wordUrl);
                    animation_own.start();
                } else {//最上面的单词播放
                    String path = Header + cetRootWord.sound.substring(0, cetRootWord.sound.indexOf("_")) + "/" + cetRootWord.sound + ".mp3";
                    Log.e("word_path", path);
                    player.setDataSource(path);
                }
            }
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void stopRecord() {
        animation_record.stop();
        recordManager.stopRecord();
        isRecording = false;
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startRecord(String word) {
        txtClickHint.setText("点击停止");
        recordManager = new RecordManager2();

        animation_record.start();
        recordManager.startRecord(word);
        file = recordManager.file;
        isRecording = true;
    }

    private Map<String, String> buildMap(String s) {
        Map<String, String> map = new HashMap<>(6);
        map.put("type", "toeic");
        map.put("sentence", s);
        map.put("userId", AccountManagerLib.Instace(mContext).userId);
        map.put("newsId", "0");
        map.put("paraId", "0");
        map.put("IdIndex", "0");
        return map;
    }

    private void refreshUI() {
        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            collectFrame.setVisibility(View.GONE);
            cbCollect.setVisibility(View.VISIBLE);
        } else {
            collectFrame.setVisibility(View.VISIBLE);
            cbCollect.setVisibility(View.GONE);
        }

        try {
            cetRootWord = cetRootWordList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        setSentenceTxt();
        llOwn.setVisibility(View.INVISIBLE);

        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            cbCollect.setClickable(true);
            if (cetRootWord.isCollect) {
                cbCollect.setChecked(true);
            } else {
                cbCollect.setChecked(false);
            }
            cbCollect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    WordOp wo = new WordOp(mContext);
                    wo.saveData(cetRootWord.getWord(AccountManagerLib.Instace(mContext).userId));
                    addNetwordWord(cetRootWord.word);
                    cetRootWord.isCollect = true;
                    helper.updateWord(cetRootWord);
                } else {
                    deleteNetWord(cetRootWord.word);
                    cetRootWord.isCollect = false;
                    helper.updateWord(cetRootWord);
                }
                EventBus.getDefault().post(new UpWordEvent());
            });
        }

        txtClickHint.setText("点击录音");
        txtWord.setText(cetRootWord.word);
        txtExplain.setText(cetRootWord.explain);
        if (cetRootWord.pron.startsWith("[") || cetRootWord.pron.startsWith("/")) {
            txtPron.setText(String.format("%s", TextAttr.decode(cetRootWord.pron)));
        } else {
            txtPron.setText(String.format("[%s]", TextAttr.decode(cetRootWord.pron)));
        }
        txtPosHint.setText(String.format("%d/%d", position + 1, cetRootWordList.size()));

        llScore.setVisibility(View.INVISIBLE);
        imgLowScore.setVisibility(View.INVISIBLE);
        if (ConfigManagerMain.Instance().loadBoolean("autoplay")) {
            playAudio(cetRootWord.word, false, false);
            animation.start();
        }
        animation_own.stop();
    }

    private void deleteNetWord(String word) {
        ClientSession.Instace().asynGetResponse(
                new WordUpdateRequest(AccountManagerLib.Instace(mContext).userId,
                        WordUpdateRequest.MODE_DELETE, word),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                    }

                }, null, null);
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


    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        animation.stop();
        animation.selectDrawable(2);
        imgOriginal.setImageDrawable(animation);
        //imgOwn.setImageDrawable(animation);
        animation_own.stop();
        animation_own.selectDrawable(2);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getSentence() {
        if (TextUtils.isEmpty(cetRootWord.sentence)) {
            ToastUtil.showToast(mContext, "数据异常！");
        } else {
            txtSentence.setText(cetRootWord.sentence);
            txtSentenceCh.setText(cetRootWord.sentenceCn);
            try {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(cetRootWord.sentence);
                spannableStringBuilder.setSpan(new RelativeSizeSpan(1.35f), cetRootWord.sentence.toLowerCase().indexOf(cetRootWord.word),
                        cetRootWord.sentence.toLowerCase().indexOf(cetRootWord.word) + cetRootWord.word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtSentence.setText(spannableStringBuilder);
            } catch (IndexOutOfBoundsException e) {
                txtSentence.setText(cetRootWord.sentence);
            }
        }
    }

    private void stopPlayer() {
        animation.stop();
        if (player.isPlaying()) {
            player.stop();
        }
    }

}
