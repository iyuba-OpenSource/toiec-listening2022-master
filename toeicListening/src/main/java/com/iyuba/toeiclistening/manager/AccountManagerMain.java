package com.iyuba.toeiclistening.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.entity.User;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;
import com.iyuba.toeiclistening.listener.OnResultListener;
import com.iyuba.toeiclistening.listener.RequestCallBack;
import com.iyuba.toeiclistening.payment.struct_user;
import com.iyuba.toeiclistening.protocol.LoginRequest;
import com.iyuba.toeiclistening.protocol.LoginResponse;
import com.iyuba.toeiclistening.setting.SettingConfig;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 用户管理 用于用户信息的保存及权限判断
 *
 * @author chentong
 */
public class AccountManagerMain {

    private static AccountManagerMain instance;
    private static Context mContext;
    public static final int LOGIN_STATUS_UNLOGIN = 0;
    public static final int LOGIN_STATUS_LOGIN = 1;
    public int loginStatus = LOGIN_STATUS_UNLOGIN; // 用户登录状态,默认为未登录状态
    public final static String USERNAME = "userName";
    public final static String USERPASSWORD = "userPassword";
    public final static String IYUBAAMOUNT = "currUserAmount";
    public final static String VALIDITY = "validity";
    public final static String ISVIP = "isVip";

    public User user = null;

    public String userId; // 用户ID
    public String userName; // 用户姓名
    public String userImg; // 用户头像
    public String userPwd; // 用户密码
    public String email;//用户邮箱
    public int isvip;// 是否VIP
    public String validity;// 有效期
    private String registError = "登录失败,请检查用户名和密码";
    public String amount;// 爱语币

    private boolean loginSuccess = false;


    private SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");

    private AccountManagerMain() {
    }

    ;

    public static AccountManagerMain Instace(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AccountManagerMain();
        }

