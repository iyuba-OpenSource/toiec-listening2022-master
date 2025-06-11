package com.iyuba.core.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.event.ChangeVideoEvent;

import org.greenrobot.eventbus.EventBus;

public class AutoLoginUtil {
    private Context mContext;

    public AutoLoginUtil(Context context){
        mContext=context;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtil.showToast(mContext, "刷新数据成功");
                    EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布
                    break;
                case 2:
                    ToastUtil.showToast(mContext, "刷新数据失败");
                    break;
                case 3:
                    ToastUtil.showToast(mContext, "账户丢失！");
                    break;
            }
        }
    };

    public void autoLogin(OperateCallBack back) {
        String[] nameAndPwd = AccountManagerLib.Instace(mContext).getUserNameAndPwd();
        String userName = nameAndPwd[0];
        String userPwd = nameAndPwd[1];
        if (!TextUtils.isEmpty(userName)) {
            if (back==null) {
                AccountManagerLib.Instace(mContext).login(userName, userPwd, callBack);
            }else {
                AccountManagerLib.Instace(mContext).login(userName, userPwd, back);
            }
        } else {
            handler.sendEmptyMessage(3);
        }
    }

    private OperateCallBack callBack = new OperateCallBack() {
        @Override
        public void success(String message) {
            handler.sendEmptyMessage(1);
        }

        @Override
        public void fail(String message) {
            handler.sendEmptyMessage(2);
        }
    };
}
