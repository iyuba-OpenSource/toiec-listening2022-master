package com.iyuba.core.me.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.stetho.common.LogUtil;
import com.google.gson.Gson;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.protocol.AddScoreRequest;
import com.iyuba.core.me.protocol.AddScoreResponse;
import com.iyuba.core.me.protocol.SignRequest;
import com.iyuba.core.me.protocol.SignResponse;
import com.iyuba.core.me.sqlite.mode.SignBean;
import com.iyuba.core.me.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.core.util.RxTimer;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;

//import com.iyuba.concept2.protocol.AddScoreRequest;
//import com.iyuba.concept2.protocol.AddScoreResponse;
//import com.iyuba.concept2.protocol.SignRequest;
//import com.iyuba.concept2.protocol.SignResponse;
//import com.iyuba.concept2.sqlite.mode.SignBean;
//import com.iyuba.concept2.sqlite.mode.StudyTimeBeanNew;


/**
 * 打卡页面
 * zh 2019.4.18
 */

public class SignActivity extends Activity {
    private ImageView imageView;
    private ImageView qrImage;
    private TextView tv1, tv2, tv3;
    private Context mContext;
    private TextView sign;
    private ImageView userIcon;
    private TextView tvShareMsg;
    private int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    String shareTxt;
    String getTimeUrl;
    LinearLayout ll;
    CustomDialog mWaittingDialog;
    String addCredit = "";//Integer.parseInt(bean.getAddcredit());
    String days = "";//Integer.parseInt(bean.getDays());
    String totalCredit = "";//bean.getTotalcredit();
    String money = "";

    private TextView tvUserName;
    private TextView tvAppName;
    private TextView tv_finish;

    private ImageView btn_close;
    private MaterialDialog dialog, dialog_share;
    private Bitmap wechatBitmap;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;

        mWaittingDialog = WaittingDialog.showDialog(SignActivity.this);
        mWaittingDialog.setTitle("请稍后");
        setContentView(R.layout.activity_sign);

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
        initView();

        initData();

