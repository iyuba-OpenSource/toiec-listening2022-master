package com.iyuba.core.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.forget.ForgetRequestCodeActivity;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.op.UserInfoOp;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.ChangeVideoEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录界面 真正登录
 *
 * @author chentong
 * @version 1.2
 *   修改内容 API更新; userinfo引入; VIP更新方式变化
 */
public class LoginActivity extends BasisActivity {

    private Button backBtn;
    private Button registBtn, loginBtn;
    private String userName, userPwd;
    private EditText userNameET, userPwdET;
    private CheckBox autoLogin;
    private CustomDialog cd;
    private Context mContext;
    private TextView findPassword,findPasswordWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);
		 CrashApplication.addActivity(this);
        mContext = this;
        cd = WaittingDialog.showDialog(mContext);
        userNameET = findViewById(R.id.editText_userId);
        userPwdET = findViewById(R.id.editText_userPwd);
        if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//            System.out.println("之后--------------------------");
            String[] nameAndPwd = AccountManagerLib.Instace(mContext).getUserNameAndPwd();
            userName = nameAndPwd[0];
            userPwd = nameAndPwd[1];
            handler.sendEmptyMessage(0);
        }



        autoLogin = (CheckBox) findViewById(R.id.checkBox_autoLogin);
        //默认为true
        SettingConfig.Instance().setAutoLogin(true);
        autoLogin.setChecked(true);

        autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                SettingConfig.Instance().setAutoLogin(isChecked);
            }
        });

        if (!autoLogin.isChecked()) {
//            System.out.println("之前--------------------------");
            autoLogin.setChecked(true);
//            System.out.println("之后--------------------------");
            SettingConfig.Instance().setAutoLogin(true);
        }

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        loginBtn = findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登录操作
                if (verification()) {
                    handler.sendEmptyMessage(1);

                    AccountManagerLib.Instace(mContext).login(userName, userPwd,
                            new OperateCallBack() {
                                @Override
                                public void success(String result) {
                                    if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
                                        AccountManagerLib.Instace(mContext)
                                                .saveUserNameAndPwd(userName, userPwd);
                                        SettingConfig.Instance().setAutoLogin(true);
                                        LogUtil.e("登录成功，有账号密码");
                                        new UserInfoOp(mContext).saveData(AccountManagerLib.Instace(mContext).userInfo);
                                    } else {
                                        AccountManagerLib.Instace(mContext)
                                                .saveUserNameAndPwd("", "");
                                    }
                                    handler.sendEmptyMessage(2);
                                    handler.sendEmptyMessage(3);
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(2);
                                }
                            });
                }
            }
        });
        registBtn = (Button) findViewById(R.id.button_regist);
        registBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //修改为手机注册优先
                Intent intent = new Intent();
//				intent.setClass(mContext, RegistActivity.class);
                intent.setClass(mContext, RegistByPhoneActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findPasswordWeb = (TextView) findViewById(R.id.find_password_web);
        findPasswordWeb.setText(Html.fromHtml("<a href=\"http://m."
                +com.iyuba.core.util.Constant.IYBHttpHead+"/m_login/inputPhonefp.jsp?\">"
                                + getResources().getString(
                                R.string.login_find_password_web) + "</a>"));
        findPasswordWeb.setMovementMethod(LinkMovementMethod.getInstance());

        findPassword=findViewById(R.id.find_password);
        findPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetRequestCodeActivity.class));
            }
        });
    }

    /**
     * 验证
     */
    public boolean verification() {
        userName = userNameET.getText().toString();
        userPwd = userPwdET.getText().toString();
        if (userName.length() < 3) {
            userNameET.setError(getResources().getString(
                    R.string.login_check_effective_user_id));
            return false;
        }

        if (userPwd.length() == 0) {
            userPwdET.setError(getResources().getString(
                    R.string.login_check_user_pwd_null));
            return false;
        }
        if (!checkUserPwd(userPwd)) {
            userPwdET.setError(getResources().getString(
                    R.string.login_check_user_pwd_constraint));
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

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(userName!=null&&!userName.equals(""))userNameET.setText(userName);
                    if(userPwd!=null&&!userPwd.equals(""))userPwdET.setText(userPwd);
                    break;
                case 1:
                    cd.show();
                    break;
                case 2:
                    cd.dismiss();
                    break;
                case 3:
                    EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布
                    finish();
                    break;
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        if (AccountManagerLib.Instace(mContext).userName == null) {
            SettingConfig.Instance().setAutoLogin(false);
        }
    }
}
