package com.iyuba.toeiclistening.activity.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.ReadEvaluatePost;
import com.iyuba.core.common.manager.RecordManager2;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.entity.EvaluateBean;
import com.iyuba.core.me.pay.RequestCallBack;
import com.iyuba.core.microclass.protocol.AddCreditsRequestNew;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.util.ResultParse;
import com.iyuba.toeiclistening.util.UtilPostFile;
import com.iyuba.toeiclistening.widget.SelectWordTextView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 评测适配器
 */

public class ReadEvaluateAdapter extends RecyclerView.Adapter {

    private static final int REQUEST_WORD_FINISH = 0;  // 请求单词数据成功
    private static final int REQUEST_WORD_FAILED = -1;  // 请求单词数据失败
    private static final int EVAL_WORD_FINISH = 1;  // 评测单词成功
    private static final int EVAL_WORD_FAILED = 2;  // 评测单词失败
    private Context mContext;
    private ArrayList<Text> textList;        //Text实体的列表
    public MediaPlayer player;
    public MediaPlayer evaluatePlayer;
    public boolean isPrepared;               //播放器初始化
    //private ReadEvaluateManager manager;
    private RecordManager2 recordManager2;
    private ReadEvaluatePost post;

    private ReadEvaluateFragment fragment;
    private TEDBHelper tedbHelper;
    private boolean isEvaluating;
    private boolean isWordEvaluating;  //单词评测
    private int evaluatingPosition = -1;
    private String filePath;

    private TextView explain;  // 展示当前纠音选中单词的释义的TextView
    private MediaPlayer wordPlayer;  // 纠音单词的音频播放器
    private boolean isRecording = false;  // 当前是否正在录音
    private int curWordIndex = 0;  // 纠音 - 当前点击单词是句子中的第几个
    private String curWordStr = "";  // 纠音，当前点击的单词
    private String curWordUserAudio = "";  // 当前单词用户录音
    private boolean isPermissionRequested;

    private TextView recordTv;  // 纠音中录音按钮下面的提示文字
    private String recFilePath = "";  // 录音保存路径
    private TextView userPron;
    private TextView rightPron;
    private LinearLayout scoreLy;
    private TextView scoreTv;
    private MediaPlayer recPlayer;  // 录音音频播放器

    private SharedPreferences evalSp;

    private String titleNum;


    public int getEvaluatingPosition() {
        return evaluatingPosition;
    }


    public void setEvaluatingPosition(int evaluatingPosition) {
        this.evaluatingPosition = evaluatingPosition;
    }

