package com.iyuba.core.me.goldvip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.Constant;
import com.iyuba.core.BuildConfig;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.NetWorkHelper;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.me.pay.BuyIyubiActivity;
import com.iyuba.core.me.pay.PayOrderActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

/**
 * 会员中心
 */
public class VipCenterGoldActivity extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView tvUserName;
    private TextView tvVipTime;
    private TextView tvVipIntroduce;
    private TextView tvVipAll;
    private TextView tvVipOnly;
    private TextView tvVipGold;
    private GridView gvVip;
    private RecyclerView rvVipSelectList;
    private TextView tvTips;
    private TextView tvGoBuy;
    private TextView tvFunction;
    private TextView tvBuyIyubi;
    private ImageButton ibBack;
    private TextView tvIyubi;

    private LinearLayout llFunction;

    private Context mContext;

    private List<Map<String, Object>> data_list;
    private SimpleAdapter simAdapter;

    private BuyVIPAdapter mAdapter;

    private List<BuyVIPItem> siteVipItems;
    private List<BuyVIPItem> goldenVipItems;
    private List<BuyVIPItem> appVipItems;

    private String validity;
    private String iyubi;
    private int vipStatus;
    private static final String TAG = "VipCenterGoldActivity";
    private String[] iconInfo = {"去除（开屏外）所有烦人的广告", "亮着VIP尊贵标识", "选择自由调节语速",
            "享受VIP高速通道，无限下载", "查看考试类所有试题答案解析", "享受智能化无限语音评测", "文章pdf无限导出",
            "使用app.iyuba.cn旗下所有APP", "积分商城换取不同价值手机充值卡"};

    public static Intent buildIntent(Context context, int type) {
        Intent intent = new Intent(context, VipCenterGoldActivity.class);
        intent.putExtra("type", type);
        return intent;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.vip_toolbar);
        setContentView(R.layout.activity_vip_center_gold);

        mContext = this;
        initView();
        tvVipAll.setSelected(true);
        tvVipOnly.setSelected(false);
        tvVipGold.setSelected(false);
        llFunction.setVisibility(View.GONE);

        BuyVIPParser parser = new BuyVIPParser(mContext);
        siteVipItems = parser.parse(R.xml.buy_site_vip_items);
        goldenVipItems = parser.parse(R.xml.buy_golden_vip_items);
        appVipItems = parser.parse(R.xml.buy_app_vip_items);
        initListener();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        rvVipSelectList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BuyVIPAdapter();
        rvVipSelectList.setAdapter(mAdapter);
        mAdapter.setData(siteVipItems);

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefresh();//刷新数据
        tvGoBuy.setClickable(true);
        tvVipIntroduce.setText("VIP权限(不包括微课和训练营)");
        int type = getIntent().getIntExtra("type", 0);
        if (type == 2) {
            tvVipGold.performClick();//模拟点击
        } else if (type == 1) {
            tvVipOnly.performClick();
        }
    }

    private void initView() {
        userPhoto = findViewById(R.id.user_photo);
        tvUserName = findViewById(R.id.tv_user_name);
        tvVipTime = findViewById(R.id.tv_vip_time);
        tvVipIntroduce = findViewById(R.id.tv_vip_introduce);
        tvVipAll = findViewById(R.id.tv_vip_all);
        tvVipOnly = findViewById(R.id.tv_vip_only);
        tvVipGold = findViewById(R.id.tv_vip_gold);
        gvVip = findViewById(R.id.gv_vip);
        rvVipSelectList = findViewById(R.id.rv_vip_select_list);
        tvTips = findViewById(R.id.tv_tips);
        tvGoBuy = findViewById(R.id.tv_go_buy);
        tvFunction = findViewById(R.id.tv_vip_function);
        tvBuyIyubi = findViewById(R.id.tv_buy_iyubi);
        llFunction = findViewById(R.id.ll_function);
        ibBack = findViewById(R.id.ib_back);
        tvIyubi = findViewById(R.id.tv_iyubi_number);
    }

    private void initListener() {

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        data_list = new ArrayList<Map<String, Object>>();
        String[] from = {"image", "text"};
        int[] to = {R.id.iv_icon, R.id.tv_text};
        getData();
        simAdapter = new SimpleAdapter(this, data_list, R.layout.item_gridview, from, to);
        gvVip.setAdapter(simAdapter);


        tvVipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(true);
                tvVipOnly.setSelected(false);
                tvVipGold.setSelected(false);
                gvVip.setVisibility(View.VISIBLE);
                llFunction.setVisibility(View.GONE);
                mAdapter.mSelectedItem = null;
                mAdapter.setData(siteVipItems);
                tvVipIntroduce.setText("VIP权限(不包括微课和训练营)");
            }
        });

        tvVipOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(false);
                tvVipOnly.setSelected(true);
                tvVipGold.setSelected(false);
                gvVip.setVisibility(View.GONE);
                llFunction.setVisibility(View.VISIBLE);
                tvFunction.setText(R.string.eternal_vip_description);
                mAdapter.mSelectedItem = null;
                mAdapter.setData(appVipItems);
                tvVipIntroduce.setText("VIP权限(不包括微课和训练营)");
            }
        });

        tvVipGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(false);
                tvVipOnly.setSelected(false);
                tvVipGold.setSelected(true);
                gvVip.setVisibility(View.GONE);
                llFunction.setVisibility(View.VISIBLE);
                tvFunction.setText(R.string.golden_vip_description);
                mAdapter.mSelectedItem = null;
                mAdapter.setData(goldenVipItems);
                tvVipIntroduce.setText("VIP权限");
            }
        });

        tvBuyIyubi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, BuyIyubiActivity.class);
                intent.putExtra("title", "购买爱语币");
                startActivity(intent);
            }
        });

        tvVipIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Web.class);
                intent.putExtra("url", "http://vip." + com.iyuba.core.util.Constant.IYBHttpHead + "/vip/vip.html");
                intent.putExtra("title", "vip说明");
                startActivity(intent);
            }
        });

        tvGoBuy.setOnClickListener(v -> {

            BuyVIPItem item = mAdapter.getSelectedItem();
            if (item == null) {
                CustomToast.showToast(mContext, "请选择要开通的VIP!");
            } else {
                boolean isLogin = AccountManagerLib.Instace(mContext).checkUserLogin();
                if (isLogin) {

                    tvGoBuy.setClickable(false);
                    String info = getString(R.string.buy_app_vip_body_info, item.name);
                    String subject = "";
                    if (item.productId == 0) {
                        subject = "全站vip";
                    } else if (item.productId == 10) {
                        subject = "永久vip";
                    } else {
                        subject = "黄金vip";
                    }
                    String prpductId = "";
                    String price = String.valueOf(item.price);
                    if (BuildConfig.DEBUG) {
                        price = "0.01";
                    }
                    //String price = "0.01";
                    //<!-- 托业听力黄金会员productId 15-->

                    Intent intent = new Intent(VipCenterGoldActivity.this, PayOrderActivity.class);
                    intent.putExtra("amount", item.month + "");
                    intent.putExtra("productId", item.productId + "");
                    intent.putExtra("body", info);//"花费" + price + "元购买全站vip"
                    intent.putExtra("price", price);  //价格 60 price + ""
                    startActivity(intent);
                } else {

                    startActivity(new Intent(VipCenterGoldActivity.this, LoginActivity.class));
                }
            }
        });

        //全站会员图标单击显示详情
        gvVip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence message = iconInfo[position];
                if (position == 7) {
                    message = Html.fromHtml("使用于<a href=http://app." + Constant.IYBHttpHead + ">app." + Constant.IYBHttpHead + "</a>旗下所有APP");
                }
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.dialog_web, null);
                TextView textView = view1.findViewById(R.id.tv_message_web);
                textView.setText(message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                new AlertDialog.Builder(mContext)
                        .setView(view1)
                        .setPositiveButton(getString(R.string.sure), null)
                        .create()
                        .show();
            }
        });

    }

    public List<Map<String, Object>> getData() {
        String[] titles = mContext.getResources().getStringArray(R.array.vip_function_name);
        int[] icon = ResourcesArrayUtil.fromTypedArray(mContext, R.array.vip_function_icon);
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", titles[i]);
            data_list.add(map);
        }
        return data_list;
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

    private void Assignment() {
        AccountManagerLib manager = AccountManagerLib.Instace(mContext);
        if (manager.checkUserLogin()) {
            tvUserName.setText(manager.userName);

            GitHubImageLoader.Instace(mContext).setCirclePicGrild(//特殊的设置圆形图片方法
                    AccountManagerLib.Instace(mContext).userId, userPhoto, mContext, R.drawable.defaultavatar);
            tvVipTime.setVisibility(View.VISIBLE);
            if (vipStatus >= 1) {
                tvVipTime.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_vip_logo_white), null, null, null);
                tvVipTime.setCompoundDrawablePadding(10);
                tvVipTime.setText(validity);//vip时间
            } else {
                tvVipTime.setText(R.string.person_common_user);//普通用户
            }
        } else {
            tvUserName.setText(mContext.getString(R.string.user_no_login));
            tvVipTime.setVisibility(View.GONE);
        }

        tvIyubi.setText(mContext.getString(R.string.iyubi) + iyubi);

    }

    private void autoRefresh() {
        AccountManagerLib manager = AccountManagerLib.Instace(mContext);
        vipStatus = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
        if (manager.userInfo != null) {
            try {
                if (TextUtils.isEmpty(manager.userInfo.deadline)) {
                    validity = "时间异常";
                } else {
                    validity = manager.userInfo.deadline;//到期时间
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            iyubi = String.valueOf(manager.userInfo.iyubi);

            Timber.e("vip到期时间" + validity + "=====");

            if (!NetWorkHelper.getInstance().isConnected()) {//检查网络
                Timber.e(TAG + "no net");
                if (manager.checkUserLogin()) {
                    Assignment();
                }
            } else {
                Assignment();

                Timber.e(TAG + "after checkAmount");
            }
        }
    }


    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity this
     * @param colorId  黄
     */
    public void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //低版本没有适配
        }
    }
}
