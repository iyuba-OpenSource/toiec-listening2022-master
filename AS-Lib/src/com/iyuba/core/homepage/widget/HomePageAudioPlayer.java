package com.iyuba.core.homepage.widget;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.activity.PlaySet;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.manager.CommonNewsDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.news.ReadCountAddRequest;
import com.iyuba.core.common.protocol.news.SimpleDetailRequest;
import com.iyuba.core.common.protocol.news.SimpleDetailResponse;
import com.iyuba.core.common.service.HeadsetPlugReceiver;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.mode.CommonNews;
import com.iyuba.core.common.util.CommonStudyRecordUtil;
import com.iyuba.core.common.util.GroundShare;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.WordCard;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.subtitle.SubtitleSum;
import com.iyuba.core.common.widget.subtitle.SubtitleSynView;
import com.iyuba.core.common.widget.subtitle.TextPageSelectTextCallBack;
import com.iyuba.core.R;

import java.util.ArrayList;
import java.util.Random;

//import com.google.ads.AdRequest;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;

/**
 * 简版新闻播放界面
 *
 * @author chentong
 * @version 1.1 修改内容 优化播放性能
 * @para "source" 类别 0 voa 1 bbc 2 听歌
 */
public class HomePageAudioPlayer extends BasisActivity {
    private Context mContext;
    private Button backBtn, moreBtn;
    private TextView title;
    // data
    private String lesson;
    private int listPosition;
    private int currPara;
    private boolean isvip = true;
    private int source;
    private SubtitleSum subtitleSum;
    private boolean syncho;
    private ArrayList<CommonNews> newsArrayList;
    private String shareUrl;
    // widget
    private SeekBar seekBar = null;
    private TextView durationTextView = null, playedTextView = null;
    private ImageButton videoFormer, videoLatter, lockButton, pause, playMode;
    private SubtitleSynView currentTextView = null;
    private BackPlayer vv;
    private CustomDialog waitting;
    private WordCard card;
    //	private AdView adView;
    private HeadsetPlugReceiver headsetPlugReceiver;
    private ContextMenu contextMenu;
    // 常亮
    private PowerManager powerManager = null;
    private WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioplayer);
        mContext = this;
//		CrashApplication.getInstance().addActivity(this);
        powerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "my :Lock");
//		isvip = ConfigManager.Instance().loadInt("isvip") == 1 ? true : false;
        listPosition = CommonNewsDataManager.Instace().position;
        source = getIntent().getIntExtra("source", source);
        shareUrl = CommonNewsDataManager.Instace().url;
        lesson = CommonNewsDataManager.Instace().lesson;
        newsArrayList = CommonNewsDataManager.Instace().voasTemp;
        waitting = WaittingDialog.showDialog(mContext);
        initWidget();
    }

    /**
     *
     */
    private void initWidget() {
        // TODO Auto-generated method stub
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        contextMenu = (ContextMenu) findViewById(R.id.context_menu);
        moreBtn = (Button) findViewById(R.id.more);
        moreBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                contextMenu.setText(mContext.getResources().getStringArray(
                        R.array.more_oper));
                contextMenu.setCallback(new ResultIntCallBack() {

                    @Override
                    public void setResult(int result) {
                        // TODO Auto-generated method stub
                        Intent intent;
                        switch (result) {
                            case 0:

                                new GroundShare(mContext).prepareMessage(
                                        newsArrayList.get(listPosition).id,
                                        newsArrayList.get(listPosition).title,
                                        shareUrl,
                                        newsArrayList.get(listPosition).content);

                                break;
                            case 1:
                                intent = new Intent(mContext, PlaySet.class);
                                mContext.startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        contextMenu.dismiss();
                    }
                });
                contextMenu.show();
            }
        });
        card = (WordCard) findViewById(R.id.word);
        card.setVisibility(View.GONE);
        initPlayer();
    }

    /**
     *
     */
    private void initPlayer() {
        // TODO Auto-generated method stub
        durationTextView = (TextView) findViewById(R.id.textView_alltime);
        playedTextView = (TextView) findViewById(R.id.textView_currenttime);
        vv = new BackPlayer(mContext);
        title = (TextView) findViewById(R.id.play_title_info);
        title.setText(newsArrayList.get(listPosition).title);
        currentTextView = (SubtitleSynView) findViewById(R.id.currnt_original);
        currentTextView.setTpstcb(tp);
        videoFormer = (ImageButton) findViewById(R.id.former_button);
        videoLatter = (ImageButton) findViewById(R.id.next_button);
        videoFormer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                CommonStudyRecordUtil.recordStop(lesson, "0");
                if (listPosition == 0) {
                    listPosition = newsArrayList.size() - 1;
                } else {
                    listPosition--;
                }
                new getDetail().start();
            }
        });
        videoLatter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                CommonStudyRecordUtil.recordStop(lesson, "0");
                if (listPosition == newsArrayList.size() - 1) {
                    listPosition = 0;
                } else {
                    listPosition++;
                }
                new getDetail().start();
            }
        });
        seekBar = (SeekBar) findViewById(R.id.small_seekBar_player);
        seekBar.getParent().requestDisallowInterceptTouchEvent(true);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                    currPara = subtitleSum.getParagraph(
                            vv.getCurrentPosition() / 1000.0, 0);
                    handler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        lockButton = (ImageButton) findViewById(R.id.lock);
        lockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                syncho = !SettingConfig.Instance().isSyncho();
                SettingConfig.Instance().setSyncho(syncho);
                currentTextView.setSyncho(syncho);
                setPlayControlButton();
            }
        });
        pause = (ImageButton) findViewById(R.id.video_play);
        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (vv.isPlaying()) {
                    pause.setBackgroundResource(R.drawable.play_button);
                    vv.pause();
                } else {
                    pause.setBackgroundResource(R.drawable.pause_button);
                    vv.start();
                }
            }
        });
        playMode = (ImageButton) findViewById(R.id.play_mode);
        playMode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Constant.mode = (Constant.mode + 1) % 3;
                setPlayControlButton();
            }
        });
