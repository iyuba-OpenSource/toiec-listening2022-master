package com.iyuba.core.me.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.GradeRequest;
import com.iyuba.core.common.protocol.base.GradeResponse;
import com.iyuba.core.common.protocol.message.RequestBasicUserInfo;
import com.iyuba.core.common.protocol.message.RequestNewInfo;
import com.iyuba.core.common.protocol.message.ResponseBasicUserInfo;
import com.iyuba.core.common.protocol.message.ResponseNewInfo;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.db.Emotion;
import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.CheckGrade;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.Expression;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.core.teacher.activity.SettingActivity;
import com.iyuba.core.teacher.activity.QuesListActivity;
import com.iyuba.core.teacher.activity.QuestionNotice;
import com.iyuba.core.teacher.activity.TheQuesListActivity;
import com.iyuba.core.R;

import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;

/**
 * 我界面 为activity设计
 *
 * @author chentong
 * @version 1.0
 */
public class MeForAt extends Activity {
    private View noLogin, login; // 登录提示面板
    private Button loginBtn, logout;
    private Context mContext;
    private ImageView photo;
    private TextView name, state, fans, attention, notification, listem_time,
            integral, position, lv;
    private View person;
    private View stateView, messageView, vipView, back ,meSummary;
    private View attentionView, fansView, notificationView, integralView,
            discover_rqlist, discover_qnotice, discover_myq, discover_mysub,
            discover_iyubaset;
    private UserInfo userInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me);
        mContext = this;
        noLogin = findViewById(R.id.noLogin);
        login = findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);
        back = findViewById(R.id.button_back);
        back.setVisibility(View.GONE);
        init();
        checkLogin();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // MobclickAgent.onResume(mContext);
        viewChange();
    }

    @Override
    public void onPause() {
        super.onPause();
        // MobclickAgent.onPause(mContext);
    }

    private void viewChange() {
//		initLocal();
        if (checkLogin()) {
            userInfo = AccountManagerLib.Instace(mContext).userInfo;
            ClientSession.Instace()
                    .asynGetResponse(
                            new RequestNewInfo(
                                    AccountManagerLib.Instace(mContext).userId),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    // TODO Auto-generated method stub
                                    ResponseNewInfo rs = (ResponseNewInfo) response;
                                    if (rs.letter > 0) {
                                        handler.sendEmptyMessage(2);
                                    } else {
                                        handler.sendEmptyMessage(5);
                                    }
                                }
                            });
            loadData();
        }
    }

    private boolean checkLogin() {
        if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
            noLogin.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            loginBtn = (Button) findViewById(R.id.button_to_login);
            loginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            });
            logout.setVisibility(View.GONE);
            return false;
        } else {
            noLogin.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            logout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(
                                    getResources().getString(
                                            R.string.alert_title))
                            .setMessage(
                                    getResources().getString(
                                            R.string.logout_alert))
                            .setPositiveButton(
                                    getResources().getString(
                                            R.string.alert_btn_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            // TODO Auto-generated method stub
                                            handler.sendEmptyMessage(4);
                                        }
                                    })
                            .setNeutralButton(
                                    getResources().getString(
                                            R.string.alert_btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                        }
                                    }).create();
                    dialog.show();
                }
            });
            return true;
        }
    }

    /**
     *
     */
    private void loadData() {
        // TODO Auto-generated method stub
        final String id = AccountManagerLib.Instace(mContext).userId;
        init();
        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        // TODO Auto-generated method stub
                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
                        userInfo = response.userInfo;
                        AccountManagerLib.Instace(mContext).userInfo = userInfo;
                        handler.sendEmptyMessage(3);
                        Looper.prepare();
                        ExeProtocol.exe(new GradeRequest(id),
                                new ProtocolResponse() {

                                    @Override
                                    public void finish(BaseHttpResponse bhr) {
                                        // TODO Auto-generated method stub
                                        GradeResponse response = (GradeResponse) bhr;
                                        userInfo.studytime = Integer
                                                .parseInt(response.totalTime);
                                        userInfo.position = response.positionByTime;
                                        handler.sendEmptyMessage(3);
                                    }

                                    @Override
                                    public void error() {
                                        // TODO Auto-generated method stub
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                        Looper.loop();
                    }

                    @Override
                    public void error() {
                        // TODO Auto-generated method stub
                    }
                });
    }

    /**
     *
     */
    private void init() {
        // TODO Auto-generated method stub
        person = findViewById(R.id.personalhome);
        photo = (ImageView) findViewById(R.id.me_pic);
        name = (TextView) findViewById(R.id.me_name);
        state = (TextView) findViewById(R.id.me_state);
        attention = (TextView) findViewById(R.id.me_attention);
        listem_time = (TextView) findViewById(R.id.me_listem_time);
        position = (TextView) findViewById(R.id.me_position);
        lv = (TextView) findViewById(R.id.lv);
        fans = (TextView) findViewById(R.id.me_fans);
        notification = (TextView) findViewById(R.id.me_notification);

        integral = (TextView) findViewById(R.id.Integral_notification);
        stateView = findViewById(R.id.me_state_change);
        vipView = findViewById(R.id.me_vip);
        messageView = findViewById(R.id.me_message);
        attentionView = findViewById(R.id.attention_area);
        fansView = findViewById(R.id.fans_area);

        discover_rqlist = findViewById(R.id.discover_rqlist);
        discover_qnotice = findViewById(R.id.discover_qnotice);
        discover_myq = findViewById(R.id.discover_myq);
        discover_mysub = findViewById(R.id.discover_mysub);
        discover_iyubaset = findViewById(R.id.discover_iyubaset);
        notificationView = findViewById(R.id.notification_area);
        integralView = findViewById(R.id.Integral);
        meSummary = findViewById(R.id.me_study_summary);

        setViewClick();
        if (userInfo != null) {
            setTextViewContent();
        }
    }

    /**
     *
     */
    private void setViewClick() {
        // TODO Auto-generated method stub
        person.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        vipView.setOnClickListener(ocl);
        messageView.setOnClickListener(ocl);
        attentionView.setOnClickListener(ocl);
        fansView.setOnClickListener(ocl);
        stateView.setOnClickListener(ocl);
        notificationView.setOnClickListener(ocl);
        integralView.setOnClickListener(ocl);
        discover_rqlist.setOnClickListener(ocl);
        discover_qnotice.setOnClickListener(ocl);
        discover_myq.setOnClickListener(ocl);
        discover_mysub.setOnClickListener(ocl);
        discover_iyubaset.setOnClickListener(ocl);
        meSummary.setOnClickListener(ocl);

    }

    /**
     *
     */
    private void setTextViewContent() {
        // TODO Auto-generated method stub
        GitHubImageLoader.Instace(mContext).setCirclePic(
                AccountManagerLib.Instace(mContext).userId, photo);
//		name.setText(AccountManagerLib.Instace(mContext).userName);
        name.setText(userInfo.username);
        if (ConfigManager.Instance().loadInt("isvip") == 1) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
        } else {
            Drawable img = mContext.getResources().getDrawable(
                    R.drawable.no_vip);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            name.setCompoundDrawables(null, null, img, null);
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
        attention.setText(userInfo.following);
        fans.setText(userInfo.follower);
        listem_time.setText(exeStudyTime());
        position.setText(exePosition());
        lv.setText(exeIyuLevel());
        notification.setText(userInfo.notification);
        integral.setText(userInfo.icoins);
    }

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
        StringBuffer sb = new StringBuffer("");
        sb.append(mContext.getString(R.string.me_lv));
        sb.append(CheckGrade.Check(userInfo.icoins));
        sb.append(" ");
        sb.append(CheckGrade.CheckLevelName(mContext,userInfo.icoins));
        // sb.append("   ");
        // sb.append(mContext.getString(R.string.me_score));
        // sb.append(userInfo.icoins);
        return sb.toString();
    }

    private OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent;
            int id = arg0.getId();
            String packageName = mContext.getPackageName();
            if (id == R.id.personalhome) {
                intent = new Intent(mContext, PersonalHome.class);
                SocialDataManager.Instance().userid = AccountManagerLib
                        .Instace(mContext).userId;
                startActivity(intent);
            } else if (id == R.id.me_state_change) {
                intent = new Intent(mContext, WriteState.class);
                startActivity(intent);
            } else if (id == R.id.me_vip) {
                intent = new Intent(mContext, VipCenterGoldActivity.class);
                startActivity(intent);
            } else if (id == R.id.me_message) {
                startActivity(new Intent(mContext, MessageActivity.class));
//                intent = new Intent(mContext, MessageCenter.class);
//                startActivity(intent);
            }