        return instance;
    }

    /**
     * 检查当前用户是否登录
     *
     * @return
     */
    public boolean checkUserLogin() {
        if (loginStatus == LOGIN_STATUS_LOGIN) {
            return true;
        }
        return false;
    }

    /**
     * 用户登录
     *
     * @param nameOREmail
     * @param userPwd
     * @return
     */
    public boolean login(final String nameOREmail, final String userPwd,
                         final RequestCallBack rc) {
        this.userPwd = userPwd;

        ClientSession.Instace().asynGetResponse(
                new LoginRequest(nameOREmail, this.userPwd, "0", "0"),
                new IResponseReceiver() {

                    @Override
                    public void onResponse(BaseHttpResponse response,
                                           BaseHttpRequest request, int rspCookie) {
                        LoginResponse rr = (LoginResponse) response;
                        if (rr.result.equals("101")) {

                            // 登录成功
                            if (user == null) {
                                user = new User();
                            }
                            userId = rr.uid;
                            loginSuccess = true;
                            userName = rr.username;
                            userImg = rr.imgsrc;
                            //用户登陆后的的这个vip指的是全站vip，如果不是全站vip，还需要对用户的vip单独检测
                            isvip = Integer.parseInt(rr.vip);
                            //修改登录状态
                            loginStatus = LOGIN_STATUS_LOGIN;
                            SettingConfig.Instance().setVip(Integer.parseInt(rr.vip));
                            if (rr.vip.equals("1")) {
                                long time = Long.parseLong(rr.validity);
                                if (System.currentTimeMillis() / 1000 < time) {

                                    Date date = new Date(time * 1000);
                                    struct_user.struct_by_login(mContext,
                                            sdf.format(date));
                                    ConfigManagerMain.Instance().putString(VALIDITY, sdf.format(date));
                                }
                            } else {
                                PayManager.Instance(mContext).recoverAppVip(userId, "0",
                                        new OnResultListener() {
                                            @Override
                                            public void OnSuccessListener(String msg) {
                                                // TODO Auto-generated method stub
                                                SettingConfig.Instance().setVip(1);//购买了此产品为vip
                                            }

                                            @Override
                                            public void OnFailureListener(String msg) {
                                                // TODO Auto-generated method stub
                                                //可能是全站vip，此处不做处理
                                            }

                                        });

                            }
                            saveUserNameAndPwd(userName, userPwd);
                            SettingConfig.Instance().setIyubaAmount(rr.amount);
                            handler.sendEmptyMessage(4);
                            if (rc != null) {
                                rc.requestResult(true);
                            }
                        } else {
                            // 注册失败
                            handler.sendEmptyMessage(1);
                            loginSuccess = false;
                            if (rc != null) {
                                rc.requestResult(false);
                            }
                        }
                    }
                }, null, new INetStateReceiver() {

                    @Override
                    public void onStartSend(BaseHttpRequest request,
                                            int rspCookie, int totalLen) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStartRecv(BaseHttpRequest request,
                                            int rspCookie, int totalLen) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStartConnect(BaseHttpRequest request,
                                               int rspCookie) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSendFinish(BaseHttpRequest request,
                                             int rspCookie) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSend(BaseHttpRequest request, int rspCookie,
                                       int len) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onRecvFinish(BaseHttpRequest request,
                                             int rspCookie) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onRecv(BaseHttpRequest request, int rspCookie,
                                       int len) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onNetError(BaseHttpRequest request,
                                           int rspCookie, ErrorResponse errorInfo) {
                        handler.sendEmptyMessage(2);
                        handler.sendEmptyMessage(0);// 网络问题提示
                        loginSuccess = false;
                        if (rc != null) {
                            rc.requestResult(false);
                        }
                    }

                    @Override
                    public void onConnected(BaseHttpRequest request,
                                            int rspCookie) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onCancel(BaseHttpRequest request, int rspCookie) {
                        // TODO Auto-generated method stub

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
        loginStatus = LOGIN_STATUS_UNLOGIN;
        userId = null; // 用户ID
        userName = null; // 用户姓名
        userImg = null; // 用户昵称
        userPwd = null; // 用户密码
        isvip = 0;
        validity = "";
        SettingConfig.Instance().setAutoLogin(false);
        SettingConfig.Instance().setVip(0);
        saveUserNameAndPwd("", "");
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
        ConfigManagerMain.Instance().putString(USERNAME, userName);
        ConfigManagerMain.Instance().putString(USERPASSWORD, userPwd);
    }

    /**
     * 获取用户名及密码
     *
     * @return string[2] [0]=userName,[1]=userPwd
     */
    public String[] getUserNameAndPwd() {
        String[] nameAndPwd = new String[]{
                ConfigManagerMain.Instance().loadString(USERNAME),
                ConfigManagerMain.Instance().loadString(USERPASSWORD)};
        return nameAndPwd;
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1: // 弹出错误信息
                    CustomToast.showToast(mContext, registError, 1000);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.login_faild,
                            1000);
                    break;
                case 3:
                    CustomToast.showToast(mContext, "VIP有效期已过", 1000);
                    break;
                case 4:
                    CustomToast.showToast(mContext, "欢迎回来，" + userName, 1000);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ZDBHelper zhelper = new ZDBHelper(mContext);
                            ArrayList<TitleInfo> favTitleList = zhelper.getFavTitleInfos();
                            ArrayList<NewWord> favoriteWordList = zhelper.getNewWords(ConfigManagerMain.Instance().loadString(AccountManagerMain.USERNAME));
                            DataManager.Instance().favTitleInfoList = favTitleList;
                            DataManager.Instance().newWordList = favoriteWordList;
                        }
                    }).start();
            }
        }
    };

    public void Refresh() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ZDBHelper helper = new ZDBHelper(mContext);
        User user = helper.getUser(userName);
        if (user == null) {
            user = new User(userName);
            helper.addUser(user);
        } else if (user.isvip > 0) {//isvip == 1
            if (!user.deadline.equals("终身VIP"))
                try {
                    Date deadline = df.parse(user.deadline);
                    if (deadline.getTime() < date.getTime()) {
                        user = new User(userName);
                        helper.updateUser(user);
                        handler.sendEmptyMessage(3);
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        isvip = user.isvip;
        ConfigManagerMain.Instance().putInt(AccountManagerMain.ISVIP, isvip);
        validity = user.deadline;
        ConfigManagerMain.Instance().putString("validity", validity);
    }

/*    private void setOption() {
        // TODO Auto-generated method stub
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);//禁止启用缓存定位
        option.setPoiNumber(5);    //最多返回POI个数
        option.setPoiDistance(1000); //poi查询距离
        option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
        System.out.println("option===" + option.getCoorType());
        System.out.println("mLocClient===" + mLocClient.getVersion());
        mLocClient.setLocOption(option);
    }*/
}