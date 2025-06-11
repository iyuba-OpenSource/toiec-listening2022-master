package com.iyuba.core.me.activity;


import static com.iyuba.core.common.manager.AccountManagerLib.LOGIN_STATUS_UNLOGIN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.stetho.common.LogUtil;
import com.google.gson.Gson;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.UpdateServiceApi;
import com.iyuba.core.common.protocol.UpdateServiceResp;
import com.iyuba.core.common.protocol.base.GradeRequest;
import com.iyuba.core.common.protocol.base.GradeResponse;
import com.iyuba.core.common.protocol.message.RequestBasicUserInfo;
import com.iyuba.core.common.protocol.message.RequestNewInfo;
import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.common.protocol.message.ResponseBasicUserInfo;
import com.iyuba.core.common.protocol.message.ResponseNewInfo;
import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.db.Emotion;
import com.iyuba.core.common.sqlite.mode.EvaluateRecord;
import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.common.sqlite.mode.test.WordsUpdateRecord;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.CheckGrade;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.Expression;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.event.GoTestCollectEvent;
import com.iyuba.core.event.LoginOutEvent;
import com.iyuba.core.event.LogoutEvent;
import com.iyuba.core.event.MoneyAddEvent;
import com.iyuba.core.event.TestLibFragmentRefreashEvent;
import com.iyuba.core.event.UpdateEvaluateRecordEvent;
import com.iyuba.core.event.UpdateExerciseRecordEvent;
import com.iyuba.core.event.UpdateListenRecordEvent;
import com.iyuba.core.event.UpdateWordsRecordEvent;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeRequest;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeResponse;
import com.iyuba.core.me.protocol.GetTestRecordDetailRequest;
import com.iyuba.core.me.protocol.GetTestRecordDetailResponse;
import com.iyuba.core.me.protocol.GetVoaTestRecordRequest;
import com.iyuba.core.me.protocol.GetVoaTestRecordResponse;
import com.iyuba.core.me.protocol.GetWordsRequest;
import com.iyuba.core.me.protocol.GetWordsResponse;
import com.iyuba.core.me.protocol.SignRequest;
import com.iyuba.core.me.protocol.SignResponse;
import com.iyuba.core.me.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.core.teacher.activity.AboutActivity;
import com.iyuba.core.teacher.activity.Feedback;
import com.iyuba.core.teacher.activity.QuesListActivity;
import com.iyuba.core.teacher.activity.QuestionNotice;
import com.iyuba.core.teacher.activity.SettingActivity;
import com.iyuba.core.teacher.activity.TheQuesListActivity;
import com.iyuba.core.util.PrivacyDialog;
import com.iyuba.headlinelibrary.data.record.RecordUpdate;
import com.iyuba.http.LOGUtils;
import com.iyuba.imooclib.ui.record.PurchaseRecordActivity;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * create 2018-12-03
 * by 赵皓
 */
public class MeMainFragment extends Fragment {

    private TextView tv_wallet;
    private RelativeLayout me_send_buglog, me_send_wallet;
    private View noLogin, login; // 登录提示面板
    private Button loginBtn, logout;
    private Context mContext;
    private ImageView photo;
    private TextView name, state, listem_time,
            integral, position, lv;
    private View person;
    private View stateView, messageView, vipView, btn_dk, collection;
    private View discover_rqlist, discover_qnotice, discover_myq, discover_mysub, updateService,
            discover_iyubaset, me_send_book, meSummary, secret, policy, under_policy;
    private UserInfo userInfo;
    private View meView;
    private RelativeLayout about_btn, feedback, meRank, mTestCollect, meRecordUpdate;
    private String moneyNum;
    private List<ListeningTestRecord> listeningDeliver = new ArrayList<>();
    private List<WordsUpdateRecord> wordsUpdateDeliver = new ArrayList<>();
    private List<ExerciseRecord> exerciseDeliver = new ArrayList<>();
    private List<EvaluateRecord> evaluateDeliver = new ArrayList<>();

    private boolean isWords = false;
    private boolean isListen = false;
    private boolean isTest = false;
    private boolean isEvaluate = false;
    private boolean isRecordUpdate = false;
    public boolean isError = false;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout me_srl;


    public MeMainFragment() {
    }

