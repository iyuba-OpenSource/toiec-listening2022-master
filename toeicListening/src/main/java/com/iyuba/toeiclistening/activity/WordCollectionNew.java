package com.iyuba.toeiclistening.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.news.WordSynRequest;
import com.iyuba.core.common.protocol.news.WordSynResponse;
import com.iyuba.core.common.protocol.news.WordUpdateRequest;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.discover.activity.WordContentActivity;
import com.iyuba.core.discover.adapter.WordListAdapter;
import com.iyuba.core.R;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 单词本列表界面 正在使用中的
 *
 * @author chentong
 * @version 1.0
 */

public class WordCollectionNew extends BasisActivity implements
        OnHeaderRefreshListener, OnFooterRefreshListener {
    private Context mContext;
    private ArrayList<Word> words;
    private ArrayList<Word> words1;
    private ArrayList<Word> tryToDeleteWords = new ArrayList<Word>();
    private WordOp wo;
    private PullToRefreshView refreshView;// 刷新列表
    private View no_login;
    private ListView wordList;
    private WordListAdapter nla;
    private boolean isDelStart = false;
    private Button back;
    private ImageButton delButton;
    private String userId;
    private int page = 1;
    private Boolean isLastPage = false;
    private Boolean isTopRefresh = false;
    private Boolean isFootRefresh = false;
    private CustomDialog wettingDialog;
    private int webDeleteStatues = 0;
    private ZDBHelper zHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_collection_list_lib);
        CrashApplication.addActivity(this);
        mContext = this;
        wettingDialog = WaittingDialog.showDialog(mContext);

        zHelper = new ZDBHelper(mContext);

        refreshView = (PullToRefreshView) findViewById(R.id.listview);
        refreshView.setOnHeaderRefreshListener(this);
        refreshView.setOnFooterRefreshListener(this);
        no_login = findViewById(R.id.noLogin);
        // ----------------------------------删除按钮
        delButton =findViewById(R.id.button_delete);
        delButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isDelStart) {
                    delButton.setImageResource(R.drawable.button_edit); //button_edit
                    StringBuffer ids = new StringBuffer("");
                    Iterator<Word> iteratorVoa = null;
                    try {
                        iteratorVoa = words.iterator();
                        ArrayList<Word> delWordTemp = new ArrayList<Word>();
                        while (iteratorVoa.hasNext()) {
                            Word word = iteratorVoa.next();
                            if (word.isDelete) {
                                delWordTemp.add(word);
                                ids.append(",").append("\'" + word.key + "\'");
                                iteratorVoa.remove();
                            }
                        }

                        if (ids.toString() != null && ids.toString().length() != 0) {
                            delNetwordWord(delWordTemp);//网络删除 每删除一个要1.5s 很慢
                            wo.tryToDeleteItemWord(ids.toString().substring(1), userId);
                            for (int i = 0; i < delWordTemp.size(); i++) {
                                wo.tryToDeleteItemWord(delWordTemp.get(i).key, userId);
                                wo.deleteKeyWord(delWordTemp.get(i).key, userId);
                                zHelper.deleteNewWord(delWordTemp.get(i).key);//删除单词
                                LogUtil.e("单词，删除"+delWordTemp.get(i).key);
                            }
                            wo.deleteItemWord(userId);//真正删除
                        } else {
                            CustomToast.showToast(mContext, R.string.newword_please_select_word);
                        }
                    } catch (Exception e) {// 当初始化单词表中尚无单词是出现异常处理
                        CustomToast.showToast(mContext, R.string.no_new_word);
                    }
                    //cancelDelete(); //
                    isDelStart = false;
                    //onHeaderRefresh(refreshView);//强行刷新
                } else {
                    delButton.setImageResource(R.drawable.button_edit_finished);// button_edit_finished
                    isDelStart = true;
                }
                changeItemDeleteStart();
                ArrayList<Word> xxx = (ArrayList<Word>) wo.findDataByAll(userId);
                //ArrayList<Word> xxxX = (ArrayList<Word>) wo.findDataByAllStatus(userId);
                //isWebToLocal=false;//网络删除是否完成
            }
        });
        wordList = (ListView) findViewById(R.id.list);
        back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        //onHeaderRefresh(refreshView);//强行刷新 有问题 有作用
    }

    public void changeItemDeleteStart() {
        if (nla != null) {
            nla.modeDelete = isDelStart;
            handler.sendEmptyMessage(0);
        }
    }

    public void cancelDelete() {
        if (words != null && words.size() != 0) {
            int size = words.size();
            for (int i = 0; i < size; i++) {
                words.get(i).isDelete = false;
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            no_login.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
            userId = AccountManagerLib.Instace(mContext).userId;
        } else {
            no_login.setVisibility(View.VISIBLE);
            refreshView.setVisibility(View.GONE);
            findViewById(R.id.button_to_login).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            intent.setClass(mContext, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
        }
        if (wordList != null) {
            isDelStart = false;
            if (nla != null) {
                //cancelDelete();
                isDelStart = false;
                changeItemDeleteStart();
            }
            wo = new WordOp(this);
            words = (ArrayList<Word>) wo.findDataByAll(userId);
            tryToDeleteWords = (ArrayList<Word>) wo.findDataByDelete(userId);//5804979
            if (tryToDeleteWords != null && NetWorkState.isConnectingToInternet()) {
                delNetwordWord(tryToDeleteWords);
            }
//            if (nla==null) {//如果数据为空，就重新加载
//            }
            nla = new WordListAdapter(this);
            if (words != null) {
                nla.setData(words);
                handler.sendEmptyMessage(0);
            } else {
                words = new ArrayList<Word>();
                onHeaderRefresh(refreshView);
            }

            wordList.setAdapter(nla);
            wordList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    if (isDelStart) {
                        if (words.get(arg2).isDelete) {
                            words.get(arg2).isDelete = false;
                        } else {
                            words.get(arg2).isDelete = true;
                        }
                        nla.notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent(mContext, WordContentActivity.class);
                        intent.putExtra("word", words.get(arg2).key);
                        startActivity(intent);
                    }
                }
            });
            //onHeaderRefresh(refreshView);//强行刷新 有问题
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    nla.notifyDataSetChanged();
                    break;
                case 1:
                    wettingDialog.show();
                    break;
                case 2:
                    wettingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 4:
                    page = 1;
                    if (words != null) {
                        words.clear();
                    }
                    handler.sendEmptyMessage(5);
                    handler.sendEmptyMessage(1);
                    break;
                case 5:
                    ExeProtocol.exe(new WordSynRequest(userId, userId, page),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    // TODO Auto-generated method stub
                                    WordSynResponse wsr = (WordSynResponse) bhr;
                                    words.addAll(wsr.wordList);
                                    if (words != null && words.size() > 0) {
                                        if (wsr.firstPage == wsr.nextPage) {
                                            isLastPage = true;
                                        } else {
                                            page++;
                                            isLastPage = false;
                                        }

                                        //nla.setData(words);
                                        //wo.deleteItemWord(userId);
                                        //if (isWebToLocal) {//是否将单词写入本地
                                        wo.saveData(wsr.wordList);
                                        //}
                                        words1 = (ArrayList<Word>) wo.findDataByAll(userId);
                                        words = words1;
                                        nla.setData(words);

                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(6);
                                    } else {
                                        handler.sendEmptyMessage(2);
                                        handler.sendEmptyMessage(7);
                                    }
                                }

                                @Override
                                public void error() {
                                    // TODO Auto-generated method stub
                                    handler.sendEmptyMessage(3);
                                    handler.sendEmptyMessage(6);
                                }
                            });
                    break;
                case 6:
                    handler.sendEmptyMessage(2);
                    if (isTopRefresh) {
                        isTopRefresh = false;
                        refreshView.onHeaderRefreshComplete();
                    } else if (isFootRefresh) {
                        isFootRefresh = false;
                        refreshView.onFooterRefreshComplete();
                    }
                    break;
                case 7:
                    CustomToast.showToast(mContext, R.string.word_no_data);
                    break;
                case 8:
                    CustomToast.showToast(mContext, R.string.word_add_all);
                    break;
                case 9://网络删除
                    if (msg.obj != null) {
                        ClientSession.Instace().asynGetResponse(
                                new WordUpdateRequest(userId,
                                        WordUpdateRequest.MODE_DELETE,
                                        msg.obj.toString()), new IResponseReceiver() {
                                    @Override
                                    public void onResponse(BaseHttpResponse response,
                                                           BaseHttpRequest request, int rspCookie) {
                                        wo.deleteItemWord(userId);
                                    }
                                });
                    }
                    if (msg.arg1 == msg.arg2) {
                        webDeleteStatues = 2;
                    }
                    break;
            }
        }
    };

    //网络删除
    private void delNetwordWord(ArrayList<Word> wordss) {
        int size = wordss.size();
        Message message;
        for (int i = 0; i < size; i++) {
            webDeleteStatues = 1;
            message = new Message();
            message.what = 9;
            message.obj = wordss.get(i).key;
            message.arg1 = i+1;
            message.arg2 = size;
            if (i==0){
                handler.sendMessageDelayed(message, 0);
            }else {
                handler.sendMessageDelayed(message, 1500);//延时操作？？？？
            }
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        if (!isLastPage) {
            handler.sendEmptyMessage(5);
            isFootRefresh = true;
        } else {
            handler.sendEmptyMessage(8);
            refreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
            if (webDeleteStatues != 1) {//如果是网络删除完成
                handler.sendEmptyMessage(4);
                refreshView.setLastUpdated(ExeRefreshTime.lastRefreshTime(mContext,"Word"));
                isTopRefresh = true;
            } else if (webDeleteStatues == 1) {//如果网络删除 正在进行中
                CustomToast.showToast(mContext, "网络删除还未结束,请等待");
                refreshView.onHeaderRefreshComplete();
            }
        }
    }
}