//			else if (id == R.id.me_local) {
//				intent = new Intent();
//				intent.setComponent(new ComponentName(packageName, packageName
//						+ ".activity.LocalNews"));
//				intent.putExtra("localType", 0);
//				startActivity(intent);
//			} else if (id == R.id.me_love) {
//				intent = new Intent();
//				intent.setComponent(new ComponentName(packageName, packageName
//						+ ".activity.LocalNews"));
//				intent.putExtra("localType", 1);
//				startActivity(intent);
//			} else if (id == R.id.me_read) {
//				intent = new Intent();
//
//				intent.setComponent(new ComponentName(packageName, packageName
//						+ ".activity.LocalNews"));
//				intent.putExtra("localType", 2);
//				startActivity(intent);
//			} 
            else if (id == R.id.attention_area) {
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
                        "http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/useractionrecordmobileList1.jsp?uid="
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
            } else if (id == R.id.discover_mysub) {
                intent = new Intent();
                intent.setClass(mContext, TheQuesListActivity.class);
                intent.putExtra("utype", "4");
                startActivity(intent);
            } else if (id == R.id.discover_iyubaset) {
                intent = new Intent();
                intent.setClass(mContext, SettingActivity.class);
                startActivity(intent);
            }else if (id == R.id.me_study_summary){
                String[] types = new String[]{
                        SummaryType.LISTEN,
                        SummaryType.EVALUATE,
                        SummaryType.WORD,
                        SummaryType.TEST
                };
                startActivity(SummaryActivity.getIntent(mContext, types));
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 1:
                    CustomToast.showToast(mContext, R.string.action_fail);
                    break;
                case 2:
                    findViewById(R.id.newletter).setVisibility(View.VISIBLE);
                    break;
                case 3:
                    setTextViewContent();
                    break;
                case 4:
                    AccountManagerLib.Instace(mContext).loginOut();
                    CustomToast.showToast(mContext, R.string.loginout_success);
                    SettingConfig.Instance().setHighSpeed(false);
                    onResume();
                    break;
                case 5:
                    findViewById(R.id.newletter).setVisibility(View.GONE);
                    break;
            }
        }
    };
}