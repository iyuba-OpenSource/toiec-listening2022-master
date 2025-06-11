/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.discover.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.discover.activity.AppGround;
import com.iyuba.core.discover.activity.FriendCircFreshListActivity;
import com.iyuba.core.discover.activity.Saying;
import com.iyuba.core.discover.activity.SearchWordActivity;
import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
import com.iyuba.core.teacher.activity.FindTeacherActivity;
import com.iyuba.core.teacher.activity.TeacherBaseInfo;
import com.iyuba.core.util.BrandUtil;
import com.iyuba.headnewslib.HeadlineActivity;
import com.iyuba.core.R;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class DiscoverFragment extends Fragment {
    private Context mContext;
    private View headline, news, exam, mob, all, searchWord, vibrate,
            collectWord, saying, back, discover_search_teacher, communicate,
            discover_search_certeacher, discover_search_circlefriend,
            latestAc, exGiftAc;
    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

//		CustomToast.showToast(mContext, "DiscoverFragment---------->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.layout_discover, container, false);
        initWidget();
        return root;
    }

    private void initWidget() {
        // TODO Auto-generated method stub
        back = root.findViewById(R.id.button_back);
        back.setVisibility(View.GONE);
        headline = root.findViewById(R.id.headline);
        headline.setOnClickListener(ocl);
        news = root.findViewById(R.id.news);
        news.setOnClickListener(ocl);
        exam = root.findViewById(R.id.exam);
        exam.setOnClickListener(ocl);
        mob = root.findViewById(R.id.mob);
        mob.setOnClickListener(ocl);
        if (Constant.APPID.equals("238")) {
            mob.setVisibility(View.GONE);
        }
        all = root.findViewById(R.id.all);
        all.setOnClickListener(ocl);
        searchWord = root.findViewById(R.id.search_word);
        searchWord.setOnClickListener(ocl);
        saying = root.findViewById(R.id.saying);
        saying.setOnClickListener(ocl);
        collectWord = root.findViewById(R.id.collect_word);
        collectWord.setOnClickListener(ocl);
//		findFriend = root.findViewById(R.id.discover_search_friend);
        discover_search_certeacher = root
                .findViewById(R.id.discover_search_certeacher);
        discover_search_circlefriend = root
                .findViewById(R.id.discover_search_circlefriend);
        discover_search_teacher = root
                .findViewById(R.id.discover_search_teacher);
        discover_search_circlefriend.setOnClickListener(ocl);
        discover_search_certeacher.setOnClickListener(ocl);
        discover_search_teacher.setOnClickListener(ocl);
//		findFriend.setOnClickListener(ocl);
        vibrate = root.findViewById(R.id.discover_vibrate);
        vibrate.setOnClickListener(ocl);

        latestAc = root.findViewById(R.id.discover_latest_activity);
        latestAc.setOnClickListener(ocl);
        exGiftAc = root.findViewById(R.id.discover_exchange_gift);
        exGiftAc.setOnClickListener(ocl);
        communicate = root.findViewById(R.id.communicate);
        exGiftAc.setOnClickListener(ocl);

        if (AccountManagerLib.Instace(mContext).isteacher.equals("1")) {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        } else {
            discover_search_certeacher.setVisibility(View.VISIBLE);

        }
        checkBrand();

    }

    private void checkBrand() {
        if (BrandUtil.getBrandName().equals("huawei")) {
            communicate.setVisibility(View.GONE);
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
                intent.putExtra("title", R.string.discover_news);
                startActivity(intent);
            } else if (id == R.id.exam) {
                intent = new Intent(mContext, AppGround.class);
                intent.putExtra("title", R.string.discover_exam);
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
                //intent = new Intent();
                //intent.setClass(mContext, WordCollectionNew.class);//WordCollectionNew
                //startActivity(intent);
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

			} */ else if (id == R.id.discover_search_teacher) {
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

                    AccountManagerLib.Instace(mContext).startLogin();
                }
            } else if (id == R.id.discover_exchange_gift) {
//				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                intent = new Intent();
                intent.setClass(mContext, Web.class);
                intent.putExtra("url", "http://m." + com.iyuba.core.util.Constant.IYBHttpHead + "/mall/index.jsp?"
                        + "&uid=" + AccountManagerLib.Instace(mContext).userId
                        + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManagerLib.Instace(mContext).userId + "camstory")
                        + "&username=" + AccountManagerLib.Instace(mContext).userName
                        + "&platform=android&appid="
                        + Constant.APPID);
                intent.putExtra("title",
                        mContext.getString(R.string.discover_iyubaac));
                startActivity(intent);
//				} else {
//					intent = new Intent();
//					intent.setClass(mContext, Login.class);
//					startActivity(intent);
//				}
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
