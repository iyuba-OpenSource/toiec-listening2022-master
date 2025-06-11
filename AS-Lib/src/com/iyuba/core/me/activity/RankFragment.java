package com.iyuba.core.me.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;


import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.RankFragmentListAdapter;
import com.iyuba.core.me.protocol.GetRankListenInfoRequest;
import com.iyuba.core.me.protocol.GetRankListenInfoResponse;
import com.iyuba.core.me.protocol.GetRankReadInfoRequest;
import com.iyuba.core.me.protocol.GetRankReadInfoResponse;
import com.iyuba.core.me.protocol.GetRankSpeakInfoRequest;
import com.iyuba.core.me.protocol.GetRankSpeakInfoResponse;
import com.iyuba.core.me.protocol.GetRankStudyInfoRequest;
import com.iyuba.core.me.protocol.GetRankStudynfoResponse;
import com.iyuba.core.me.protocol.GetRankTestInfoRequest;
import com.iyuba.core.me.protocol.GetRankTestInfoResponse;
import com.iyuba.core.me.sqlite.mode.RankBean;
import com.iyuba.core.me.widget.CircleImageView;

import com.iyuba.headnewslib.fragment.BaseFragment;
import com.iyuba.core.R;
import com.iyuba.multithread.util.NetStatusUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;


/**
 * 排行榜
 */
public class RankFragment extends BaseFragment {
    private static final String TAG = RankFragment.class.getSimpleName();
    private Context mContext;
    private String uid;
    private String type;
    private String total;
    private String start;


    private String myImgSrc = "";
    private String myId = "";
    private String myRanking = "";
    private String myName = "";

    private String result = "";
    private String message = "";

    //阅读
    private String myWords = "";//文章数
    private String myWpm = "";
    private String myCnt = "";//单词数

    //听力和学习
    private String myTotalTime = "";
    private String myTotalWord = "";
    private String myTotalEssay = "";

    //口语
    private String myScores = "";//总分
    private String myCount = "";//总句子数

    //测试

    private String myTotalRight;//正确数
    private String myTotalTest;  //总题数

    private List<RankBean> rankBeans = new ArrayList<RankBean>();
    private RankBean champion; //第一名的详细数据

//    private RankAdapter rankListAdapter;


    private LayoutInflater inflater;
    private boolean scorllable = true;
    private static boolean isNight = false;


    private Pattern p;
    private Matcher m;


    private TextView note;
    private CircleImageView userImage;
    private TextView userImageText;
    private TextView userName;

    private TextView myUsername;
    private TextView userInfo;
    private ListView rankListView;


    private CircleImageView myImage;

    private View listFooter;

    private static final String RANKTYPE = "rank_type";
    private int dialog_position = 0;
    private String rankType;

    public String frag_text, frag_siteUrl;


    private boolean isPrepared;
    private boolean mHasLoadedOnce;
    private CustomDialog waitDialog;

    private RankFragmentListAdapter adapter;