    public static MeMainFragment newInstance(String title) {
        MeMainFragment fragment = new MeMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        View view = inflater.inflate(R.layout.fragment_me2, container, false);
        meView = view;
        mContext = getActivity();
        init(view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);

        getLetter();//是否显示新消息
        checkLogin();
        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

            me_srl.setEnabled(true);
            String id = AccountManagerLib.Instace(mContext).userId;
            if (id != null && !id.isEmpty() && !id.equals("0")) {
                loadData(id);
            } else {
                handler.sendEmptyMessage(7);
            }
        } else {

            me_srl.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取消息中心是否有新消息
     */
    private void getLetter() {

        String uId = AccountManagerLib.Instace(mContext).userId;
        if (uId != null && !uId.equals("0")) {//new letter
            ClientSession.Instace()
                    .asynGetResponse(
                            new RequestNewInfo(uId), (response, request, rspCookie) -> {

                                ResponseNewInfo rs = (ResponseNewInfo) response;
                                if (rs.letter > 0) {
                                    handler.sendEmptyMessage(2);
                                } else {
                                    handler.sendEmptyMessage(5);
                                }
                            });
        }
    }

    /**
     * 通过用户id获取用户数据
     *
     * @param id
     */
    private void loadData(final String id) {

        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {

                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
                        userInfo = response.userInfo;
                        AccountManagerLib.Instace(mContext).userInfo = userInfo;

                        DecimalFormat decimalFormat = new DecimalFormat("##.##");
                        new Handler(Looper.getMainLooper()).post(() -> {

                            tv_wallet.setText("我的钱包：" + decimalFormat.format(Integer.parseInt(response.userInfo.money) / 100.0) + "元");
                        });


                        //handler.sendEmptyMessage(3);//写入数据后，刷新
                        Looper.prepare();
                        ExeProtocol.exe(new GradeRequest(id),
                                new ProtocolResponse() {
                                    @Override
                                    public void finish(BaseHttpResponse bhr) {
                                        GradeResponse response = (GradeResponse) bhr;
                                        userInfo.studytime = Integer
                                                .parseInt(response.totalTime);
                                        userInfo.position = response.positionByTime;
                                        handler.sendEmptyMessage(3);//写入数据后，刷新
                                        //EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布 刷新
                                    }

                                    @Override
                                    public void error() {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(7);//注销
                                    }
                                });
                        Looper.loop();
                    }

                    @Override
                    public void error() {
                        handler.sendEmptyMessage(7);//注销
                    }
                });
    }

    private boolean checkLogin() {

        userInfo = AccountManagerLib.Instace(mContext).userInfo;
        if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {//如果没登录

            LogUtil.e("登录状态： 未登录");
            noLogin.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountManagerLib.Instace(mContext).startLogin();
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
                }
            });
            logout.setVisibility(View.GONE);
            btn_dk.setVisibility(View.GONE);//剑桥英语小说馆

            feedback.setVisibility(View.GONE);
            about_btn.setVisibility(View.VISIBLE);
            secret.setVisibility(View.GONE);
            policy.setVisibility(View.GONE);
            under_policy.setVisibility(View.GONE);
            me_send_buglog.setVisibility(View.GONE);
            me_send_wallet.setVisibility(View.GONE);
            return false;
        } else {

            noLogin.setVisibility(View.GONE);
            me_send_wallet.setVisibility(View.VISIBLE);
            me_send_buglog.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            btn_dk.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            secret.setVisibility(View.VISIBLE);
            policy.setVisibility(View.VISIBLE);
            under_policy.setVisibility(View.VISIBLE);

            if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
                logout.setText(R.string.login_login);
            } else {
                logout.setText(R.string.exit2);
            }

            feedback.setVisibility(View.VISIBLE);
            about_btn.setVisibility(View.VISIBLE);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getResources().getString(R.string.alert_title))
                            .setMessage("退出登录")
                            .setPositiveButton(
                                    getResources().getString(
                                            R.string.alert_btn_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            handler.sendEmptyMessage(4);
                                        }
                                    })
                            .setNeutralButton(
                                    getResources().getString(
                                            R.string.alert_btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).create();
                    dialog.show();
                }
            });
            btn_dk.setOnClickListener(ocl);
            setTextViewContent();
            return true;
        }
    }

    private void init(View view) {

        me_srl = view.findViewById(R.id.me_srl);
        noLogin = view.findViewById(R.id.noLogin);
        login = view.findViewById(R.id.login);
        logout = view.findViewById(R.id.logout);
        btn_dk = view.findViewById(R.id.button_dk);
        btn_dk.setVisibility(View.VISIBLE);//打卡显示

        loginBtn = view.findViewById(R.id.button_to_login);

        person = view.findViewById(R.id.personalhome);
        photo = view.findViewById(R.id.me_pic);
        name = view.findViewById(R.id.me_name);
        state = view.findViewById(R.id.me_state);
        listem_time = view.findViewById(R.id.me_listem_time);
        position = view.findViewById(R.id.me_position);
        lv = view.findViewById(R.id.lv);

        integral = view.findViewById(R.id.Integral_notification);
        stateView = view.findViewById(R.id.me_state_change);
        vipView = view.findViewById(R.id.me_vip);
        collection = view.findViewById(R.id.me_collect_layout);
        messageView = view.findViewById(R.id.me_message);

        discover_rqlist = view.findViewById(R.id.discover_rqlist);
        discover_qnotice = view.findViewById(R.id.discover_qnotice);
        discover_myq = view.findViewById(R.id.discover_myq);
        discover_mysub = view.findViewById(R.id.discover_mysub);
        discover_iyubaset = view.findViewById(R.id.discover_iyubaset);

//        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
//            //是老师，显示我的回复
//            discover_mysub.setVisibility(View.VISIBLE);
//            discover_myq.setVisibility(View.VISIBLE);
//        } else {
//            //不是老师，显示我的问题
//            discover_myq.setVisibility(View.VISIBLE);
//            discover_mysub.setVisibility(View.GONE);
//        }

        me_send_book = view.findViewById(R.id.me_send_book);

        feedback = view.findViewById(R.id.feedback_btn);
        about_btn = view.findViewById(R.id.about_btn);
        meRank = view.findViewById(R.id.me_rank);
        meRecordUpdate = view.findViewById(R.id.me_record_update);
        mTestCollect = view.findViewById(R.id.me_collect_layout_test);
        meSummary = view.findViewById(R.id.me_study_summary);
        secret = view.findViewById(R.id.secret_btn);
        policy = view.findViewById(R.id.policy_btn);
        updateService = view.findViewById(R.id.update_service);
        under_policy = view.findViewById(R.id.under_policy);
        view.findViewById(R.id.my_credits).setOnClickListener(ocl);
        if (ConfigManager.Instance().loadInt("startCount", 0) >= 20) {
            me_send_book.setVisibility(View.VISIBLE);
        }


        me_send_buglog = view.findViewById(R.id.me_send_buglog);
        me_send_wallet = view.findViewById(R.id.me_send_wallet);
        tv_wallet = view.findViewById(R.id.tv_wallet);

        setViewClick();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViewClick() {
        person.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        vipView.setOnClickListener(ocl);
        collection.setOnClickListener(ocl);
        messageView.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        discover_rqlist.setOnClickListener(ocl);
        discover_qnotice.setOnClickListener(ocl);
        discover_myq.setOnClickListener(ocl);
        discover_mysub.setOnClickListener(ocl);
        discover_iyubaset.setOnClickListener(ocl);
        me_send_book.setOnClickListener(ocl);
        btn_dk.setOnClickListener(ocl);
        feedback.setOnClickListener(ocl);
        about_btn.setOnClickListener(ocl);
        meRank.setOnClickListener(ocl);
        meRecordUpdate.setOnClickListener(ocl);
        mTestCollect.setOnClickListener(ocl);
        meSummary.setOnClickListener(ocl);
        secret.setOnClickListener(ocl);
        policy.setOnClickListener(ocl);
        updateService.setOnClickListener(ocl);

        me_send_buglog.setOnClickListener(v -> {

            String uId = AccountManagerLib.Instace(mContext).userId;
            if (uId.equals("0")) {

                AccountManagerLib.Instace(mContext).startLogin();
            } else {

                startActivity(PurchaseRecordActivity.buildIntent(requireActivity()));
            }
        });

        me_send_wallet.setOnClickListener(v -> {


            String uId = AccountManagerLib.Instace(mContext).userId;
            if (uId.equals("0")) {

                AccountManagerLib.Instace(mContext).startLogin();
            } else {

                Intent intent = new Intent("com.iyuba.toeiclistening.activity.me.MyWalletActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        me_srl.setOnRefreshListener(() -> {

            String id = AccountManagerLib.Instace(mContext).userId;
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                me_srl.setRefreshing(false);
                loadData(id);
            }
        });
    }

    /**
     * 写入文字数据
     */
    private void setTextViewContent() {
        GitHubImageLoader.Instace(mContext).setCirclePic(
                AccountManagerLib.Instace(mContext).userId, photo);
//		name.setText(AccountManagerLib.Instace(mContext).userName);
        String userName1 = AccountManagerLib.Instace(mContext).userName;
        if (!TextUtils.isEmpty(userName1)) {
            userName1 = userInfo.username;
        }
        name.setText(userName1);
        if (ConfigManager.Instance().loadInt("isvip") > 0) {//== 1
            Drawable img = mContext.getResources().getDrawable(R.drawable.vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
            LOGUtils.e("Vip 是ViP" + ConfigManager.Instance().loadInt("isvip"));
        } else {
            Drawable img = mContext.getResources().getDrawable(
                    R.drawable.no_vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
            LOGUtils.e("Vip 不是ViP" + ConfigManager.Instance().loadInt("isvip"));
        }
        if (userInfo.text == null) {
            state.setText(R.string.social_default_state);
        } else {
            String zhengze = "image[0-9]{2}|image[0-9]";
            Emotion emotion = new Emotion();
            userInfo.text = emotion.replace(userInfo.text);
            SpannableString spannableString = Expression.getExpressionString(
                    mContext, userInfo.text, zhengze);
            state.setText(spannableString);
        }
        listem_time.setText(exeStudyTime());
        position.setText(exePosition());
        lv.setText(exeIyuLevel());
        integral.setText(userInfo.icoins);
        //EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布 刷新
        //checkLoginStatues();
    }

/*    private void checkLoginStatues() {
        if (name.getText().equals("爱语吧 未登录") || userInfo.username.equals("")) {
            //登录状态异常，网络不通畅，
            //handler.sendEmptyMessage(4);//强制注销，不好，不好
            //只是把UI改成未登录就可以了
            AccountManagerLib.Instace(mContext).loginStatus = LOGIN_STATUS_UNLOGIN;
            checkLogin();
            //noLogin.setVisibility(View.VISIBLE);
            LogUtil.e("登录状态异常，网络不通畅，未登录显示");
        } else {
            AccountManagerLib.Instace(mContext).loginStatus = LOGIN_STATUS_LOGIN;
            checkLogin();
            LogUtil.e("登录成功，修改状态");
        }
    }*/

    private String exeStudyTime() {
        StringBuffer sb = new StringBuffer(
                mContext.getString(R.string.me_study_time));
        int time = userInfo.studytime;
        int minus = time % 60;
        int minute = time / 60 % 60;
        int hour = time / 60 / 60;
        if (hour > 0) {
            sb.append(hour).append(mContext.getString(R.string.me_hour));
        } else if (minute > 0) {
            sb.append(minute).append(mContext.getString(R.string.me_minute));
        } else {
            sb.append(minus).append(mContext.getString(R.string.me_minus));
        }
        return sb.toString();
    }

    private String exePosition() {
        StringBuffer sb = new StringBuffer(
                mContext.getString(R.string.me_study_position));
        int position = Integer.parseInt(userInfo.position);

        if (position < 10000) {
            sb.append(position);
        } else {
            sb.append(position / 10000 * 10000).append("+");
        }
        return sb.toString();
    }

    private String exeIyuLevel() {
        StringBuffer stringBuffer = new StringBuffer("");
        stringBuffer.append(mContext.getString(R.string.me_lv));
        stringBuffer.append(CheckGrade.Check(userInfo.icoins));
        stringBuffer.append(" ");
        stringBuffer.append(CheckGrade.CheckLevelName(mContext, userInfo.icoins));
        return stringBuffer.toString();
    }

    //获取我的钱包
    private String myMoney() {
        String moneys = null;
        if (AccountManagerLib.Instace(mContext).userId != null &&
                !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                !AccountManagerLib.Instace(mContext).userId.equals("0")
                && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
            float moneyThisTime = 0;
            if (AccountManagerLib.Instace(getActivity()).getMONEY() != null && !AccountManagerLib.Instace(getActivity()).getMONEY().equals("")) {
                LogUtil.e("断网崩溃拦截" + AccountManagerLib.Instace(getActivity()).getMONEY());
                moneyThisTime = Float.parseFloat(AccountManagerLib.Instace(getActivity()).getMONEY());
            }

            moneyNum = floatToString(moneyThisTime);
            moneys = "钱包:" + floatToString(moneyThisTime) + "元";
        } else {
            //临时账户 没有钱包
        }
        return moneys;
    }

    private String floatToString(float fNumber) {

        fNumber = (float) (fNumber * 0.01);

        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }


    private View.OnClickListener ocl = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            Intent intent;
            int id = arg0.getId();
            String packageName = mContext.getPackageName();
            if (id == R.id.personalhome) {

                int userid = Integer.valueOf(AccountManagerLib.Instace(mContext).userId);
                startActivity(PersonalHomeActivity.buildIntent(mContext,
                        userid, AccountManagerLib.Instace(mContext).userName,
                        0));

//                intent = new Intent(mContext, PersonalHome.class);
//                SocialDataManager.Instance().userid = AccountManagerLib
//                        .Instace(mContext).userId;
//                startActivity(intent);
            } else if (id == R.id.me_state_change) {
                if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    showNormalDialog("请登录。");
                } else {
                    intent = new Intent(mContext, WriteState.class);
                    startActivity(intent);
                }
            } else if (id == R.id.update_service) {//跟新服务
                updateService();
            } else if (id == R.id.me_vip) {//会员中心
                startActivity(VipCenterGoldActivity.buildIntent(mContext, 0));
//                startActivity(new Intent(mContext,UpLoadImageActivity.class));
            } else if (id == R.id.me_collect_layout) {//我的收藏
                // 收藏页面
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                    int uid;
                    try {
                        uid = Integer.parseInt(AccountManagerLib.Instace(mContext).userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        uid = 0;
                    }

                    Intent intents = BasicFavorActivity.buildIntent(mContext);
                    startActivity(intents);
                } else {

                    AccountManagerLib.Instace(mContext).startLogin();
                }
            } else if (id == R.id.me_message) {
                startActivity(new Intent(mContext, MessageActivity.class));
//                intent = new Intent(mContext, MessageCenter.class);
//                startActivity(intent);

            } else if (id == R.id.attention_area) {
                intent = new Intent(mContext, AttentionCenter.class);
                intent.putExtra("userid",
                        AccountManagerLib.Instace(mContext).userId);
                startActivity(intent);
            } else if (id == R.id.fans_area) {
                intent = new Intent(mContext, FansCenter.class);
                intent.putExtra("userid",
                        AccountManagerLib.Instace(mContext).userId);
                startActivity(intent);
            } else if (id == R.id.notification_area) {
                intent = new Intent(mContext, NoticeCenter.class);
                intent.putExtra("userid",
                        AccountManagerLib.Instace(mContext).userId);
                startActivity(intent);
            } else if (id == R.id.Integral) {
                intent = new Intent(mContext, Web.class);
                intent.putExtra("title", "积分明细");
                intent.putExtra("url",
                        "http://api." + com.iyuba.core.util.Constant.IYBHttpHead + "/credits/useractionrecordmobileList1.jsp?uid="
                                + AccountManagerLib.Instace(mContext).userId);
                startActivity(intent);
            } else if (id == R.id.discover_rqlist) {
                intent = new Intent();
                intent.setClass(mContext, QuesListActivity.class);
                startActivity(intent);
            } else if (id == R.id.discover_qnotice) {
                intent = new Intent();
                intent.setClass(mContext, QuestionNotice.class);
                startActivity(intent);
            } else if (id == R.id.discover_myq) {
                intent = new Intent();
                intent.setClass(mContext, TheQuesListActivity.class);
                intent.putExtra("utype", "2");
                startActivity(intent);
            } else if (id == R.id.secret_btn) {
                PrivacyDialog.goToSecret(mContext);
            } else if (id == R.id.policy_btn) {
                PrivacyDialog.goToPolicy(mContext);
            } else if (id == R.id.discover_mysub) {
                intent = new Intent();
                intent.setClass(mContext, TheQuesListActivity.class);
                intent.putExtra("utype", "4");
                startActivity(intent);
            } else if (id == R.id.discover_iyubaset) {
                intent = new Intent();
                intent.setClass(mContext, SettingActivity.class);
                startActivity(intent);
            } else if (id == R.id.button_dk) {
                if (AccountManagerLib.Instace(mContext).userId != null &&
                        !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                        !AccountManagerLib.Instace(mContext).userId.equals("0")
                        && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
                    //跳转打卡页面
                    initData();

                } else {
                    Toast.makeText(mContext, "临时用户无法打卡。", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.me_send_book) {
//                startActivity(new Intent(mContext, UpLoadImageActivity.class));
                startActivity(new Intent(mContext, SendBookActivity.class));
            } else if (id == R.id.about_btn) {

                Intent intent1 = new Intent();
                intent1.setClass(mContext, AboutActivity.class);
                startActivity(intent1);
            } else if (id == R.id.feedback_btn) {
                Intent intent2 = new Intent();
                intent2.setClass(mContext, Feedback.class);
                startActivity(intent2);
            } else if (id == R.id.me_rank) {
                startActivity(new Intent(mContext, RankActivity.class));

            } else if (id == R.id.me_record_update) {//同步学习记录
                //初始化，防止多次单击直接跳过
                isError = false;
                isEvaluate = false;
                isListen = false;
                isRecordUpdate = false;
                isTest = false;
                isWords = false;
                if (AccountManagerLib.Instace(getContext()).getUserId() == 0) {
                    ToastUtil.showLongToast(mContext, "请先登录");
                    return;
                }
                Timber.e("同步学习数据开始");
                progressDialog = ProgressDialog.show(getActivity(), "", "正在同步学习数据");
                progressDialog.show();
                //五个同步指令因服务器无法做到并发，故采取顺序执行 顺序为1、2、3、4、5；此为1
                listenUpdate();


            } else if (id == R.id.tv_my_money) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("当前钱包金额: " + moneyNum + " 元,满10元可在[爱语吧]微信公众号提现(关注绑定当前账号),每天坚持打卡分享,获得更多红包吧!")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else if (id == R.id.me_collect_layout_test) {
                EventBus.getDefault().post(new GoTestCollectEvent());
            } else if (id == R.id.me_study_summary) {//学习报告                              
                String[] types = new String[]{
                        SummaryType.LISTEN,
                        SummaryType.EVALUATE,
                        SummaryType.WORD,
                        SummaryType.TEST
                };
                startActivity(SummaryActivity.getIntent(mContext, "toeic", types, 10));
            } else if (id == R.id.discover_exchange_gift) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, Web.class);
                    intent.putExtra("url", "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/mall/index.jsp?"
                            + "&uid=" + AccountManagerLib.Instace(mContext).userId
                            + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManagerLib.Instace(mContext).userId + "camstory")
                            + "&username=" + AccountManagerLib.Instace(mContext).userName
                            + "&platform=android&appid="
                            + Constant.APPID);
//					intent.putExtra("url", "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/mall/index.jsp?uid="
//							+AccountManagerLib.Instace(mContext).userId+"&platform=android&appid="
//							+Constant.APPID+"&userName="+AccountManagerLib.Instace(mContext).userName);
                    intent.putExtra("title",
                            mContext.getString(R.string.discover_exgift));
                    startActivity(intent);

                } else {

                    AccountManagerLib.Instace(mContext).startLogin();
                }
            }


        }
    };

    private void updateService() {
        Log.d("TAG", "onResponse: start");
        UpdateServiceApi updateServiceApi = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://111.198.52.105:8085").client(new OkHttpClient()).build()
                .create(UpdateServiceApi.class);
        updateServiceApi.requestNewService(Constant.APPID, Constant.IYBHttpHead, Constant.IYBHttpHead2).enqueue(new Callback<UpdateServiceResp>() {
            @Override
            public void onResponse(Call<UpdateServiceResp> call, Response<UpdateServiceResp> response) {
                UpdateServiceResp body = response.body();
                Log.d("TAG", "onResponse: " + body.getUpdateflg());
                if (body.getUpdateflg() == 1) {
                    ConfigManager.Instance().putString("short1", body.getShort1());
                    ConfigManager.Instance().putString("short2", body.getShort2());
                    ToastUtil.showToast(mContext, "已更新至最新服务");
                } else {
                    ToastUtil.showToast(mContext, "当前已是最新服务");
                }
            }

            @Override
            public void onFailure(Call<UpdateServiceResp> call, Throwable t) {

            }
        });
    }

    private RecordUpdate.UpdateCallback updateCallback = new RecordUpdate.UpdateCallback() {
        @Override
        public void updateSuccess() {
            isRecordUpdate = true;
            checkUpdate();
        }

        @Override
        public void updateError() {
            isError = true;
            isRecordUpdate = false;
            checkUpdate();
        }
    };

    private void listenUpdate() {
        //听力进度同步 1

        ExeProtocol.exe(
                new GetStudyRecordByTestModeRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetStudyRecordByTestModeResponse response = (GetStudyRecordByTestModeResponse) bhr;
                        listeningDeliver = response.listeningTestRecords;
                        EventBus.getDefault().post(new UpdateListenRecordEvent((ArrayList<ListeningTestRecord>) listeningDeliver, response));
                        isListen = true;
                        handler.sendEmptyMessage(8);//checkUpdate()
                        testUpdate();
                    }

                    @Override
                    public void error() {
                        isError = true;
                        isListen = false;
                        //请求失败
                        handler.sendEmptyMessage(8);//checkUpdate()
                        testUpdate();

                    }
                });
    }

    private void testUpdate() {
        //做题进度同步 2

        ExeProtocol.exe(
                new GetTestRecordDetailRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {
                    @Override
                    public void error() {
                        isError = true;
                        isTest = false;
                        //请求失败
                        handler.sendEmptyMessage(8);//checkUpdate()
                        wordsUpdate();

                    }

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetTestRecordDetailResponse response = (GetTestRecordDetailResponse) bhr;
                        exerciseDeliver = response.exerciseRecords;
                        EventBus.getDefault().post(new UpdateExerciseRecordEvent((ArrayList<ExerciseRecord>) exerciseDeliver, response));
                        isTest = true;
                        handler.sendEmptyMessage(8);//checkUpdate()
                        wordsUpdate();
                    }
                });
    }

    private void wordsUpdate() {
        //单词进度同步 3

        Timber.e("执行单词进度同步接口");
        ExeProtocol.exe(
                new GetWordsRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        isWords = true;
                        GetWordsResponse response = (GetWordsResponse) bhr;
                        wordsUpdateDeliver = response.wordsUpdateRecord;
//                        Timber.e("单词进度同步获取的wordsUpdateDeliver === " + wordsUpdateDeliver);
                        EventBus.getDefault().post(new UpdateWordsRecordEvent(
                                (ArrayList<WordsUpdateRecord>) wordsUpdateDeliver,
                                response));
                        handler.sendEmptyMessage(8);//checkUpdate()
                        evaluateUpdate();
                    }

                    @Override
                    public void error() {
                        isError = true;
                        isWords = false;
                        //请求失败
                        handler.sendEmptyMessage(8);//checkUpdate()
                        evaluateUpdate();

                    }
                });
    }

    private void evaluateUpdate() {
        //评测进度同步 4

        ExeProtocol.exe(
                new GetVoaTestRecordRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetVoaTestRecordResponse response = (GetVoaTestRecordResponse) bhr;
                        evaluateDeliver = response.evaluateRecords;
                        EventBus.getDefault().post(new UpdateEvaluateRecordEvent((ArrayList<EvaluateRecord>) evaluateDeliver, response));
                        isEvaluate = true;
                        handler.sendEmptyMessage(8);//checkUpdate()
                        RecordUpdate.getInstance().update(updateCallback);//视频学习记录同步

                    }

                    @Override
                    public void error() {
                        isError = true;
                        isEvaluate = false;
                        //请求失败
                        handler.sendEmptyMessage(8);//checkUpdate()
                        RecordUpdate.getInstance().update(updateCallback);//视频学习记录同步

                    }
                });
    }

    //判断更新是否完成
    public void checkUpdate() {
        if (isEvaluate && isListen && isTest && isRecordUpdate && isWords && !isError) {//isEvaluate && isListen && isTest && isRecordUpdate &&isWords
            Timber.e("同步学习数据成功");
            ToastUtil.showToast(mContext, "同步学习数据成功！");
            EventBus.getDefault().post(new TestLibFragmentRefreashEvent());
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else if (isError) {
            ToastUtil.showToast(mContext, "同步学习数据失败！");
            Timber.e("同步学习数据失败");
            Timber.e("同步具体失败部分为 " + "isEvaluate = " + isEvaluate
                    + " isListen = " + isListen
                    + " isTest = " + isTest
                    + " isRecordUpdate = " + isRecordUpdate
                    + " isError = " + isError);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            Timber.e("同步中");
            Timber.e("同步具体部分为 " + "isEvaluate = " + isEvaluate
                    + " isListen = " + isListen
                    + " isTest = " + isTest
                    + " isWords = " + isWords
                    + " isRecordUpdate = " + isRecordUpdate
                    + " isError = " + isError);
        }
    }


    CustomDialog mWaittingDialog;
    private int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    //打卡
    private void initData() {

        mWaittingDialog = WaittingDialog.showDialog(getActivity());
        mWaittingDialog.setTitle("请稍后");
        mWaittingDialog.show();

        String uid = AccountManagerLib.Instace(mContext).userId;
        final String url = String.format(Locale.CHINA,
                "http://daxue." + com.iyuba.core.util.Constant.IYBHttpHead + "/ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());
        Log.d("dddd", url);

        ExeProtocol.exe(
                new SignRequest(AccountManagerLib.Instace(mContext).userId),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
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

                                if (time < signStudyTime) {
                                    toast(String.format(Locale.CHINA, "当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));
                                } else {
                                    //跳转打卡页面
                                    Intent intent1 = new Intent(getActivity(), SignActivity.class);
                                    startActivity(intent1);
                                }
                            } else {
                                handler.sendEmptyMessage(11);//加载失败
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //toast(loadFiledHint + "！！" + e);
                            handler.sendEmptyMessage(11);
                            //Toast.makeText(mContext, loadFiledHint + "！！" + e, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void error() {
                        toast("请求失败！请稍后再试");
                    }
                });

    }


    private void toast(String msg) {
        Looper.prepare();
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
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


    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 1:
                    CustomToast.showToast(mContext, R.string.action_fail);
                    break;
                case 11:
                    CustomToast.showToast(mContext, loadFiledHint);// + bean.getResult()
                    break;
                case 3:
                    if (userInfo != null) {
                        setTextViewContent();
                    } else {
                        //toast("网络异常，请重新登录");
                        Toast.makeText(mContext, "网络异常，请重新登录", Toast.LENGTH_SHORT).show();
                        AccountManagerLib.Instace(mContext).loginStatus = LOGIN_STATUS_UNLOGIN;
                        checkLogin();
                    }
                    break;
                case 7:
                    AccountManagerLib.Instace(mContext).loginStatus = LOGIN_STATUS_UNLOGIN;
                    checkLogin();
                    break;
                case 4:
                    AccountManagerLib.Instace(mContext).loginOut();
                    CustomToast.showToast(mContext, "退出登录");
                    SettingConfig.Instance().setHighSpeed(false);
                    LOGUtils.e("退出登录刷新状态");

                    //删除评测数据
                    EventBus.getDefault().post(new LoginOutEvent());
                    //需要手动写一下
                    ConfigManager.Instance().putString("userId", "");
                    ConfigManager.Instance().putString("userName", "");
                    ConfigManager.Instance().putInt("isvip", 0);
                    //退出登录刷新状态
                    EventBus.getDefault().post(new ChangeVideoEvent(false));// 退出登录
                    AccountManagerLib.Instace(mContext).loginStatus = LOGIN_STATUS_UNLOGIN;
                    checkLogin();
                    break;
                case 2:
                    meView.findViewById(R.id.newletter).setVisibility(View.VISIBLE);
                    break;
                case 5:
                    meView.findViewById(R.id.newletter).setVisibility(View.GONE);
                    break;
                case 6:
                    ExeProtocol.exe(
                            new RequestUserDetailInfo(AccountManagerLib
                                    .Instace(mContext).userId),
                            new ProtocolResponse() {
                                @SuppressLint("NewApi")
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    ResponseUserDetailInfo responseUserDetailInfo =
                                            (ResponseUserDetailInfo) bhr;
                                    if (responseUserDetailInfo.result.equals("211")) {
                                        ResponseUserDetailInfo userDetailInfo = responseUserDetailInfo;
                                        if (userDetailInfo.gender.isEmpty()
                                                || userDetailInfo.birthday.isEmpty()
                                                || userDetailInfo.resideLocation.isEmpty()
                                                || userDetailInfo.occupation.isEmpty()
                                                || userDetailInfo.education.isEmpty()
                                                || userDetailInfo.graduateschool.isEmpty()) {
                                            //infoFlag = true;// 有为空的用户信息

                                        }
                                        if (userDetailInfo.editUserInfo.getPlevel()
                                                .isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getPlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getPtalklevel().isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getPtalklevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getPreadlevel().isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getPreadlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGlevel().isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getGlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGtalklevel().isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getGtalklevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGreadlevel().isEmpty()
                                                || Integer
                                                .valueOf(userDetailInfo.editUserInfo
                                                        .getGreadlevel()) <= 0) {
                                            //levelFlag = true;// 有为空的用户等级信息
                                        }
                                    }
                                }

                                @Override
                                public void error() {
                                }
                            });
                    break;

                case 8:
                    checkUpdate();
                    break;
            }
            return false;
        }
    });

    private void showNormalDialog(String content) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                        AccountManagerLib.Instace(mContext).startLogin();

                    }
                });
        normalDialog.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        normalDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeVideoEvent event) {
        //登录刷新状态
        final String id = AccountManagerLib.Instace(mContext).userId;
        if (!id.isEmpty()) {
            loadData(id);///????
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LogoutEvent event) {
        handler.sendEmptyMessage(4);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MoneyAddEvent event) {

    }
}