        initBackGround();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initData() {

        mWaittingDialog.show();

        String uid = AccountManagerLib.Instace(mContext).userId;


        final String url = String.format(Locale.CHINA,
                "http://daxue." + com.iyuba.core.util.Constant.IYBHttpHead + "/ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());
        Log.d("dddd", url);
        getTimeUrl = url;

        ExeProtocol.exe(
                new SignRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {

                    @Override
                    public void finish(final BaseHttpResponse bhr) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                SignResponse response = (SignResponse) bhr;
                                try {
                                    if (null != mWaittingDialog) {
                                        if (mWaittingDialog.isShowing()) {
                                            mWaittingDialog.dismiss();
                                        }
                                    }
                                    final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
                                    Log.d("dddd", response.jsonObjectRoot.toString());
                                    if ("1".equals(bean.getResult())) {
                                        final int time = Integer.parseInt(bean.getTotalTime());
                                        int totaltime = Integer.parseInt(bean.getTotalDaysTime());

                                        tv1.setText(bean.getTotalDays() + ""); //学习天数
                                        tv2.setText(bean.getTotalWord() + "");//今日单词
                                        //TODO
                                        int nowRank = Integer.parseInt(bean.getRanking());
                                        double allPerson = Double.parseDouble(bean.getTotalUser());
                                        double carry;
                                        String over = null;
                                        if (allPerson != 0) {
                                            carry = 1 - nowRank / allPerson;
                                            DecimalFormat df = new DecimalFormat("0.00");
                                            Log.e("百分比", df.format(carry) + "--" + nowRank + "--" + allPerson);

                                            over = df.format(carry).substring(2, 4);
                                        }

                                        tv3.setText(over + "%同学"); //超越了
                                        shareTxt = bean.getSentence() + "我在托业听力坚持学习了" + bean.getTotalDays() + "天,积累了" + bean.getTotalWords()
                                                + "单词如下";

                                        if (time < signStudyTime) {
                                            sign.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {


                                                    toast(String.format(Locale.CHINA, "打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));

                                                }
                                            });
                                        } else {
                                            sign.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    qrImage.setVisibility(View.VISIBLE);
                                                    sign.setVisibility(View.GONE);
                                                    tvShareMsg.setVisibility(View.VISIBLE);
                                                    tv_finish.setVisibility(View.VISIBLE);
                                                    tvShareMsg.setText("长按图片识别二维码");
                                                    tvShareMsg.setBackgroundResource(R.drawable.sign_bg_yellow);

                                                    writeBitmapToFile();
                                                    LogUtil.e("打卡执行，showShareOnMoment");
                                                    RxTimer.timer(100, a -> {
                                                        showShareOnMoment(mContext, AccountManagerLib.Instace(mContext).userId, Constant.APPID);
                                                    });
                                                    //startInterfaceADDScore(AccountManagerLib.Instace(mContext).userId, Constant.APPID);

//                                        shareUrl(shareDownloadUrl,shareTxt,bitmap,"我的学习记录", SendMessageToWX.Req.WXSceneTimeline);
                                                }
                                            });
//                            startShareInterface(); //朋友圈分享
                                        }
                                    } else {
                                        toast(loadFiledHint + bean.getResult());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    toast(loadFiledHint + "！！");
                                }


                            }
                        });
                    }

                    @Override
                    public void error() {

                    }
                });


    }

    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }

    private void initView() {

        imageView = (ImageView) findViewById(R.id.iv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);

        sign = (TextView) findViewById(R.id.tv_sign);
        ll = (LinearLayout) findViewById(R.id.ll);
        qrImage = (ImageView) findViewById(R.id.tv_qrcode);
        userIcon = (ImageView) findViewById(R.id.iv_userimg);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvAppName = (TextView) findViewById(R.id.tv_appname);
        tvShareMsg = (TextView) findViewById(R.id.tv_sharemsg);

        btn_close = (ImageView) findViewById(R.id.btn_close);
        tv_finish = (TextView) findViewById(R.id.tv_finish);

        tv_finish.setText(" 刚刚在『" + "托业听力" + "』上完成了打卡");


        tv_finish.setVisibility(View.INVISIBLE);

        //关闭打卡页面弹出提示
        dialog = new MaterialDialog(SignActivity.this);
        dialog.setTitle("温馨提示");
        dialog.setMessage("点击下边的打卡按钮，成功分享至微信朋友圈才算成功打卡，才能领取红包哦！确定退出么？");
        dialog.setPositiveButton("继续打卡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("去意已决", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

            }
        });


        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(SignActivity.this);
        dialog_share.setTitle("提醒");
        dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
        dialog_share.setPositiveButton("好的", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_share.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initBackGround() {

        int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
        String url = WebConstant.HTTP_STATIC + com.iyuba.core.util.Constant.IYBHttpHead + "/images/mobile/" + day + ".jpg";

//        bitmap = returnBitMap(url);
        Glide.with(mContext).load(url).placeholder(R.drawable.sign_background).error(R.drawable.sign_background).into(imageView);
        final String userIconUrl = "http://api." + Constant.IYBHttpHead2 + "/v2/api.iyuba?protocol=10005&uid="
                + AccountManagerLib.Instace(mContext).userId + "&size=middle";


//        Glide.with(mContext).load(userIconUrl).into(userIcon);

        Glide.with(mContext)
                .asBitmap()
                .load(userIconUrl)
                  //这句不能少，否则下面的方法会报错
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .centerCrop()
                .into(new BitmapImageViewTarget(userIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });


        if (TextUtils.isEmpty(AccountManagerLib.Instace(mContext).userName)) {
            tvUserName.setText(AccountManagerLib.Instace(mContext).userId);
        } else {
            tvUserName.setText(AccountManagerLib.Instace(mContext).userName);
        }
        tvAppName.setText("托业听力" + "--英语学习必备软件");
//        Glide.with(mContext).load("").placeholder(R.drawable.qr_code).into(qrImage);
    }


    public void writeBitmapToFile() {
        View view = getWindow().getDecorView();
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.measure(0, 0);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        Log.d("diao", "writeBitmapToFile: "+view.getMeasuredWidth()+":"+view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        wechatBitmap = view.getDrawingCache();
        if (wechatBitmap == null) {
            return;
        }

        wechatBitmap.setHasAlpha(false);
        wechatBitmap.prepareToDraw();

        File newpngfile = new File(Environment.getExternalStorageDirectory(), "aaa.png");
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            wechatBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_finish.setVisibility(View.GONE);
    }


    private void startInterfaceADDScore(String userID, String appid) {

        LogUtil.e("打卡成功 startInterfaceADDScore");

        Date currentTime = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String time = Base64Coder.encode(dateString);
        ExeProtocol.exe(
                new AddScoreRequest(userID.trim(), appid.trim(), time.trim()),//有空格，虽然看不出来
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        LogUtil.e("打卡成功,finish" + bhr);
                        AddScoreResponse response = (AddScoreResponse) bhr;
                        final SignBean bean = new Gson().fromJson(response.jsonObjectRoot.toString(), SignBean.class);
                        LogUtil.e("打卡成功" + bean.getResult());

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

                                        AccountManagerLib.Instace(mContext).setMONEY(allmoney + "");

                                        dialog_share.setMessage("打卡成功," + "您已连续打卡" + days + "天," +
                                                "获得" + floatToString(moneyThisTime) + "元,总计: "
                                                + floatToString(allmoney) + "元," + "满十元可在\"爱语吧\"公众号提现");
                                        dialog_share.show();
                                    } else {

                                        dialog_share.setMessage("打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit);
                                        dialog_share.show();
                                    }
                                }
                            });


                        } else {
                            LogUtil.e("打卡成功，今日已打卡");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dialog_share.setMessage("今日已打卡，重复打卡不能再次获取红包或积分哦！");
                                    dialog_share.show();
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


    public void showShareOnMoment(Context context, final String userID, final String AppId) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // Bitmap bitmap = BitmapFactory.decodeResource(R.drawable.abc_ab_share_pack_holo_dark, )

        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setImagePath(Environment.getExternalStorageDirectory() + "/aaa.png");
        oks.setImageData(wechatBitmap);

        oks.setSilent(true);
        LogUtil.e("打卡setSilent");
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("打卡成功onComplete");
                startInterfaceADDScore(userID, AppId);
                tv_finish.setVisibility(View.GONE);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", throwable.toString());
                tv_finish.setVisibility(View.GONE);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享取消===", "....");
                tv_finish.setVisibility(View.GONE);
            }
        });
        // 启动分享GUI
        oks.show(context);
    }


    private String floatToString(float fNumber) {
        fNumber = (float) (fNumber * 0.01);
        DecimalFormat myformat = new DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

}



