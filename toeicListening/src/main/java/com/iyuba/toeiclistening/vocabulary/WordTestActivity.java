package com.iyuba.toeiclistening.vocabulary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.network.TestSendBean;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.news.WordUpdateRequest;
import com.iyuba.core.common.util.GetMAC;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.api.ApiRetrofit;
import com.iyuba.toeiclistening.api.ApiUpdateWordsRecordInput;
import com.iyuba.toeiclistening.api.UpdateWordsRecordBeanList;
import com.iyuba.toeiclistening.api.UpdateWordsRecordScoreBean;
import com.iyuba.toeiclistening.api.UpdateWordsRecordTestBean;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.event.UpWordEvent;
import com.iyuba.toeiclistening.event.UpWordPassEvent;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class WordTestActivity extends AppCompatActivity {


    TextView jiexi;
    TextView nextButton;
    TextView jiexiWord;
    TextView jiexiDef;
    TextView jiexiPron;
    TextView wordNum;
    CheckBox cb;
    RelativeLayout jiexiRoot;

    Toolbar toolbar;
    TextView word;
    TextView answera;
    TextView answerb;
    TextView answerc;
    TextView answerd;
    LinearLayout ll;

    TextView right;

    private String playurl = "http://dict.youdao.com/dictvoice?audio=";

    MediaPlayer player;

    TEDBHelper tedbHelper;

    public static void start(Context context, int level) {
        Intent starter = new Intent(context, WordTestActivity.class);
        starter.putExtra("level", level);
        context.startActivity(starter);
    }


    private List<RememberWord> words = new ArrayList<>();
    private List<String> wrongAnswers = new ArrayList<>();//错误答案列表
    RememberWord cetRootWord;
    int level;
    //CetDataBase db;
    TextView[] tvs;
    int wrong = 0;

    ObjectAnimator animator;
    ObjectAnimator animator1;
    AnimatorSet animatorSet = new AnimatorSet();
    int position;
    boolean isCheckable;
    private Context mContext;


    //为添加数据上传增加字段
    private String uid;
    private String deviceId;

    private String score;//对/总
    private String category = "单词闯关";
    private String lessontype = "W";
    private String testCnt;//当前已做试题个数

    private String answerResut;//答案是否正确
    private String beginTime;
    private String isUpload = "1";
    private String lessonId;//单词id 数据库无 暂传1
    private String rightAnswer;//单词内容
    private String testId = "0";
    private String testMode = "W";
    private String testTime;
    private String titleNum;//单词id 数据库无 暂传1
    private String userAnswer;//用户答案
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String chooseAnswer;//选择的答案
    private String Resut;//选择是否正确"0"错 "1"对


    List<UpdateWordsRecordTestBean> updateWordsRecordTestBeanList = new ArrayList<>();
    List<UpdateWordsRecordScoreBean> updateWordsRecordScoreBeanList = new ArrayList<>();


    private void initView() {

        jiexi = findViewById(R.id.jiexi);
        nextButton = findViewById(R.id.next_button);
        jiexiWord = findViewById(R.id.jiexi_word);
        jiexiDef = findViewById(R.id.jiexi_def);

        jiexiPron = findViewById(R.id.jiexi_pron);
        wordNum = findViewById(R.id.tv_word_number);
        cb = findViewById(R.id.cb);
        jiexiRoot = findViewById(R.id.jiexi_root);

        toolbar = findViewById(R.id.toolbar);
        word = findViewById(R.id.word);
        answera = findViewById(R.id.answera);
        answerb = findViewById(R.id.answerb);
        answerc = findViewById(R.id.answerc);
        answerd = findViewById(R.id.answerd);
        ll = findViewById(R.id.ll);

        jiexi.setOnClickListener(onViewClicked);
        nextButton.setOnClickListener(onViewClicked );
        answera.setOnClickListener(onViewClicked);
        answerb.setOnClickListener(onViewClicked);
        answerc.setOnClickListener(onViewClicked);
        answerd.setOnClickListener(onViewClicked);
        ll.setOnClickListener(onViewClicked);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_test);
        initView();

        mContext = this;
        tedbHelper = new TEDBHelper(mContext);
        initActionBar();
        getData();
        initPlayer();
        initAnswerTv();
        initAnimator();

        initData(0, true);

        cb.setOnCheckedChangeListener(listener);
        uid = AccountManagerLib.Instace(mContext).userId ==
                null ? "0" : AccountManagerLib.Instace(mContext).userId;
        deviceId = GetMAC.getMAC();
        Date date = new Date();
        beginTime = sdf.format(date).replace(" ", "%20");
    }


    private void getData() {
        level = getIntent().getIntExtra("level", 1);
        int count = com.iyuba.configation.ConfigManager.Instance().loadInt("wpd", 30);
        words = tedbHelper.geWords().subList((level - 1) * count, level * count);
        //words =  tedbHelper.geWords().subList();
        Collections.shuffle(words);
        for (RememberWord word : tedbHelper.geWords()) {
            wrongAnswers.add(word.explain);
        }
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                addNetwordWord(cetRootWord.word);
                cetRootWord.isCollect = true;
                tedbHelper.updateWord(cetRootWord);
            } else {
                deleteNetWord(cetRootWord.word);
                cetRootWord.isCollect = false;
                tedbHelper.updateWord(cetRootWord);
            }
            tedbHelper.updateWord(cetRootWord);
        }
    };

    private void initActionBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void initAnswerTv() {
        tvs = new TextView[]{answera, answerb, answerc, answerd};
    }

    private void initAnimator() {
        animator = ObjectAnimator.ofFloat(ll, "translationY", -200, 0);
        animator1 = ObjectAnimator.ofFloat(ll, "alpha", 0.2f, 1f);
        animatorSet.playTogether(animator, animator1);
        animatorSet.setDuration(600);
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setOnPreparedListener(onPreparedListener);
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            player.start();
        }
    };

    private void initData(int position, boolean reset) {
        try {
            cetRootWord = words.get(position);
            setData(cetRootWord);
            if (reset) {
                wrong = 0;
            }
        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }
        wordNum.setText(position + 1 + "");

    }

    private void setData(RememberWord words) {
        isCheckable = true;
        animatorSet.start();
        word.setText(words.word);
        for (TextView textView : tvs) {
            textView.setBackground(getResources().getDrawable(R.drawable.wordtest_rect_default1));
            textView.setTextColor(Color.parseColor("#333333"));
        }
        int random = new Random().nextInt(100) % 4;
        fillinAnswers(tvs[random], words.explain);
        Log.d("diao", random + "");
        jiexi.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        playWord(words.word);
    }

    private void playWord(String word) {
        try {
            player.reset();
            player.setDataSource(playurl + word);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void fillinAnswers(TextView textView, String def) {
        textView.setText(def);
        right = textView;
        for (int i = 0; i < tvs.length; i++) {
            if (tvs[i] != textView) {
                tvs[i].setText(getWorngText(def));
            }
        }
    }

    private String getWorngText(String def) {
        String text = wrongAnswers.get(new Random().nextInt(1100));
        if (def.equals(text)) {
            text = getWorngText(def);
        }
        return text;
    }


    View.OnClickListener onViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (jiexiRoot.getVisibility() == View.VISIBLE) {
                jiexiRoot.setVisibility(View.GONE);
                if (view.getId() == R.id.next_button) {
                    initData(++position, false);
                }
                return;
            }
            switch (view.getId()) {
                case R.id.jiexi:
                    showJiexiView();
                    break;
                case R.id.next_button:
                    initData(++position, false);
                    //ConfigManager.Instance().putInt("stage", level + 1);
                    //showSuccessDialog();
                    break;
                case R.id.answera:
                case R.id.answerb:
                case R.id.answerc:
                case R.id.answerd:
                    if (!isCheckable) return;
                    isCheckable = false;
                    jiexi.setVisibility(View.VISIBLE);
                    if (position == words.size() - 1) nextButton.setText("完成");
                    nextButton.setVisibility(View.VISIBLE);
                    if (view == right) {//选择正确
                        view.setBackground(getResources().getDrawable(R.drawable.answer_right));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                        Resut = "1";
                        chooseAnswer = String.valueOf(((TextView) view).getText());
                        cetRootWord.statues = 2;
                        if (position == words.size() - 1) {
                            showSuccessDialog();
                            if (level == ConfigManagerMain.Instance().loadIntTest("stage")) {
                                ConfigManagerMain.Instance().putInt("stage", level + 1);
                            }
                        }
                    } else {//选择错误
                        view.setBackground(getResources().getDrawable(R.drawable.answer_wrong));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                        view.startAnimation(shakeAnimation(1));
                        Resut = "0";
                        chooseAnswer = String.valueOf(((TextView) view).getText());
                        cetRootWord.statues = 1;
                        wrong++;
//                    showSuccessDialog();

                        if (wrong * 100 / words.size() > 10) {
                            showFailDialog();
                        } else {
                            if (position == words.size() - 1) {
                                showSuccessDialog();
                                if (level == ConfigManagerMain.Instance().loadIntTest("stage")) {
                                    ConfigManagerMain.Instance().putInt("stage", level + 1);
                                }
                            }
                        }
                    }
                    tedbHelper.updateWord(cetRootWord);
                    EventBus.getDefault().post(new UpWordEvent());
                    addToDataPassenger(position, chooseAnswer, Resut);
                    break;
            }
        }
    };

    private void showJiexiView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            jiexiRoot.setVisibility(View.VISIBLE);
            jiexiDef.setText(cetRootWord.explain);
            if (!cetRootWord.pron.startsWith("[")) {
                jiexiPron.setText(String.format("[%s]", TextAttr.decode(cetRootWord.pron)));
            } else {
                jiexiPron.setText(String.format("%s", TextAttr.decode(cetRootWord.pron)));
            }
            jiexiWord.setText(cetRootWord.word);
            cb.setChecked(cetRootWord.isCollect);
            Animator animator = ObjectAnimator.ofFloat(jiexiRoot, "scaleX", 0.1f, 1f);
            animator.setDuration(600);
            animator.start();
        } else {
            jiexiRoot.setVisibility(View.VISIBLE);
            jiexiDef.setText(cetRootWord.explain);
            if (!cetRootWord.pron.startsWith("[")) {
                jiexiPron.setText(String.format("[%s]", TextAttr.decode(cetRootWord.pron)));
            } else {
                jiexiPron.setText(String.format("%s", TextAttr.decode(cetRootWord.pron)));
            }
//            jiexiPron.setText(String.format("[%s]", TextAttr.decode(cetRootWord.pron)));
            jiexiWord.setText(cetRootWord.word);
        }
    }

    private void showFailDialog() {
        String c = String.format("闯关失败,回答%s题,答错%s题,错误率%s,是否重新闯关?", position + 1, wrong, wrong * 100 / (position + 1) + "%");
        new AlertDialog.Builder(this).setMessage(c)
                .setPositiveButton("重新闯关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initData(0, true);
                    }
                })
                .setNegativeButton("再学一会儿", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new UpWordEvent());
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showSuccessDialog() {
        EventBus.getDefault().post(new UpWordPassEvent());
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.TransparentDialog);

        View view = View.inflate(mContext, R.layout.dialog_word_success, null);
        TextView textView = view.findViewById(R.id.tv_word_sign);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.showToast(mContext,"去打卡了");
                int stage = getIntent().getExtras().getInt("stage");
                level = getIntent().getIntExtra("level", 1);
                int count = com.iyuba.configation.ConfigManager.Instance().loadInt("wpd", 30);
                int right = count - wrong;
                double scale = right * 100 / count;
                WordSignActivity.start(mContext, level, count, (int) scale);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    public Animation shakeAnimation(int counts) {
        Animation translate = new TranslateAnimation(0, 18, 0, 0);
        translate.setInterpolator(new CycleInterpolator(counts));
        translate.setRepeatCount(1);
        translate.setDuration(100);
        return translate;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBackDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (jiexiRoot.getVisibility() == View.VISIBLE) {
            jiexiRoot.setVisibility(View.GONE);
        } else {
            showBackDialog();
        }
    }

    private void showBackDialog() {
        new AlertDialog.Builder(this).setMessage("您正在进行闯关,确定要退出吗?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                initData(++position, false);
                        dataPassenger();
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
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

    private void addToDataPassenger(int position, String chooseAnswer, String Resut) {
        UpdateWordsRecordScoreBean updateWordsRecordScoreBean = new UpdateWordsRecordScoreBean();
        UpdateWordsRecordTestBean updateWordsRecordTestBean = new UpdateWordsRecordTestBean();

        //赋值
        double counter = 0;
        counter = tedbHelper.getTotalDoneWordsNum() / tedbHelper.getTotalWordsNum();
        score = String.valueOf(counter);
        testCnt = String.valueOf(position);
        rightAnswer = words.get(position).word;
        userAnswer = chooseAnswer;
        answerResut = Resut;
        lessonId = "1";
        titleNum = "1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date();
        testTime = sdf.format(date).replace(" ", "%20");

        updateWordsRecordScoreBean.setScore(score);
        updateWordsRecordScoreBean.setCategory(category);
        updateWordsRecordScoreBean.setLessontype(lessontype);
        updateWordsRecordScoreBean.setTestCnt(testCnt);
        updateWordsRecordScoreBeanList.add(updateWordsRecordScoreBean);

        updateWordsRecordTestBean.setAnswerResut(answerResut);
        updateWordsRecordTestBean.setBeginTime(beginTime);
        updateWordsRecordTestBean.setCategory(category);
        updateWordsRecordTestBean.setIsUpload(isUpload);
        updateWordsRecordTestBean.setLessonId(lessonId);
        updateWordsRecordTestBean.setRightAnswer(rightAnswer);
        updateWordsRecordTestBean.setTestId(testId);
        updateWordsRecordTestBean.setTestMode(testMode);
        updateWordsRecordTestBean.setTestTime(testTime);
        updateWordsRecordTestBean.setTitleNum(titleNum);
        updateWordsRecordTestBean.setUserAnswer(userAnswer);
        updateWordsRecordTestBean.setUid(uid);
        updateWordsRecordTestBeanList.add(updateWordsRecordTestBean);
    }

    private void dataPassenger() {
        UpdateWordsRecordBeanList uploadList = new UpdateWordsRecordBeanList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String postdate = sdf.format(new Date());
        String sign = MD5.getMD5ofStr(uid + "224" + "toeiciyubaExam" + postdate);
        Date date = new Date();
        testTime = sdf.format(date).replace(" ", "%20");

        uploadList.scoreList = updateWordsRecordScoreBeanList;
        uploadList.testList = updateWordsRecordTestBeanList;


        //网络请求头部
        uploadList.DeviceId = deviceId;
        uploadList.uid = String.valueOf(uid);
        uploadList.sign = sign;


        //网络请求构造
        ApiUpdateWordsRecordInput apiUpdateWordsRecordInput = ApiRetrofit.getInstance().getApiUpdateWordsRecordInput();
        apiUpdateWordsRecordInput.postWordsRecord(getBody(uploadList)).enqueue
                (new Callback<TestSendBean>() {
                    @Override
                    public void onResponse(Call<TestSendBean> call, Response<TestSendBean> response) {
                        try {
                            String result = response.body().getResultCode();
                            Timber.e("UpdateWordsResult" + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TestSendBean> call, Throwable t) {
                        t.printStackTrace();
                        Timber.e("UpdateWordsFailure" + t);
                    }
                });

        Timber.e("ApiUpdateWordsRecordInput.apiUpdateWordsRecordInput" + apiUpdateWordsRecordInput);

    }

    //网络请求body构造
    private RequestBody getBody(UpdateWordsRecordBeanList item) {
        Gson gson = new Gson();
        RequestBody body = null;

        try {
            String json = gson.toJson(item);
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
//            return null;
    }


}



