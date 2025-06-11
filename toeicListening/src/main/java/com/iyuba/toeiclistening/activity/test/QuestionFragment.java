package com.iyuba.toeiclistening.activity.test;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.TitleQuestionAdapter;
import com.iyuba.toeiclistening.entity.Answer;
import com.iyuba.toeiclistening.entity.Explain;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.event.PlayAnimationEvent;
import com.iyuba.toeiclistening.event.ResetPlayDataEvent;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.widget.WordCard;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.subtitle.TextPageSelectTextCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;


/**
 * 问题
 */
public class QuestionFragment extends Fragment {

    private static final String DATA_POSITION = "data_position";
    ListView rvQuestionList;
    TextView tvExplain;
    TextView tvExplainMatch;
    TextView tvExplainText;
    RelativeLayout rlLayout;
    ImageView ivPlay;

    WordCard wordCard;

    private TextView dialogTv;

    private TitleInfo curTitleInfo;            //当前题的信息
    private int curPosition;
    public TitleQuestionAdapter adapter;    // 问题列表的适配器
    private Context mContext;
    private ArrayList<Answer> answerList;    //Answer实体的列表
    private Explain explain;
    private ZDBHelper zHelper;
    //private TEDBHelper teHelper;


    public static QuestionFragment newInstance(int position) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_POSITION, position);
        questionFragment.setArguments(bundle);
        return questionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            curPosition = bundle.getInt(DATA_POSITION);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initView(view);
        initData();
        initListener();

    }

    private void initView(View view) {

        rvQuestionList = view.findViewById(R.id.rv_question_list);
        tvExplain = view.findViewById(R.id.tv_explain);
        tvExplainMatch = view.findViewById(R.id.tv_explain_match);
        tvExplainText = view.findViewById(R.id.tv_explain_text);
        rlLayout = view.findViewById(R.id.rl_layout);
        ivPlay = view.findViewById(R.id.iv_play_animation);
        wordCard = view.findViewById(R.id.title_base_wordcard);
    }


    private void initData() {
        //curTitleInfo = ((TestDetailActivity) Objects.requireNonNull(getActivity())).curTitleInfo;
        curTitleInfo = DataManager.Instance().titleInfoList.get(curPosition);
        answerList = DataManager.Instance().anwserList;
        zHelper = new ZDBHelper(mContext);
        //teHelper = new TEDBHelper(mContext);
        adapter = new TitleQuestionAdapter(mContext, answerList, tp);
        explain = DataManager.Instance().explain;

        tvExplainText.setText(explain.Explain);
        rvQuestionList.setAdapter(adapter);
        if (answerList.size() > 1) {
            tvExplainMatch.setVisibility(View.VISIBLE);
            tvExplain.setVisibility(View.GONE);
        } else {
            tvExplain.setVisibility(View.VISIBLE);
            tvExplainMatch.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        tvExplain.setOnClickListener(v -> {
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                int vip = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
                if (vip != 0) {
                    if (tvExplainText.getVisibility() == View.VISIBLE) {
                        tvExplainText.setVisibility(View.GONE);
                        tvExplain.setText("查看解析");
                    } else {
                        tvExplainText.setVisibility(View.VISIBLE);
                        tvExplain.setText("隐藏解析");
                    }
                } else {
                    showVipDialog();
                }
            } else {
                CustomToast.showToast(mContext, "请先登录！");
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
        tvExplainMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    int vip = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
                    if (vip != 0) {
                        showDialog();
                    } else {
                        showVipDialog();
                    }
                } else {
                    CustomToast.showToast(mContext, "请先登录！");
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ResetPlayDataEvent event) {
        TEDBHelper teHelper = ((TestDetailActivity) Objects.requireNonNull(getActivity())).teHelper;
        curTitleInfo = DataManager.Instance().titleInfoList.get(event.position);

        //保存刚才做题的记录 需要知道是上一题还是下一题啊 下一题就减一保存上一题 上一题就加一保存下一题
        TitleInfo titleInfo;
        if (event.isNest) {
            titleInfo = DataManager.Instance().titleInfoList.get(event.position - 1);
            tvExplainText.setVisibility(View.GONE);
            tvExplain.setText("查看解析");
        } else {
            titleInfo = DataManager.Instance().titleInfoList.get(event.position + 1);
            tvExplainText.setVisibility(View.GONE);
            tvExplain.setText("查看解析");
        }
        zHelper.setRightNum(titleInfo.TitleNum, adapter.getRightNum());

        answerList = teHelper.getAnswers(curTitleInfo.TitleNum);
        explain = teHelper.getExplain(curTitleInfo.TitleNum);

        DataManager.Instance().anwserList = answerList;
        adapter.answerList = answerList;

        adapter.iniQuestionCondList();
        adapter.notifyDataSetChanged();

        if (answerList.size() > 1) {
            tvExplainMatch.setVisibility(View.VISIBLE);
            tvExplain.setVisibility(View.GONE);
        } else {
            tvExplain.setVisibility(View.VISIBLE);
            tvExplainText.setText(explain.Explain);
            tvExplainMatch.setVisibility(View.GONE);
        }

        if (dialogTv != null) {
            dialogTv.setText(explainReSet(explain.Explain));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayAnimationEvent event) {
        animation(event.status);
    }

    public void animation(int i) {

        AnimationDrawable animationDrawable = (AnimationDrawable) ivPlay.getDrawable();
        if (i == 0 && animationDrawable != null) {
            animationDrawable.stop();
        } else if (i == 1 && animationDrawable != null) {
            animationDrawable.start();
        } else {
            LogUtil.e("动画为空");
        }
    }


    private void showDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.dialogStyle);

        //设置要显示的view
        View view = View.inflate(mContext, R.layout.dialog_bottom_explain, null);
        //此处可按需求为各控件设置属性
        view.findViewById(R.id.tv_close).setOnClickListener(view1 -> dialog.dismiss());
        dialogTv = ((TextView) view.findViewById(R.id.tv_explain_text));
        dialogTv.setText(explainReSet(explain.Explain));
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);//点击空白关闭
        Window window = dialog.getWindow();
        //设置弹出窗口大小
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置显示位置
        window.setGravity(Gravity.BOTTOM);
        //设置动画效果
        window.setWindowAnimations(R.style.AnimBottom);
        dialog.show();
    }

    public void showVipDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage
                        ("VIP用户可查看所有试题解析\n您可以去会员中心开通VIP权限").
                setPositiveButton("购买VIP", (dialog1, whichButton) -> {
                    startActivity(VipCenterGoldActivity.buildIntent(mContext, 1));
                }).setNeutralButton("下一次", (dialog12, which) -> dialog12.dismiss()).create();
        dialog.show();
    }

    private String explainReSet(String explain) {
        String index1 = curTitleInfo.TitleName.substring(8, 10);
        int index = Integer.valueOf(index1);

        StringBuffer stringBuffer = new StringBuffer(explain);
        //stringBuffer.indexOf(" \r",explain.indexOf(index));
        stringBuffer.insert(explain.indexOf(String.valueOf(index + 1)), "\r\n");
        stringBuffer.insert(explain.indexOf(String.valueOf(index + 2)) + 2, "\r\n");
        LogUtil.e("解析： " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 选词选中后的回调函数
     */
    private TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {

        @Override
        public void selectTextEvent(String selectText) {
//			Toast.makeText(getBaseContext(), "选词回调", 1000).show();
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

    @Override
    public void onPause() {
        LogUtil.e("onPause");
        ArrayList<TitleInfo> titleInfoList = DataManager.Instance().titleInfoList;
        TitleInfo titleInfo = null;
        if (curPosition >= titleInfoList.size())
            curPosition = titleInfoList.size() - 1;
        titleInfo = titleInfoList.get(curPosition);
        //更新数据
        ArrayList<TitleInfo> titleInfosList = zHelper.getTitleInfos(titleInfo.PackName, titleInfo.TestType);
        ArrayList<TitleInfo> favTitleInfos = zHelper.getFavTitleInfos();
        int rightNum = adapter.getRightNum();
        zHelper.setRightNum(curTitleInfo.TitleNum, rightNum);//这个生效慢？
        DataManager.Instance().titleInfoList = titleInfosList;
        //DataManager.Instance().favTitleInfoList = favTitleInfos;
        //if (curPosition <= DataManager.Instance().titleInfoList.size())//越界异常
        //DataManager.Instance().titleInfoList.get(curPosition).RightNum = rightNum;//写入当前题是否正确！
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter.bitmap != null && !adapter.bitmap.isRecycled()) {
            adapter.bitmap.recycle();
            adapter.bitmap = null;
        }
    }
}
