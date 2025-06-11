package com.iyuba.core.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.teacher.manager.VersionManager;
import com.iyuba.core.teacher.protocol.FeedBackJsonRequest;
import com.iyuba.core.util.BrandUtil;
import com.iyuba.core.util.ScreenUtils;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 意见反馈Activity
 *
 * @author chentong
 */

public class Feedback extends BasisActivity {
    private CustomDialog wettingDialog;
    private Button backBtn;
    private View submit;
    private EditText context, email;
    //private String content1;
    private Context mContext;
    private ImageView qq_image;
    private boolean underway = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback);
        mContext = this;
        CrashApplication.addActivity(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        wettingDialog = WaittingDialog.showDialog(this);
        context = (EditText) findViewById(R.id.editText_info);
        email = (EditText) findViewById(R.id.editText_Contact);
        qq_image = (ImageView) findViewById(R.id.qq_image);
        qq_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showQQDialog(mContext, v);
            }
        });
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        submit = findViewById(R.id.ImageView_submit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!underway) {

                    String content = makeContent();
                    String uid = "0";
                    if (AccountManagerLib.Instace(mContext).userId != null &&
                            !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                            !AccountManagerLib.Instace(mContext).userId.equals("0")
                            && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
                        uid = AccountManagerLib.Instace(Feedback.this).userId;
                    }
                    if (verification()) {
                        underway = true;
                        if ("".equals(email.getText().toString().trim())) {
                            Toast.makeText(mContext, "请填写正确的邮箱", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            ExeProtocol.exe(new FeedBackJsonRequest(content, email
                                            .getText().toString(), uid),
                                    new ProtocolResponse() {

                                        @Override
                                        public void finish(BaseHttpResponse bhr) {
                                            // TODO Auto-generated method stub
                                            wettingDialog.dismiss();
                                            handler.sendEmptyMessage(0);
                                            onBackPressed();
                                        }

                                        @Override
                                        public void error() {
                                            // TODO Auto-generated method stub
                                            underway = false;
                                            handler.sendEmptyMessage(1);
                                            wettingDialog.dismiss();
                                        }
                                    });
                        }
                    }//失败
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    /**
     * @return
     */
    private String makeContent() {
        // TODO Auto-generated method stub
        String content = context.getText().toString() + "  appversion:["
                + VersionManager.VERSION_CODE + "]versionCode:["
                + VersionManager.version + "]phone:[" + android.os.Build.BRAND
                + android.os.Build.MODEL + android.os.Build.DEVICE + "]sdk:["
                + android.os.Build.VERSION.SDK_INT + "]sysversion:["
                + android.os.Build.VERSION.RELEASE + "]";
        return content;
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    underway = false;
                    CustomToast.showToast(Feedback.this,
                            R.string.feedback_submit_success);
                    finish();
                    break;
                case 1:
                    CustomToast.showToast(Feedback.this,
                            R.string.feedback_network_error);
                    break;
                case 2:
                    CustomToast.showToast(Feedback.this,
                            R.string.feedback_submitting);
                    break;
            }
        }
    };

    /**
     * 验证
     */
    public boolean verification() {
        String contextString = context.getText().toString();
        String emailString = email.getText().toString();

        if (contextString.length() == 0) {
            context.setError(getResources().getString(
                    R.string.feedback_info_null));
            return false;
        }

        if (emailString.length() != 0) {
            if (!emailCheck(emailString)) {
                email.setError(getResources().getString(
                        R.string.feedback_effective_email));
                return false;
            }
        } else {
            if (!AccountManagerLib.Instace(Feedback.this).checkUserLogin()) {
                email.setError(getResources().getString(
                        R.string.feedback_email_null));
                return false;
            }
        }

        return true;
    }

    /**
     * email格式匹配
     *
     * @param email
     * @return
     */
    public boolean emailCheck(String email) {
        String check = "^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void showQQDialog(final Context mContext, View v) {


        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv1 = new TextView(mContext);
        TextView tv2 = new TextView(mContext);
        TextView tv3 = new TextView(mContext);

        tv1.setTextSize(16);
        tv2.setTextSize(16);
        tv3.setTextSize(16);

        tv1.setPadding(10, ScreenUtils.dp2px(mContext, 10), 10, 0);
        tv2.setPadding(10, ScreenUtils.dp2px(mContext, 20), 10, ScreenUtils.dp2px(mContext, 20));
        tv3.setPadding(10, 0, 10, ScreenUtils.dp2px(mContext, 10));

        tv1.setTextColor(Color.BLACK);
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);

        BrandUtil.requestQQGroupNumber(mContext);//网络请求
        tv1.setText(String.format("%s用户群: %s", BrandUtil.getBrandChinese(), BrandUtil.getQQGroupNumber(mContext)));
        tv2.setText("客服QQ: 3099007489");
        tv3.setText("技术QQ: 1771874056");

        linearLayout.addView(tv1);
        linearLayout.addView(tv2);
        linearLayout.addView(tv3);

        tv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQGroup(mContext, BrandUtil.getQQGroupKey(mContext));
            }
        });

        tv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQ(mContext, "3099007489");
            }
        });

        tv3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startQQ(mContext, "1161178411");
            }
        });


        BubbleLayout bl = new BubbleLayout(this);
        new BubbleDialog(mContext)
                .addContentView(linearLayout)
                .setClickedView(v)
                .setPosition(BubbleDialog.Position.BOTTOM)
                .calBar(true)
                .setBubbleLayout(bl)
                .show();


        bl.setLook(BubbleLayout.Look.TOP);
    }

    public void startQQ(Context context, String qq) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq + "&version=1";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(context, "您的设备尚未安装QQ客户端", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?" +
                "url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(context, "您的设备尚未安装QQ客户端", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
