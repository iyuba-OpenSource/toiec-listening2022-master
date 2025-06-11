package com.iyuba.toeiclistening.activity.test;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.EvaSendBean;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.api.ApiRetrofit;
import com.iyuba.toeiclistening.api.ApiService;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.event.EvaluatePlayEvent;
import com.iyuba.toeiclistening.event.ResetPlayDataEvent;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 评测页面
 */
@RuntimePermissions
public class ReadEvaluateFragment extends Fragment {


    RecyclerView recycler;
    TextView textCurrent;
    TextView textTotal;
    SeekBar seekBar;
    TextView tvMergeAndPlay;
    TextView tvScore;

    TextView tvPublishAndShare;

    private Context mContext;
    private TitleInfo curTitleInfo;          //当前题的信息
    private ArrayList<Text> textList;        //Text实体的列表
    private String composeVoicePath;         //合成后的地址
    private int senceNum;
    public int totalScore;               //totalScore 总分数
    private ReadEvaluateAdapter readEvaluateAdapter;
    public MediaPlayer player;
    private int playTime, maxTime;
    private boolean isSendSound;//是否发送中
    private String shuoshuoId;

    public static ReadEvaluateFragment newInstance(TitleInfo curTitleInfo) {

        ReadEvaluateFragment readEvaluateFragment = new ReadEvaluateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("TITLE_INFO", curTitleInfo);
        readEvaluateFragment.setArguments(bundle);
        return readEvaluateFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        player = new MediaPlayer();
        EventBus.getDefault().register(this);


        if (getArguments() != null) {

            Bundle bundle = getArguments();
            curTitleInfo = (TitleInfo) bundle.getSerializable("TITLE_INFO");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read_evaluate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView(view);
        seekBar.setEnabled(false);
        initListener();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (readEvaluateAdapter.player.isPlaying()) {
            readEvaluateAdapter.player.pause();
            readEvaluateAdapter.player.stop();
            readEvaluateAdapter.player.release();
            readEvaluateAdapter.player = null;
        }

        if (readEvaluateAdapter.evaluatePlayer != null && readEvaluateAdapter.evaluatePlayer.isPlaying()) {
            readEvaluateAdapter.evaluatePlayer.pause();
            readEvaluateAdapter.evaluatePlayer.stop();
            readEvaluateAdapter.evaluatePlayer.release();
            readEvaluateAdapter.evaluatePlayer = null;
        }

        if (player != null) {
            player.pause();
            player.stop();
            player.release();
            player = null;
        }

        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        textList = ((TestDetailActivity) requireActivity()).textList;//DataManager.Instance().textList;
    }

    private void initView(View view) {

        recycler = view.findViewById(R.id.recycler);
        textCurrent = view.findViewById(R.id.text_current);
        textTotal = view.findViewById(R.id.text_total);
        seekBar = view.findViewById(R.id.seek_bar);
        tvMergeAndPlay = view.findViewById(R.id.tv_merge_and_play);
        tvScore = view.findViewById(R.id.tv_read_score);
        tvPublishAndShare = view.findViewById(R.id.tv_publish_and_share);


        //创建LinearLayoutManager 对象
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        //设置RecyclerView 布局
        recycler.setLayoutManager(mLayoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        ((SimpleItemAnimator) Objects.requireNonNull(recycler.getItemAnimator())).setSupportsChangeAnimations(false);
        //设置Adapter

//        curTitleInfo = ((TestDetailActivity) requireActivity()).curTitleInfo;
        readEvaluateAdapter = new ReadEvaluateAdapter(mContext, this, curTitleInfo.TitleNum + "");
        readEvaluateAdapter.setData(textList, curTitleInfo);
        recycler.setAdapter(readEvaluateAdapter);
    }

    private void initListener() {
        tvMergeAndPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvMergeAndPlay.getText().toString().equals("合成")) {
                    ArrayList<String> web_path_list = new ArrayList<>();
                    //initData();
                    totalScore = 0;
                    for (Text text : textList) {
                        if (text.getBean() != null && !text.getBean().URL.equals("")) {
                            web_path_list.add(text.getBean().URL);
                            try {
                                int score = (int) Float.parseFloat(text.getBean().total_score);
                                totalScore = totalScore + score;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    senceNum = web_path_list.size();
                    if (senceNum >= 2) {
                        requestComposeVoice(web_path_list);
                    } else {
                        CustomToast.showToast(mContext, "至少读两句方可合成！");
                    }
                } else {
                    if (!player.isPlaying()) {
                        player.start();
                        //maxTime = player.getDuration();
                        //textTotal.setText(getDurationInFormat(maxTime));
                        //seekBar.setMax(maxTime);
                        handler.sendEmptyMessage(2);
                    }
                }
            }


        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                maxTime = player.getDuration();
                LogUtil.e("maxTime =" + maxTime);
                textTotal.setText(getDurationInFormat(maxTime));
                seekBar.setMax(maxTime);
                LogUtil.e("总时间：" + getDurationInFormat(maxTime) + " maxTime" + maxTime);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("播放器错误" + what + " " + extra);
                return false;
            }
        });


        tvPublishAndShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvMergeAndPlay.getText().toString().equals("合成")) {
                    CustomToast.showToast(mContext, "请先合成！");
                    return;
                } else if (tvPublishAndShare.getText().toString().equals("发送")) {
                    //开始发送合成语音
                    sendSound();
                } else if (tvPublishAndShare.getText().toString().equals("分享")) {
                    //分享合成语句
                    showShareSound();
                }
            }
        });
    }


    private void setPlayer() {
        //播放录音
        try {
            player.reset();
            player.setDataSource("http://voa." + WebConstant.CN_SUFFIX + "voa/" + composeVoicePath);
            LogUtil.e("合成音频" + "http://voa." + WebConstant.CN_SUFFIX + "voa/" + composeVoicePath);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, "合成失败！");
                    break;
                case 1:
                    CustomToast.showToast(mContext, "合成成功！");
                    tvScore.setText(totalScore / senceNum + "");
                    tvScore.setVisibility(View.VISIBLE);
                    tvMergeAndPlay.setText("试听");
                    setPlayer();
                    break;
                case 2:
                    LogUtil.e("playtime=" + playTime + "maxTime=" + maxTime);
                    int d = maxTime / 150;
                    if (playTime < maxTime) {
                        playTime = playTime + d;
                        textCurrent.setText(getDurationInFormat(playTime));//设置播放时间
                        seekBar.setProgress(playTime);
                        handler.sendEmptyMessageDelayed(2, maxTime / 150);
                    } else {
                        CustomToast.showToast(mContext, "播放完成");
                        playTime = 0;
                        seekBar.setProgress(0);
                    }

                    break;

                case 10:
                    String addscore = String.valueOf(msg.arg1);
                    if (addscore.equals("5")) {
                        String mg = "语音成功发送至排行榜，恭喜您获得了" + addscore + "分";
                        CustomToast.showToast(mContext, mg);
                    } else {
                        String mg = "语音成功发送至排行榜";
                        CustomToast.showToast(mContext, mg);
                    }
                    tvPublishAndShare.setText("分享");
                    break;
                case 11:
                    CustomToast.showToast(mContext, "发布失败！");
                    break;
                default:
            }
            return false;
        }
    });


    /**
     * 时间格式转化为00：00
     *
     * @return
     */
    private String getDurationInFormat(int time) {
        StringBuffer sb = new StringBuffer("");
        int musicTime = time / 1000;

        String minu = String.valueOf(musicTime / 60);
        if (minu.length() == 1) {
            minu = "0" + minu;
        }
        String sec = String.valueOf(musicTime % 60);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }

        sb.append(minu).append(":").append(sec);
        return sb.toString();
    }

    /**
     * 合成录音请求
     */
    private void requestComposeVoice(ArrayList<String> pathList) {

        StringBuffer stringBuffer = new StringBuffer();

        for (String str : pathList) {
            stringBuffer.append(str).append(",");
        }
        String path = stringBuffer.toString();
        path = path.substring(0, path.length() - 1);
        Map<String, String> pathParams = new HashMap<String, String>();
        Map<String, String> typeParams = new HashMap<String, String>();
        LogUtil.e("合成原接口网络地址" + path);
//        try {
//            path = URLEncoder.encode(path, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        pathParams.put("audios", path);
        typeParams.put("type", "toeic");//familyalbum

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(15, TimeUnit.SECONDS).
                readTimeout(15, TimeUnit.SECONDS).
                writeTimeout(15, TimeUnit.SECONDS)
                .build();
        //文本类的
        MultipartBody.Builder urlBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (pathParams != null) {
            for (String key : pathParams.keySet()) {
                if (pathParams.get(key) != null) {
                    urlBuilder.addFormDataPart(key, pathParams.get(key));
                }
            }
        }

        if (typeParams != null) {
            for (String key : typeParams.keySet()) {
                if (typeParams.get(key) != null) {
                    urlBuilder.addFormDataPart(key, typeParams.get(key));
                }
            }
        }
        //String actionUrl = "http://speech." + Constant.IYBHttpHead + "/test/merge/";
        String actionUrl = WebConstant.HTTP_SPEECH_ALL + "/test/merge/";//:9001
        LogUtil.e("合成提交 " + "path:" + path + " toeic " + actionUrl);
        // 构造Request->call->执行
        final okhttp3.Request request = new okhttp3.Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
                .url(actionUrl).post(urlBuilder.build())//参数放在body体里
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtil.e("合成请求失败" + e);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        LogUtil.e("合成请求返回数据：" + jsonObject);
                        String result = jsonObject.getString("result");
                        if (result.equals("1")) {
                            composeVoicePath = jsonObject.getString("URL");
                            String message = jsonObject.getString("message");
                            LogUtil.e("合成请求请求成功");//返回的试听地址拼接前缀:http://voa.iyuba.cn/voa/
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(0);
                            LogUtil.e("合成请求请求失败0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("合成请求请求失败" + e);
                        handler.sendEmptyMessage(0);
                    }
                } else {
                    LogUtil.e("合成请求失败2");
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    //合成语音上发至服务器 发布  不能使用旧版本的post请求git
    private void sendSound() {

        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            if (isSendSound) {
                CustomToast.showToast(mContext, "评论发送中，请不要重复提交");
            } else {
                if (composeVoicePath != null && !composeVoicePath.equals("")) {
                    //新的网络请求！！！
                    isSendSound = true;


                    ApiService service = ApiRetrofit.getInstance().getApiService();
                    final String head = "http://voa." + Constant.IYBHttpHead + "/voa/UnicomApi";
                    service.audioSendApi(head, "toeic", "android",
                            "json", "60003", AccountManagerLib.Instace(mContext).userId,
                            String.valueOf(textList.get(0).TitleNum), String.valueOf(totalScore / senceNum),
                            "4", composeVoicePath, 1, Integer.parseInt(Constant.APPID)).enqueue(new Callback<EvaSendBean>() {
                        @Override
                        public void onResponse(Call<EvaSendBean> call, Response<EvaSendBean> response) {
                            String result = response.body().getResultCode();
                            Timber.e("ReadEvaluateResult" + result);

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                LogUtil.e("合成发送返回数据：" + jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (response.isSuccessful()) {

                                EvaSendBean evaSendBean = response.body();
                                if (!evaSendBean.getReward().equals("0")) {

                                    showJL(evaSendBean);
                                }
                            }


                            shuoshuoId = String.valueOf(response.body().getShuoshuoId());
                            String addscore = String.valueOf(response.body().getAddScore());
                            if (result.equals("501") || result.equals("1")) {
                                Message msg = handler.obtainMessage();
                                msg.what = 10;
                                msg.arg1 = Integer.parseInt(addscore);
                                handler.sendMessage(msg);
                                //rankHandler.sendEmptyMessage(0);//刷新排名
                                LogUtil.e("发布成功");
                            } else {
                                LogUtil.e("发布失败1");
                                handler.sendEmptyMessage(11);
                            }
                            isSendSound = false;
                        }

                        @Override
                        public void onFailure(Call<EvaSendBean> call, Throwable e) {
                            LogUtil.e("发布失败" + e);
                            isSendSound = false;
                            handler.sendEmptyMessage(11);
                        }
                    });
                } else {
                    LogUtil.e("发布失败，合成数据为空");
                    CustomToast.showToast(mContext, "请重新合成！");
                }
            }
        } else {
            startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    /**
     * 显示奖励
     *
     * @param evaSendBean
     */
    private void showJL(EvaSendBean evaSendBean) {

        if (evaSendBean.getReward() != null && !evaSendBean.getReward().equals("0")) {

            int reward = Integer.parseInt(evaSendBean.getReward());
            double rewardDouble = reward / 100.0f;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");


            new AlertDialog.Builder(requireActivity())
                    .setTitle("恭喜您！")
                    .setMessage("本次学习获得" + decimalFormat.format(rewardDouble) + "元红包奖励,已自动存入您的钱包账户。\n红包可在【爱语吧】微信公众体现，继续学习领取更多红包奖励吧！")
                    .setPositiveButton("好的", (dialog, which) -> {

                        dialog.dismiss();
                    })
                    .show();
        }
    }

    private void showShareSound() {
        String siteUrl = "http://voa." + Constant.IYBHttpHead + "/voa/play.jsp?id="
                + shuoshuoId + "&addr=" + composeVoicePath + "&apptype=" + Constant.TOPICID;

        String text = "我在爱语吧语音评测中获得了" + (totalScore / senceNum) + "分";
        String imageUrl = Constant.AppIcon;
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(text);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(textList.get(0).TitleName);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//         oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", arg2.toString());

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (AccountManagerLib.Instace(mContext).userId != null) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();
                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 49;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                            || arg0.getName().equals("TencentWeibo")) {
                        msg.what = 19;
                    }
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(mContext);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ResetPlayDataEvent event) {
        TEDBHelper teHelper = ((TestDetailActivity) Objects.requireNonNull(getActivity())).teHelper;
        curTitleInfo = DataManager.Instance().titleInfoList.get(event.position);
        textList = teHelper.geTexts(curTitleInfo.TitleNum);
        tvMergeAndPlay.setText("合成");
        totalScore = 0;
        readEvaluateAdapter.setData(textList, curTitleInfo);
        readEvaluateAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EvaluatePlayEvent event) {
        if (readEvaluateAdapter.player.isPlaying()) {
            for (Text text : textList) {
                text.isPlay = false;
            }
            readEvaluateAdapter.setData(textList, curTitleInfo);
            readEvaluateAdapter.notifyDataSetChanged();
        }
    }

    @NeedsPermission({android.Manifest.permission.RECORD_AUDIO})
    public void requestEvaluate() {

    }

    @OnPermissionDenied({android.Manifest.permission.RECORD_AUDIO})
    public void requestEvaluateDenied() {
        CustomToast.showToast(getActivity(), "申请权限失败,此功能无法正常使用!");
    }

    public void checkEvaluatePermission() {
        ReadEvaluateFragmentPermissionsDispatcher.requestEvaluateWithPermissionCheck(ReadEvaluateFragment.this);
    }


    public void showShare(Context context, String title, String content, String shareurl,
                          String imageUrl, String comment, String site, String titleurl,
                          PlatformActionListener actionListener) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(titleurl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(imageUrl);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(site);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareurl);
        if (actionListener != null) oks.setCallback(actionListener);
        // 启动分享GUI
        oks.show(context);
    }


}