//		String showAD = MobclickAgent.getConfigParams(mContext, "ad");
//		if (showAD != null && showAD.equals("1")) {
//			initAD();
//		}
        setPlayControlButton();
        new getDetail().start();
    }

    private void setPlayControlButton() {
        switch (Constant.mode) {
            case 0:
                playMode.setBackgroundResource(R.drawable.play_mode_one);
                break;
            case 1:
                playMode.setBackgroundResource(R.drawable.play_mode_order);
                break;
            case 2:
                playMode.setBackgroundResource(R.drawable.play_mode_random);
                break;
            default:
                break;
        }
        if (syncho) {
            lockButton.setBackgroundResource(R.drawable.lock_button_press);
        } else {
            lockButton.setBackgroundResource(R.drawable.lock_button_normal);
        }
    }

    private void setSeekbar() {
        int i = vv.getDuration();
        seekBar.setMax(i);
        i /= 1000;
        int minute = i / 60;
        int second = i % 60;
        minute %= 60;
        durationTextView.setText(String.format("%02d:%02d", minute, second));
    }

//	private void initAD() {
//		if (!isvip) {
//			// Create the adView
//			adView = new AdView(this, AdSize.BANNER, "a150c0277858794");
//			LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
//			layout.addView(adView);
//			adView.loadAd(new AdRequest());
//		}
//	}

    private void playVideo() {// finish是否从头开始
        new addReadCountThread().start();
        String url = null, append = null;
        currPara = 0;
        if (source == 0) {
            if (SettingConfig.Instance().isHighSpeed()) {
                append = "http://staticvip." + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/voa";
            } else {
                append = WebConstant.HTTP_STATIC + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/voa";
            }
            url = append + newsArrayList.get(listPosition).musicUrl;
        } else if (source == 1) {
            if (SettingConfig.Instance().isHighSpeed()) {
                append = "http://staticvip." + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/minutes/";
            } else {
                append = WebConstant.HTTP_STATIC + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/minutes/";
            }
            url = append + newsArrayList.get(listPosition).musicUrl;
        } else if (source == 2) {
            if (SettingConfig.Instance().isHighSpeed()) {
                append = "http://staticvip." + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/song/";
            } else {
                append = WebConstant.HTTP_STATIC + com.iyuba.core.util.Constant.IYBHttpHead + "/sounds/song/";
            }
            url = append + newsArrayList.get(listPosition).musicUrl;
        }
        CommonStudyRecordUtil
                .groundRecordStart(newsArrayList.get(listPosition).id);
        handler.sendEmptyMessage(1);
        vv.setVideoPath(url);
        seekBar.setSecondaryProgress(0);

        title.setText(newsArrayList.get(listPosition).title);

        controlVideo();
    }

    private void controlVideo() {
        vv.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer arg0) {
                setSeekbar();
                vv.start();
                if (waitting != null && waitting.isShowing()) {
                    handler.sendEmptyMessage(2);
                }
                videoHandler.sendEmptyMessage(0);
                videoHandler.sendEmptyMessage(1);
            }
        });
        vv.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                CommonStudyRecordUtil.recordStop(lesson, "1");
                if (Constant.mode == 0) {
                } else if (Constant.mode == 1) {
                    if (listPosition == newsArrayList.size() - 1) {
                        listPosition = 0;
                    } else {
                        listPosition++;
                    }
                } else {
                    Random rnd = new Random();
                    int nextID = rnd.nextInt(200) / 10;
                    while (nextID == listPosition) {
                        nextID = rnd.nextInt(200) / 10;
                    }
                    listPosition = nextID;
                }
                new getDetail().start();
            }
        });
    }

    private String getDetailUrl() {
        String url = null;
        if (source == 1) {
            url = "http://apps." + com.iyuba.core.util.Constant.IYBHttpHead + "/minutes/textApi.jsp?bbcid="
                    + newsArrayList.get(listPosition).id;
        } else if (source == 0) {
            url = "http://apps." + com.iyuba.core.util.Constant.IYBHttpHead + "/voa/textNewApi.jsp?voaid="
                    + newsArrayList.get(listPosition).id;
        } else if (source == 2) {
            url = "http://apps." + com.iyuba.core.util.Constant.IYBHttpHead + "/afterclass/getText.jsp?SongId="
                    + newsArrayList.get(listPosition).id;
        }
        return url;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                    if (currPara != 0) {
                        currentTextView.snyParagraph(currPara);
                    }
                    break;
                case 1:
                    waitting.show();
                    break;
                case 2:
                    waitting.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext,
                            R.string.play_please_take_the_word);
                    break;
                case 4:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 5:
                    currentTextView.unsnyParagraph();
                    break;
                case 7:
                    currentTextView.setSubtitleSum(subtitleSum, 1);
                    currentTextView.setSyncho(syncho);
                    setPlayControlButton();
                    if (BackgroundManager.Instace().bindService != null) {
                        BackPlayer backPlayer = BackgroundManager.Instace().bindService
                                .getPlayer();
                        if (backPlayer.isPlaying()) {
                            backPlayer.pause();
                        }
                    }
                    playVideo();
                    break;
            }
        }
    };
    Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                    int i = vv.getCurrentPosition();
                    seekBar.setProgress(i);
                    i /= 1000;
                    int minute = i / 60;
                    int second = i % 60;
                    minute %= 60;
                    playedTextView.setText(String.format("%02d:%02d", minute,
                            second));
                    try {
                        if (vv.isPlaying()) {
                            currPara = subtitleSum.getParagraph(
                                    vv.getCurrentPosition() / 1000.0, 1);
                            handler.sendEmptyMessage(0);
                            pause.setBackgroundResource(R.drawable.pause_button);
                        } else if (currentTextView != null) {
                            pause.setBackgroundResource(R.drawable.play_button);
                            handler.sendEmptyMessage(5);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    } finally {
                        videoHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
                case 1:
                    int percent = vv.getBufferPercentage();
                    if (percent == 0) {
                        seekBar.setSecondaryProgress(seekBar.getMax());
                    } else {
                        seekBar.setSecondaryProgress(percent * seekBar.getMax()
                                / 100);
                    }
                    videoHandler.sendEmptyMessageDelayed(1, 2000);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {
        @Override
        public void selectTextEvent(String selectText) {
            // TODO Auto-generated method stub
            card.setVisibility(View.GONE);
            if (selectText.matches("^[a-zA-Z'-]*")) {
                card.setVisibility(View.VISIBLE);
                card.searchWord(selectText);
            } else {
                handler.sendEmptyMessage(3);
            }
        }

        @Override
        public void selectParagraph(int paragraph) {
            vv.seekTo((int) (CommonNewsDataManager.Instace().voaDetailsTemp
                    .get(paragraph).startTime * 1000));
            currPara = paragraph;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		if (adView != null) {
//			adView.stopLoading();
//			adView.destroy();
//			adView.destroyDrawingCache();
//		}

        if (BackgroundManager.Instace().bindService != null
                && !BackgroundManager.Instace().bindService.getPlayer()
                .isPlaying()) {
            BackgroundManager.Instace().bindService.getPlayer().start();
        }
        videoHandler.removeMessages(0);
        videoHandler.removeMessages(1);
        vv.stopPlayback();
    }

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver(vv);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    public class getDetail extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            ClientSession.Instace().asynGetResponse(
                    new SimpleDetailRequest(getDetailUrl()),
                    new IResponseReceiver() {

                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            SimpleDetailResponse tr = (SimpleDetailResponse) response;
                            if (tr.voaDetailsTemps != null
                                    && tr.voaDetailsTemps.size() != 0) {
                                CommonNewsDataManager.Instace().voaDetailsTemp = tr.voaDetailsTemps;
                                CommonNewsDataManager.Instace()
                                        .setSubtitleSum();
                                subtitleSum = CommonNewsDataManager.Instace().subtitleSum;
                                handler.sendEmptyMessage(7);
                            }
                        }
                    });
        }
    }

    public class addReadCountThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            ClientSession.Instace().asynGetResponse(
                    new ReadCountAddRequest(String.valueOf(newsArrayList
                            .get(listPosition).id)), new IResponseReceiver() {

                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHeadsetPlugReceiver();
//		isvip = ConfigManager.Instance().loadInt("isvip") == 1 ? true : false;
        syncho = SettingConfig.Instance().isSyncho();
        currentTextView.setSyncho(syncho);
        if (SettingConfig.Instance().isLight()) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(headsetPlugReceiver);
        if (SettingConfig.Instance().isLight()) {
            wakeLock.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (contextMenu.isShown()) {
            contextMenu.dismiss();
        } else {
            CommonStudyRecordUtil.recordStop(lesson, "0");
            super.onBackPressed();
        }
    }


}