    private final Handler mHandle = new Handler(message -> {
        int what = message.what;
        if (what == REQUEST_WORD_FINISH) {
            JSONObject obj = (JSONObject) message.obj;
            // 当前纠音选中单词的释义
            String curWordDef = obj.getString("def");
            // 当前纠音选中单词的音频
            String curWordAudio = obj.getString("audio");
            Log.d("TAG", "单词音频-------->: " + curWordAudio);
            // 设置单词释义
            explain.setText("单词释义: " + curWordDef);
            // 设置单词音频
            if (wordPlayer != null) {
                wordPlayer.release();
                wordPlayer = null;
            }
            wordPlayer = new MediaPlayer();
            try {
                if (curWordAudio != null) {
                    wordPlayer.setDataSource(curWordAudio);
                    wordPlayer.prepare();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else if (what == REQUEST_WORD_FAILED) {
            ToastUtil.showToast(mContext, "网络错误，请稍后重试.");
            return true;
        } else if (what == EVAL_WORD_FINISH) {
            JSONObject jo = (JSONObject) message.obj;
            if (jo.containsKey("URL")) {
                curWordUserAudio = jo.getString("URL");
                return true;
            }
            curWordUserAudio = jo.getString("");
            userPron.setText("你的发音: " + jo.getString("content") + " [" + jo.getString("user_pron2") + "]");
            scoreTv.setText(String.valueOf(message.arg1));
            scoreLy.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    });

    public ReadEvaluateAdapter(Context context, ReadEvaluateFragment fragment, String titleNum) {
        mContext = context;
        player = new MediaPlayer();
        evaluatePlayer = new MediaPlayer();
        this.fragment = fragment;
        //manager = ReadEvaluateManager.getInstance(mContext);
        recordManager2 = new RecordManager2();
        post = new ReadEvaluatePost();
        tedbHelper = new TEDBHelper(mContext);
        this.titleNum = titleNum;
    }

    public void setData(ArrayList<Text> list, TitleInfo titleInfo) {
        textList = list;
        if (player.isPlaying()) {
            player.pause();
            player.seekTo(0);
            //player.stop(); 谨慎使用
        }

        String playUrl;
        if (titleInfo != null) {
            if (titleInfo.songPath != null && !titleInfo.songPath.isEmpty() && fileIsExists(titleInfo.songPath)) {
                playUrl = titleInfo.songPath;
            } else {
                playUrl = titleInfo.webSongPath;
            }

            try {
                player.reset();
                player.setDataSource(playUrl);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("播放器初始化异常" + e);
            }
        } else {
            LogUtil.e("播放地址错误！");
            ToastUtil.showToast(mContext, "播放地址错误!");
        }

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //初始化完成，可以播放
                isPrepared = true;
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("原文句子播放错误" + what + " " + extra);
                return false;
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_read_evaluate, viewGroup, false);
        return new EvaluateTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof EvaluateTextViewHolder evaluateTextViewHolder) {

            Text text = textList.get(i);
            evaluateTextViewHolder.setData(text, i);
            evaluateTextViewHolder.setListener(i);
        }
    }

    @Override
    public int getItemCount() {
        if (textList != null) {
            return textList.size();
        }
        return 0;
    }

    @SuppressLint("SetTextI18n")
    void showCorrect(Text text, EvaluateBean data) {


        data.setParaId(text.TitleNum + "");

        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_correct);
            window.setGravity(Gravity.CENTER);

            String baseString = text.Sentence;
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(baseString.replace("‘", "'"));

            List<EvaluateBean.WordsBean> wordList = data.getWords();

            int beginIndex = 0;

            // 遍历给单词染色
            for (int i = 0; i < wordList.size(); i++) {
                if (beginIndex > text.Sentence.length())
                    break;
                EvaluateBean.WordsBean word = wordList.get(i);
                if (word.getScore() < 3 && word.getScore() != -1) {
                    int tempEndIndex = beginIndex + word.getContent().length();

                    sb.setSpan(new ForegroundColorSpan(Color.RED), beginIndex, tempEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // System.out.println("-------------------->设置红色" + beginIndex + "---" + tempEndIndex + "---" + word.getContent());
                } else if (word.getScore() >= 3) {
                    int tempEndIndex = beginIndex + word.getContent().length();
                    sb.setSpan(new ForegroundColorSpan(Color.GREEN), beginIndex, tempEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                beginIndex += word.getContent().length() + 1;
            }
            // 遍历找出第一个低分评测单词
            int firstLow = 0;
            for (int i = 0; i < wordList.size(); i++) {
                if (wordList.get(i).getScore() < 2) {
                    // Log.d(TAG, "showCorrect: score --- index --- " + wordList.get(i).getScore() + " --- " + wordList.get(i).getIndex());
                    firstLow = i;
                    break;
                }
            }

            // 请求当前纠音单词的音频等信息
            requestWord(wordList.get(firstLow).getContent(), wordList.get(firstLow).getUserPron(), wordList.get(firstLow).getPron());
            curWordStr = wordList.get(firstLow).getContent();
            curWordIndex = firstLow;
            Log.d("TAG", "showCorrect: 请求单词数据传递参数---------------->" + wordList.get(firstLow).getContent() + " --- " + wordList.get(firstLow).getUserPron() + " --- " + wordList.get(firstLow).getPron());

            TextView curWord = window.findViewById(R.id.tv_correct_word);
            // SelectableTextView correctSen = window.findViewById(R.id.tv_correct_sen);
            SelectWordTextView correctSen = window.findViewById(R.id.tv_correct_sen);
            rightPron = window.findViewById(R.id.right_pron);
            userPron = window.findViewById(R.id.user_pron);
            explain = window.findViewById(R.id.correct_explain);

            recordTv = window.findViewById(R.id.correct_rec_tv);
            scoreTv = window.findViewById(R.id.correct_score);
            scoreLy = window.findViewById(R.id.correct_score_ly);

            ImageView correctClose = window.findViewById(R.id.correct_close);
            ImageView playWord = window.findViewById(R.id.correct_play_word);
            ImageView playOri = window.findViewById(R.id.correct_play_ori);
            ImageView record = window.findViewById(R.id.correct_rec);
            ImageView playRec = window.findViewById(R.id.correct_play_user);
            ImageView goToMic = window.findViewById(R.id.goto_micro);

            curWord.setText(wordList.get(firstLow).getContent().replaceAll("‘", "'"));
            rightPron.setText("正确发音: " + wordList.get(firstLow).getContent() + " [" + wordList.get(firstLow).getPron2() + "]");
            userPron.setText("你的发音: " + wordList.get(firstLow).getContent() + " [" + wordList.get(firstLow).getUserPron2() + "]");
            // explain.setText(wordList.get(firstLow).get);
            Log.d("TAG", "showCorrect: ================> " + wordList.get(firstLow).getIndex());

//            correctSen.setSelectTextBackColor(R.color.tab_layout_font_color);
            correctSen.setText(sb);
            correctSen.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
                @Override
                public void onClickWord(String s) {
                    curWord.setText(s);
                    rightPron.setText("正确发音: " + s + " []");
                    userPron.setText("你的发音: " + s + " []");
                    for (EvaluateBean.WordsBean word : wordList) {
                        //[`~!@#$%^&*()+=|{}':;',\[\].<>/?~！@#￥%……&;*（）——+|{}【】‘；：”“’。，、？|-]
                        Log.d("TAG", s + " --- " + "onNoDoubleClick: ---------->" + word.getContent().replaceAll("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】；：”“’。，、？|-]", ""));
                        if (s.equals(word.getContent().replaceAll("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……& ;*（）——+|{}【】；：”“’。，、？|-]", "").replace("‘", "'"))) {
                            rightPron.setText("正确发音: " + s + " [" + word.getPron2() + "]");
                            userPron.setText("你的发音: " + s + " [" + word.getUserPron2() + "]");
                            requestWord(word.getContent().replaceAll("[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】；：”“’。，、？|-]", ""), word.getUserPron(), word.getPron());
                            curWordStr = word.getContent();
                            curWordIndex = word.getIndex();
                            break;
                        }
                    }
                }


            });
            // 设置关闭弹框监听
            correctClose.setOnClickListener(v -> alertDialog.cancel());
            // 设置播放单词音频监听
            playWord.setOnClickListener(view -> {
                if (wordPlayer != null)
                    wordPlayer.start();
            });
            // 设置播放原句子音频监听
            playOri.setOnClickListener(v -> {
                if (wordPlayer != null)
                    wordPlayer.start();
//                mOnItemClickListener.onItemClick(v, "5", position);
            });
            // 点击录音设置监听
            record.setOnClickListener(v -> {
                if (isRecording) {
                    stopRec(data);
                } else {
                    startCorRec();
                }
            });
            playRec.setOnClickListener(v -> {
                playWordRec();
            });
            goToMic.setOnClickListener(v -> {
                mContext.startActivity(MobClassActivity.buildIntent(mContext, 3, true, null));
            });
        }
    }

    private void playWordRec() {
        if (recPlayer != null) {
            recPlayer.release();
            recPlayer = null;
        }
        recPlayer = new MediaPlayer();
        try {
            if (!"".equals(curWordUserAudio)) {
                recPlayer.setDataSource("http://userspeech.iyuba.cn/voa/" + curWordUserAudio);
            } else {
                recPlayer.setDataSource(recFilePath);
            }
            recPlayer.prepare();
            System.out.println("===============录音长度" + recPlayer.getDuration());
            recPlayer.start();
        } catch (IOException e) {
            Log.e("TAG", "playWordRec: ", e);
        }
    }

    /**
     * 纠音点击单词发送的请求
     *
     * @param q        查询的单词
     * @param userPron 该单词用户的发音
     * @param oriPron  该单词正确的发音
     */
    private void requestWord(String q, String userPron, String oriPron) {
        String urlStr = null;
        try {
            urlStr = "http://word.iyuba.cn/words/apiWordAi.jsp?" +
                    "q=" + q.replaceAll("‘", "'") +
                    "&user_pron=" + URLEncoder.encode(userPron, "UTF-8") +
                    "&ori_pron=" + URLEncoder.encode(oriPron, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .get()
                .url(urlStr)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = REQUEST_WORD_FAILED;
                mHandle.sendMessage(msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonStr = Objects.requireNonNull(response.body()).string();
                Log.d("TAG", "onResponse: ------->" + jsonStr);
                JSONObject jo = JSONObject.parseObject(jsonStr);
                Message msg = new Message();
                msg.what = REQUEST_WORD_FINISH;
                msg.obj = jo;
                mHandle.sendMessage(msg);
            }
        });
    }

    /**
     * 纠音 - 开始录音
     */
    private void startCorRec() {
        isRecording = true;
        recordTv.setText("点击结束");
//        requestPermission();
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(Objects.requireNonNull(mContext), Manifest.permission.RECORD_AUDIO)) {
            ToastUtil.showToast(mContext, "请打开录音权限继续");
        } else {
            ToastUtil.showToast(mContext, "开始录音，再次点击结束录音并打分");
//            manager2.setHandler(mHandle);
            recordManager2.startRecord(String.valueOf(System.currentTimeMillis()));
//            isEvaluating = true;
            filePath = recordManager2.file.getPath();
        }
    }

