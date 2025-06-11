package com.iyuba.core.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.iyuba.core.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.RegistRequest;
import com.iyuba.core.common.protocol.base.RegistResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.activity.UpLoadImageActivity;
import com.iyuba.core.util.PrivacyDialog;

import timber.log.Timber;

/**
 * 注册界面 邮箱注册
 *
 * @author chentong
 * @version 1.1 修改内容 更新API
 */
public class RegisterEmailActivity extends BasisActivity {
    private Context mContext;
    private Button backBtn;
    private EditText userName, userPwd, reUserPwd, email;
    private Button regBtn;
    private String userNameString;
    private String userPwdString;
    private String reUserPwdString;
    private String emailString;
    private CheckBox checkBox;
    private boolean send = false;
    private CustomDialog wettingDialog;
    private TextView protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setContentView(R.layout.regist_layout);
         CrashApplication.addActivity(this);
        wettingDialog = WaittingDialog.showDialog(mContext);
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userName = (EditText) findViewById(R.id.editText_userId);
        userPwd = (EditText) findViewById(R.id.editText_userPwd);
        reUserPwd = (EditText) findViewById(R.id.editText_reUserPwd);
        email = (EditText) findViewById(R.id.editText_email);
        regBtn = (Button) findViewById(R.id.button_regist);
        checkBox = findViewById(R.id.register_checkbox);
        regBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (verification()) { // 验证通过
                    // 开始注册
                    if (!send) {
                        send = true;
                        handler.sendEmptyMessage(5);
                        regist();
                    } else {
                        handler.sendEmptyMessage(7);
                    }
                }
            }
        });
        protocol = (TextView) findViewById(R.id.protocol);
        String remindString = "我已阅读并同意使用协议和隐私政策";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
        ClickableSpan secretString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyDialog.goToSecret(mContext);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(mContext, R.color.app_color));
            }
        };
        ClickableSpan policyString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyDialog.goToPolicy(mContext);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(mContext, R.color.app_color));
            }
        };
        spannableStringBuilder.setSpan(secretString, remindString.indexOf("隐私政策"), remindString.indexOf("隐私政策") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(policyString, remindString.indexOf("使用协议"), remindString.indexOf("使用协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        protocol.setText(spannableStringBuilder);
        protocol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 验证
     */
    public boolean verification() {
        userNameString = userName.getText().toString();
        userPwdString = userPwd.getText().toString();
        reUserPwdString = reUserPwd.getText().toString();
        emailString = email.getText().toString();
        if (!checkBox.isChecked()) {
            Toast.makeText(mContext, "必须要先同意使用条款与隐私协议", Toast.LENGTH_SHORT).show();
        }
        if (userNameString.length() == 0) {
            userName.setError(mContext
                    .getString(R.string.regist_check_username_1));
            return false;
        }
        if (!checkUserId(userNameString)) {
            userName.setError(mContext
                    .getString(R.string.regist_check_username_1));
            return false;
        }
        if (!checkUserName(userNameString)) {
            userName.setError(mContext
                    .getString(R.string.regist_check_username_2));
            return false;
        }
        if (userPwdString.length() == 0) {
            userPwd.setError(mContext
                    .getString(R.string.regist_check_userpwd_1));
            return false;
        }
        if (!checkUserPwd(userPwdString)) {
            userPwd.setError(mContext
                    .getString(R.string.regist_check_userpwd_1));
            return false;
        }
        if (!reUserPwdString.equals(userPwdString)) {
            reUserPwd.setError(mContext
                    .getString(R.string.regist_check_reuserpwd));
            return false;
        }
        if (emailString.length() == 0) {
            email.setError(getResources().getString(
                    R.string.regist_check_email_1));
            return false;
        }
        if (!emailCheck(emailString)) {
            email.setError(mContext.getString(R.string.regist_check_email_2));
            return false;
        }
        return true;
    }

    /**
     * 匹配用户名
     *
     * @param userId
     * @return
     */
    public boolean checkUserId(String userId) {
        if (userId.length() < 3 || userId.length() > 20)
            return false;
        return true;
    }

    /**
     * 匹配用户名2 验证非手机号 邮箱号
     *
     * @param userId
     * @return
     */
    public boolean checkUserName(String userId) {
        if (userId
                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
            return false;
        }
        if (userId.matches("^(1)\\d{10}$")) {
            return false;
        }

        return true;
    }

    /**
     * 匹配密码
     *
     * @param userPwd
     * @return
     */
    public boolean checkUserPwd(String userPwd) {
        if (userPwd.length() < 6 || userPwd.length() > 20)
            return false;
        return true;
    }

    /**
     * email格式匹配
     *
     * @param email
     * @return
     */
    public boolean emailCheck(String email) {
        return email
                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$");
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    finish();
                    break;
                case 1: // 弹出错误信息
                    CustomToast.showToast(mContext, R.string.regist_email_used);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.regist_userid_exist);
                    break;
                case 4:
                    CustomToast.showToast(mContext, msg.obj.toString());
                    break;
                case 5:
                    wettingDialog.show();
                    break;
                case 6:
                    wettingDialog.dismiss();
                    break;
                case 7:
                    CustomToast.showToast(mContext, R.string.regist_operating);
                    break;
            }
        }
    };

    private void regist() {
        ExeProtocol.exe(new RegistRequest(userName.getText().toString(),
                        userPwd.getText().toString(), email.getText().toString()),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        RegistResponse rr = (RegistResponse) bhr;
                        send = false;
                        handler.sendEmptyMessage(6);
                        if (rr.result.equals("111")) {
                            Looper.prepare();
                            AccountManagerLib.Instace(mContext).login(
                                    userName.getText().toString(),
                                    userPwd.getText().toString(),
                                    new OperateCallBack() {
                                        @Override
                                        public void success(String result) {
                                            String name = userName.getText().toString();
                                            String pass = userPwd.getText().toString();
                                            Timber.e("name:"+name+"pass:"+pass);
                                            AccountManagerLib.Instace(mContext).saveUserNameAndPwd(name,pass);
                                            SettingConfig.Instance().setAutoLogin(true);

                                            Intent intent = new Intent(
                                                    mContext, UpLoadImageActivity.class);
                                            intent.putExtra("regist", true);
                                            startActivity(intent);
                                            handler.sendEmptyMessage(0);
                                        }

                                        @Override
                                        public void fail(String message) {
                                            // TODO Auto-generated method stub

                                        }
                                    });
                            Looper.loop();
                        } else if (rr.result.equals("112")) {
                            handler.sendEmptyMessage(3);
                        } else if (rr.result.equals("114")) {
                            handler.obtainMessage(4, rr.message).sendToTarget();
                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    }

                    @Override
                    public void error() {
                        // TODO Auto-generated method stub
                        send = false;
                        handler.sendEmptyMessage(2);
                        handler.sendEmptyMessage(6);
                    }
                });
    }

    @Override
    protected void onResume() {
        // TODO 自动生成的方法存根
        super.onResume();
        findViewById(R.id.button_regist_phone).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO 自动生成的方法存根
                        startActivity(new Intent(mContext,
                                RegistByPhoneActivity.class));
                        finish();
                    }
                });
    }
}
