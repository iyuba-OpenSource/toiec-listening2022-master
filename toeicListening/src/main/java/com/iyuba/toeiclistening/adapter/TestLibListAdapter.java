package com.iyuba.toeiclistening.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.activity.TestActivity;
import com.iyuba.toeiclistening.entity.DownTest;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.listener.OnDownloadStateListener;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.util.FilesDownProcessBar;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;

import java.util.ArrayList;


public class TestLibListAdapter extends BaseAdapter {

    private FilesDownProcessBar fDownProcessBar;
    private boolean pauseLoading = false;// 标识当前下载是否暂停
    private boolean isLoading = false;// 标识当前正在下载
    private ViewHolder curViewHolder = null;//保存当前正在下载的控件
    private ViewHolder viewHolder;
    private View curView = null;
    private int curPosition = -1;

    private Context mContext;
    private ArrayList<PackInfo> mList; // listview绑定的数据
    private ArrayList<DownTest> downTests;
    private TEDBHelper teHelper;
    private ZDBHelper zHelper;
    private LayoutInflater mInflater;

    private final int maxProgress = 100;     //获取对应的进度

    public TestLibListAdapter(Context context, ArrayList<PackInfo> list, ArrayList<TitleInfo> titleList) {
        mContext = context;
        mList = list;
        //mTitleList = titleList;
        init();
    }

    public void resetData(ArrayList<PackInfo> list) {
        mList.clear();
        mList.addAll(list);
    }

    public void init() {
        teHelper = new TEDBHelper(mContext);
        zHelper = new ZDBHelper(mContext);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //arg0:int position
        //arg1:View convertView
        //arg2:ViewGroup parent

        final PackInfo curPackInfo;
        final int tempPosition;            // 标记当前操作的控件的id
        final ViewHolder tempHolder;
        final View tempView;

        //两个判断条件怎么判断的？
        //如果position是当前正在下载的Item的位置，且将要消失的Item不为空，则表示当前正在下载的Item将要滑出
        if (position == curPosition && curView != null) {
            convertView = curView;
        }
        //convertView==null：表示ListView刚被创建时；
        //convertView == curView && position != curPosition：表示正在下载的Item将要消失，新出现的Item要重新加载，不能带有下载的动画
        if (convertView == null || (convertView == curView && position != curPosition)) {
            convertView = mInflater.inflate(R.layout.testlib_in, null);        //加载一个Item项
            viewHolder = new ViewHolder();
            findView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //保存当前操作的变量
        curPackInfo = mList.get(position);
        tempPosition = position;
        tempHolder = viewHolder;
        tempView = convertView;

        //当ListViews刷新的时候同时刷新，curViewHolder

        setView(curPackInfo);

        OnClickListener layoutClickListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 根据数据判断试题下载状态，等于3 已经下载  downloadStatus
                if (curPackInfo.downloadStatus == 3) {
                    ZDBHelper helper = new ZDBHelper(mContext);
                    ArrayList<TitleInfo> titleInfoList = helper.getTitleInfos(curPackInfo.PackName, curPackInfo.TestType);
                    DataManager.Instance().titleInfoList = titleInfoList;

                    //进入下一个activity
                    Intent intent = new Intent();
                    intent.setClass(mContext, TestActivity.class);
                    intent.putExtra("packName", curPackInfo.PackName);
                    mContext.startActivity(intent);

                } else {
                    CustomToast.showToast(mContext, "请下载后查看！");
                }
            }
        };