    // 停止录音
    void stopRec(EvaluateBean data) {
        isRecording = false;
        recordTv.setText("点击开始");
        recordManager2.stopRecord();
        recFilePath = recordManager2.file.getPath();
        if (!"".equals(recFilePath)) {
            // 成功录音则调用评测接口
            doEval(data);
        }
    }

    // 调用评测接口
    void doEval(EvaluateBean data) {
        ToastUtil.showToast(mContext, "评测中...");
        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
        File file = new File(recFilePath);
        RequestBody fileBody = RequestBody.create(type, file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("flg", "2")
                .addFormDataPart("IdIndex", data.getIdIndex())
                .addFormDataPart("paraId", data.getParaId())
                .addFormDataPart("appId", Constant.APPID)
                .addFormDataPart("newsId", String.valueOf(data.voaId))
                .addFormDataPart("userId", "0")
                .addFormDataPart("sentence", curWordStr)
                .addFormDataPart("wordId", String.valueOf(curWordIndex))
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("type", "bbc")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                //.url("http://userspeech.iyuba.cn/test/eval/")
                .url(WebConstant.HTTP_SPEECH_ALL + "/test/ai/")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // ToastUtil.showToast(getContext(), "网络错误，请稍后重试。");
                System.out.println("评测接口调用失败");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("传送发生错误！" + response);
                } else {
                    JSONObject jo = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                    Log.d("TAG", "onResponse: eval word response --------->" + jo.toString());
                    if (jo.getInteger("result") == 1) {
                        JSONObject joData = jo.getJSONObject("data");
                        String wordsStr = joData.getString("words");
                        JSONArray wordsJo = JSON.parseArray(wordsStr);
                        for (Object obj : wordsJo) {
                            JSONObject tempJo = JSONObject.parseObject(obj.toString());
                            Message msg = new Message();
                            msg.what = EVAL_WORD_FINISH;
                            msg.arg1 = joData.getInteger("scores");
                            msg.obj = tempJo;
                            mHandle.sendMessage(msg);
                            Message msg2 = new Message();
                            msg2.what = EVAL_WORD_FINISH;
                            msg2.obj = joData;
                            mHandle.sendMessage(msg2);
                        }
                    }

                }
            }
        });
    }


    public class EvaluateTextViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSenIndex;
        private TextView tvSenEn;
        private View tvCorrect;
        private Button word_correct_commit;
        private RoundProgressBar rpbSenPlay;
        private RoundProgressBar rpbSenIRead;
        private ImageView ivSenReadPlay;
        private RoundProgressBar senReadPlaying;//录音播放
        private ImageView ivSenReadSend;
        private ImageView ivSenReadCollect;
        private TextView senReadResult;
        private LinearLayout llChoose;
        private EvaluateBean evaluateBean;


        private int positions;
        private boolean isUploadVoice = false;//是否正在发送评测录音
        private int allTime;
        private int playTime;
        private int maxTime;
        private boolean isStop;

        private Text text;


        public EvaluateTextViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSenIndex = itemView.findViewById(R.id.tv_index);
            tvSenEn = itemView.findViewById(R.id.tv_sen_text);

            rpbSenPlay = itemView.findViewById(R.id.rpb_sen_play);
            rpbSenIRead = itemView.findViewById(R.id.rpb_sen_i_read);
            ivSenReadPlay = itemView.findViewById(R.id.iv_sen_read_play);//录音播放
            senReadPlaying = itemView.findViewById(R.id.sen_read_playing);
            ivSenReadSend = itemView.findViewById(R.id.iv_sen_read_send);
            ivSenReadCollect = itemView.findViewById(R.id.iv_sen_read_collect);
            senReadResult = itemView.findViewById(R.id.sen_read_result);
            llChoose = itemView.findViewById(R.id.ll_choose);
            tvCorrect = itemView.findViewById(R.id.tv_correct);
