package com.iyuba.core.common.activity.forget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.core.R;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestForgetCode;
import com.iyuba.core.common.protocol.message.ResponseForgetCheck;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.SmsContent;
import com.iyuba.core.common.util.TelNumMatch;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.http.LOGUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ForgetRequestCodeActivity extends AppCompatActivity {


    private Context mContext;
    private EditText phoneNum, messageCode;
    private Button getCodeButton;
    private TextView toEmailButton;
    private Button backBtn;
    private String phoneNumString = "", messageCodeString = "";
    private Timer timer;
    private TextView protocol;
    private EventHandler eh;
    private TimerTask timerTask;
    private SmsContent smsContent;
    private CustomDialog waittingDialog;
    private EditTextWatch editTextWatch;
    private Button nextstep_unfocus;
    private Button nextstep_focus;
    private String username,userPhoto;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_request_code);

        mContext = this;
        editTextWatch =new EditTextWatch();
         CrashApplication.addActivity(this);
        waittingDialog = WaittingDialog.showDialog(mContext);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    private void initView(){
        messageCode = (EditText) findViewById(R.id.regist_phone_code);
        messageCode.addTextChangedListener(editTextWatch);
        phoneNum = (EditText) findViewById(R.id.regist_phone_numb);
        phoneNum.addTextChangedListener(editTextWatch);
        getCodeButton = (Button) findViewById(R.id.regist_getcode);
        nextstep_unfocus = (Button) findViewById(R.id.nextstep_unfocus);
        nextstep_unfocus.setEnabled(false);
        nextstep_focus = (Button) findViewById(R.id.nextstep_focus);
        title =  findViewById(R.id.play_title_info);
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

        //smsContent = new SmsContent(ForgetRequestCodeActivity.this, handler_verify);

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        getCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根

                //获取手机验证吗
                if (Build.VERSION.SDK_INT >= 23) {
                    ForgetRequestCodeActivityPermissionsDispatcher.permissionRequstWithPermissionCheck(ForgetRequestCodeActivity.this);
                } else {
                    permissionRequst();
                }


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

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//测试后门
//                Intent intent = new Intent();
//                intent.setClass(mContext, ForgetPassWordActivity.class);//修改密码
//                intent.putExtra("phoneNumb", "18701342549");
//                intent.putExtra("userName", "myj1020");
//                intent.putExtra("userPhoto", "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=6036246");
//                startActivity(intent);
//                finish();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handlerSms = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    CustomToast.showToast(mContext, "验证成功");
                    Intent intent = new Intent();
                    intent.setClass(mContext, ForgetPassWordActivity.class);//修改密码
                    intent.putExtra("phoneNumb", phoneNumString);
                    intent.putExtra("userName", username);
                    intent.putExtra("userPhoto", userPhoto);
                    startActivity(intent);
                    finish();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        boolean smart = (Boolean)msg.obj;
                        if(smart) {
                            CustomToast.showToast(mContext, "已通过智能验证，成功");
                            Intent intent = new Intent();
                            intent.setClass(mContext, ForgetPassWordActivity.class);//修改密码
                            intent.putExtra("phoneNumb", phoneNumString);
                            intent.putExtra("userName", username);
                            intent.putExtra("userPhoto", userPhoto);
                            startActivity(intent);
                            finish();
                            //通过智能验证 ,已经关闭
                        } else {
                            //依然走短信验证
                            Log.e("tagyzm",""+smart);
                            CustomToast.showToast(mContext, "验证码已经发送，请等待接收");
                    }
                }
            } else {
                Log.e("RegistByPhoneActivity"+result,"");
                //CustomToast.showToast(mContext, "验证失败，请输入正确的验证码！");
                getCodeButton.setText("获取验证码");
                getCodeButton.setEnabled(true);
            }
        }
    };


    public boolean verification() {
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
        if (messageCodeString.length() == 0) {
            messageCode.setError("验证码不能为空");
            return false;
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
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
                getCodeButton.setEnabled(false);
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

    public class EditTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if((s.toString().length()==4&&verificationNum())
                    ||(verificationNum()&&messageCode.getText().toString().length()==4)){
                if(timer!=null){
                    timer.cancel();
                }
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            }else{
                nextstep_focus.setVisibility(View.GONE);
                nextstep_focus.setEnabled(false);
            }

        }
        @Override
        public void afterTextChanged(Editable s) {

        }

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
        if(flag ==5){
            return false;
        }else {
            return true;
        }
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_SMS})
    public void permissionRequst() {

        if (verificationNum()) {
            if(timer!=null){
                timer.cancel();
            }
            handler_waitting.sendEmptyMessage(1);
            phoneNumString = phoneNum.getText().toString();
            ExeProtocol.exe(new RequestForgetCode(
                    phoneNumString), new ProtocolResponse() {

                @Override
                public void finish(BaseHttpResponse bhr) {
                    Log.e("RequestForgetCode","请求成功！"+bhr);
                    ResponseForgetCheck res = (ResponseForgetCheck) bhr;
                    Log.e("RequestForgetCode","请求成功！2");
                    if (res != null) {
                        if("101".equals(res.result)){
                            handler_verify.sendEmptyMessage(1);
                            //ForgetRequestCodeActivity.this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
                            LOGUtils.e("http"+res.result);
                        }else if("102".equals(res.result)){//102 没有注册过 不能通过
                            handler_waitting.sendEmptyMessage(3);//未注册
                        }
                        handler_waitting.sendEmptyMessage(2);
                        username = res.userName;
                        userPhoto = res.userPhoto;
                    }

                }

                @Override
                public void error() {
                    handler_waitting.sendEmptyMessage(3);
                    LOGUtils.e("请求验证码失败");
                }
            });
        } else {
            CustomToast.showToast(mContext, "电话不能为空");
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler_waitting = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    waittingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext,
                            "手机号还未注册，请检查输入的号码~");
                    waittingDialog.dismiss();
                    break;
                    default:
            }
        }
    };

    @OnPermissionDenied({Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_SMS})
    public void permissionDenied() {
        CustomToast.showToast(mContext, "申请权限失败,无法获取验证码");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ForgetRequestCodeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
