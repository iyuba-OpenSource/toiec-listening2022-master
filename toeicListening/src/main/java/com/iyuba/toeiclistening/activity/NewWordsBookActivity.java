package com.iyuba.toeiclistening.activity;

import java.util.ArrayList;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.NewWordAdapter;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;
//import com.iyuba.toeiclistening.manager.AccountManagerLib;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.protocol.WordSynRequest;
import com.iyuba.toeiclistening.protocol.WordSynRespose;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/*
* 老旧的单词本 ，需要清除
*
* */
public class NewWordsBookActivity extends Activity {
    private Context mContext;
    private ArrayList<NewWord> newWordList;
    private NewWordAdapter adapter;
    // view控件
    private Button delButton;
    private ListView newWordListView;
    private Button synsButton;
    private String userName;
    private ZDBHelper zHelper;
    private CustomDialog wettingDialog;
    private Button btn_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newwordsbook);
        init();
        getView();
        setView();
    }


    public void init() {
        mContext = this;
        newWordList = DataManager.Instance().newWordList;
        adapter = new NewWordAdapter(mContext, newWordList);
        zHelper = new ZDBHelper(mContext);
        wettingDialog = new WaittingDialog().wettingDialog(mContext);
    }

    public void getView() {
        btn_Back = (Button) this.findViewById(R.id.btn_back);
        delButton = (Button) this.findViewById(R.id.new_words_book_delete);
        newWordListView = (ListView) this.findViewById(R.id.new_words_book_list);
//        synsButton = (Button) this.findViewById(R.id.new_words_book_synchro);
//        synsButton.setVisibility(View.GONE);
    }

    public void setView() {
        btn_Back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        delButton.setOnClickListener(onClickListener);
        newWordListView.setAdapter(adapter);
        newWordListView.setOnItemClickListener(onItemClickListener);
//        synsButton.setOnClickListener(synsButtOnClickListener);
    }

    // 批量删除的监听器
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int state = adapter.getState();
            if (state == 1) {// 如果不处在批量删除的状态
                delButton.setText(R.string.favtitle_finish);
                adapter.setState(0);
            } else {// 处在批量删除的状态
                delButton.setText(R.string.favtitle_delete);
                adapter.setState(1);
            }
        }
    };
    /**
     * 同步按钮的监听器
     */
    private OnClickListener synsButtOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                wettingDialog.show();
                new Thread(new SynchoWordThread()).run();
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext, LoginActivity.class);
                startActivity(intent);
            }

        }
    };
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            int state = adapter.getState();
            if (state == 1) {
                adapter.updateExplainView(arg2);
            } else if (state == 0) {
                adapter.updateView(arg2);
            }
        }
    };

    class SynchoWordThread implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ClientSession.Instace().asynGetResponse(
                    new WordSynRequest(AccountManagerLib.Instace(mContext).userId,
                            userName), new IResponseReceiver() {

                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            WordSynRespose wsr = (WordSynRespose) response;
                            ArrayList<NewWord> tempWords = (ArrayList<NewWord>) wsr.wordList;
                            if (tempWords != null) {
                                for (NewWord word : tempWords) {
                                    word.userName = AccountManagerLib.Instace(mContext).userName;
                                    word.CreateDate = "";
                                    zHelper.saveNewWord(word);
                                }
                                newWordList = zHelper.getNewWords(AccountManagerLib.Instace(mContext).userName);
                                handler.sendEmptyMessage(2);
                            } else {
                                CustomToast.showToast(mContext, "同步失败", 1000);
                                wettingDialog.dismiss();
                            }
                        }
                    }, null, mNetStateReceiver);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    wettingDialog.dismiss();
                    adapter = new NewWordAdapter(mContext, newWordList);
                    newWordListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    CustomToast.showToast(mContext, "同步成功", 1000);
                    break;

                default:
                    break;
            }
        }
    };
    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {
            handler.sendEmptyMessage(1);
        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }
    };
}
