package com.iyuba.core.me.pay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.stetho.common.Utf8Charset;
import com.iyuba.configation.Constant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.BuildConfig;
import com.iyuba.core.R;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.util.AutoLoginUtil;
import com.iyuba.core.util.MD5Util;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.toolbox.MD5;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * 支付页面
 */

public class PayOrderActivity extends Activity {
    private TextView payorder_username;
    private TextView payorder_rmb_amount;
    private NoScrollListView methodList;
    private PayMethodAdapter methodAdapter;
    private Button payorder_submit_btn, test;
    private boolean confirmMutex = true;
    private static final String TAG = PayOrderActivity.class.getSimpleName();
    private Context mContext;
    private String price;
    private String body;
    private String amount;
    private IWXAPI mWXAPI;
    private String mWeiXinKey;
    private int selectPosition = 0;
    private Button button;
    private String productId;
    private TextView mOrderInfo;
    private boolean isIyubi;
    private static final SimpleDateFormat WXSDF = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buyvip);
        mContext = this;

        Intent intent = getIntent();
        isIyubi = intent.getBooleanExtra("isIyubi", false);
        price = intent.getStringExtra("price");
        amount = intent.getStringExtra("amount");
        body = intent.getStringExtra("body");
        productId = intent.getStringExtra("productId");

        if (body == null) {
            body = "";
        }
        findView();
        mWeiXinKey = "wx4b3175639bf0a681";
        mWXAPI = WXAPIFactory.createWXAPI(this, mWeiXinKey, true);
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    private void findView() {
        mOrderInfo = findViewById(R.id.tv_vip_info);
        mOrderInfo.setText(body);
        button = (Button) findViewById(R.id.btn_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        payorder_username = (TextView) findViewById(R.id.payorder_username_tv);
        payorder_username.setText(AccountManagerLib.Instace(mContext).userName);
        payorder_rmb_amount = (TextView) findViewById(R.id.payorder_rmb_amount_tv);
        payorder_rmb_amount.setText(price + "元");
        methodList = (NoScrollListView) findViewById(R.id.payorder_methods_lv);
        payorder_submit_btn = (Button) findViewById(R.id.payorder_submit_btn);
        test = (Button) findViewById(R.id.payorder_submit_btn_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLogin();
            }
        });
        methodList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                methodAdapter.changeSelectPosition(position);
                methodAdapter.notifyDataSetChanged();
            }
        });
        methodAdapter = new PayMethodAdapter(this);
        methodList.setAdapter(methodAdapter);
        payorder_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmMutex) {
                    confirmMutex = false;

                    switch (selectPosition) {
                        case PayMethodAdapter.PayMethod.ALIPAY:
                            payByAlipay();
                            break;
                        case PayMethodAdapter.PayMethod.WEIXIN:
                            if (mWXAPI.isWXAppInstalled()) {
                                payByWeiXin();
                            } else {
                                new AlertDialog.Builder(PayOrderActivity.this)
                                        .setTitle("提示")
                                        .setMessage("微信未安装无法使用微信支付!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                confirmMutex = true;
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                                ToastUtil.showToast(mContext, "您还未安装微信客户端");
                            }
                            break;
                      /*  case PayMethodAdapter.PayMethod.BANKCARD:
                            payByWeb();
                            break;*/
                        default:
                            payorder_submit_btn.setClickable(false);
                            payByAlipay();
                            break;
                    }
                }
            }
        });
    }

    private void payByAlipay() {

        confirmMutex = true;
        RequestCallBack rc = result -> {

            OrderGenerateRequest request = (OrderGenerateRequest) result;
            if (request.isRequestSuccessful()) {
                // 完整的符合支付宝参数规范的订单信息
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(PayOrderActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(request.alipayTradeStr, true);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        alipayHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            } else {
                validateOrderFail();
            }
        };

        String userId = AccountManagerLib.Instace(mContext).userId;
        // 拼接code
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd");
        String code = MD5Util.MD5(userId + "iyuba" + sdf.format(System.currentTimeMillis()));

        //会员类型
        String cate;
        if (productId.equals("0")) {

            cate = "本应用VIP";
        } else if (productId.equals("10")) {

            cate = "全站VIP";
        } else if (productId.equals("15")) {//15

            cate = "黄金VIP";
        } else {

            try {
                cate = URLEncoder.encode(body, Utf8Charset.NAME);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        // 拼接WID_body
        String WID_body = null;
        try {

            if (BuildConfig.DEBUG) {

                price = 0.01 + "";
            }

            if (productId.equals("0") || productId.equals("10") || productId.equals("15")) {

                WID_body = URLEncoder.encode("花费" + price + "元购买" + cate, "utf-8");
            } else if (productId.equals("1")) {

                WID_body = URLEncoder.encode("花费" + price + "元购买爱语币", "utf-8");
            } else {

                WID_body = URLEncoder.encode(cate, "utf-8");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            OrderGenerateRequest orderRequest = new OrderGenerateRequest(Integer.parseInt(Constant.APPID)
                    , Integer.parseInt(userId), code, price, Integer.parseInt(amount), Integer.parseInt(productId),
                    WID_body, URLEncoder.encode(cate, "utf-8"),
                    mOrderErrorListener, rc);
            CrashApplication.getQueue().add(orderRequest);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
                if (first.isRequestSuccessful()) {
                    Log.e(TAG, "OrderGenerateWeiXinRequest success!");
                    PayReq req = new PayReq();
                    req.appId = mWeiXinKey;
                    req.partnerId = first.partnerId;
                    req.prepayId = first.prepayId;
                    req.nonceStr = first.nonceStr;
                    req.timeStamp = first.timeStamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = buildWeixinSign(req, first.mchKey);
                    mWXAPI.sendReq(req);
                } else {
                    validateOrderFail();
                }
            }
        };
        String uid = String.valueOf(AccountManagerLib.Instace(mContext).userId);
        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(mWeiXinKey, Constant.APPID,
                uid, price, amount, productId, "json", buildWeixinSign(uid, price, amount), rc);
        CrashApplication.getQueue().add(request);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        Timber.i("weixin sign : %s", sb.toString());
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinSign(String uid, String money, String amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constant.APPID).append(uid).append(money).append(amount);
        sb.append(WXSDF.format(new Date()));
        return MD5.getMD5ofStr(sb.toString());
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }

    /*private void payByWeb() {
        String url = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/servlet/paychannellist?";
        url += "out_user=" + AccountManagerLib.Instace(mContext).userId;
        url += "&appid=" + Constant.APPID;
        url += "&amount=" + 0;
        Intent intent = WebActivity.buildIntent(this, url, "订单支付");
        startActivity(intent);
        confirmMutex = true;
        finish();
    }*/

    private void validateOrderFail() {
//       CustomToast.showToast(mContext, "服务器正忙,请稍后再试!");
//       PayOrderActivity.this.finish();
        new AlertDialog.Builder(PayOrderActivity.this)
                .setTitle("订单异常!")
                .setMessage("订单验证失败!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PayOrderActivity.this.finish();
                    }
                })
                .show();
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            ToastUtil.showToast(mContext, "订单异常!");
//            PayOrderActivity.this.finish();
            new AlertDialog.Builder(PayOrderActivity.this)
                    .setTitle("订单提交出现问题!")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmMutex = true;
                            dialog.dismiss();
                            PayOrderActivity.this.finish();
                        }
                    })
                    .show();
        }
    };

    private Handler alipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    payorder_submit_btn.setClickable(true);
                    confirmMutex = true;
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showHint("支付成功！");

                        if (productId.equals("200")) {//如果是微课直购则刷新

                            IMooc.notifyCoursePurchased();
                        }

