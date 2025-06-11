package com.iyuba.core.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.R;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.me.adapter.MyGridAdapter;
import com.iyuba.core.me.pay.BuyIyubiActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.core.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.MyGridView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 会员中心 显示VIP信息 特权 VIP购买页面 2019.04.12 前使用
 *
 * @author ct
 * @version 1.0
 */
public class VipCenter extends BasisActivity implements OnClickListener {
    private View relativeLayout_noLogin, relativetLayout_login;
    private Button backBtn, loginBtn;
    private TextView buy_iyubi;
    private ImageView photo;
    private String username;
    private Context mContext;
    private int isvip;
    private TextView name, state, deadline, account;
    private Button buy;
    private View ad, read, limit, speed;
    private Button month;
    private Button quarter;
    private Button half_year;
    private Button year;
    private Button thisVip12, thisVipForever;
    private double price;
    private Button threeyear;
    private MyGridView gv_tequan;
    private RelativeLayout lifelong;
    private TabHost th;
    private TextView localVip;
    private RelativeLayout rl_forever;
    private TextView tv_vip_html;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vip_center);
         CrashApplication.addActivity(this);
        mContext = this;
        username = AccountManagerLib.Instace(mContext).userName;
        relativeLayout_noLogin = findViewById(R.id.relativeLayout_noLogin);
        relativetLayout_login = findViewById(R.id.relativeLayout_Login);
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
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
            relativeLayout_noLogin.setVisibility(View.VISIBLE);
            relativetLayout_login.setVisibility(View.GONE);
        } else {
            relativeLayout_noLogin.setVisibility(View.GONE);
            relativetLayout_login.setVisibility(View.VISIBLE);
            isvip = ConfigManager.Instance().loadInt("isvip");
            init();
        }
    }

    private void init() {
        photo = (ImageView) findViewById(R.id.img);
        name = (TextView) findViewById(R.id.buy_username);
        state = (TextView) findViewById(R.id.buy_state);
        deadline = (TextView) findViewById(R.id.buy_deadline);
        account = (TextView) findViewById(R.id.buy_account);
        rl_forever = (RelativeLayout) findViewById(R.id.rl_forever);

        tv_vip_html = (TextView) findViewById(R.id.tv_vip_html);
        tv_vip_html.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VipCenter.this, Web.class);
                intent.putExtra("url", "http://vip." + com.iyuba.core.util.Constant.IYBHttpHead + "/vip/vip.html");
                intent.putExtra("title", "全站VIP");
                startActivity(intent);
            }
        });

        //rl_forever.setVisibility(View.GONE);//本应用会员
//        th = (TabHost) findViewById(R.id.tabhost);
//        localVip = (TextView) findViewById(R.id.view2);
//        th.setup();
//        th.setFocusable(false);
//        th.addTab(th.newTabSpec("tab1").setIndicator(composeLayout("全站vip", R.drawable.all_vip)).setContent(R.id.gv_tequan));
//        th.addTab(th.newTabSpec("tab2").setIndicator(composeLayout("本应用永久vip", R.drawable.forever_vip)).setContent(R.id.view2));

        gv_tequan = (MyGridView) findViewById(R.id.gv_tequan);
        gv_tequan.setFocusable(false);
        final MyGridAdapter adapter = new MyGridAdapter(mContext);
        gv_tequan.setAdapter(adapter);
        gv_tequan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String hint = adapter.getHint(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(hint);
                builder.setTitle("权限介绍");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            }
        });
//        localVip.setText("1. 尊贵V标识，永久开放所有试题独家解析\n" +
//                "2. 同享全站会员无广告、高速下载、换话费等特权\n" +
//                "3. 永久VIP仅限Android端使用\n" +
//                "4. VIP更多功能，敬请期待");
//
//        View view = th.getTabWidget().getChildAt(0);
////        view.setBackgroundColor(0xc3c8f9);
////        view.setBackgroundResource(R.drawable.boy);
//        view.setBackgroundColor(0xFFAFEEEE);
//
//        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                Log.e("tabid", tabId);
//                View view;
//                switch (tabId) {
//                    case "tab1":
//                        view = th.getTabWidget().getChildAt(0);
//                        view.setBackgroundColor(0xFFE2F3FF);
//                        view = th.getTabWidget().getChildAt(1);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        break;
//                    case "tab2":
//                        view = th.getTabWidget().getChildAt(0);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        view = th.getTabWidget().getChildAt(1);
//                        view.setBackgroundColor(0xFFE2F3FF);
//                        break;
//                }
//            }
//        });

        lifelong = (RelativeLayout) findViewById(R.id.rl_buyforevervip);//底部，已经被隐藏

        month = (Button) findViewById(R.id.btn_buyapp1);
        quarter = (Button) findViewById(R.id.btn_buyapp2);
        half_year = (Button) findViewById(R.id.btn_buyapp3);
        year = (Button) findViewById(R.id.btn_buyapp4);
        threeyear = (Button) findViewById(R.id.btn_buyapp5);

        thisVip12 = (Button) findViewById(R.id.btn_buyapp1_forever_12);
        thisVipForever = (Button) findViewById(R.id.btn_buyapp1_forever_this);

        month.setOnClickListener(this);
        quarter.setOnClickListener(this);
        half_year.setOnClickListener(this);
        year.setOnClickListener(this);
        threeyear.setOnClickListener(this);
        lifelong.setOnClickListener(this);
        thisVip12.setOnClickListener(this);
        thisVipForever.setOnClickListener(this);

        buy_iyubi = (TextView) findViewById(R.id.buy_iyubi);
        //buy_iyubi.setVisibility(View.GONE);
        buy_iyubi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buyIyubi();
            }
        });
    }

