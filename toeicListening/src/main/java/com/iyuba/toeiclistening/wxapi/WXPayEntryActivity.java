package com.iyuba.toeiclistening.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.iyuba.configation.Constant;
import com.iyuba.imooclib.IMooc;
import com.iyuba.toeiclistening.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import timber.log.Timber;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpayentry);

        api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APP_KEY);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

//            EventBus.getDefault().post(new WxPayEvent(resp.errCode));
            IMooc.notifyCoursePurchased();
        }

        Timber.i("onPayFinish, errCode= %s errStr= %s", resp.errCode, resp.errStr);
        //ToastFactory.showShort(WXPayEntryActivity.this,"errCode："+resp.errCode+"errStr："+resp.errStr);
        finish();
//        EventBus.getDefault().post(new WXResponseEvent(resp));
    }
}