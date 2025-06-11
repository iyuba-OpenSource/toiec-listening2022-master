package com.iyuba.toeiclistening.activity.test;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.SentenceAdapter;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.event.ResetPlayDataEvent;
import com.iyuba.toeiclistening.event.TextParagraphEvent;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.widget.WordCard;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.subtitle.SubtitleSynViewMain;
import com.iyuba.toeiclistening.widget.subtitle.TextPageSelectTextCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * 原文列表Fragment
 * zh 2019.05.06
 */
public class TextFragment extends Fragment {

    private static final String DATA_POSITION = "data_position";

    SentenceAdapter sentenceAdapter;
    WordCard wordCard;

    RecyclerView text_rv_sentence;

    private Context mContext;
    private TitleInfo curTitleInfo;            //当前题的信息
    private ArrayList<Text> textList;        //Text实体的列表

    public TextFragment() {


    }

    public static TextFragment newInstance(int position) {
        TextFragment textFragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_POSITION, position);
        textFragment.setArguments(bundle);
        return textFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        RuntimeManager.setApplication(Objects.requireNonNull(getActivity()).getApplication());
//        RuntimeManager.setApplicationContext(mContext);

        View view = inflater.inflate(R.layout.fragment_text, container, false);
        mContext = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cView(view);
        initData();
        initView();
    }

    private void cView(View view) {

        text_rv_sentence = view.findViewById(R.id.text_rv_sentence);
        wordCard = view.findViewById(R.id.title_base_wordcard);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        //初始化T界面上的同步原文
        new SubtitleSynViewMain(mContext);
     /*   subtitleSynView.setTextList(textList);
        subtitleSynView.setTpstcb(tp);
        subtitleSynView.initSubtitleSum();*/


        if (sentenceAdapter == null) {

            sentenceAdapter = new SentenceAdapter(R.layout.item_sentence, textList);
            text_rv_sentence.setLayoutManager(new LinearLayoutManager(text_rv_sentence.getContext()));
            text_rv_sentence.setAdapter(sentenceAdapter);
            sentenceAdapter.setCallback(new SentenceAdapter.Callback() {
                @Override
                public void getWord(String word) {

                    wordCard.setVisibility(View.VISIBLE);
                    wordCard.searchWord(word);
                }
            });
        } else {

            sentenceAdapter.setNewData(textList);
        }

    }

    private void initData() {
        textList = DataManager.Instance().textList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ResetPlayDataEvent event) {

        TEDBHelper teHelper = ((TestDetailActivity) requireActivity()).teHelper;
        curTitleInfo = DataManager.Instance().titleInfoList.get(event.position);
        textList = teHelper.geTexts(curTitleInfo.TitleNum);
        DataManager.Instance().textList = textList;
        initView();
//        subtitleSynView.unsnyParagraph();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TextParagraphEvent event) {
        int currParagraph = event.currParagraph;
        if (currParagraph != 0 && sentenceAdapter != null) {
//            subtitleSynView.snyParagraphT(currParagraph);

            int p = currParagraph - 1;
            sentenceAdapter.setPosition(p);
            sentenceAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选词选中后的回调函数
     */
    private TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {
        @Override
        public void selectTextEvent(String selectText) {
            wordCard.setVisibility(View.GONE);
            if (selectText.matches("^[a-zA-Z]*")) {
                if (CheckNetWork.isNetworkAvailable(mContext)) {
                    wordCard.setVisibility(View.VISIBLE);
                    wordCard.searchWord(selectText);
                } else {
                    Toast.makeText(mContext, R.string.play_check_network, Toast.LENGTH_SHORT).show();
                    LogUtil.e("网络: " + R.string.play_check_network);
                }
            } else {
                CustomToast.showToast(mContext,
                        R.string.play_please_take_the_word, 1000);
            }
        }

        @SuppressLint("ShowToast")
        @Override
        public void selectParagraph(int paragraph) {

        }
    };
}
