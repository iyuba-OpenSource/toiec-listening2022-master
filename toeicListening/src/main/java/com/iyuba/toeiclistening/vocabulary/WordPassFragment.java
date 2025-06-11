package com.iyuba.toeiclistening.vocabulary;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.widget.MyGridView;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.event.UpWordPassEvent;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词闯关
 */
public class WordPassFragment extends Fragment {

    MyGridView gridview;

    TextView words_all;

    //@BindView(R.id.toolbar)
    // Toolbar toolbar;
    ImageView ivBack;
    ImageView ivSet;

    ImageView ivWordList;

    StepAdapter adapter;
    RememberWord word;
    int dbSize;
    int wpd;
    int step;
    private Context mContext;

    private int checkedItem;
    public static int WORD_COUNT = 30;
    private ProgressDialog dialog;
    private TEDBHelper helper;
    List<RememberWord> wordList;
    private boolean isUpData;

    public WordPassFragment() {
    }

    public static WordPassFragment newInstance() {

        Bundle args = new Bundle();
        WordPassFragment fragment = new WordPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new TEDBHelper(getActivity());
        wordList = helper.geWords();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_word_step, container, false);
        mContext = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(mContext);

        initView(view);
        initData();
        initListener();
    }

    private void initView(View view) {

        gridview = view.findViewById(R.id.gridview);
        words_all = view.findViewById(R.id.all_words_text);
        ivBack = view.findViewById(R.id.iv_back);
        ivSet = view.findViewById(R.id.set);
        ivWordList = view.findViewById(R.id.words_all);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isUpData) {
            initData();
        }
    }

    private void initListener() {
        ivWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordListActivity.startIntent(mContext, -1, true, false);
            }
        });
        ivSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
        ivBack.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }

    private void showAlert() {
        final String[] wpd = {"30", "50", "70", "100"};
        final String select = String.valueOf(ConfigManager.Instance().loadInt("wpd", 30));
        for (int i = 0; i < wpd.length; i++) {
            if (wpd[i].equals(select)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(mContext)
                .setTitle("请选择每关单词数")
                .setSingleChoiceItems(wpd, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WORD_COUNT = Integer.parseInt(wpd[which]);
                    }
                })
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfigManager.Instance().putInt("wpd", WORD_COUNT);
                        initData();
                        dialog.dismiss();
                    }
                }).create().show();
        ConfigManager.Instance().putBoolean("isWordNumberSelected", true);
        ConfigManager.Instance().putBoolean("dbChange", true);

    }

    public void initData() {
        step = ConfigManager.Instance().loadInt("stage", 1);
//        db = CetDataBase.getInstance(this);
//        if (db.getCetRootWordDao().getWordsByStage(0).size() > 0) {
//            wordList = db.getCetRootWordDao().getAllRootWord();
//        } else {
//            wordList = db.getCetRootWordDao().getAllRootWord(step);
//        }
        dbSize = wordList.size();
        dialog.setMessage("正在加载单词");
        dialog.show();
        wpd = ConfigManager.Instance().loadInt("wpd", 30);
        if (ConfigManager.Instance().loadBoolean("dbChange", true)) {
            new Thread(runnable).start();
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            List<RememberWord> list = new ArrayList<>();
            for (int i = 0; i < dbSize; i++) {
                word = wordList.get(i);
                word.stage = i / wpd + step;
                list.add(word);
            }
            helper.updateWords(list);
            mHandler.sendEmptyMessage(1);
        }
    };


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            ConfigManager.Instance().putBoolean("dbChange", false);
            int count = ConfigManager.Instance().loadInt("wpd", 30);
            step = ConfigManager.Instance().loadInt("stage", 1);
            words_all.setText(String.format("单词总数:%s   闯关单词数:%s", wordList.size(), count));
//            adapter = new StepAdapter(3, step, dbSize / wpd + step);
            adapter = new StepAdapter(3, step, dbSize / wpd);
            gridview.setAdapter(adapter);
            super.handleMessage(msg);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpWordPassEvent event) {
        isUpData = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
