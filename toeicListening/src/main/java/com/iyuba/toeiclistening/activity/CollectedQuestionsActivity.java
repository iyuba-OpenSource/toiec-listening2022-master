package com.iyuba.toeiclistening.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.iyuba.toeiclistening.activity.test.TestDetailActivity;
import com.iyuba.toeiclistening.adapter.FavTitleAdapter;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

//收藏题目
public class CollectedQuestionsActivity extends Activity {
    private Context mContext;
    private ArrayList<TitleInfo> favTitleInfoList;
    private FavTitleAdapter adapter;

    private Button delButton;
    private ListView favTitleListView;
    private Button btn_back;
    private ZDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.collectedquestions);
        helper = new ZDBHelper(mContext);
        init();
        getView();
        setView();
    }

    public void init() {
        mContext = this;
//        favTitleInfoList = DataManager.Instance().favTitleInfoList;
        favTitleInfoList = helper.getFavTitleInfos();
        adapter = new FavTitleAdapter(mContext, favTitleInfoList);
    }

    public void getView() {
        btn_back = (Button) this.findViewById(R.id.btn_back);
        delButton = (Button) this.findViewById(R.id.collectedquestions_delete);
        favTitleListView = (ListView) this.findViewById(R.id.collectedquestions_list);
    }

    public void setView() {
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        delButton.setOnClickListener(onClickListener);
        favTitleListView.setAdapter(adapter);
        favTitleListView.setOnItemClickListener(onItemClickListener);
    }

    //批量删除的监听器
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int state = adapter.getState();
            if (state == 1) {//如果不处在批量删除的状态
                delButton.setText(R.string.favtitle_finish);
                adapter.setState(0);
                adapter.notifyDataSetChanged();
            } else {//处在批量删除的状态
                delButton.setText(R.string.favtitle_delete);
                adapter.setState(1);
            }
        }
    };
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            int state = adapter.getState();
            if (state == 1) {//如果不处在批量删除的状态，则跳转到学习界面
                TitleInfo selectTitle = favTitleInfoList.get(arg2);
                int titleNum = selectTitle.TitleNum;
                TEDBHelper helper = new TEDBHelper(mContext);
                DataManager.Instance().anwserList = helper.getAnswers(titleNum);
                DataManager.Instance().textList = helper.geTexts(titleNum);
                DataManager.Instance().explain = helper.getExplain(titleNum);
                DataManager.Instance().titleInfoList = favTitleInfoList;
                Intent intent = new Intent();
                intent.setClass(mContext, TestDetailActivity.class);//TitleBaseActivity
                intent.putExtra("position", arg2);
                mContext.startActivity(intent);
            } else if (state == 0) {
                adapter.updateView(arg2);
            }
        }
    };

}