//    public View composeLayout(String s, int i) {
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        ImageView iv = new ImageView(this);
//        iv.setImageResource(i);
//        iv.setAdjustViewBounds(true);
//        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(85, 15, 85, 0);
//        layout.addView(iv, lp);
//        TextView tv = new TextView(this);
//        tv.setGravity(Gravity.CENTER);
//        tv.setSingleLine(true);
//        tv.setText(s);
//        tv.setTextColor(0xFF598aad);
//        tv.setTextSize(14);
//        LinearLayout.LayoutParams lpo = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        lpo.setMargins(0, 0, 0, 15);
//        layout.addView(tv, lpo);
//        return layout;
//    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    final int month = msg.arg1;
                    buyVip(month);
                    break;
                default:
                    break;
            }
        }
    };

    public double getSpend(int month) {
        double result = 2000;
        switch (month) {
            case 1:
                result = 30;
                break;
            case 3:
                result = 88;
                break;
            case 6:
                result = 158;
                break;
            case 12:
                result = 298;
                break;
            case 36:
                result = 588;
                break;
            case 0:
                result = 199;//永久VIP
                break;
            case 120:
                result = 99;//本应用一年
                break;
        }
        return result;
    }

    private void buyVip(int month) {
        if (AccountManagerLib.Instace(mContext).userId != null &&
                !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                !AccountManagerLib.Instace(mContext).userId.equals("0")
                && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
            Intent intent = new Intent(mContext, PayOrderActivity.class);
            price = getSpend(month);
            intent.putExtra("type", month);//120
            intent.putExtra("out_trade_no", getOutTradeNo());
            intent.putExtra("subject", "全站vip");
            intent.putExtra("body", "花费" + price + "元购买全站vip");
            intent.putExtra("price", String.valueOf(price));  //价格 60 price + ""
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "临时用户不能购买，请登录正式账户", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == month) {
            handler.obtainMessage(0, 1, 0).sendToTarget();
        } else if (v == quarter) {
            handler.obtainMessage(0, 3, 0).sendToTarget();
        } else if (v == half_year) {
            handler.obtainMessage(0, 6, 0).sendToTarget();
        } else if (v == year) {
            handler.obtainMessage(0, 12, 0).sendToTarget();
        } else if (v == threeyear) {
            handler.obtainMessage(0, 36, 0).sendToTarget();
        } else if (v == lifelong) {
            handler.obtainMessage(0, 0, 0).sendToTarget();//本应用永久VIP
        } else if (v == thisVip12) {
            handler.obtainMessage(0, 120, 0).sendToTarget();//本应用12个月
        } else if (v == thisVipForever) {
            handler.obtainMessage(0, 0, 0).sendToTarget();//本应用永久VIP
        }
    }

    private void Assignment() {
        name.setText(username);
        account.setText(AccountManagerLib.Instace(mContext).userInfo.iyubi);
        GitHubImageLoader.Instace(mContext).setCirclePic(
                AccountManagerLib.Instace(mContext).userId, photo);
        if (isvip > 0) { //isvip== 1 || isvip == 2
            state.setText("VIP ");//+isvip 21 wxx102
            deadline.setText(AccountManagerLib.Instace(mContext).userInfo.deadline);
        } else {
            state.setText(R.string.person_common_user);
            deadline.setText(R.string.person_not_vip);
        }
    }

//    private void showAlertAndCancel(String title, String msg) {
//        final AlertDialog alert = new AlertDialog.Builder(this).create();
//        alert.setTitle(title);
//        alert.setMessage(msg);
//        alert.setIcon(android.R.drawable.ic_dialog_info);
//        alert.setButton(AlertDialog.BUTTON_POSITIVE,
//                getResources().getString(R.string.alert_btn_ok),
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        alert.dismiss();
//                    }
//                });
//        alert.show();
//    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            Assignment();
        }
//        if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {// 未登录
//            relativeLayout_noLogin.setVisibility(View.VISIBLE);
//            relativetLayout_login.setVisibility(View.GONE);
//        } else {
//            relativeLayout_noLogin.setVisibility(View.GONE);
//            relativetLayout_login.setVisibility(View.VISIBLE);
//            isvip = ConfigManager.Instance().loadInt("isvip");
//            init();
//            Assignment();
//        }
    }

    private void buyIyubi() {
        Intent intent = new Intent();
        intent.setClass(mContext, BuyIyubiActivity.class);
        intent.putExtra("title", "购买爱语币");
        startActivity(intent);
//        intent.putExtra("url", "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
//                + AccountManagerLib.Instace(mContext).userId + "&appid="
//                + Constant.APPID);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }


}
