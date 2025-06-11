package com.iyuba.toeiclistening.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
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

/**
 * 首页试题列表
 * 2019.04.16 zh
 */

public class TestListRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<PackInfo> mList; // 列表数据
    private Context mContext;
    private TEDBHelper teHelper;//数据
    private ZDBHelper zHelper;//下载
    private final int maxProgress = 100;//最大进度
    private int image[];

    public TestListRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<PackInfo> list) {
        ArrayList<PackInfo> packInfo = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).TestType == 102) {
                packInfo.add(list.get(i));
                //list.remove(i);
            }
        }
        for (int i = 0; i < packInfo.size(); i++) {
            list.remove(list.size() - 1);
            list.add(i, packInfo.get(i));
        }

        mList = list;
        teHelper = new TEDBHelper(mContext);
        zHelper = new ZDBHelper(mContext);

        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.test_list_icon);

        image = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            image[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
    }

    public void resetData(ArrayList<PackInfo> list) {
        mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.testlib_in, viewGroup, false);
        return new TestViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TestViewHolder) {
            ((TestViewHolder) viewHolder).setData(i);
            ((TestViewHolder) viewHolder).setListener(i);
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvEvaluate, tvProgress, tvCorrect;
        private RoundProgressBar ivDownload;//下载图片
        private RoundProgressBar ivTotalProgress;//总进度
        private RoundProgressBar ivEvaluating;//评测进度
        private RoundProgressBar ivCorrect;//正确数
        private ImageView ivTips;//标识图片
        private RelativeLayout rlTestsLayout;
        //        private RoundProgressBar downloadProgressBar;//下载进度
        private View bottomView;
        //private ImageView tvNewTest;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);

            //tvNewTest = itemView.findViewById(R.id.iv_new_test);
            tvName = itemView.findViewById(R.id.testlib_in_test_name);
            tvEvaluate = itemView.findViewById(R.id.testlib_in_test_evaluate);
            tvProgress = itemView.findViewById(R.id.testlib_in_test_progress);
            tvCorrect = itemView.findViewById(R.id.testlib_in_test_correct);
            ivDownload = itemView.findViewById(R.id.testlib_in_download_button);
            ivTotalProgress = itemView.findViewById(R.id.testlib_in_totalProgress);
            ivEvaluating = itemView.findViewById(R.id.testlib_in_evaluating);
            ivCorrect = itemView.findViewById(R.id.testlib_in_correct);
            ivTips = itemView.findViewById(R.id.iv_tips);
            rlTestsLayout = itemView.findViewById(R.id.testlib_in_relativelayout_test);
//            downloadProgressBar = itemView.findViewById(R.id.testlib_in_download_progress);
            bottomView = itemView.findViewById(R.id.v_bottom_view);
        }

        public void setData(int position) {
            PackInfo data = mList.get(position);
            String rightNum = zHelper.getRightNum(data.PackName, data.TestType) + "";
            String studyTime = zHelper.getStudyTime(data.PackName, data.TestType) + "";
            String totalTime = zHelper.getTotalTime(data.PackName, data.TestType) + "";
            String totalIsEvaluateNum = teHelper.getTotalIsEvaluateNum(data.PackName, data.TestType) + "";
            String totalEvaluateNum = teHelper.getTotalEvaluateNum(data.PackName, data.TestType) + "";
            //已学习时间和总学习时间
            Double studyTime1 = Double.valueOf(studyTime);
            Double totalTime1 = Double.valueOf(totalTime);
            //已评测数量和总评测数量
            Double totalEvaluateNum1 = Double.valueOf(totalEvaluateNum);
            Double totalIsEvaluateNum1 = Double.valueOf(totalIsEvaluateNum);
            //进度计算
            Double totalProgreass1 = studyTime1 / totalTime1 * 100;
            Double evaluateProgreass1 = totalIsEvaluateNum1 / totalEvaluateNum1 * 100;
            String totalProgress = String.valueOf((int) Math.round(totalProgreass1));
            String evaluateProgreass = String.valueOf((int) Math.round(evaluateProgreass1));


//            Timber.e("总时间为" + totalTime1);
//            Timber.e("已学时间为" + studyTime1);
//            Timber.e("总体进度" + totalProgress);
//            Timber.e("总评测数量" + totalEvaluateNum);
//            Timber.e("评测进度比" + evaluateProgreass);
            // testCond由正确比列
            //未完成 需要接口传递数据
            String tcStringProgress = totalProgress + "%";
            String tcStringEvaluate = totalIsEvaluateNum + "/" + totalEvaluateNum;
            String tcStringCorrect = rightNum + "/" + data.QuestionSum;

            String tnString = data.PackName + "(" + data.TitleSum + "篇听力)";
            tvName.setText(tnString);

//            float correctProgress = Integer.valueOf(rightNum)/data.QuestionSum;

            //暂时设置为10；20；30，具体数值需要进行计算传值
            //听力按进度条设置进度条
            if (Integer.valueOf(studyTime) != 0) {
                ivTotalProgress.setBackgroundResource(R.drawable.ic_total_progress2);
            } else {
                ivTotalProgress.setBackgroundResource(R.drawable.ic_total_progress);
            }
            ivTotalProgress.setProgress(Integer.valueOf(totalProgress));
            ivTotalProgress.setCricleProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
            tvProgress.setText(tcStringProgress);

            //评测按进度设置进度条
            if (Integer.valueOf(totalIsEvaluateNum) != 0) {
                ivEvaluating.setBackgroundResource(R.drawable.ic_evaluating2);
            } else {
                ivEvaluating.setBackgroundResource(R.drawable.ic_evaluating);
            }
            ivEvaluating.setProgress(Integer.valueOf(evaluateProgreass));
            ivEvaluating.setCricleProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
            tvEvaluate.setText(tcStringEvaluate);

            //正确数按进度设置进度条
            if (Integer.valueOf(rightNum) != 0) {
                ivCorrect.setBackgroundResource(R.drawable.ic_correct2);
            } else {
                ivCorrect.setBackgroundResource(R.drawable.ic_correct);
            }
            ivCorrect.setProgress(Integer.valueOf(rightNum));
            ivCorrect.setCricleProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
            tvCorrect.setText(tcStringCorrect);


            if (position < image.length) {
                ivTips.setImageResource(image[position]);
            } else {
                ivTips.setImageResource(image[position - image.length]);
            }

            if (data.TestType == 102) {
                rlTestsLayout.setBackgroundResource(R.drawable.ic_item_background_new);
            } else {
                rlTestsLayout.setBackgroundResource(R.drawable.ic_item_background);
            }

            if (position == mList.size() - 1) {
                bottomView.setVisibility(View.VISIBLE);
            } else {
                bottomView.setVisibility(View.GONE);
            }


            if (data.downloadStatus == 1) {
                statDownLoadAnimation();
                LogUtil.e("下载，开始动画" + data.downloadStatus);
            } else if (data.downloadStatus == 0) {

                stopDownLoadAnimation();
            }

            if (data.IsDownload) {//|| data.downloadStatus == 3 || data.Progress == 1.0
                setYesDownloadUi();
            } else {
                setNoDownloadUi(data);
                ivDownload.setMax(maxProgress);
                ivDownload.setCricleProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
                ivDownload.setProgress((int) data.Progress);//有意外情况需要消除
            }
        }

        public void setListener(final int position) {
            final PackInfo data = mList.get(position);
            rlTestsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 根据数据判断试题下载状态，等于3 已经下载  downloadStatus
                    ZDBHelper helper = new ZDBHelper(mContext);
                    ArrayList<TitleInfo> titleInfoList = helper.getTitleInfos(data.PackName, data.TestType);
                    DataManager.Instance().titleInfoList = titleInfoList;//???数据不全

                    Intent intent = new Intent();
                    intent.setClass(mContext, TestActivity.class);
//                    intent.putExtra("packName", data.PackName);
                    Bundle bundle = new Bundle();
                    bundle.putString("packName", data.PackName);
                    bundle.putInt("testType", data.TestType);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isDownLoading()) {// 当前没有点击任何标签(没有正在下载的项)
                        LogUtil.e("下载:onClick以前没有下载，现在开始下载");
                        showDialog(position, data); //开始新的下载
                        data.downloadStatus = 1;
                    } else if (data.downloadStatus == 1) { //去掉暂停功能
                        Toast.makeText(mContext, "暂停下载", Toast.LENGTH_SHORT).show();
                        LogUtil.e("下载:onClick Download 暂停状态");
                        data.downloadStatus = 2;
                        //DataManager.Instance().packInfoList.get(position).downloadStatus = 2;
                        stopDownLoadAnimation();//暂停动画
                        ivDownload.setBackgroundResource(R.drawable.ic_download_stop);
                    } else if (data.downloadStatus == 2) {//恢复下载
                        LogUtil.e("下载：onClick恢复下载!");
                        data.downloadStatus = 1;
                        statDownLoadAnimation();//继续下载
                        downloadAudios(position, data);
                    } else {
                        Toast.makeText(mContext, "下载:当前下载任务还未结束，请稍等再试", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

        //下载前提示
        public void showDialog(final int tempPosition, final PackInfo curPackInfo) {
            if (CheckNetWork.checkWifiStatus(mContext)) {
                statDownLoadAnimation();//下载动画
                downloadAudios(tempPosition, curPackInfo);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示").
                        setIcon(android.R.drawable.ic_dialog_alert).setMessage
                                ("您所需要的试题资源建议使用wifi下载，是否继续下载?").
                        setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                statDownLoadAnimation();//流量继续下载
                                downloadAudios(tempPosition, curPackInfo);
                            }
                        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        }

        private void downloadAudios(final int curPosition, final PackInfo curPackInfo) {
            //    arg1传递当前操作的进度条id，arg2传递的当前操作的下载button的id
            if (CheckNetWork.isNetworkAvailable(mContext)) { // 判断有网络
                Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();//!!!
                FilesDownProcessBar fDownProcessBar = new FilesDownProcessBar(mContext, curPackInfo,
                        new OnDownloadStateListener() {
                            @Override
                            public void onFinishedListener(int progress) {

                                LogUtil.e("下载:finish", "onFinishedListener");
                                updatadData(progress, curPosition);
                                handler.sendEmptyMessage(1);
                                curPackInfo.downloadStatus = 3;//下载完成
                            }

                            @Override
                            public boolean isPausedListener() {
                                //LogUtil.e("下载:是否暂停，" + curPackInfo.downloadStatus);
                                return curPackInfo.downloadStatus == 2;
                            }

                            @Override
                            public void onErrorListener(String errorDesc, int progress) {
                                LogUtil.e("下载:错误，" + errorDesc);
                                updatadDataOnError(progress, curPosition);
                                //statDownLoadAnimation();
                                handler.sendEmptyMessage(5);//下载出错
                            }

                            @Override
                            public void onPausedListener(int progress) {
                                LogUtil.e("下载:状态响应2 onPausedListener.progress:" + progress);
                                // 暂停下载
                                //curPackInfo.downloadStatus = 2;
                                updatadDataOnError(progress, curPosition);
                                //handler.sendEmptyMessage(3);
                            }

                            @Override
                            public void onStartListener(int progress) {//开始下载和继续下载
                                LogUtil.e("下载:状态响应3 onStartListener.progress:" + progress);

                                Message message = Message.obtain();
                                message.what = 7;
                                message.arg1 = curPosition;
                                handler.sendMessage(message);
                            }

                            @Override
                            public ArrayList<DownTest> onPreparedListener() {
                                LogUtil.e("下载:状态响应4 onPreparedListener");
                                String packName = mList.get(curPosition).PackName;
                                int packNum = zHelper.getDownPackNum(packName);
                                int testType = mList.get(curPosition).TestType;
                                ArrayList<DownTest> downTests = teHelper.getDownTests(packName, packNum, testType);
                                downTests.get(0).testNum = curPackInfo.TitleSum;
                                LogUtil.e("下载: DownTest 的大小为：" + downTests.size(), "");
                                return downTests;
                            }
                        });
            }
            //没有网络连接
            else {
                Toast.makeText(mContext, mContext.getString(R.string.net_disconected), Toast.LENGTH_SHORT).show();
            }
        }


        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                switch (msg.what) {
                    case 1://已经下载显示
                        setYesDownloadUi();
                        notifyDataSetChanged();
                        break;
                    case 2://没有下载显示
                        setNoDownloadUi(null);
                        break;
                    case 3://下载动画开始
                        statDownLoadAnimation();
                        break;
                    case 4://下载动画关闭
                        stopDownLoadAnimation();
                        break;
                    case 5://下载动出错
                        Toast.makeText(mContext, R.string.down_data_error, Toast.LENGTH_SHORT).show();
                    case 6://下载暂停显示
                        ivDownload.setBackgroundResource(R.drawable.ic_download_stop);
                        break;
                    case 7://刷新adapter，来更新下载进度
                        notifyItemChanged(msg.arg1);
                        break;
                }
                return false;
            }
        });

        private void setYesDownloadUi() {
            ivDownload.setBackgroundResource(R.drawable.news_downloaded);
            ivDownload.setProgress(0);
            ivDownload.setEnabled(false);
//            downloadProgressBar.setVisibility(View.INVISIBLE);
        }

        private void setNoDownloadUi(PackInfo data) {
            if (data != null && data.downloadStatus == 2) {
                ivDownload.setBackgroundResource(R.drawable.ic_download_stop);
            } else {
                ivDownload.setBackgroundResource(R.drawable.download_animation);
            }
            ivDownload.setEnabled(true);
//            downloadProgressBar.setVisibility(View.INVISIBLE);
        }

        private boolean getPermissions() {
            //使用前必须开启-存储权限--
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    CustomToast.showToast(mContext, "存储权限开通才可以正常下载，请到系统设置中开启", 3000);
                    return false;
                }
            }
            return true;
        }

        private boolean isDownLoading() {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).downloadStatus == 1 || mList.get(i).downloadStatus == 2) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 开始下载的动态效果
         */
        public void statDownLoadAnimation() {
//            downloadProgressBar.setVisibility(View.VISIBLE);
            LogUtil.e("animation开始");
            try {
                ((AnimationDrawable) ivDownload.getBackground()).start();
            } catch (Exception e) {
                LogUtil.e("开始动画异常！" + e);
                ivDownload.setBackgroundResource(R.drawable.download_animation);
                ((AnimationDrawable) ivDownload.getBackground()).start();
            }
        }

        /**
         * 结束下载的动态效果
         */
        public void stopDownLoadAnimation() {
            LogUtil.e("animation关闭");
            try {
                AnimationDrawable animation = (AnimationDrawable) ivDownload.getBackground();
                animation.stop();
                animation.selectDrawable(0);
                ivDownload.setBackgroundResource(R.drawable.news_downloaded);
                ivDownload.setProgress(0);
            } catch (Exception e) {
                LogUtil.e("关闭动画异常！" + e);
                ivDownload.setBackgroundResource(R.drawable.ic_download_stop);
            }
        }

        //更新数据包括DataManager，mList中的，和数据库中的
        public void updatadData(int progress, int curPosition) {

            float transProgress = (float) progress / maxProgress;
            DataManager.Instance().packInfoList.get(curPosition).Progress = transProgress;
            DataManager.Instance().packInfoList.get(curPosition).IsDownload = true;

            mList.get(curPosition).Progress = transProgress;
            mList.get(curPosition).IsDownload = true;
            //更新数据库
            String packName = mList.get(curPosition).PackName;
            zHelper.setProgress(packName, transProgress);

            //notifyDataSetChanged();
        }

        //更新数据包括DataManager，mList中的，和数据库中的
        public void updatadDataOnError(int progress, int curPosition) {

            float transProgress = (float) progress / maxProgress;
            DataManager.Instance().packInfoList.get(curPosition).Progress = transProgress;
            DataManager.Instance().packInfoList.get(curPosition).IsDownload = false;//Error

            mList.get(curPosition).Progress = transProgress;
            mList.get(curPosition).IsDownload = false;//Error
            //更新数据库
            String packName = mList.get(curPosition).PackName;
            zHelper.setProgress(packName, 0);
        }
    }
}