//            word_correct_commit = itemView.findViewById(R.id.word_correct_commit);
        }

        public void setData(Text text, int pos) {

            this.positions = pos;
            this.text = text;

            if (handler != null) {
                handler.removeMessages(1);//有的时候能关掉，有的时候不能
                handler.removeMessages(0);
                //handler.removeCallbacksAndMessages(null);
                LogUtil.e("进度移除：" + positions);
            }

            if (!text.isChoose) {
                playTime = 0;
                maxTime = 0;
                isStop = true;
            } else {
                isStop = false;
            }
            tvSenEn.setText(text.Sentence);
            tvSenIndex.setText(String.valueOf(positions + 1));

            if (!text.isPlay) {
                handler.removeMessages(1);
                handler.sendEmptyMessage(2);//停止播放（动画）
            }

            if (text.isEvaluate()) {
                handler.removeMessages(3);//关闭录音播放动画
                senReadPlaying.setProgress(0); //录音播放动画进度归零
                senReadPlaying.setVisibility(View.INVISIBLE);//隐藏进度条
                ArrayList<EvaluateBean> evaluateBeans = tedbHelper.getEvaluate(text.TitleNum, text.SenIndex);
                if (evaluateBeans.size() > 0) {
                    evaluateBean = evaluateBeans.get(0);//text.getBean();
                    text.setBean(evaluateBean);
                    //写入得分  这个数据在写入数据库的时候已经转换为整数
                    setReadScoreViewContent(Integer.valueOf(evaluateBean.total_score));//(int) (Double.valueOf(evaluateBean.getTotal_score()) * 20)
                    //字体变色   //拼接之后的数据

                    if (evaluateBean.textScore != null) {
                        String[] floats = evaluateBean.textScore.split("-");
                        LogUtil.e("======" + evaluateBean.getSentence());
                        SpannableStringBuilder readResult;
                        if (text.Sentence != null && !text.Sentence.equals("")) {
                            readResult = ResultParse.getSenResultLocal(floats, text.Sentence);
                            tvSenEn.setText(readResult);//变色写入
                        }
                    }
                    tvCorrect.setVisibility(View.VISIBLE);//纠音
                } else {

                    tvCorrect.setVisibility(View.GONE);//纠音
                }


                llChoose.setVisibility(View.VISIBLE);
                ivSenReadPlay.setVisibility(View.VISIBLE);//播放录音

                ivSenReadSend.setVisibility(View.VISIBLE);//发送评论区
                senReadResult.setVisibility(View.VISIBLE);//得分  //如果当前item llChoose布局隐藏，这些显示都不会生效

                if (text.shuoShuoId != null && !text.shuoShuoId.equals("")) {
                    ivSenReadCollect.setVisibility(View.VISIBLE);//分享
                } else {
                    ivSenReadCollect.setVisibility(View.GONE);//分享
                }

            } else {
                ivSenReadPlay.setVisibility(View.GONE);//播放录音
                ivSenReadSend.setVisibility(View.GONE);//发送评论区
                senReadResult.setVisibility(View.GONE);//得分 表情
                ivSenReadCollect.setVisibility(View.GONE);//分享
                tvCorrect.setVisibility(View.GONE);//纠音
            }

            if (text.isChoose) {
                isStop = false;
                llChoose.setVisibility(View.VISIBLE);
            } else {
                isStop = true;
                llChoose.setVisibility(View.GONE);
            }

            boolean noChoose = true;
            for (Text text1 : textList) {
                if (text1.isChoose) {
                    noChoose = false;
                }
            }
            if (noChoose && positions == 0) {
                llChoose.setVisibility(View.VISIBLE);
                isStop = false;
            }

            if (!isEvaluating) {
                rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮
                rpbSenIRead.setProgress(0);
                rpbSenPlay.setEnabled(true);
                ivSenReadPlay.setEnabled(true);
                senReadPlaying.setEnabled(true);
            }

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完成
                    rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                }
            });


            //设置评测单词
            List<EvaluateBean.WordsBean> wordsBeanList = tedbHelper.getEvalWord(text.TitleNum + "", text.SenIndex + "");
            if (evaluateBean != null) {

                evaluateBean.setWords(wordsBeanList);
            }
        }

        public void setListener(final int position) {
            tvCorrect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showCorrect(text, evaluateBean);
                }
            });

          /*  word_correct_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCorrect(evaluateBean);
                }
            });*/

            //录音播放进度进度
            senReadPlaying.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    evaluatePlayer.pause();
                    evaluatePlayer.seekTo(0);
                    senReadPlaying.setProgress(0);
                    senReadPlaying.setVisibility(View.INVISIBLE);
                    ivSenReadPlay.setVisibility(View.VISIBLE);
                    handler.removeMessages(3);
                    playTime = 0;

                    if (fragment.player.isPlaying()) {
                        fragment.player.pause();
                        fragment.player.seekTo(0);
                    }
                }
            });
            //录音播放按钮
            ivSenReadPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeMessages(1);
                    handler.sendEmptyMessage(2);
                    handler.sendEmptyMessage(0); //录音播放按钮

                    if (fragment.player.isPlaying()) {
                        fragment.player.pause();
                        fragment.player.seekTo(0);
                    }

                    try {
                        evaluatePlayer.reset();
                        evaluatePlayer.setDataSource(text.getBean().getURL());//"http://voa.iyuba.cn/voa/" +
                        LogUtil.e(text.getBean().getURL());//"http://voa.iyuba.cn/voa/" +
                        evaluatePlayer.prepareAsync();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //初始化完成后播放
                    evaluatePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            playTime = 0;
                            evaluatePlayUI();
                        }
                    });

                    evaluatePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //播放完成
                        }
                    });

                    evaluatePlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            LogUtil.e("播放器播放失败" + what + " " + extra);
                            mp.stop();
                            return false;
                        }
                    });
                }
            });

            rpbSenIRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                        new androidx.appcompat.app.AlertDialog.Builder(mContext)
                                .setTitle("权限说明")
                                .setMessage("录音权限：评测功能需要录音")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        fragment.checkEvaluatePermission();
                                    }
                                }).show();

                        return;
                    }
                    //评测按钮点击
                    if (player.isPlaying()) {
                        handler.removeMessages(1);
                        handler.sendEmptyMessage(2);
                        handler.sendEmptyMessage(0);//评测按钮点击
                    }

                    if (evaluatePlayer.isPlaying()) {
                        evaluatePlayer.pause();
                        evaluatePlayer.seekTo(0);
                        senReadPlaying.setProgress(0);
                        senReadPlaying.setVisibility(View.INVISIBLE);
                        ivSenReadPlay.setVisibility(View.VISIBLE);
                        handler.removeMessages(3);
                    }

                    //获取次数
                    evalSp = mContext.getSharedPreferences("EVAL", Context.MODE_PRIVATE);
                    if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {//未登录

                        int evalCount = evalSp.getInt(titleNum, 0);
                        if (evalCount >= 3) {

                            Toast.makeText(mContext, "非会员用户只能评测三次", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {//登录且不是会员

                        if (AccountManagerLib.Instace(mContext).getVipStringStatus().equals("0")) {

                            int evalCount = evalSp.getInt(titleNum, 0);
                            if (evalCount >= 3) {

                                Toast.makeText(mContext, "非会员用户只能评测三次", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    if (!isEvaluating) {

                        evaluatingPosition = getBindingAdapterPosition();
                        rpbSenPlay.setEnabled(false);
                        ivSenReadPlay.setEnabled(false);
                        senReadPlaying.setEnabled(false);
                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_stop);
                        setPlayTime(position);//重新设置播放的时间
                        //开始录音
                        recordManager2.setHandler(handler);
                        recordManager2.startRecord(getBindingAdapterPosition() + text.TitleNum + "");
                        isEvaluating = true;
                        filePath = recordManager2.file.getPath();

                        int playTime = maxTime;
                        LogUtil.e("录音 持续时间：" + playTime * 1.7 + "原始时间" + maxTime);
                        if (playTime < 1) {     //排除时间为0的情况
                            playTime = 20000;
                            LogUtil.e("录音 持续时间：异常，修改为" + playTime);
                        }
                        Message msg = new Message();
                        msg.what = 11;
                        msg.arg1 = getBindingAdapterPosition();
                        handler.sendMessageDelayed(msg, (int) (playTime * 1.7));//延时1.7倍关闭·
                    } else if (evaluatingPosition == getBindingAdapterPosition()) {//处理一个没操作完成，又操作另一个情况

                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);
                        CustomToast.showToast(mContext, "正在评测！");
                        recordManager2.stopRecord();
                        Map<String, String> map = buildMap(text);
                        post.post(map, filePath, handler, position, text.TitleNum);
                    }
                }


            });

            ivSenReadSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //单句发布排行
                    readSend(text);
                }
            });

            ivSenReadCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //单句评测分享
                    toShare(text);
                }
            });

            //原文播放
            rpbSenPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (evaluatePlayer.isPlaying()) {
                        evaluatePlayer.pause();
                        evaluatePlayer.seekTo(0);
                        senReadPlaying.setProgress(0);
                        senReadPlaying.setVisibility(View.INVISIBLE);
                        ivSenReadPlay.setVisibility(View.VISIBLE);
                        handler.removeMessages(3);
                    }

                    if (player.isPlaying()) {
                        handler.removeMessages(1);
                        rpbSenPlay.setProgress(0);
                        handler.removeMessages(0);
                        handler.sendEmptyMessage(0);
                        rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                    } /*else if (text.isPlay) {
                        player.start();
                        rpbSenPlay.setBackgroundResource(R.drawable.sen_stop);
                        handler.sendEmptyMessage(1);//恢复
                    }*/ else {
                        handler.removeMessages(1);
                        startPlay(position);
                    }
                }
            });

            itemView.setOnClickListener(v -> {

                if (!text.isChoose) {
                    text.isChoose = true;
                    llChoose.setVisibility(View.VISIBLE);
                    for (int i = 0; i < textList.size(); i++) {
                        if (i != position) {
                            textList.get(i).isChoose = false;
                        }
                        textList.get(i).isPlay = false;
                    }
                    if (evaluatePlayer.isPlaying()) {
                        evaluatePlayer.pause();
                        evaluatePlayer.seekTo(0);
                    }

                    if (player != null && player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                    maxTime = 0;
                    playTime = 0;
                    handler.removeMessages(1);
                    LogUtil.e("进度点击！应该移除" + position);
                    if (isEvaluating) {
                    /*    Message msg = new Message();
                        msg.what = 11;
//                        msg.arg1 = evaluatingPosition;
                        msg.arg1 = getEvaluatingPosition();
                        handler.sendMessage(msg);//录音终止，开始评测*/
                        isEvaluating = false;
                        recordManager2.stopRecord();
                    }
                    notifyDataSetChanged();
                }
            });
        }


        private Map<String, String> buildMap(Text text) {

            String userid = "0";
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                userid = AccountManagerLib.Instace(mContext).userId;
            }

            Map<String, String> textParams = new HashMap<String, String>();
            textParams.put("type", "toeic");
            textParams.put("userId", userid);
            textParams.put("newsId", text.TitleNum + "");
            textParams.put("paraId", text.TitleNum + "");
            textParams.put("IdIndex", text.SenIndex + "");
            textParams.put("sentence", text.Sentence + "");
            textParams.put("wordId", 0 + "");
            textParams.put("flg", 0 + "0");
            textParams.put("appId", Constant.APPID);
            String sentence = null;
            try {
                sentence = URLEncoder.encode(text.Sentence, "UTF-8")
                        .replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            textParams.put("sentence", sentence);
            return textParams;
        }

        //录音播放 UI变化
        private void evaluatePlayUI() {
            senReadPlaying.setVisibility(View.VISIBLE);
            senReadPlaying.setCricleProgressColor(0xff66a6e8);
            ivSenReadPlay.setVisibility(View.GONE);
            LogUtil.e("进度evaluatePlayer.getDuration" + evaluatePlayer.getDuration());
            if (evaluatePlayer.getDuration() > 1000000) {
                allTime = evaluatePlayer.getDuration() / 1000000;
            } else {
                allTime = evaluatePlayer.getDuration();
            }
            senReadPlaying.setMax(allTime);
            handler.sendEmptyMessage(3);//开启录音播放动画
        }

        private void startPlay(int position) {
            Text text = textList.get(position);
            if (isPrepared) {
                setPlayTime(position);//设置播放的时间

                player.seekTo(text.Timing * 1000);
                player.start();
                text.isPlay = true;
                rpbSenPlay.setBackgroundResource(R.drawable.sen_stop);

                rpbSenPlay.setMax(maxTime);
                rpbSenPlay.setCricleProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
                playTime = 0;
                handler.removeMessages(1);//清除以前的播放数据 playTime maxTime
                handler.sendEmptyMessage(1);//根据动画来控制停止播放
                handler.sendEmptyMessageDelayed(0, maxTime);
            }
        }

        private void setPlayTime(int position) {
            Text text = textList.get(position);
            if (position < textList.size() - 1) {
                maxTime = (textList.get(position + 1).Timing - text.Timing) * 1000;
                LogUtil.e("播发开始时间：" + text.Timing * 1000 + "播放结束时间：" + textList.get(position + 1).Timing * 1000);
            } else {
                maxTime = player.getDuration() - text.Timing * 1000;
            }

            if (maxTime < 0) {
                CustomToast.showToast(mContext, "播放时间异常" + maxTime);
                maxTime = 0;
            }
        }

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                switch (msg.what) {
                    case 0:
                        if (player != null && player.isPlaying()) {
                            player.pause();
                            player.seekTo(0);
                            LogUtil.e("播放停止");
                        }
                        break;
                    case 1://原文播放进度条
                        rpbSenPlay.setProgress(playTime);
                        if (maxTime != 0 && !isStop)
                            updateProgressThread.run();
                        break;
                    case 2://暂停播放动画
                        rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                        if (positions < textList.size())
                            textList.get(positions).isPlay = false;
                        playTime = 0;
                        maxTime = 0;
                        rpbSenPlay.setProgress(playTime);
                        break;

                    case 3://录音播放进度条
                        senReadPlaying.setProgress(playTime);
                        updateProgressReadThread.run();
                        break;

                    case 4:
                        handler.removeMessages(1);
                        LogUtil.e("进度删除+++ 没删掉fuck");
                        break;

                    case 6:
                        CustomToast.showToast(mContext, "评测成功");
                        LogUtil.e("评测成功" + msg.obj);
                        evaluateBean = (EvaluateBean) msg.obj;

                        //存储评测数据
                        tedbHelper.saveEvalWord(evaluateBean);

                        int evaluatePosition = evaluateBean.getPosition();
                        textList.get(evaluatePosition).setBean(evaluateBean);
                        //评测成功后的操作 字体变色 分数显示
                        Text text = textList.get(evaluatePosition);
                        if (!text.isEvaluate()) {
                            tedbHelper.setIsEvaluate(text.TitleNum, text.SenIndex);//数据库 标识
                            tedbHelper.setEvaluate(text.TitleNum,
                                    text.SenIndex, (int) (Double.valueOf(evaluateBean.getTotal_score()) * 20),
                                    evaluateBean.getWords().size(), evaluateBean.getTextScore(),
                                    evaluateBean.URL);
                        } else {
                            String textScore = evaluateBean.getTextScore().substring(0, evaluateBean.getTextScore().length() - 1);
                            tedbHelper.updateEvaluate(text.TitleNum,
                                    text.SenIndex, (int) (Double.valueOf(evaluateBean.getTotal_score()) * 20),
                                    evaluateBean.getWords().size(), textScore,
                                    evaluateBean.URL);
                        }
                        text.setEvaluate(true);
                        fragment.tvMergeAndPlay.setText("合成");
                        fragment.totalScore = 0;
                        playTime = 0;
//                        tvCorrect.setVisibility(View.VISIBLE);
                        rpbSenIRead.setProgress(0);
                        rpbSenPlay.setEnabled(true);
                        ivSenReadPlay.setEnabled(true);
                        senReadPlaying.setEnabled(true);
                        text.isEvaluating = false;//结束评测
                        isEvaluating = false;
                        setEvaluatingPosition(-1);
                        notifyItemChanged(evaluatePosition);

                        if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {

                            evalSp = mContext.getSharedPreferences("EVAL", Context.MODE_PRIVATE);
                            int evalCount = evalSp.getInt(titleNum, 0);
                            SharedPreferences.Editor editor = evalSp.edit();
                            editor.putInt(titleNum, ++evalCount);
                            editor.apply();
                        }

                        handler.removeMessages(11);
                        break;
                    case 104:
                        //录音音量动画
                        if (!isEvaluating) {
                            return false;
                        }
                        rpbSenIRead.setMax(10000);
                        rpbSenIRead.setCricleProgressColor(Color.GREEN);
                        rpbSenIRead.setProgress(msg.arg1 * 100);
                        LogUtil.e("录音104 ，音量" + msg.arg1 * 100);

                        notifyItemChanged(positions);
                        break;

                    case 12:
                        if (msg.arg1 == 404) {
                            CustomToast.showToast(mContext, "评测失败 未连接到服务器，请检查网络！");
                        } else {
                            CustomToast.showToast(mContext, "评测失败");
                        }
                        textList.get(positions).isEvaluating = false;//结束评测
                        rpbSenPlay.setEnabled(true);
                        LogUtil.e("评测失败");
                        isEvaluating = false;
                        break;

                    case 10:
                        if (isEvaluating) {
                            CustomToast.showToast(mContext, "评测中");
                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);
                        }
                        break;

                    case 11:
                        //录音终止 进入评测
