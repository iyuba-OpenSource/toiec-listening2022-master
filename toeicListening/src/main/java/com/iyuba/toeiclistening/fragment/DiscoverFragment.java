package com.iyuba.toeiclistening.fragment;


import static com.iyuba.configation.RuntimeManager.getApplication;
import static com.youdao.sdk.common.YoudaoSDK.getApplicationContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.CommunityActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.discover.activity.AppGround;
import com.iyuba.core.discover.activity.FriendCircFreshListActivity;
import com.iyuba.core.discover.activity.Saying;
import com.iyuba.core.discover.activity.SearchWordActivity;
import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.core.teacher.activity.FindTeacherActivity;
import com.iyuba.core.teacher.activity.TeacherBaseInfo;
import com.iyuba.core.util.AdBlocker;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.data.model.Headline;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headnewslib.HeadlineActivity;
import com.iyuba.http.LOGUtils;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.imooclib.ui.content.ContentActivity;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.module.headlinesearch.event.HeadlineSearchItemEvent;
import com.iyuba.module.headlinesearch.ui.MSearchActivity;
import com.iyuba.module.movies.IMovies;
import com.iyuba.module.movies.IMoviesManager;
import com.iyuba.module.movies.event.IMovieGoVipCenterEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.toeiclistening.activity.WordCollectionNew;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * create 2018-12-03
 * by 赵皓
 */
public class DiscoverFragment extends Fragment {

    private Context mContext;
    private View headline, news, exam, mob, all, searchWord, vibrate,
            collectWord, saying, back, discover_search_teacher,
            discover_search_certeacher, discover_search_circlefriend,
            latestAc, exGiftAc;
    private View ll_word;

    private RelativeLayout mWatchwatch, mSearchSearch, mStudybs, mQuesAns, mSearchWord;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public DiscoverFragment() {
        // Required empty public constructor
    }

