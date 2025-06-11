package com.iyuba.core.me.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.me.adapter.RankFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;

public class RankActivity extends FragmentActivity implements RankFragment.RankFragmentInteraction {


    private TextView rankRead;
    private TextView rankListen;
    private TextView rankSpeak;
    private TextView rankStudy;
    private TextView rankTest;


    private Button buttonBack;
    private Button share;

    private ViewPager viewPager;

    private List<Fragment> fragmentList = new ArrayList<>();
    private RankFragmentAdapter adapter;
    private String readText, readUrl, listenUrl, listenText, studyText, studyUrl, testText, testUrl, speakText, speakurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        init();
        setViewClick();
        //设置阅读按钮颜色
        resetBtnColor(R.id.rank_listen);
        initViewPager();
    }

    private void init() {
        rankRead = findViewById(R.id.rank_read);
        rankRead.setVisibility(View.GONE);//阅读隐藏
        rankListen = findViewById(R.id.rank_listen);
        rankSpeak = findViewById(R.id.rank_speak);
        rankSpeak.setVisibility(View.GONE);//口语隐藏
        rankStudy = findViewById(R.id.rank_study);
        rankTest = findViewById(R.id.rank_test);
        buttonBack = findViewById(R.id.button_back);
        share = findViewById(R.id.share);
        viewPager = findViewById(R.id.view_pager);
    }

    /**
     * 重置按钮颜色
     */
    private void resetBtnColor(int viewId) {

        rankRead.setSelected(false);
        rankRead.setTextColor(getResources().getColor(R.color.green));

        rankListen.setSelected(false);
        rankListen.setTextColor(getResources().getColor(R.color.green));

        rankSpeak.setSelected(false);
        rankSpeak.setTextColor(getResources().getColor(R.color.green));

        rankStudy.setSelected(false);
        rankStudy.setTextColor(getResources().getColor(R.color.green));

        rankTest.setSelected(false);
        rankTest.setTextColor(getResources().getColor(R.color.green));


        if (viewId == R.id.rank_read) {
            rankRead.setSelected(true);
            rankRead.setTextColor(0xffffffff);
        } else if (viewId == R.id.rank_listen) {
            rankListen.setSelected(true);
            rankListen.setTextColor(0xffffffff);
        } else if (viewId == R.id.rank_speak) {
            rankSpeak.setSelected(true);
            rankSpeak.setTextColor(0xffffffff);
        } else if (viewId == R.id.rank_study) {
            rankStudy.setSelected(true);
            rankStudy.setTextColor(0xffffffff);
        } else if (viewId == R.id.rank_test) {
            rankTest.setSelected(true);
            rankTest.setTextColor(0xffffffff);
        }

    }

    private void setViewClick() {
        rankListen.setOnClickListener(ocl);
        rankRead.setOnClickListener(ocl);
        rankSpeak.setOnClickListener(ocl);
        rankStudy.setOnClickListener(ocl);

        rankTest.setOnClickListener(ocl);
        buttonBack.setOnClickListener(ocl);
        share.setOnClickListener(ocl);
    }

    private View.OnClickListener ocl = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            //点击事件
            int id = arg0.getId();

            if (id == R.id.rank_listen) {
                if (!rankListen.isSelected()) {
                    resetBtnColor(R.id.rank_listen);
                    viewPager.setCurrentItem(0);

                }
            } else if (id == R.id.rank_read) {
                if (!rankRead.isSelected()) {
                    resetBtnColor(R.id.rank_read);
                    viewPager.setCurrentItem(1);

                }
            } else if (id == R.id.rank_speak) {
                if (!rankSpeak.isSelected()) {
                    resetBtnColor(R.id.rank_speak);
                    viewPager.setCurrentItem(2);

                }
            } else if (id == R.id.rank_study) {
                if (!rankStudy.isSelected()) {
                    resetBtnColor(R.id.rank_study);
                    viewPager.setCurrentItem(1);

                }
            } else if (id == R.id.rank_test) {
                if (!rankTest.isSelected()) {
                    resetBtnColor(R.id.rank_test);
                    viewPager.setCurrentItem(2);

                }
            } else if (id == R.id.button_back) {
                finish();

            } else if (id == R.id.share) {
                gerShare();
            }
        }
    };

    private void gerShare() {

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);
        String text = "", siteUrl = "";

        String imageUrl = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android/images/ToeicListening/ToeicListening.png";
        //http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android/images/FamilyAlbum/FamilyAlbum.png
        if (rankRead.isSelected()) {
            text = readText;
            siteUrl = readUrl;
        }
        if (rankListen.isSelected()) {

            text = listenText;
            siteUrl = listenUrl;
        }
        if (rankStudy.isSelected()) {
            text = studyText;
            siteUrl = studyUrl;
        }
        if (rankTest.isSelected()) {

            text = testText;
            siteUrl = testUrl;
        }

        if (rankSpeak.isSelected()) {

            text = speakText;
            siteUrl = speakurl;
        }
        if (text == null) {
            LogUtil.e("字段为空", "===");
            return;
        }
                /*switch (type) {
                    case "D":
                        text = "今天我阅读了" + myWords + "个单词," + myCnt + "篇文章,WPM为" + myWpm + "word/min,战胜了" + myRanking + "个人";
                        siteUrl = "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID);
                        break;
                    case "W":
                        text = "这周我阅读了" + myWords + "个单词," + myCnt + "篇文章,WPM为" + myWpm + "word/min,战胜了" + myRanking + "个人";
                        siteUrl = "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID);
                        break;
                    case "M":
                        text = "这个月我阅读了" + myWords + "个单词," + myCnt + "篇文章,WPM为" + myWpm + "word/min,战胜了" + myRanking + "个人";
                        siteUrl = "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/i/getRanking.jsp?uid=" + uid + "&appId=" + Constant.APPID + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + Constant.APPID);
                        break;
                }*/

        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("托业听力英语排行榜");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用

        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", arg2.toString());

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (AccountManagerLib.Instace(RankActivity.this).userId != null) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();
                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 49;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                            || arg0.getName().equals("TencentWeibo")) {
                        msg.what = 19;
                    }
//                    handler.sendMessage(msg);
                } else {
//                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(RankActivity.this);

    }

    private void initViewPager() {

        fragmentList.add(RankFragment.newInstance("听力"));
        //fragmentList.add(RankFragment.newInstance("阅读"));
        //fragmentList.add(RankFragment.newInstance("口语"));
        fragmentList.add(RankFragment.newInstance("学习"));
        fragmentList.add(RankFragment.newInstance("测试"));
        FragmentManager fm = getSupportFragmentManager();

        adapter = new RankFragmentAdapter(fm, fragmentList);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {

                    case 0:
                        resetBtnColor(R.id.rank_listen);
                        break;
                    //case 1:
                        //resetBtnColor(R.id.rank_read);
                        //break;
                    //case 2:
                        //resetBtnColor(R.id.rank_speak);
                        //break;
                    case 1:
                        resetBtnColor(R.id.rank_study);
                        break;
                    case 2:
                        resetBtnColor(R.id.rank_test);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void process(String str, String text, String url) {


        switch (str) {

            case "阅读":

                readText = text;
                readUrl = url;
                break;
            case "听力":

                listenText = text;
                listenUrl = url;
                break;
            case "口语":
                speakText = text;
                speakurl = url;

                break;
            case "学习":
                studyText = text;
                studyUrl = url;
                break;
            case "测试":
                testText = text;
                testUrl = url;
                break;


        }
    }
}
