package com.iyuba.toeiclistening.vocabulary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.widget.RecyclerViewSideBar;
import com.iyuba.play.ExtendedPlayer;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.activity.word.WordExerciseActivity;
import com.iyuba.toeiclistening.activity.word.WordListenActivity;
import com.iyuba.toeiclistening.activity.word.WordSpellExerciseActivity;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.event.UpWordEvent;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 单词列表
 */
public class WordListActivity extends AppCompatActivity {


    LinearLayout word_ll_en2cn, word_ll_cn2en, word_ll_spell, word_ll_listen;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerViewSideBar sidebar;
    TextView study;
    TextView test;
    private boolean showSideBar;
    private TEDBHelper tedbHelper;
    private Context mContext;
    private boolean isUpData;

    private boolean isShowWordExercise = false;

    SimpleWordListAdapter adapter;
    List<RememberWord> list;
    int stage;


    public static void startIntent(Context mContext, int stage, boolean showSideBar, boolean isShowWordExercise) {
        Intent intent = new Intent(mContext, WordListActivity.class);
        intent.putExtra("stage", stage);
        intent.putExtra("showSideBar", showSideBar);
        intent.putExtra("isShowWordExercise", isShowWordExercise);
        mContext.startActivity(intent);
    }


    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        sidebar = findViewById(R.id.sidebar);
        study = findViewById(R.id.study);
        test = findViewById(R.id.test);

        word_ll_en2cn = findViewById(R.id.word_ll_en2cn);
        word_ll_cn2en = findViewById(R.id.word_ll_cn2en);
        word_ll_spell = findViewById(R.id.word_ll_spell);
        word_ll_listen = findViewById(R.id.word_ll_listen);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        mContext = this;
        initView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tedbHelper = new TEDBHelper(mContext);
        initData();
        initListener();

        ExtendedPlayer player = new ExtendedPlayer(mContext);

        EventBus.getDefault().register(this);
    }


    private void initData() {
        stage = getIntent().getExtras().getInt("stage");
        showSideBar = getIntent().getExtras().getBoolean("showSideBar");
        isShowWordExercise = getIntent().getExtras().getBoolean("isShowWordExercise",true);


        if (!isShowWordExercise) {

            word_ll_en2cn.setVisibility(View.GONE);
            word_ll_cn2en.setVisibility(View.GONE);
            word_ll_spell.setVisibility(View.GONE);
            word_ll_listen.setVisibility(View.GONE);
        }


        int count = ConfigManager.Instance().loadInt("wpd", 30);

        ArrayList<RememberWord> rememberWords = tedbHelper.geWords();
        if (stage == -1) {
            list = rememberWords;
        } else {
            try {
                if (stage * count > rememberWords.size()) {
                    list = rememberWords.subList((stage - 1) * count, rememberWords.size());
                } else {
                    list = rememberWords.subList((stage - 1) * count, stage * count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleWordListAdapter(list, !showSideBar);
        adapter.setShowOrder(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void resetData() {
        stage = getIntent().getExtras().getInt("stage");
        int count = ConfigManager.Instance().loadInt("wpd", 30);
        if (stage == -1) {
            list = tedbHelper.geWords();
        } else {
            list = tedbHelper.geWords().subList((stage - 1) * count, stage * count);
        }
        adapter.setData(list);
    }


    private void initListener() {
        study.setOnClickListener(v -> WordStudyActivity.start(mContext, new ArrayList(list), 0));
        test.setOnClickListener(v -> WordTestActivity.start(mContext, stage));

        word_ll_en2cn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordExerciseActivity.startActivity(WordListActivity.this, new ArrayList<>(adapter.getCetRootWords()), 0);
            }
        });
        word_ll_cn2en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordExerciseActivity.startActivity(WordListActivity.this, new ArrayList<>(adapter.getCetRootWords()), 1);
            }
        });
        word_ll_spell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordSpellExerciseActivity.startActivity(WordListActivity.this, new ArrayList<>(adapter.getCetRootWords()), 0);
            }
        });
        word_ll_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordListenActivity.startActivity(WordListActivity.this, new ArrayList<>(adapter.getCetRootWords()), 0);
            }
        });
    }

    private void setView() {
        if (!showSideBar) {
            sidebar.setVisibility(View.GONE);
            test.setVisibility(View.VISIBLE);
            study.setVisibility(View.VISIBLE);
        } else {
            test.setVisibility(View.GONE);
            study.setVisibility(View.GONE);
            sidebar.setSelectedSideBarColor(R.color.app_color);
            sidebar.setRecyclerView(recyclerView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
        if (isUpData) {
            resetData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpWordEvent event) {
        isUpData = true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


}
