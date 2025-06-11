package com.iyuba.core.common.activity;

/**
 * 手机注册界面
 *
 * @author czf
 * @version 1.0 2019.4.18 更换验证手机号接口
 * 2019.07.22 注册前需要权限了，READ_SMS 不知道哪里需要它，后台会报错
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iyuba.core.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestSubmitMessageCode;
import com.iyuba.core.common.protocol.message.ResponseSubmitMessageCode;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.SmsContent;
import com.iyuba.core.common.util.TelNumMatch;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.util.PrivacyDialog;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class RegistByPhoneActivity extends BasisActivity {
    private Context mContext;
    private EditText phoneNum, messageCode;
    private Button getCodeButton;
    private TextView toEmailButton;
    private Button backBtn;
    private String phoneNumString = "", messageCodeString = "";
    private Timer timer;
    private TextView protocol;
    private EventHandler eh;
    private CheckBox checkBox;
    private TimerTask timerTask;
    private SmsContent smsContent;
    private CustomDialog waittingDialog;
    private EditTextWatch editTextWatch;
    private Button nextstep_unfocus;//下一步 不可点击
    private Button nextstep_focus;//下一步 可点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        editTextWatch = new EditTextWatch();
        setContentView(R.layout.regist_layout_phone);
        CrashApplication.addActivity(this);
        waittingDialog = WaittingDialog.showDialog(mContext);
        messageCode = (EditText) findViewById(R.id.regist_phone_code);
        messageCode.addTextChangedListener(editTextWatch);
        phoneNum = (EditText) findViewById(R.id.regist_phone_numb);
        phoneNum.addTextChangedListener(editTextWatch);
        getCodeButton = (Button) findViewById(R.id.regist_getcode);
        nextstep_unfocus = (Button) findViewById(R.id.nextstep_unfocus);
        nextstep_unfocus.setEnabled(false);
        nextstep_focus = (Button) findViewById(R.id.nextstep_focus);

        //SMSSDK.initSDK(this, Constant.SMSAPPID, Constant.SMSAPPSECRET);
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handlerSms.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
        //smsContent = new SmsContent(RegistByPhoneActivity.this, handler_verify);
        protocol = findViewById(R.id.protocol);
        checkBox = findViewById(R.id.register_checkbox);
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
                ds.setColor(ContextCompat.getColor(RegistByPhoneActivity.this, R.color.app_color));
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
                ds.setColor(ContextCompat.getColor(RegistByPhoneActivity.this, R.color.app_color));
            }
        };
        spannableStringBuilder.setSpan(secretString, remindString.indexOf("隐私政策"), remindString.indexOf("隐私政策") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(policyString, remindString.indexOf("使用协议"), remindString.indexOf("使用协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        protocol.setText(spannableStringBuilder);
        protocol.setMovementMethod(LinkMovementMethod.getInstance());
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toEmailButton = (TextView) findViewById(R.id.regist_email);
        toEmailButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        toEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, RegisterEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        getCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取手机验证吗
             /*   if (Build.VERSION.SDK_INT >= 23) {
                    RegistByPhoneActivityPermissionsDispatcher.permissionRequestWithPermissionCheck(RegistByPhoneActivity.this);
                } else {

                }*/

                permissionRequest();
            }
        });
        nextstep_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verification()) {
                    SMSSDK.submitVerificationCode("86", phoneNumString,
                            messageCode.getText().toString());
                } else {
                    CustomToast.showToast(mContext, "验证码不能为空");
                }

            }
        });
    }

    public class EditTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if ((s.toString().length() == 4 && verificationNum()) || (verificationNum() && messageCode.getText().toString().length() == 4)) {
                if (timer != null) {
                    timer.cancel();
                }
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else {
                nextstep_focus.setVisibility(View.GONE);
                nextstep_focus.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
        if (waittingDialog.isShowing())
            waittingDialog.dismiss();
    }

    public boolean verification() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (!checkBox.isChecked()) {
            CustomToast.showToast(mContext, "必须要先同意使用条款与隐私协议");
            return false;
        }
        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }
        if (messageCodeString.length() == 0) {
            messageCode.setError("验证码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 验证
     */
    public boolean verificationNum() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }

        return true;
    }

    public boolean checkPhoneNum(String userId) {
        if (userId.length() < 2)
            return false;
        TelNumMatch match = new TelNumMatch(userId);
        int flag = match.matchNum();
        /*不check 号码的正确性，只check 号码的长度*/
		/*if (flag == 1 || flag == 2 || flag == 3) {
			return true;
		} else {
			return false;
		}*/
        if (flag == 5) {
            return false;
        } else {
            return true;
        }
    }

    Handler handlerSms = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    CustomToast.showToast(mContext, "验证成功");
                    Intent intent = new Intent();
                    intent.setClass(mContext, RegistSubmitActivity.class);
                    intent.putExtra("phoneNumb", phoneNumString);
                    startActivity(intent);
                    finish();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    CustomToast.showToast(mContext, "验证码已经发送，请等待接收");
                }
            } else {
                Log.e("RegistByPhoneActivity", "" + result);
                //CustomToast.showToast(mContext, "验证失败，请输入正确的验证码！");
                getCodeButton.setText("获取验证码");
                getCodeButton.setEnabled(true);
            }
        }
    };

    Handler handler_time = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what > 0) {
                getCodeButton.setText("重新发送(" + msg.what + "s)");
            } else {
                timer.cancel();
                getCodeButton.setEnabled(true);
                getCodeButton.setText("获取验证码");
            }
        }
    };

    Handler handler_waitting = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    if (waittingDialog.isShowing())
                        waittingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext, "手机号已注册，请换一个号码试试~");
                    break;
                case 4:
                    CustomToast.showToast(mContext, "电话不能为空");
                    break;
            }
        }
    };

    Handler handler_verify = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what == 0) {
                timer.cancel();
                /*
                 * getCodeButton.setText("下一步"); getCodeButton.setEnabled(true);
                 */
                String verifyCode = (String) msg.obj;
                messageCode.setText(verifyCode);
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else if (msg.what == 1) {
                SMSSDK.getVerificationCode("86", phoneNum.getText().toString());
                timer = new Timer();
                timerTask = new TimerTask() {
                    int i = 60;

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = i--;
                        handler_time.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 1000, 1000);
                getCodeButton.setTextColor(Color.WHITE);
                /*getCodeButton.setEnabled(false);*/
            }
        }
    };


    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_SMS})
    public void permissionRequest() {

        getCodeButton.setEnabled(false);
        if (verificationNum()) {

            if (timer != null) {
                timer.cancel();
            }
            handler_waitting.sendEmptyMessage(1);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            phoneNumString = phoneNum.getText().toString();

            ExeProtocol.exe(new RequestSubmitMessageCode(phoneNumString), new ProtocolResponse() {

                @Override
                public void finish(BaseHttpResponse bhr) {
                    ResponseSubmitMessageCode res = (ResponseSubmitMessageCode) bhr;
                    if (res != null) {
                        if (!res.result.equals("101")) {//101为注册过
                            handler_verify.sendEmptyMessage(1);
                            //RegistByPhoneActivity.this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
                        } else if (res.result.equals("101")) {
                            handler_waitting.sendEmptyMessage(3);
                        }
                        handler_waitting.sendEmptyMessage(2);
                    }

                }

                @Override
                public void error() {
                    handler_waitting.sendEmptyMessage(2);
                    getCodeButton.setEnabled(true);
                }
            });
        } else {
            handler_waitting.sendEmptyMessage(4);
            getCodeButton.setEnabled(true);
        }
    }

    @OnPermissionDenied({Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_SMS})
    public void permissionDenied() {
        CustomToast.showToast(mContext, "申请权限失败,无法获取验证码");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RegistByPhoneActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        permissionRequest();
    }
}
