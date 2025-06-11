package com.iyuba.core.common.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.RegistByPhoneActivity;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.Login2Request;
import com.iyuba.core.common.protocol.base.Login2Response;
import com.iyuba.core.common.protocol.base.LoginRequest;
import com.iyuba.core.common.protocol.base.LoginResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.common.sqlite.op.UserInfoOp;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.event.MoneyAddEvent;
import com.iyuba.http.LOGUtils;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.ui.component.CommonProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import personal.iyuba.personalhomelibrary.PersonalHome;
import timber.log.Timber;

/**
 * 用户管理 用于用户信息的保存及权限判断
 *
 * @author chentong
 * @version 1.1 更改内容 引入userinfo数据结构统一管理用户信息
 */
public class AccountManagerLib {
    private static AccountManagerLib instance;
    private static Context mContext;
    public static final int LOGIN_STATUS_UNLOGIN = 0;
    public static final int LOGIN_STATUS_LOGIN = 1;
    public int loginStatus = LOGIN_STATUS_UNLOGIN; // 用户登录状态,默认为未登录状态
    public UserInfo userInfo;
    public String userId = "0"; // 用户ID
    public String userName = ""; // 用户姓名
    public String userPwd; // 用户密码
    public String vipStatus;//用户vip状态
    public String isteacher = "0";
    private boolean loginSuccess = false;
    public final static String USERNAME = "userName";
    public final static String USERID = "userId";
    public final static String USERPASSWORD = "userPassword";
    public final static String IYUBAAMOUNT = "currUserAmount";
    public final static String VALIDITY = "validity";
    public final static String ISVIP = "isvip";

    private String MONEY = "";

    private AccountManagerLib() {
    }

    ;

