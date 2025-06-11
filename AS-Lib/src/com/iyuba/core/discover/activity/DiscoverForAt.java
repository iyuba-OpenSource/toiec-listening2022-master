/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.discover.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
import com.iyuba.core.teacher.activity.FindTeacherActivity;
import com.iyuba.core.teacher.activity.TeacherBaseInfo;
import com.iyuba.headnewslib.HeadlineActivity;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class DiscoverForAt extends Activity {
    private Context mContext;
    private View headline, news, exam, mob, all, searchWord, vibrate,
            collectWord, saying, back, discover_search_teacher,
            discover_search_certeacher, discover_search_circlefriend,
            latestAc, exGiftAc;
    private View ll_word;
    //	private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_discover);
        mContext = this;
        initWidget();
    }

    private void initWidget() {
        ll_word = findViewById(R.id.ll_word);
        ll_word.setVisibility(View.GONE);
        // TODO Auto-generated method stub
        back = findViewById(R.id.button_back);
        back.setVisibility(View.GONE);
        headline = findViewById(R.id.headline);
        headline.setOnClickListener(ocl);
        news = findViewById(R.id.news);
        news.setOnClickListener(ocl);
        exam = findViewById(R.id.exam);
        exam.setOnClickListener(ocl);
        mob = findViewById(R.id.mob);
        mob.setOnClickListener(ocl);
        if (Constant.APPID.equals("238")) {
            mob.setVisibility(View.GONE);
        }
        TextView tv_more = (TextView) findViewById(R.id.tv_more);
//        tv_more.setVisibility(View.GONE);
        TextView tv_app = (TextView) findViewById(R.id.tv_app);
        tv_app.setText("应用广场");
        all = findViewById(R.id.all);
//        all.setVisibility(View.GONE);
        all.setOnClickListener(ocl);
        searchWord = findViewById(R.id.search_word);
        searchWord.setOnClickListener(ocl);
        saying = findViewById(R.id.saying);
        saying.setOnClickListener(ocl);
        collectWord = findViewById(R.id.collect_word);
        collectWord.setVisibility(View.GONE);
        collectWord.setOnClickListener(ocl);
//        findFriend = findViewById(R.id.discover_search_friend);
        discover_search_certeacher = findViewById(R.id.discover_search_certeacher);
        discover_search_circlefriend = findViewById(R.id.discover_search_circlefriend);
        discover_search_teacher = findViewById(R.id.discover_search_teacher);
        discover_search_circlefriend.setOnClickListener(ocl);
        discover_search_certeacher.setOnClickListener(ocl);
        discover_search_teacher.setOnClickListener(ocl);
//        findFriend.setOnClickListener(ocl);
        vibrate = findViewById(R.id.discover_vibrate);
        vibrate.setOnClickListener(ocl);

        latestAc = findViewById(R.id.discover_latest_activity);
        latestAc.setVisibility(View.GONE);
//		latestAc.setOnClickListener(ocl);
        exGiftAc = findViewById(R.id.discover_exchange_gift);
        exGiftAc.setOnClickListener(ocl);

        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        } else {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        }

    }

    private OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent;
            int id = v.getId();
            if (id == R.id.headline) {
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
                intent.setClass(mContext, WordCollection.class);//WordCollection
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

            } */else if (id == R.id.discover_search_teacher) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, FindTeacherActivity.class);
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
                    intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }

            } else if (id == R.id.discover_search_circlefriend) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, FriendCircFreshListActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }

            } else if (id == R.id.discover_latest_activity) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, Web.class);
                    intent.putExtra("url", "http://www." + com.iyuba.core.util.Constant.IYBHttpHead + "/book/book.jsp?uid="
                            + AccountManagerLib.Instace(mContext).userId + "&platform=android&appid="
                            + Constant.APPID);
                    intent.putExtra("title",
                            mContext.getString(R.string.discover_iyubaac));
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            } else if (id == R.id.discover_exchange_gift) {
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    intent = new Intent();
                    intent.setClass(mContext, Web.class);
                    intent.putExtra("url", "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/mall/index.jsp?"
                            + "&uid=" + AccountManagerLib.Instace(mContext).userId
                            + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManagerLib.Instace(mContext).userId + "camstory")
                            + "&username=" + AccountManagerLib.Instace(mContext).userName
                            + "&platform=android&appid="
                            + Constant.APPID);
//					intent.putExtra("url", "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/mall/index.jsp?uid="
//							+AccountManagerLib.Instace(mContext).userId+"&platform=android&appid="
//							+Constant.APPID+"&userName="+AccountManagerLib.Instace(mContext).userName);
                    intent.putExtra("title",
                            mContext.getString(R.string.discover_exgift));
                    startActivity(intent);

                } else {
                    intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // MobclickAgent.onPause(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
        // MobclickAgent.onResume(mContext);
        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        } else {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        }
    }
}
