package com.iyuba.core.microclass.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.AppUpdateCallBack;
import com.iyuba.core.common.manager.DownloadManager;
import com.iyuba.core.common.sqlite.mode.DownloadFile;
import com.iyuba.core.common.thread.AppUpdateThread;
import com.iyuba.core.common.util.FileOpera;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.microclass.manager.VersionManager;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 关于界面
 *
 * @author chentong
 */

public class About extends BasisActivity implements AppUpdateCallBack {
    private Context mContext;
    private Button backBtn;
    private TextView version;
    private View appUpdate, praise, offical, weixin, announcer;
    private View appNewImg;
    private String version_code;
    private String appUpdateUrl;// 版本号
    private RoundProgressBar progressBar;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_microclass);
        mContext = this;
        CrashApplication.addActivity(this);
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        praise = findViewById(R.id.praise);
        praise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("market://details?id="
                            + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(
                                    getResources().getString(
                                            R.string.alert_title))
                            .setMessage(
                                    getResources().getString(
                                            R.string.about_market_error))
                            .setNeutralButton(
                                    getResources().getString(
                                            R.string.alert_btn_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                        }
                                    }).create();
                    dialog.show();
                }

            }
        });
        offical = findViewById(R.id.official);
        offical.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, Web.class);
                intent.putExtra("url", "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android");
                intent.putExtra("title",
                        About.this.getString(R.string.about_offical));
                startActivity(intent);
            }
        });
        weixin = findViewById(R.id.weixin);// 下载其他应用
        weixin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        announcer = findViewById(R.id.announcer);
        announcer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Dialog dialog = new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(
                                getResources().getString(R.string.alert_title))
                        .setMessage(
                                getResources().getString(
                                        R.string.about_announcer_alert))
                        .setNeutralButton(
                                getResources().getString(R.string.alert_btn_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
                dialog.show();
            }
        });
        appUpdate = findViewById(R.id.update);
        appUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                checkAppUpdate();
            }
        });
        appNewImg = findViewById(R.id.newApp);
        version = (TextView) findViewById(R.id.version);
        version.setText("version " + VersionManager.VERSION_CODE);
        progressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        update = getIntent().getBooleanExtra("update", false);
        boolean has = false;
        int size = DownloadManager.Instance().fileList.size();
        DownloadFile file;
        for (int i = 0; i < size; i++) {
            file = DownloadManager.Instance().fileList.get(i);
            if (file.id == -1) {
                Message message = new Message();
                message.what = 2;
                message.obj = file;
                handler.sendMessage(message);
                has = true;
            }
        }
        if (!has) {
            if (update) {
                this.appUpdateUrl = getIntent().getStringExtra("url");
                this.version_code = getIntent().getStringExtra("version");
                handler.sendEmptyMessage(0);
            } else {
                checkAppUpdate();
            }
        } else {
            appUpdate.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate() {
        VersionManager.Instace(this).checkNewVersion(VersionManager.version,
                this);
    }

    @Override
    public void appUpdateSave(String version_code, String newAppNetworkUrl) {
        // TODO Auto-generated method stub
        this.version_code = version_code;
        this.appUpdateUrl = newAppNetworkUrl;
        Looper.prepare();
        AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(getResources()
                .getString(R.string.about_update_alert_1)
                + version_code
                + getResources().getString(R.string.about_update_alert_2));
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(0);
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(3);
                    }
                });
        alert.show();
        Looper.loop();
    }

    @Override
    public void appUpdateFaild() {
        // TODO Auto-generated method stub
        handler.sendEmptyMessage(1);
    }

    private void initDownload(String path) {
        // TODO Auto-generated method stub
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.id = -1;
        downloadFile.downloadState = "start";
        downloadFile.fileAppend = ".apk";
        downloadFile.downLoadAddress = appUpdateUrl;
        downloadFile.filePath = Constant.envir + "/appUpdate/";
        downloadFile.fileName = path;
        DownloadManager.Instance().fileList.add(downloadFile);
        Message message = new Message();
        message.what = 2;
        message.obj = downloadFile;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    appUpdate.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    appNewImg.setVisibility(View.GONE);
                    initDownload(Constant.appfile + "_" + version_code);
                    AppUpdateThread appUpdateThread = new AppUpdateThread();
                    appUpdateThread.start();
                    break;
                case 1:
                    appNewImg.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    CustomToast.showToast(mContext, R.string.about_update_isnew);
                    break;
                case 2:
                    DownloadFile file = (DownloadFile) msg.obj;
                    if (file.downloadState.equals("start")) {
                        progressBar.setCricleProgressColor(0xff87c973);
                        progressBar.setMax(file.fileSize);
                        progressBar.setProgress(file.downloadSize);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = file;
                        handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("finish")) {
                        appUpdate.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        appNewImg.setVisibility(View.GONE);
                        DownloadManager.Instance().fileList.remove(file);
                        new FileOpera(mContext).openFile(file.filePath
                                + file.fileName + file.fileAppend);
                    }
                    break;
                case 3:
                    appNewImg.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