    public static synchronized AccountManagerLib Instace(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AccountManagerLib();
        }
        return instance;
    }

    /**
     * 检查当前用户是否登录
     *
     * @return
     */
    public boolean checkUserLogin() {
        if ((loginStatus == LOGIN_STATUS_LOGIN) && userInfo != null && userId != null) {//是临时，
            return true;
        }
        return false;
    }

    public void setLoginState(int state) {
        loginStatus = state;
        userId = ConfigManager.Instance().loadString("userId");
        String[] nameAndPwd = getUserNameAndPwd();
        userName = nameAndPwd[0];
        userPwd = nameAndPwd[1];
        userInfo = new UserInfoOp(mContext).selectData(userId);
        Timber.e("userid是啥 === " + userId);
        Timber.e("userInfo是不是空 === " + userInfo);
        if (userInfo != null) {
            ConfigManager.Instance().putInt(ISVIP,
                    Integer.parseInt(userInfo.vipStatus));
            Timber.e("userInfo是不是空 === " + userInfo);

        } else {
            ConfigManager.Instance().putInt(ISVIP, 0);
            Timber.e("userInfo是不是空 === " + userInfo);

        }
        vipStatus = String.valueOf(ConfigManager.Instance().loadInt(ISVIP));
        //传递用户参数给个人中心模块
//        PersonalManager.getInstance().setUserId(Integer.parseInt(userId));//修改
//        PersonalManager.getInstance().userName = userName;
//        PersonalManager.getInstance().setVipState(vipStatus);//新的修改！！！

        //        PersonalHome.setDebug(BuildConfig.DEBUG);
        PersonalHome.setAppInfo(Constant.APPID, Constant.AppName);
        PersonalHome.setIsCompress(true);
        PersonalHome.setCategoryType(Constant.APPName);
        PersonalHome.setEnableEditNickname(false);
//        PersonalHome.setUserComplain(true);

        PersonalHome.setMainPath(AccountManagerLib.class.getName());
        PersonalHome.setSaveUserinfo(Integer.parseInt(userId), userName, vipStatus);
        //传递用户参数给视频模块
        User user = new User();
        user.vipStatus = String.valueOf(vipStatus);
        user.uid = Integer.parseInt(userId);
        user.name = userName;

    }

    /**
     * 用户登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    public boolean login(final String userName, String userPwd,
                         final OperateCallBack rc) {
        this.userName = userName;
        this.userPwd = userPwd;
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
            return false;
        }

        String latitude;
        String longitude;
        latitude = "0";
        longitude = "0";
        ExeProtocol.exe(new LoginRequest(this.userName, this.userPwd,
                latitude, longitude), new ProtocolResponse() {

            @Override
            public void finish(BaseHttpResponse bhr) {
                LoginResponse rr = (LoginResponse) bhr;
                if (rr.result.equals("101")) {

                    // 登录成功
                    Refresh(rr);//写入数据
                    if (rc != null) {
                        rc.success(null);
                    }
                    AccountManagerLib manager = AccountManagerLib.Instace(mContext);
                    PersonalHome.setSaveUserinfo(manager.getUserId(), manager.userName, manager.getVipStringStatus());
                } else {
                    // 登录失败
                    handler.sendEmptyMessage(1);
                    loginSuccess = false;
                    if (rc != null) {
                        rc.fail(null);
                    }
                }
            }

            @Override
            public void error() {
                handler.sendEmptyMessage(2);
                loginSuccess = false;
                if (rc != null) {
                    rc.fail(null);
                }
            }
        });
        return loginSuccess;
    }

    /**
     * 用户登出
     *
     * @return
     */
    public boolean loginOut() {
        new UserInfoOp(mContext).delete(userId);
        loginStatus = LOGIN_STATUS_UNLOGIN;
        userId = null; // 用户ID
        userName = null; // 用户姓名
        userPwd = null; // 用户密码
        userInfo = null;
        SettingConfig.Instance().setAutoLogin(false);
        saveUserNameAndPwd("", "");
        ConfigManager.Instance().putInt(ISVIP, 0);
        ConfigManager.Instance().putInt("isteacher", 0);

        AccountManagerLib manager = AccountManagerLib.Instace(mContext);
        IyuUserManager.getInstance().logout();

        User user = new User();
        user.vipStatus = "0";
        user.uid = 0;
        user.name = null;
        return true;
    }

    /**
     * 更换用户
     *
     * @param userName
     * @param userPwd
     * @return
     */
    public boolean replaceUserLogin(String userName, String userPwd) {
        if (loginOut()) { // 登出
            if (login(userName, userPwd, null)) { // 登录
                return true;
            }
        }
        return false;
    }

    /**
     * 保存账户密码
     *
     * @param userName
     * @param userPwd
     */
    public void saveUserNameAndPwd(String userName, String userPwd) {
        ConfigManager.Instance().putString("userName", userName);
        ConfigManager.Instance().putString("userPwd", userPwd);
        SettingConfig.Instance().setAutoLogin(true);
    }

    /**
     * 获取用户名及密码
     *
     * @return string[2] [0]=userName,[1]=userPwd
     */
    public String[] getUserNameAndPwd() {
        String[] nameAndPwd = new String[]{
                ConfigManager.Instance().loadString("userName"),
                ConfigManager.Instance().loadString("userPwd")};
        return nameAndPwd;
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1: // 弹出错误信息
                    CustomToast.showToast(mContext, R.string.login_fail);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.login_faild);
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.person_vip_limit);
                    break;
            }
        }
    };

    /*
     * 处理userinfo数据写入
     */
    public void Refresh(LoginResponse rr) {

        userId = rr.uid; // 成功返回用户ID
        ConfigManager.Instance().putString("userId", userId);
        ConfigManager.Instance().putString("isteacher", rr.isteacher);
        ConfigManager.Instance().putString("vipStatus", rr.vip);
        ConfigManager.Instance().putString("expireTime", rr.validity);

        AccountManagerLib.Instace(mContext).userName = rr.username;
        userInfo = new UserInfoOp(mContext).selectData(userId);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.uid = userId;
        userInfo.username = userName;

        setMONEY(rr.money);
        Log.e("钱包", getMONEY() + "==");

        userInfo.iyubi = rr.amount;
        userInfo.vipStatus = rr.vip;
        userInfo.isteacher = rr.isteacher;

        isteacher = rr.isteacher;

        long time = Long.parseLong(rr.validity);
        if (time < 0) {
            userInfo.deadline = "终身VIP";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            try {
                long allLife = sdf.parse("2099-01-01").getTime() / 1000;
                if (time > allLife) {
                    userInfo.deadline = "终身VIP";
                } else {
                    userInfo.deadline = sdf.format(new Date(time * 1000));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        loginSuccess = true;
        loginStatus = LOGIN_STATUS_LOGIN;
        if (userInfo.vipStatus.equals("1")) {
            SettingConfig.Instance().setHighSpeed(true);
        } else {
            SettingConfig.Instance().setHighSpeed(false);
        }
        ConfigManager.Instance().putInt(ISVIP,
                Integer.parseInt(userInfo.vipStatus));

        User user = new User();
        user.vipStatus = rr.vip;
        user.uid = Integer.parseInt(rr.uid);
        user.name = rr.username;
        IyuUserManager.getInstance().setCurrentUser(user);
    }

    public String getMONEY() {
        return MONEY;
    }

    public void setMONEY(String MONEY) {
        this.MONEY = MONEY;
        EventBus.getDefault().post(new MoneyAddEvent(MONEY));
    }


    /**
     * 获得int型userid
     */

    public int getUserId() {
        if (userId != null && !userId.equals("")) {
            return Integer.parseInt(userId);
        }
        return 0;
    }

    /**
     * 获得String型vipSatus
     **/

    public String getVipStringStatus() {
        String vip = "0";
        vip = String.valueOf(ConfigManager.Instance().loadInt(ISVIP));

        if (userInfo != null) {
            userInfo.vipStatus = vip;
        }
        return vip;
    }

    public void startLogin() {
//        startActivity(LoginActivity.class);
//1.4暂时隐藏 以下
        boolean isVerifySupport = SecVerify.isVerifySupport();
        if (isVerifySupport && Constant.isPreVerifyDone) {
            verify();
        } else {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));

        }
    }


    /**
     * 免密登录
     */
    private void verify() {
        LOGUtils.i("Verify called");
        //需要在verify之前设置
        SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
            @Override
            public void initCallback(OAuthPageEventResultCallback cb) {
                cb.pageOpenCallback(new PageOpenedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " pageOpened");
                    }
                });
                cb.loginBtnClickedCallback(new LoginBtnClickedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " loginBtnClicked");
                    }
                });
                cb.agreementPageClosedCallback(new AgreementPageClosedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " agreementPageClosed");
                    }
                });
                cb.agreementPageOpenedCallback(new AgreementClickedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " agreementPageOpened");
                    }
                });
                cb.cusAgreement1ClickedCallback(new CusAgreement1ClickedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " cusAgreement1ClickedCallback");
                    }
                });
                cb.cusAgreement2ClickedCallback(new CusAgreement2ClickedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " cusAgreement2ClickedCallback");
                    }
                });
                cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
                    @Override
                    public void handle(boolean b) {
                        LOGUtils.i(System.currentTimeMillis() + " current status is " + b);
                    }
                });
                cb.pageCloseCallback(new PageClosedCallback() {
                    @Override
                    public void handle() {
                        LOGUtils.i(System.currentTimeMillis() + " pageClosed");
                        CommonProgressDialog.dismissProgressDialog();
                    }
                });
            }
        });
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {
                CommonProgressDialog.dismissProgressDialog();
                // 用户点击“其他登录方式”，处理自己的逻辑
                LOGUtils.i("onOtherLogin called");
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }

            @Override
            public void onUserCanceled() {
                CommonProgressDialog.dismissProgressDialog();
                SecVerify.finishOAuthPage();
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
                LOGUtils.i("onUserCanceled called");
            }

            @Override
            public void onComplete(VerifyResult data) {
                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                LOGUtils.i("onComplete called");
                tokenToPhone(data);
            }

            @Override
            public void onFailure(VerifyException e) {
                LOGUtils.i("onFailure called");
            }
        });
    }


    private void tokenToPhone(VerifyResult verifyResult) {
        if (verifyResult != null) {
            LOGUtils.i("verifyResult.getOperator()  " + verifyResult.getOperator());
            LOGUtils.i("verifyResult.getOpToken()  " + verifyResult.getOpToken());
            LOGUtils.i("verifyResult.getToken()  " + verifyResult.getToken());
            ExeProtocol.exe(new Login2Request(verifyResult), new ProtocolResponse() {
                @Override
                public void finish(BaseHttpResponse bhr) {
                    Login2Response lr = (Login2Response) bhr;
                    com.iyuba.core.util.LoginResponse un = lr.userInfo;

                    if (un == null) {
//                        ToastFactory.showShort(mContext, "验证失败，账号未注册");
                        mContext.startActivity(new Intent(mContext, RegistByPhoneActivity.class));
                        return;
                    }
                    LoginResponse loginResponse = new LoginResponse(un.getResult(), un.getUid(), un.getUsername(), un.getImgSrc(), un.getVipStatus(), un.getExpireTime(), un.getAmount(), un.getIsteacher(), un.getMoney());
                    // 登录成功
                    Refresh(loginResponse);//写入数据
               /*     if (rc != null) {
                        rc.success(null);
                    }*/
                    AccountManagerLib manager = AccountManagerLib.Instace(mContext);
//                    PersonalManager.getInstance().setUserId(manager.getUserId());//修改
//                    PersonalManager.getInstance().userName=manager.userName;
//                    PersonalManager.getInstance().setVipState(manager.getVipStringStatus());//新的修改！！！

//                    PersonalHome.setAppInfo(Constant.APPID,Constant.AppName);
//                    PersonalHome.setIsCompress(true);
//                    PersonalHome.setCategoryType(Constant.APPName);
//                    PersonalHome.setEnableEditNickname(false);
//                    PersonalHome.setMainPath(AccountManagerLib.class.getName());
//                    manager.setLoginState(1);


                    PersonalHome.setSaveUserinfo(manager.getUserId(), manager.userName, manager.getVipStringStatus());

                    new UserInfoOp(mContext).saveData(userInfo);

                    AccountManagerLib.Instace(mContext)
                            .saveUserNameAndPwd(manager.userName, userPwd);
                    User user = new User();
                    user.vipStatus = String.valueOf(manager.getVipStringStatus());
                    user.uid = manager.getUserId();
                    user.name = manager.userName;
                    EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布
//                    ((Activity) mContext).finish();
                    SecVerify.finishOAuthPage();

                    manager.setLoginState(1);
                    SettingConfig.Instance().setAutoLogin(true);
                }

                @Override
                public void error() {
//				intent.setClass(mContext, RegistActivity.class);
                    SecVerify.finishOAuthPage();
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));

                }
            });


        } else {
            LOGUtils.i("tokenToPhone verifyResult is null.");
            mContext.startActivity(new Intent(mContext, LoginActivity.class));

        }
    }

}