//                        if (manager.isRecording) {
//                            dismissDia();//关闭录音
//                            handler.removeMessages(11);
//                        }
                        if (isEvaluating) {

                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);
                            recordManager2.stopRecord();
                            Text text1 = textList.get(msg.arg1);
                            Map<String, String> map = buildMap(text1);
                            post.post(map, filePath, handler, msg.arg1, text1.TitleNum);
                            handler.removeMessages(11);
                        }
                        break;

                    case 14:
                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮恢复初始蓝色
                        rpbSenIRead.setProgress(0);
                        break;
                    case 16:
                        String addScore = String.valueOf(msg.arg1);
                        if (addScore.equals("5")) {
                            String mg = "语音成功发送至排行榜，恭喜您获得了" + addScore + "分";
                            CustomToast.showToast(mContext, mg);
                        } else {
                            String mg = "语音成功发送至排行榜";
                            CustomToast.showToast(mContext, mg);
                        }
                        ivSenReadCollect.setVisibility(View.VISIBLE);//分享
                        break;
                    case 17:
                        ToastUtil.showToast(mContext, (String) msg.obj);
                        break;
                    default:
                }
                return false;
            }
        });


        // 将要执行的操作写在线程对象的run方法当中
        Runnable updateProgressThread = new Runnable() {
            public void run() {
                // 播放音频时，计时
                if (playTime <= maxTime && maxTime != 0) {
                    int d = maxTime / 150;
                    playTime = playTime + d;
                    LogUtil.e("进度" + playTime + " 总时间" + maxTime);
                    if (!isStop)
                        handler.sendEmptyMessageDelayed(1, maxTime / 150);
                } else if (maxTime != 0) {
                    LogUtil.e("进度" + playTime + " 总时间" + maxTime + "完成播放，停止");
                    // if (!isStop)
                    handler.sendEmptyMessage(0);//播放结束，停止播放
                    handler.sendEmptyMessage(2);//暂停动画
                    playTime = 0;
                    maxTime = 0;
                } else {
                    CustomToast.showToast(mContext, "进度时间maxTime异常" + maxTime);
                }
            }
        };

        Runnable updateProgressReadThread = new Runnable() {
            public void run() {
                if (playTime <= allTime && allTime != 0) {
                    int d = (int) (allTime / 150);
                    playTime = playTime + d;
                    LogUtil.e("进度" + playTime + " 总时间" + allTime);
                    handler.sendEmptyMessageDelayed(3, allTime / 150);
                } else {
                    LogUtil.e("进度" + playTime + " 总时间" + allTime + "完成播放，停止");
                    senReadPlaying.setVisibility(View.INVISIBLE);
                    senReadPlaying.setProgress(0);
                    allTime = 0;
                    playTime = 0;
                    ivSenReadPlay.setVisibility(View.VISIBLE);
                }
            }
        };

        //得分
        private void setReadScoreViewContent(int score) {
            if (score < 50) {
                senReadResult.setBackgroundResource(R.drawable.sen_score_lower60);
                senReadResult.setText("");
            } else if (score > 80) {
                senReadResult.setText(String.valueOf(score));
                senReadResult.setBackgroundResource(R.drawable.sen_score_higher_80);
            } else {
                senReadResult.setText(String.valueOf(score));
                senReadResult.setBackgroundResource(R.drawable.sen_score_60_80);
            }
        }

        private void readSend(final Text text) {
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                if (isUploadVoice) {
                    CustomToast.showToast(mContext, "评论发送中，请不要重复提交");
                } else {
                    //mWaitingDialog.show();
                    Thread threadsend = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                isUploadVoice = true;
                                String response = "";
                                response = UtilPostFile  //单句语音发送
                                        .post("http://voa." + Constant.IYBHttpHead + "/voa/UnicomApi?topic=toeic"
                                                + "&platform=android&format=json&protocol=60002"
                                                + "&userid="
                                                + AccountManagerLib
                                                .Instace(mContext).userId
                                                + "&voaid="
                                                + text.TitleNum
                                                + "&idIndex=" + text.SenIndex
                                                + "&score="
                                                //+ searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
                                                + text.getBean().getTotal_score()
                                                + "&shuoshuotype=2"
                                                + "&content="
                                                + text.getBean().getSendUrl());
                                LogUtil.e("sendRank" + response);
                                LogUtil.e("sendRank：内容" + "http://voa." + Constant.IYBHttpHead + "/voa/UnicomApi?topic=toeic"//
                                        + "&platform=android&format=json&protocol=60002"
                                        + "&userid="
                                        + AccountManagerLib
                                        .Instace(mContext).userId
                                        + "&voaid="
                                        + text.TitleNum
                                        + "&idIndex=" + text.SenIndex
                                        + "&score="
                                        //+ searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
                                        + text.getBean().getTotal_score()
                                        + "&shuoshuotype=2"
                                        + "&content="
                                        + text.getBean().getSendUrl());


                                org.json.JSONObject jsonObjectRoot;
                                jsonObjectRoot = new org.json.JSONObject(response);
                                String result = jsonObjectRoot
                                        .getInt("ResultCode") + "";
                                String addscore = jsonObjectRoot
                                        .getString("AddScore");
                                if (result.equals("501") || result.equals("1")) {
                                    String suosuoId = String.valueOf(jsonObjectRoot.getInt("ShuoshuoId"));
                                    text.shuoShuoId = suosuoId;
                                    Message msg = handler
                                            .obtainMessage();
                                    msg.what = 16;
                                    msg.arg1 = Integer
                                            .parseInt(addscore);
                                    handler.sendMessage(msg);
                                    isUploadVoice = false;
                                }
                            } catch (Exception e) {
                                isUploadVoice = false;
                                LogUtil.e("sendRank 发布异常" + e);
                                Message msg = new Message();
                                msg.what = 17;
                                msg.obj = "发布异常!" + e;
                                handler.sendMessage(msg);
                                e.printStackTrace();
                            }
                        }
                    });
                    threadsend.start();
                }
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext, LoginActivity.class);
                mContext.startActivity(intent);
            }
        }

        private void toShare(final Text text) {
            PlatformActionListener pal = new PlatformActionListener() {

                @Override
                public void onError(Platform platform, int arg1,
                                    Throwable arg2) {
                    CustomToast.showToast(mContext, "分享失败");
                }

                @Override
                public void onComplete(Platform platform, int arg1,
                                       HashMap<String, Object> arg2) {
                    int srid = 46;
                    String name = platform.getName();
                    if (name.equals(QQ.NAME)
                            || name.equals(Wechat.NAME)
                            || name.equals(WechatFavorite.NAME)) {
                        srid = 45;
                    } else if (name.equals(QZone.NAME)
                            || name.equals(WechatMoments.NAME)
                            || name.equals(SinaWeibo.NAME)) {
                        srid = 46;
                    }
                    if (AccountManagerLib.Instace(mContext)
                            .checkUserLogin()) {
                        RequestCallBack rc = new RequestCallBack() {

                            @Override
                            public void requestResult(Request result) {
                                AddCreditsRequestNew rq = (AddCreditsRequestNew) result;
                                if (rq.isShareFirstlySuccess()) {
                                    String msg = "分享成功，增加了"
                                            + rq.addCredit + "积分，共有"
                                            + rq.totalCredit + "积分";
                                    CustomToast.showToast(mContext,
                                            msg);
                                } else if (rq.isShareRepeatlySuccess()) {
                                    CustomToast.showToast(mContext,
                                            "分享成功");
                                }
                            }
                        };
                        int uid = Integer.parseInt(AccountManagerLib
                                .Instace(mContext).userId);
                        AddCreditsRequestNew rq = new AddCreditsRequestNew(
                                uid,
                                Integer.valueOf(text.TitleName),
                                srid, rc);
                        RequestQueue queue = Volley
                                .newRequestQueue(mContext);
                        queue.add(rq);
                    }
                }

                @Override
                public void onCancel(Platform platform, int arg1) {
                    CustomToast.showToast(mContext, "分享已取消");
                }
            };
            String name = Constant.APPName;
            String userName = AccountManagerLib.Instace(mContext).userName;
            String shareTitle = "[" + userName + "]"
                    + "在" + name + "中获得了"
                    + text.getBean().total_score
                    + "分";
            String shortText = text.Sentence;
            String ArticleShareUrl = "http://voa." + Constant.IYBHttpHead + "/voa/play.jsp?id=" + text.shuoShuoId + "&apptype=toeic";
            fragment.showShare(mContext, shareTitle, shortText, ArticleShareUrl,
                    Constant.AppIcon, "很不错的应用，大家快来使用呀！", Constant.APPName,
                    ArticleShareUrl, pal);
            LogUtil.e("shareShow" + shareTitle);
            LogUtil.e("shareShow" + shortText);
            LogUtil.e("shareShow" + ArticleShareUrl);
        }
    }


    //判断文件是否存在
    private boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
