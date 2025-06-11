package com.iyuba.core.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.iyuba.core.common.manager.AccountManagerLib;
import com.umeng.analytics.MobclickAgent;
/**
 * 应用基类
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());
        initCommons();
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    private void initCommons() {
        /*Button btn_nav = (Button) findViewById(R.id.btn_nav);
        if (btn_nav != null) {
            btn_nav.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });
        }*/

    }

    /**
     * 返回用于显示界面的id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化变量,包括intent携带的数据和activity内的变量
     */
    protected abstract void initVariables();

    /**
     * 加载layout布局,初始化控件,为事件挂上事件的方法
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * 调用mobileAPI
     */
    protected abstract void loadData();

    /**
     * 查找View，省去强转的操作
     *
     * @param id 布局文件中的i控件id
     * @return 返回对应的控件View
     */
    protected <T> T findView(int id) {
        @SuppressWarnings("unchecked")
        T view = (T) findViewById(id);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    /**
     * 判断用户有没有登录的方法,没有登录去登录呀
     */
    public boolean checkUserLoginAndLogin() {
        // 用户是否登录
        boolean isLogIn = AccountManagerLib.Instace(getApplicationContext())
                .checkUserLogin();
        if (isLogIn) {
            return true;
        } else {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        }
    }

    /**
     * 判断用户有没有登录的方法
     */
    public boolean isUserLogin() {
        // 用户是否登录
        boolean isLogIn = AccountManagerLib.Instace(getApplicationContext()).checkUserLogin();
        if (isLogIn) {
            return true;
        } else {
            return false;
        }
    }

}
