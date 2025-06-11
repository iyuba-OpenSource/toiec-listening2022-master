package com.iyuba.toeiclistening.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.stetho.common.LogUtil;
import com.google.gson.Gson;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.circularimageview.GetPhotoPath;
import com.iyuba.core.me.protocol.AddScoreResponse;
import com.iyuba.core.me.protocol.AddScoreWordRequest;
import com.iyuba.core.me.sqlite.mode.SignBean;
import com.iyuba.core.util.RxTimer;
import com.iyuba.toeiclistening.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class WordSignActivity extends AppCompatActivity {



    TextView tvUserName;
    TextView tvIndexNum;
    TextView tvWordNumber;
    TextView tvTrueScaleNum;
    TextView tvShareWeChat;
    TextView tvChanel;
    CircleImageView rivPhoto;

    private Context mContext;

    String addCredit = "";//Integer.parseInt(bean.getAddcredit());
    String days = "";//Integer.parseInt(bean.getDays());
    String totalCredit = "";//bean.getTotalcredit();
    String money = "";
    private MaterialDialog dialog_share;

    private int index, wordNum;
    private int scale;
    private Bitmap wechatShareBitmap;


    private void fView() {

        tvUserName = findViewById(R.id.tv_user_name);
        tvIndexNum = findViewById(R.id.tv_index_num);
        tvWordNumber = findViewById(R.id.tv_word_number);
        tvTrueScaleNum = findViewById(R.id.tv_true_scale_num);
        tvShareWeChat = findViewById(R.id.tv_share_weChat);
        tvChanel = findViewById(R.id.tv_chanel);
        rivPhoto = findViewById(R.id.riv_photo);
    }


    public static void start(Context context, int index, int wordNum, int scale) {
        Intent intent = new Intent(context, WordSignActivity.class);
        intent.putExtra("word_pass", index);
        intent.putExtra("word_num", wordNum);
        intent.putExtra("word_scale", scale);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_sign);
        fView();
        mContext = this;

        //状态栏处理
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            //android手机小于5.0的直接全屏显示，防止截图留白边
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getIntentData();
        initView();
    }

    private void getIntentData() {
        index = getIntent().getIntExtra("word_pass", 0);
        wordNum = getIntent().getIntExtra("word_num", 0);
        scale = getIntent().getIntExtra("word_scale", 0);
    }

    private void initView() {
        tvUserName.setText(AccountManagerLib.Instace(mContext).userName);
        tvIndexNum.setText(index + "");
        tvWordNumber.setText(wordNum + "");
        tvTrueScaleNum.setText(scale + "%");
        tvChanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageLoader.getInstance().displayImage(GetPhotoPath.getThisUserPhoto(mContext), rivPhoto);

        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(mContext);
        dialog_share.setTitle("提醒");
        dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
        dialog_share.setPositiveButton("好的", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_share.dismiss();
                finish();
            }
        });

        tvShareWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBitmapToFile();
                //LogUtil.e("打卡执行，showShareOnMoment");
                RxTimer.timer(100, a -> {
                    showShareOnMoment(mContext, AccountManagerLib.Instace(mContext).userId, Constant.APPID);
                });
                //startInterfaceADDScore(AccountManagerLib.Instace(mContext).userId, Constant.APPID);
            }
        });
    }


    public void showShareOnMoment(Context context, final String userID, final String AppId) {
        OnekeyShare oks = new OnekeyShare();
        oks.setDisappearShareToast(true);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
//         Bitmap bitmap = BitmapFactory.de(R.drawable.button_pres,0);
        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setImagePath(getFileCachePath() + "/share.png");
        oks.setImageData(wechatShareBitmap);
        ShareSDK.getPlatform(Wechat.NAME);
        oks.setSilent(true);
        LogUtil.e("打卡setSilent");
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("打卡成功onComplete");
                startInterfaceADDScore(userID, AppId);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", throwable.toString());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享取消===", "....");
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    private void startInterfaceADDScore(String userID, String appId) {
        Date currentTime = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String time = Base64Coder.encode(dateString);

        String url = "";
        ExeProtocol.exe(new AddScoreWordRequest(userID.trim(), appId.trim(), time.trim(), index),//有空格，虽然看不出来
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        SignBean bean = null;
                        try {
                            AddScoreResponse response = (AddScoreResponse) bhr;
                            bean = new Gson().fromJson(response.jsonObjectRoot.toString(), SignBean.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("打卡失败，");
                        }
                        LogUtil.e("打卡成功bean--->" + bean.getResult());

                        if (bean.getResult().trim().equals("200")) {
                            money = bean.getMoney();
                            addCredit = bean.getAddcredit();
                            days = bean.getDays();
                            totalCredit = bean.getTotalcredit();
                            LogUtil.e("打卡成功200，回调");
                            //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float moneyThisTime = Float.parseFloat(money);

                                    if (moneyThisTime > 0) {
                                        float allmoney = Float.parseFloat(totalCredit);

                                        AccountManagerLib.Instace(WordSignActivity.this).setMONEY(allmoney + "");
                                        dialog_share.setMessage("打卡成功," + "您已连续打卡" + days + "天," +
                                                "获得" + floatToString(moneyThisTime) + "元,总计: "
                                                + floatToString(allmoney) + "元," + "满十元可在\"爱语吧\"公众号提现");
                                        showDialog();
                                    } else {

                                        dialog_share.setMessage("打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit);
                                        showDialog();
                                    }
                                }
                            });


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog_share.setTitle("提醒");
                                    dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
                                    showDialog();
                                    //ToastUtil.showToast(mContext,"打卡成功，今日已打卡");
                                }
                            });

                        }


                    }

                    @Override
                    public void error() {
                        LogUtil.e("打卡失败，回调");
                    }
                });
    }

    private void showDialog() {
        if (!isDestroyed()) {
            dialog_share.show();
        }
    }


    public void writeBitmapToFile() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        wechatShareBitmap = view.getDrawingCache();
        if (wechatShareBitmap == null) {
            return;
        }

        wechatShareBitmap.setHasAlpha(false);
        wechatShareBitmap.prepareToDraw();

        File newpngfile = new File(getFileCachePath() + "/share.png");
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            wechatShareBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String floatToString(float fNumber) {
        fNumber = (float) (fNumber * 0.01);
        DecimalFormat myformat = new DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

    public String getFileCachePath() {
        String filePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            filePath = getExternalCacheDir().getPath();
        } else {
            filePath = getCacheDir().getPath();
        }
        return filePath;
    }
}