//                        Toast.makeText(mContext, "支付成功!", Toast.LENGTH_SHORT).show();
//                        ConfigManager.Instance().putInt("isvip", 1);
//                        User user =new User();
//                        user.vipStatus=productId;
//                        IyuUserManager.getInstance().setCurrentUser(user);
//                        if (AccountManagerLib.Instace(mContext).userInfo!=null){
//                            AccountManagerLib.Instace(mContext).userInfo.vipStatus=productId;
//                        }
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.showToast(PayOrderActivity.this, "支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.showToast(PayOrderActivity.this, "您已取消支付");
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.showToast(PayOrderActivity.this, "网络连接出错");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.showToast(PayOrderActivity.this, "支付失败");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    public void autoLogin() {
        new AutoLoginUtil(mContext).autoLogin(null);
    }


    private void showHint(final String message) {
        autoLogin();
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                })
                .create()
                .show();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(WXResponseEvent event) {
//        if (event.response.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            switch (event.response.errCode) {
//                case 0: {
//                    showHint("支付成功！");
//                    break;
//                }
//                case -1:
//                    CustomToast.showToast(PayOrderActivity.this, "支付失败");
//                    break;
//                case -2:
//                    CustomToast.showToast(PayOrderActivity.this, "您已取消支付");
//                    break;
//            }
//        }
//    }
}