        viewHolder.testsLayout.setOnClickListener(layoutClickListener);//整体点击事件
        viewHolder.download.setOnClickListener(new OnClickListener() { //监听带有下载图标（即未被下载的项）项被点击时的事件
            @Override
            public void onClick(View v) {
                LogUtil.e("下载:下载按钮被点击");
                if (getPermissions()) {//有权限才下载

                    if (curViewHolder == null)// 当前没有点击任何标签(没有正在下载的项)
                    {
                        curViewHolder = tempHolder;
                        curView = tempView;
                        curPosition = tempPosition;
                        LogUtil.e("下载:以前没有下载，现在开始下载");
                    }
                    if (v != curViewHolder.download)//点击的id与当前操作的id不一致（当前正在下载的项和当前点击的下载项不一致）
                    {
                        LogUtil.e("下载:状态isLoading:" + isLoading);
                        //如果当前Item正在下载，用户点击了别的Item
                        if (isLoading) {
                            Toast.makeText(mContext, "下载:当前下载任务还未结束，请稍等再试", Toast.LENGTH_SHORT).show();
                        }
                        //如果正在下载的Item已下载完成，则开始下载新的Item
                        else {
                            curViewHolder = tempHolder;
                            curView = tempView;
                            curPosition = tempPosition;

                            //开始新的下载  包含开始下载动画
//                            downloadAudios(curViewHolder.downloadProgressBar, tempPosition, curPackInfo);
                            curPackInfo.downloadStatus = 3;
                            LogUtil.e("下载:正在下载的Item已下载完成，开始下载新的Item");

                        }
                    } else {//点击Id与当前操作id一致
                        //如果正在下载则暂停
                        if (isLoading) {
                            //去掉暂停功能太卡了，受不了
                            handler.sendEmptyMessage(6);
                            pauseLoading = true;
                            isLoading = false;
                            LogUtil.d("下载:Download state当前下载处于暂停状态");
                            curPackInfo.downloadStatus = 2;
                            //notifyDataSetChanged();//必须进行刷新
                            //updatedDataOnError(progress, curPosition);

                            //刷新view
                            Message message = handler.obtainMessage(2);
                            handler.sendMessage(message);
                            //((ImageView) v).setImageResource(R.drawable.waiting_btn);
                            //viewHolder.download.setImageResource(R.drawable.waiting_btn);
                        } else {//进行下载   （第一次下载时执行的地方）
                            showDialog(tempPosition, curPackInfo);
                            curPackInfo.downloadStatus = 1;
                            LogUtil.e("下载:初始下载");
                            //notifyDataSetChanged();====
                        }
                    }
                }
            }
        });//下载点击事件
        return convertView;
    }

    private void downloadAudios(final int curPosition, final PackInfo curPackInfo) {
        // arg1传递当前操作的进度条id，arg2传递的当前操作的下载button的id
        if (CheckNetWork.isNetworkAvailable(mContext)) { // 判断有网络
            handler.sendEmptyMessage(5);// toast 开始下载
            fDownProcessBar = new FilesDownProcessBar(mContext, curPackInfo,
                    new OnDownloadStateListener() {
                        @Override
                        public void onFinishedListener(int progress) {
                            LogUtil.e("--OnDownloadStateListener-", "onFinishedListener");
                            updatedData(progress, curPosition);
                            Message message = handler.obtainMessage(1);////隐藏topView
                            curPackInfo.downloadStatus = 3;
                            handler.sendMessage(message);//隐藏topView
                            pauseLoading = true;
                            isLoading = false;
                        }

                        @Override
                        public boolean isPausedListener() {
                            return pauseLoading;
                        }

                        @Override
                        public void onErrorListener(String errorDesc, int progress) {

                            LogUtil.e("下载:状态响应1 onErrorListener.progress:" + progress);
                            LogUtil.e("下载:错误，" + errorDesc);
                            updatedDataOnError(progress, curPosition);
                            // Log.e("sss", "update over");
                            pauseLoading = true;
                            isLoading = false;
                            ////刷新view
                            Message message = handler.obtainMessage(2);
                            handler.sendMessage(message);
                            // 显示错误信息
                            Message msg = handler.obtainMessage(3, errorDesc);
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onPausedListener(int progress) {
                            LogUtil.e("下载:状态响应2 onPausedListener.progress:" + progress);
                            // 暂停下载
                            updatedDataOnError(progress, curPosition);
                            pauseLoading = true;
                            isLoading = false;
                            //刷新view
                            Message message = handler.obtainMessage(2);
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onStartListener(int progress) {//开始下载和继续下载
                            LogUtil.e("下载:状态响应3 onStartListener.progress:" + progress);
                            // 更新下载状态文字
                            pauseLoading = false;
                            isLoading = true;

                            //更新UI
                            //updatedData(progress, curPosition);
                            //Message message = handler.obtainMessage(4);
                            //handler.sendMessage(message);
                        }

                        @Override
                        public ArrayList<DownTest> onPreparedListener() {
                            LogUtil.e("下载:状态响应4 onPreparedListener");
                            pauseLoading = false;
                            isLoading = true;

                            String packName = mList.get(curPosition).PackName;
                            int testType = mList.get(curPosition).TestType;
                            int packNum = zHelper.getDownPackNum(packName);
                            downTests = teHelper.getDownTests(packName, packNum, testType);

//                for(int i = 0; i < downTests.size();i ++){
//                	Log.e("下载前的准备======》", downTests.get(i).packName + "id: " + downTests.get(i).packNum);
//                }

                            LogUtil.e("DownTest 的大小为：", downTests.size() + "");

                            return downTests;
                        }
                    });

            //handler.sendEmptyMessage(5);// toast 开始下载
            //notifyDataSetChanged();=====
            statDownLoadAnimation();//开始下载动画
        }
        //没有网络连接
        else {
            Toast.makeText(mContext, mContext.getString(R.string.net_disconected), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getPermissions() {
        //notifyDataSetChanged();
        //使用前必须开启-存储权限--
        if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                CustomToast.showToast(mContext, "存储权限开通才可以正常下载，请到系统设置中开启", 3000);
                return false;
            }
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://隐藏TopLayout
                    viewHolder.ivTips.setImageResource(R.drawable.ic_notes_main_list);
                    viewHolder.testsLayout.setBackgroundResource(R.drawable.shape_list_10);
                    viewHolder.download.setImageResource(R.drawable.ic_download_ok);
                    viewHolder.download.setEnabled(false);
                    viewHolder.downloadProgressBar.setVisibility(View.GONE);
                    //stopDownLoadAnimation();
                    break;
                case 2://暂停
                    stopDownLoadAnimation();//暂停
                    break;

                case 3://报告错误信息
                    Toast.makeText(mContext, R.string.down_data_error, Toast.LENGTH_SHORT).show();
                    LogUtil.e("下载出现错误！报告错误信息");

                    stopDownLoadAnimation();//下载出现错误
                    break;
                case 4://开始下载的动态动作
                    //notifyDataSetChanged();===
                    break;
                case 5:
                    Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(mContext, "暂停下载", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    //更新数据包括DataManager，mList中的，和数据库中的
    public void updatedData(int progress, int curPosition) {

        float transProgress = (float) progress / maxProgress;
        DataManager.Instance().packInfoList.get(curPosition).Progress = transProgress;
        DataManager.Instance().packInfoList.get(curPosition).IsDownload = true;

        mList.get(curPosition).Progress = transProgress;
        mList.get(curPosition).IsDownload = true;
        //更新数据库
        String packName = mList.get(curPosition).PackName;
        zHelper.setProgress(packName, transProgress);

        notifyDataSetChanged();
    }

    //更新数据包括DataManager，mList中的，和数据库中的
    public void updatedDataOnError(int progress, int curPosition) {

        float transProgress = (float) progress / maxProgress;
        DataManager.Instance().packInfoList.get(curPosition).Progress = transProgress;
        DataManager.Instance().packInfoList.get(curPosition).IsDownload = false;//Error

        mList.get(curPosition).Progress = transProgress;
        mList.get(curPosition).IsDownload = false;//Error
        //更新数据库
        String packName = mList.get(curPosition).PackName;
        zHelper.setProgress(packName, 0);
    }

    class ViewHolder {
        public TextView testName = null;
        public TextView testCond = null;
        public ImageView download = null;//下载图片
        private ImageView ivTips = null;//标识图片
        public RelativeLayout testsLayout = null;
        public ProgressBar downloadProgressBar = null;//下载进度
        public RelativeLayout rlDownLoad;
    }

    /**
     * 开始下载的动态效果
     */
    public void statDownLoadAnimation() {
        LogUtil.e("animation开始");
        try {
            ((AnimationDrawable) curViewHolder.download.getDrawable()).start();
        } catch (Exception e) {
            LogUtil.e("开始动画异常！" + e);
            curViewHolder.download.setImageResource(R.drawable.download_animation);
            ((AnimationDrawable) curViewHolder.download.getDrawable()).start();
        }
    }

    /**
     * 结束下载的动态效果
     */
    public void stopDownLoadAnimation() {
        LogUtil.e("animation关闭");
        try {
            AnimationDrawable animation = (AnimationDrawable) curViewHolder.download.getDrawable();
            animation.stop();
            animation.selectDrawable(0);
        } catch (Exception e) {
            LogUtil.e("关闭动画异常！" + e);
            curViewHolder.download.setImageResource(R.drawable.ic_download_stop);
        }
    }

    public void findView(View convertView) {
        viewHolder.testName = (TextView) convertView
                .findViewById(R.id.testlib_in_test_name);
        viewHolder.testCond = (TextView) convertView
                .findViewById(R.id.testlib_in_test_evaluate);
        viewHolder.download = (ImageView) convertView
                .findViewById(R.id.testlib_in_download_button);
        //viewHolder.rlDownLoad = convertView.findViewById(R.id.rl_iv_download);
//        viewHolder.downloadProgressBar = (ProgressBar) convertView
//                .findViewById(R.id.testlib_in_download_progress);

        viewHolder.testsLayout = (RelativeLayout) convertView
                .findViewById(R.id.testlib_in_relativelayout_test);
        viewHolder.ivTips = (ImageView) convertView
                .findViewById(R.id.iv_tips);//!!
    }

    //设置每一个套题Item上要显示的信息，以及设置下载的Layout是否可见
    public void setView(PackInfo curPackInfo) {

        switch (curPackInfo.downloadStatus) {//下载图标变化
            case 0:
                //stopDownLoadAnimation();//未下载
                break;
            case 1:
                //statDownLoadAnimation();//下载中
                LogUtil.e("下载: 下载动画开启");
                break;
            case 2:
                stopDownLoadAnimation();//下载暂停
                LogUtil.e("下载: 暂停 下载动画关闭");
                viewHolder.download.setImageResource(R.drawable.ic_download_stop);
                break;
            case 3:
                //stopDownLoadAnimation();//下载完成
                LogUtil.e("下载: 下载完成 下载动画关闭");
                //viewHolder.download.setVisibility();
                break;
            default:
        }
        // testName由PackName和TitleSum组成
        String tnString = curPackInfo.PackName + "(" + curPackInfo.TitleSum + "篇听力)";
        viewHolder.testName.setText(tnString);

        String rightNum = zHelper.getRightNum(curPackInfo.PackName, curPackInfo.TestType) + "";


        // testCond由正确比列
        String tcString = "正确比例：" + rightNum + "/" + curPackInfo.QuestionSum + "题";
        viewHolder.testCond.setText("" + tcString + "");
        // 为testLayout添加监听器

        //带有下载按钮的Layout先设为可见的（同时设置其透明度）
        //viewHolder.download.setVisibility(View.VISIBLE);
        viewHolder.download.setImageResource(R.drawable.download_animation);
        viewHolder.download.setEnabled(true);
        viewHolder.downloadProgressBar.setVisibility(View.VISIBLE);
        //viewHolder.ivTips.setImageResource(R.drawable.ic_lock_main_list);
        //viewHolder.testsLayout.setBackgroundResource(R.drawable.shape_list_10);

        //Log.e(curPackInfo.IsDownload+" "+curPackInfo.PackName, curPackInfo.Progress+" ");
        // 判断此题是否可用，语音IsFree=true是打包在程序之中，语音isDowmload=true是下载了的
        // 可用，则上面的layout为不可见

        if (curPackInfo.IsFree || (curPackInfo.Progress == 1.0) || curPackInfo.IsDownload) {
            viewHolder.ivTips.setImageResource(R.drawable.ic_notes_main_list);
            viewHolder.testsLayout.setBackgroundResource(R.drawable.shape_list_10);
            //viewHolder.download.setVisibility(View.GONE);
            viewHolder.download.setImageResource(R.drawable.ic_download_ok);
            viewHolder.download.setEnabled(false);
            viewHolder.downloadProgressBar.setVisibility(View.GONE);
            curPackInfo.downloadStatus = 3;//下载完成！
        } else {
            viewHolder.ivTips.setImageResource(R.drawable.ic_lock_main_list);
            viewHolder.testsLayout.setBackgroundResource(R.drawable.shape_list_gray);
            //viewHolder.download.setVisibility(View.VISIBLE);
            viewHolder.download.setImageResource(R.drawable.download_animation);
            viewHolder.download.setEnabled(true);
            viewHolder.downloadProgressBar.setVisibility(View.VISIBLE);
        }
    }

    //下载前提示
    public void showDialog(final int tempPosition, final PackInfo curPackInfo) {
        if (CheckNetWork.checkWifiStatus(mContext)) {
//            downloadAudios(curViewHolder.downloadProgressBar, tempPosition, curPackInfo);
        } else {
            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示").
                    setIcon(android.R.drawable.ic_dialog_alert).setMessage
                            ("您所需要的试题资源建议使用wifi下载，是否继续下载?").
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            statDownLoadAnimation();//流量继续下载
//                            downloadAudios(curViewHolder.downloadProgressBar, tempPosition, curPackInfo);
                        }
                    }).setNeutralButton("取消", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }

    }


}
