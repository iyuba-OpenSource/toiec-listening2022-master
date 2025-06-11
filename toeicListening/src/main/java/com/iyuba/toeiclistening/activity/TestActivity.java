package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.activity.test.TestDetailActivity;
import com.iyuba.toeiclistening.adapter.TestAdapter;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

import java.util.ArrayList;

/**
 * 题库-试题列表
 */
public class TestActivity extends Activity {
    private ListView titleListView;
    private Button backButton;
    private ArrayList<TitleInfo> titleList;
    private TextView testTitleTextView;
    String testTitleString = "";

    private int testType;

    private Context mContext;
    private TestAdapter testAdapter;
    private boolean isClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.test);
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            testTitleString = bundle.getString("packName");
            testType = bundle.getInt("testType");
        }

        ZDBHelper helper = new ZDBHelper(mContext);
        titleList = helper.getTitleInfos(testTitleString, testType);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        //针对从题库界面选中的试题进行数据的准备
        //第一次获取试题正确，第二次没有改变？？？
        // 获取intent里面传递的数据
        findView();
        setView();
        isClick = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void findView() {
        titleListView = (ListView) findViewById(R.id.test_list);
        backButton = (Button) findViewById(R.id.btnTestBack);
        testTitleTextView = (TextView) findViewById(R.id.test_title);
    }

    public void setView() {
        //把该套题的所有试题Item信息放到ListView中
        if (titleList == null) {
            LogUtil.e("titleList=null!!!");
        }

        if (testAdapter == null) {
            testAdapter = new TestAdapter(this, titleList);
            titleListView.setAdapter(testAdapter);
        } else {
            testAdapter.setData(titleList);
        }
        testAdapter.notifyDataSetChanged();
        LogUtil.e("TestActivity更新数据！！！");

        titleListView.setOnItemClickListener(titleOnItemClickListener);
        backButton.setOnClickListener(backOnClickListener);
        testTitleTextView.setText(testTitleString);
    }

    private OnClickListener backOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    private OnItemClickListener titleOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isClick) {
                isClick = false;
                TitleInfo selectTitle = titleList.get(arg2);
                int titleNum = selectTitle.TitleNum;
                TEDBHelper helper = new TEDBHelper(mContext);
                DataManager.Instance().anwserList = helper.getAnswers(titleNum);
                DataManager.Instance().textList = helper.geTexts(titleNum);//安装包更新失败，数据库未知问题
                DataManager.Instance().explain = helper.getExplain(titleNum);

                Intent intent = new Intent();
                intent.setClass(mContext, TestDetailActivity.class);//TitleBaseActivity TestDetailActivity
                intent.putExtra("position", arg2);
                mContext.startActivity(intent);
            }
        }
    };


}