    public static DiscoverFragment newInstance(String title) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        View view = inflater.inflate(R.layout.layout_discover, container, false);
        mContext = getActivity();
        EventBus.getDefault().register(this);
        initWidget(view);
        return view;
    }

    private void initWidget(View view) {
        mSearchSearch = view.findViewById(R.id.search_search);//搜一搜
        mSearchSearch.setOnClickListener(ocl);
        mWatchwatch = view.findViewById(R.id.watch_watch);//看一看
        mWatchwatch.setOnClickListener(ocl);
        mStudybs = view.findViewById(R.id.studybs);//学一学
        mStudybs.setOnClickListener(ocl);
        mQuesAns = view.findViewById(R.id.ans_ques);//问一问
        mQuesAns.setOnClickListener(ocl);
        mSearchWord = view.findViewById(R.id.search_word_one);//查一查
        mSearchWord.setOnClickListener(ocl);

        ll_word = view.findViewById(R.id.ll_word);//单词相关
        ll_word.setVisibility(View.VISIBLE);

        back = view.findViewById(R.id.button_back);
        back.setVisibility(View.GONE);
        headline = view.findViewById(R.id.headline);//头条已经被隐藏
        headline.setOnClickListener(ocl);
        news = view.findViewById(R.id.news);
        news.setOnClickListener(ocl);
        exam = view.findViewById(R.id.exam);
        exam.setOnClickListener(ocl);
        mob = view.findViewById(R.id.mob);
        mob.setOnClickListener(ocl);
        if (Constant.APPID.equals("238")) {
            mob.setVisibility(View.GONE);
        }
        TextView tv_more = (TextView) view.findViewById(R.id.tv_more);
//        tv_more.setVisibility(View.GONE);
        TextView tv_app = (TextView) view.findViewById(R.id.tv_app);
        tv_app.setText("应用广场");
        all = view.findViewById(R.id.all);
//        all.setVisibility(View.GONE);
        all.setOnClickListener(ocl);
        searchWord = view.findViewById(R.id.search_word);//被隐藏
        searchWord.setOnClickListener(ocl);
        saying = view.findViewById(R.id.saying);
        saying.setOnClickListener(ocl);
        collectWord = view.findViewById(R.id.collect_word);
        collectWord.setVisibility(View.VISIBLE);//单词本


        collectWord.setOnClickListener(ocl);
//        findFriend = view.findViewById(R.id.discover_search_friend);
        discover_search_certeacher = view.findViewById(R.id.discover_search_certeacher);
        discover_search_circlefriend = view.findViewById(R.id.discover_search_circlefriend);
        discover_search_teacher = view.findViewById(R.id.discover_search_teacher);

        discover_search_circlefriend.setOnClickListener(ocl);
        discover_search_certeacher.setOnClickListener(ocl);
        discover_search_teacher.setOnClickListener(ocl);
//        findFriend.setOnClickListener(ocl);
        vibrate = view.findViewById(R.id.discover_vibrate);
        vibrate.setOnClickListener(ocl);

        latestAc = view.findViewById(R.id.discover_latest_activity);
        //       latestAc.setVisibility(View.GONE);
        latestAc.setOnClickListener(ocl);
        exGiftAc = view.findViewById(R.id.discover_exchange_gift);
        exGiftAc.setOnClickListener(ocl);

        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        } else {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        }

    }

    private View.OnClickListener ocl = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            int id = v.getId();

            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                User user = new User();
                AccountManagerLib managerLib = AccountManagerLib.Instace(mContext);
                user.vipStatus = (managerLib.userInfo) == null ? "0" : managerLib.userInfo.vipStatus;
                int userId;
                if (managerLib.userId == null || managerLib.userId.isEmpty()) {
                    userId = 0;
                } else {
                    userId = Integer.parseInt(managerLib.userId);
                }
                user.uid = userId;
                user.name = managerLib.userName;

                if (AdBlocker.shouldBlockAd(getApplication())) {
                    user.vipStatus = "1";
                }

                IyuUserManager.getInstance().setCurrentUser(user);
            }

            if (id == R.id.watch_watch) {//看一看

                IMovies.init(getApplicationContext(), Constant.APPID, Constant.AppName);
                IMoviesManager.appId = Constant.APPID;
                startActivity(new Intent(mContext, com.iyuba.module.movies.ui.movie.MovieActivity.class));
            } else if (id == R.id.search_search) {//搜一搜
                if (AccountManagerLib.Instace(getApplicationContext())
                        .checkUserLogin()) {
                    ImoocManager.appId = Constant.APPID;

                } else {
                    ImoocManager.appId = Constant.APPID;
                }
                String[] types = new String[]{
                        HeadlineType.NEWS,
                        HeadlineType.SONG,
                        HeadlineType.VOA,
                        HeadlineType.BBC,
                        HeadlineType.TED,
                        HeadlineType.VIDEO,
                        HeadlineType.WORD,
                        HeadlineType.QUESTION,
                        HeadlineType.CLASS,
                };
                startActivity(MSearchActivity.buildIntent(getContext(), types));

            } else if (id == R.id.studybs) {//学一学

                ArrayList<Integer> typeIdFilter = new ArrayList<>();
                typeIdFilter.add(-2);//全部
                typeIdFilter.add(-1);//最新
                typeIdFilter.add(2);//四级
                typeIdFilter.add(3);//VOA
                typeIdFilter.add(4);//六级
                typeIdFilter.add(7);//托福
                typeIdFilter.add(8);//考研
                typeIdFilter.add(9);//BBC
                typeIdFilter.add(21);//新概念
                typeIdFilter.add(22);//走遍美国
                typeIdFilter.add(28);//学位
                typeIdFilter.add(52);//考研二
                typeIdFilter.add(61);//雅思
                typeIdFilter.add(91);//中职
                //typeIdFilter.add(1);//N1
                //typeIdFilter.add(5);//N2
                //typeIdFilter.add(6);//N3
//                ImoocManager.appId = Constant.APPID;
                startActivity(MobClassActivity.buildIntent(mContext, 3, true, typeIdFilter));//21为新概念 22走遍美国 这是猜的。。
            } else if (id == R.id.ans_ques) {//问一问
                intent = new Intent(mContext, CommunityActivity.class);
                startActivity(intent);
            } else if (id == R.id.search_word_one) {//查一查
                intent = new Intent(mContext, SearchWordActivity.class);
                startActivity(intent);
            } else if (id == R.id.headline) {
                intent = new Intent(mContext, HeadlineActivity.class);
                startActivity(intent);
            } else if (id == R.id.news) {
                intent = new Intent(mContext, AppGround.class);
                intent.putExtra("title", "听说系列应用");
                startActivity(intent);
            } else if (id == R.id.exam) {
                intent = new Intent(mContext, AppGround.class);
                intent.putExtra("title", "考试系列应用");
                startActivity(intent);
            } else if (id == R.id.mob) {
                intent = new Intent(mContext, SimpleMobClassList.class);
                intent.putExtra("title", R.string.discover_mobclass);
                startActivity(intent);
            } else if (id == R.id.all) {
                intent = new Intent();
                intent.setClass(mContext, Web.class);
                intent.putExtra("url", "http://app." + com.iyuba.core.util.Constant.IYBHttpHead + "/android");
                intent.putExtra("title",
                        mContext.getString(R.string.discover_appall));
                startActivity(intent);
            } else if (id == R.id.search_word) {
                intent = new Intent();
                intent.setClass(mContext, SearchWordActivity.class);
                startActivity(intent);
            } else if (id == R.id.saying) {
                intent = new Intent();
                intent.setClass(mContext, Saying.class);
                startActivity(intent);
            } else if (id == R.id.collect_word) {
                intent = new Intent();
                intent.setClass(mContext, WordCollectionNew.class);//WordCollectionNew
                startActivity(intent);
            } /*else if (id == R.id.discover_search_friend) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, SearchFriend.class);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(mContext, Login.class);
                    startActivity(intent);
                }
            }*/ else if (id == R.id.discover_search_teacher) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, com.iyuba.core.teacher.activity.FindTeacherActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(mContext, FindTeacherActivity.class);
                    startActivity(intent);
                }
            } else if (id == R.id.discover_search_certeacher) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                    intent = new Intent();
                    intent.setClass(mContext, TeacherBaseInfo.class);
                    startActivity(intent);
                } else {
                    AccountManagerLib.Instace(mContext).startLogin();
                }

            } else if (id == R.id.discover_search_circlefriend) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, FriendCircFreshListActivity.class);
                    startActivity(intent);
                } else {
                    AccountManagerLib.Instace(mContext).startLogin();
                }

            } else if (id == R.id.discover_latest_activity) {//礼物
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    if (AccountManagerLib.Instace(mContext).userId != null &&
                            !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                            !AccountManagerLib.Instace(mContext).userId.equals("0")
                            && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
                        intent = new Intent();
                        intent.setClass(mContext, Web.class);
                        intent.putExtra("url", "http://vip." + com.iyuba.core.util.Constant.IYBHttpHead + "/mycode.jsp?"
                                + "uid=" + AccountManagerLib.Instace(mContext).userId
                                + "&appid=" + Constant.APPID
                                + "&sign=" + MD5.getMD5ofStr(AccountManagerLib.Instace(mContext).userId
                                + "iyuba" + Constant.APPID + date
                                + "&username=" + TextAttr.encode(AccountManagerLib.Instace(mContext).userName)));
                        LogUtil.e("礼物请求地址：");
                        intent.putExtra("title",
                                mContext.getString(R.string.discover_iyubaac));
                        startActivity(intent);
                    } else {
                        showNormalDialog("临时账户无法查看礼物，请登录正式账户");
                    }

                } else {

                    AccountManagerLib.Instace(mContext).startLogin();
                }
            } else if (id == R.id.discover_exchange_gift) {//积分商城
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    if (AccountManagerLib.Instace(mContext).userId != null &&
                            !"".equals(AccountManagerLib.Instace(mContext).userId) &&
                            !AccountManagerLib.Instace(mContext).userId.equals("0")
                            && Integer.parseInt(AccountManagerLib.Instace(mContext).userId) < 50000000) {
                        intent = new Intent();
                        intent.setClass(mContext, Web.class);
                        intent.putExtra("url", "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/mall/index.jsp?"
                                + "&uid=" + AccountManagerLib.Instace(mContext).userId
                                + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManagerLib.Instace(mContext).userId + "camstory")
                                + "&username=" + AccountManagerLib.Instace(mContext).userName
                                + "&platform=android&appid="
                                + Constant.APPID
                                + "&username=" + TextAttr.encode(AccountManagerLib.Instace(mContext).userName));
                        intent.putExtra("title",
                                mContext.getString(R.string.discover_exgift));
                        startActivity(intent);
                    } else {
                        showNormalDialog("临时账户无法查看积分商城，请登录正式账户");
                    }
                } else {

                    AccountManagerLib.Instace(mContext).startLogin();
                }
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        } else {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 美剧-下载开通会员
     *
     * @param event
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMovieGoVipCenterEvent event) {//非会员提示
        LOGUtils.e("美剧-下载开通会员");//如果登录了，就去开通会员界面，否则就去登录
        if (AccountManagerLib.Instace(getActivity()).checkUserLogin()) {
            //startActivity(new Intent(mContext, VipCenter.class));//NewVipCenterActivity
            VipCenterGoldActivity.buildIntent(mContext, 0);//黄金VIP中心
        } else {

            AccountManagerLib.Instace(mContext).startLogin();
        }
    }


    /**
     * 搜一搜列表点击
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineSearchItemEvent event) {
        Headline headline = event.headline;
        switch (headline.type) {
            case "news":

                startActivity(TextContentActivity.getIntent2Me(getContext(), headline));

                break;
            case "voa":
            case "csvoa":
            case "bbc":

                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
                startActivity(VideoContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "bbcwordvideo":
            case "topvideos":
                startActivity(VideoContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "class": {
                if (headline.id == null || headline.id.isEmpty()) headline.id = "0";
                int packId = Integer.parseInt(headline.id);
                Intent intent = ContentActivity.buildIntent(getContext(), packId, "class.cet4");
                startActivity(intent);
                break;
            }
        }
    }

    private void showNormalDialog(String content) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        AccountManagerLib.Instace(mContext).startLogin();
                    }
                });
        normalDialog.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        normalDialog.show();
    }

}