    public static RankFragment newInstance(String rank_type) {
        Bundle bundle = new Bundle();
        bundle.putString(RANKTYPE, rank_type);
        RankFragment fragment = new RankFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isPrepared = true;
        View parent = inflater.inflate(R.layout.fragment_rank_lib,  container, false);

        init(parent);
        mContext = getActivity();

        waitDialog = WaittingDialog.showDialog(getActivity());
        waitDialog.setTitle("请稍后");
        rankType = getArguments().getString(RANKTYPE);

        Log.e("RANKTYPE", rankType);
        note.setText("今天");


        uid = AccountManagerLib.Instace(mContext).userId == null ? "0" : AccountManagerLib.Instace(mContext).userId;
        type = "D";
        total = "30";
        start = "0";
        listFooter = inflater.inflate(R.layout.comment_footer, null);
        rankListView.addFooterView(listFooter);
        lazyload();


        //选择今天，本周，本月
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseHeartType(note.getText().toString());

            }
        });
        rankListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            // 当comment不为空且comment.size()不为0且没有完全加载
                            if (scorllable)
                                mHanlder.sendEmptyMessage(0);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

        return parent;
    }

    private void init(View view) {
        note = view.findViewById(R.id.rank_note);
        userImage = view.findViewById(R.id.rank_user_image);
        userImageText = view.findViewById(R.id.rank_user_image_text);
        userName = view.findViewById(R.id.rank_user_name);
        myUsername = view.findViewById(R.id.username);
        userInfo = view.findViewById(R.id.rank_info);
        rankListView = view.findViewById(R.id.rank_list);
        myImage = view.findViewById(R.id.my_image);
    }

    @Override
    protected void lazyload() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        mHanlder.sendEmptyMessage(0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * 提供给用户选择动态类型的单选列表对话框
     */
    private void choseHeartType(String choose_type) {
        final String typeArray[] = new String[]{"今天", "本周", "本月"};

        for (int i = 0; i < typeArray.length; i++) {

            if (choose_type.equals(typeArray[i])) {

                dialog_position = i;
            }
        }

        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setSingleChoiceItems(typeArray,  //装载数组信息
                //默认选中选项
                dialog_position,
                //为列表添加监听事件
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (dialog_position != 0) {
                                    rankBeans.clear();
                                    type = "D";
                                    note.setText(typeArray[0]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;
                            case 1:
                                if (dialog_position != 1) {
                                    rankBeans.clear();
                                    type = "W";
                                    note.setText(typeArray[1]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;
                            case 2:
                                if (dialog_position != 2) {
                                    rankBeans.clear();
                                    type = "M";
                                    note.setText(typeArray[2]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;


                        }

                        dialog.cancel();  //用户选择后，关闭对话框
                    }
                })
                .create()
                .show();


    }


    // 2.1 定义用来与外部activity交互，获取到宿主activity
    private RankFragmentInteraction listterner;


    // 1 定义了所有activity必须实现的接口方法
    public interface RankFragmentInteraction {
        void process(String str, String str2, String str3);
    }

    // 当FRagmen被加载到activity的时候会被回调
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof RankFragmentInteraction) {
            listterner = (RankFragmentInteraction) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    private Handler mHanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    if (NetStatusUtil.isNetworkAvailable(mContext)) {

                        if (rankBeans.size() == 0) {
                            start = "0";
                        } else {
                            start = String.valueOf(adapter.getCount());
                        }
                        getInfoRead();
                    } else {


                    }

                    break;
                case 1:
                    if (!waitDialog.isShowing())
                        waitDialog.show();
                    break;
                case 2:
                    if (waitDialog.isShowing())
                        waitDialog.dismiss();
                    break;
                case 3:
                    String firstChar;
                    if (champion.getName() == null || "".equals(champion.getName())) {
                        firstChar = getFirstChar(champion.getUid());
                    } else {
                        firstChar = getFirstChar(champion.getName());
                    }
                    if (champion.getImgSrc().equals("http://static1." + com.iyuba.core.util.Constant.IYBHttpHead + "/uc_server/images/noavatar_middle.jpg")) {
                        userImage.setVisibility(View.INVISIBLE);
                        userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            userImageText.setBackgroundResource(R.drawable.rank_blue);
                            userImageText.setText(firstChar);
                            myUsername.setText(myName);
                            if (champion.getName() == null || "".equals(champion.getName())) {
                                userName.setText(champion.getUid());
                            } else {
                                userName.setText(champion.getName());
                            }
                            setInfo();
                            GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
                                    R.drawable.defaultavatar);
                            mHasLoadedOnce = true;
                        } else {

                            userImageText.setBackgroundResource(R.drawable.rank_green);
                            userImageText.setText(firstChar);
                            myUsername.setText(myName);
                            if (champion.getName() == null || "".equals(champion.getName())) {
                                userName.setText(champion.getUid());
                            } else
                                userName.setText(champion.getName());
                            setInfo();

                            GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
                                    R.drawable.defaultavatar);
                            mHasLoadedOnce = true;
                        }
                    } else {
                        userImageText.setVisibility(View.INVISIBLE);
                        userImage.setVisibility(View.VISIBLE);
                        GitHubImageLoader.Instace(mContext).setRawPic(champion.getImgSrc(), userImage,
                                R.drawable.noavatar_small);
                        if (champion.getName() == null || "".equals(champion.getName())) {
                            userName.setText(champion.getUid());
                        } else
                            userName.setText(champion.getName());
                        myUsername.setText(myName);
                        setInfo();
                        GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
                                R.drawable.defaultavatar);

                        mHasLoadedOnce = true;
                    }
                    break;


                case 4:
                    listFooter.setVisibility(View.GONE);
                    break;

                case 5:
                    CustomToast.showToast(mContext, "与服务器连接异常，请稍后再试");
                    break;

                case 6:
                    listFooter.setVisibility(View.VISIBLE);
                    break;
                case 7:

                    if (adapter == null) {
                        adapter = new RankFragmentListAdapter(mContext, rankBeans, rankType);
                        rankListView.setAdapter(adapter);
                    } else if (champion.getRanking().equals("1")) {
                        adapter.resetList(rankBeans);
                        rankListView.setSelection(0);
                    } else {

                        adapter.addList(rankBeans);
                    }
                    break;

                case 8:
                    CustomToast.showToast(getActivity(), "暂无更多数据");
                    break;

            }
        }
    };

    private void getInfoRead() {
        switch (rankType) {

            case "阅读":
                mHanlder.sendEmptyMessage(1);
                ExeProtocol.exe(new GetRankReadInfoRequest(uid, type, start, total), new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        mHanlder.sendEmptyMessage(2);
                        Log.e("RankActivity", "rankrank");
                        GetRankReadInfoResponse response = (GetRankReadInfoResponse) bhr;
                        myWpm = response.myWpm;
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        myCnt = response.myCnt;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;

                        if (myName == null || "".equals(myName))
                            myName = uid;
                        myWords = response.myWords;
                        rankBeans = response.rankBeans;
                        champion = rankBeans.get(0);

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        String r_frag_text = "我在" + Constant.APPName + " 阅读排行榜排名：" + myRanking;
                        String r_frag_siteUrl = "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID)
                                + "&topic=concept&rankingType=reading";
                        listterner.process(rankType, r_frag_text, r_frag_siteUrl);


                    }

                    @Override
                    public void error() {
//                        handler.sendEmptyMessage(4);
//                        handler.sendEmptyMessage(5);
                    }
                });
                break;
            case "听力":
                mHanlder.sendEmptyMessage(1);
                String mode = "listening";
                ExeProtocol.exe(new GetRankListenInfoRequest(uid, type, start, total, mode), new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetRankListenInfoResponse response = (GetRankListenInfoResponse) bhr;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName))
                            myName = uid;

                        rankBeans = response.rankBeans;
                        champion = rankBeans.get(0);

                        myTotalTime = response.myTotalTime;
                        myTotalWord = response.myTotalWord;
                        myTotalEssay = response.myTotalEssay;

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }


                        String l_frag_text = "我在" + Constant.APPName + " 听力排行榜排名：" + myRanking;
                        String l_frag_siteUrl = "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID)
                                + "&topic=concept&rankingType=listening";
                        listterner.process(rankType, l_frag_text, l_frag_siteUrl);
                    }

                    @Override
                    public void error() {
                        //请求失败
                    }
                });
                break;
            case "口语":
                mHanlder.sendEmptyMessage(1);
                String topic = "toeic", topicid = "0", shuoshuotype = "4";//familyalbum
                ExeProtocol.exe(new GetRankSpeakInfoRequest(uid, type, start, total, topic, topicid, shuoshuotype), new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetRankSpeakInfoResponse response = (GetRankSpeakInfoResponse) bhr;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName))
                            myName = uid;

                        rankBeans = response.rankBeans;
                        if (rankBeans.size() == 0 || rankBeans == null) {
                            mHanlder.sendEmptyMessage(8);
                        }
                        champion = rankBeans.get(0);


                        myScores = response.myScores;//总分
                        myCount = response.myCount;//总文章数

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        String k_frag_text = "我在" + Constant.APPName + " 口语排行榜排名：" + myRanking;
                        String k_frag_siteUrl = "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID)
                                + "&topic=concept&rankingType=speaking";
                        listterner.process(rankType, k_frag_text, k_frag_siteUrl);
                    }

                    @Override
                    public void error() {
                        //请求失败
                    }
                });
                break;
            case "学习":

                mHanlder.sendEmptyMessage(1);
                String modes = "all";
                ExeProtocol.exe(new GetRankStudyInfoRequest(uid, type, start, total, modes), new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetRankStudynfoResponse response = (GetRankStudynfoResponse) bhr;
                        Timber.e("排名返回显示" + response);
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName))
                            myName = uid;

                        rankBeans = response.rankBeans;
                        champion = rankBeans.get(0);

                        myTotalTime = response.myTotalTime;
                        myTotalWord = response.myTotalWord;
                        myTotalEssay = response.myTotalEssay;

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        String x_frag_text = "我在" + Constant.APPName + " 学习排行榜排名：" + myRanking;
                        String x_frag_siteUrl = "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID)
                                + "&topic=concept&rankingType=studying";
                        listterner.process(rankType, x_frag_text, x_frag_siteUrl);
                    }

                    @Override
                    public void error() {
                        //请求失败
                    }
                });
                break;
            case "测试":

                mHanlder.sendEmptyMessage(1);
                ExeProtocol.exe(new GetRankTestInfoRequest(uid, type, start, total), new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //处理数据
                        GetRankTestInfoResponse response = (GetRankTestInfoResponse) bhr;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName))
                            myName = uid;

                        rankBeans = response.rankBeans;
                        Timber.e("接口返回记录"+rankBeans+"接口返回记录显示完成！");
                        champion = rankBeans.get(0);


                        myTotalRight = response.myTotalRight;//正确数
                        myTotalTest = response.myTotalTest;  //总题数

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        frag_text = "我在" + Constant.APPName + " 测试排行榜排名：" + myRanking;
                        frag_siteUrl = "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID)
                                + "&topic=concept&rankingType=testing";
                        listterner.process(rankType, frag_text, frag_siteUrl);
                    }

                    @Override
                    public void error() {
                        //请求失败
                    }
                });
                break;
        }

    }


    private void setInfo() {

        switch (rankType) {
            case "阅读":
                userInfo.setText("单词数: " + myWords + "，文章数: " + myCnt + "，wpm: " + myWpm + "，排名: " + myRanking);
                break;
            case "听力":
                userInfo.setText(myTotalTime + "分钟，文章数: " + myTotalEssay + "，单词数: " + myTotalWord + "，排名: " + myRanking);
                break;
            case "口语":
                double scores_avg = 0;
                if (myCount.equals("0")) {
                    scores_avg = 0;
                } else {

                    scores_avg = Double.valueOf(myScores) / Double.valueOf(myCount);
                }

                DecimalFormat df = new DecimalFormat("0.00");
                userInfo.setText("句子数: " + myCount + "，总分: " + myScores + "，平均分: " + df.format(scores_avg) + "，排名: " + myRanking);
                break;
            case "学习":

                double hour = Double.valueOf(myTotalTime) / 3600;

                DecimalFormat df1 = new DecimalFormat("0.00");

                userInfo.setText(df1.format(hour) + "小时，文章数: " + myTotalEssay + "，单词数: " + myTotalWord + "，排名: " + myRanking);
                break;
            case "测试":
                double min;
                if (Double.valueOf(myTotalTest) == 0) {
                    userInfo.setText("总题数: " + myTotalTest + "，正确数: " + myTotalRight + "，正确率: " + "0，排名: " + myRanking);
                } else {

                    min = Double.valueOf(myTotalRight) / Double.valueOf(myTotalTest);

                    DecimalFormat df2 = new DecimalFormat("0.00");

                    userInfo.setText("总题数:" + myTotalTest + "，正确数:" + myTotalRight + "，正确率:" + df2.format(min) + "，排名:" + myRanking);
                }
                break;

        }

    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }

}
