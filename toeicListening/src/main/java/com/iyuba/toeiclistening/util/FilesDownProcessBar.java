package com.iyuba.toeiclistening.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.DownTest;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.listener.OnDownloadStateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FilesDownProcessBar {
    private Context mContext;

    private int FileLength;
    private int DownedFileLength = 0;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    private String saveName;
    private String fileUrl;
    private ArrayList<DownTest> downTests = new ArrayList<DownTest>();
    private DownTest curDownTest = new DownTest();// 当前下载文件
    private String savePathString;

    public boolean finished = false;// 完成下载标记
    private int filesSum = 0;// 记录总文件数
    private OnDownloadStateListener downloadStateListener = null;

    private PackInfo packInfo;

    public FilesDownProcessBar(Context context) {
        this.mContext = context;
    }

    /**
     * 下载多个文件并根据已下载文件数更新进度条
     *
     * @param context
     * @param downloadStateListener --要下载的文件名列表信息
     */
    public FilesDownProcessBar(Context context, PackInfo packInfo, OnDownloadStateListener downloadStateListener) {

        this.mContext = context;
        this.downloadStateListener = downloadStateListener;
        this.downTests = this.downloadStateListener.onPreparedListener();
        this.packInfo = packInfo;

        // Log.e("FilesDownProcessBar.onPreparedListener准备下载数据","要下载的题数:"+downTests.size());
        //this.filesSum = downTests.size();
        this.filesSum = downTests.get(0).testNum;//数据查询有问题，只能这样先写 fuck!
        if (this.downTests.size() > 0) {
            // 从队列中获取当前下载文件
            curDownTest = this.downTests.get(0);
            // 从请求队列中将当前请求删除
            // this.downTests.remove(0);
            downLoadFile(curDownTest);
            // this.downloadStateListener.onPreparedListener();
            Log.e("下载 开始-------------", "");
        } else {
            Log.e("下载 文件总数-------------", "filesSum:" + filesSum);
            finished = true;
            this.downloadStateListener.onFinishedListener(100);
        }
    }

    public void downLoadFile(DownTest curDownTest) {
        final String packName;


        if (curDownTest.titleNum / 1000 >= 8) {//适配新数据
            packName = "2019" + curDownTest.packName;
        } else {
            packName = curDownTest.packName;
        }

        final String saveName = curDownTest.sound;
        final int packNum;
        if (curDownTest.titleNum / 1000 >= 8) {
            packNum = curDownTest.titleNum / 1000;//8,9,10
        } else {
            packNum = curDownTest.packNum;
        }

        String newWorkFileUrl = packNum + "/" + curDownTest.sound;
        if (packNum > 7) {
            newWorkFileUrl = newWorkFileUrl.replace("mp4", "mp3");
        }
        //final String newWorkFileUrl = curDownTest.sound +Constant.AUDIO_FORMATE;

        if (SDCard.hasSDCard()) {
            DownedFileLength = 0;
            // 检测网络是否可用
            if (CheckNetWork.isNetworkAvailable(mContext)) {
                if (downloadStateListener != null && downloadStateListener.isPausedListener()) {
                    downloadStateListener.onPausedListener((int) packInfo.Progress);// 手动暂停下载
                    return;
                }
                final String finalNewWorkFileUrl = newWorkFileUrl;
                ThreadManageUtil.sendRequest(new ThreadObject() {

                    @Override
                    public Object handleOperation() {
                        // downloadStateListener.onStartListener();//开始下载
                        //Log.d("联网获取URL的地址为：---->", newWorkFileUrl);
                        DownFile(finalNewWorkFileUrl, packNum, packName, saveName);
                        return null;
                    }
                });

            } else {//网络不可用时
                this.downloadStateListener.onErrorListener(
                        mContext.getString(R.string.net_disabled_wifi),
                        (int) packInfo.Progress);
                this.downloadStateListener.onPausedListener((int) packInfo.Progress);

            }

        } else { //没有SD卡时
            this.downloadStateListener.onErrorListener(
                    mContext.getString(R.string.sdcard_loss), (int) packInfo.Progress);
            this.downloadStateListener.onPausedListener((int) packInfo.Progress);
            Log.e("net", "in");
            // Toast.makeText(mContext,
            // mContext.getString(R.string.sdcard_loss),
            // Toast.LENGTH_LONG).show();
        }

    }

    public void DownFile(String urlString, int packNum, String packName, String saveName) {
        this.saveName = saveName;
        // URL地址的构造形式
        // http://static."+com.iyuba.core.util.Constant.IYBHttpHead+"/sounds/考试名称/文件名.音频格式

        this.fileUrl = Constant.SERVER_PATH + urlString;

        // SDcard根目录//iyuba/toelflistening/audio/包名/音乐名称
        //音乐名称由titleNum+sound
        savePathString = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                + "/" + packName + "/" + this.saveName;

        makeRootDirectory(com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                + "/" + packName + "/");//生成文件夹


        LogUtil.e("savePathString" + savePathString);
        //Log.e("下载文件--获取网络连接的URL", urlString);
        //Log.e("下载文件--保存路径", savePathString);
        //这个地方toefl和toeic数据库中的音频文件保存形式不同，toeic不需要后缀名
        //File mp3File = new File(savePathString+Constant.AUDIO_FORMATE);
        File mp3File = new File(savePathString);
        // 粗略的判断此文件是否下载完成
        if (mp3File.exists() && mp3File.length() > 256) {// 说明文件已下载
            downTests.remove(0);
            if (downloadStateListener != null && downloadStateListener.isPausedListener()) {
                downloadStateListener
                        .onPausedListener((int) packInfo.Progress);
                return;
            }
            if (downTests.size() > 0) {
                // 从队列中获取当前下载文件
                curDownTest = downTests.get(0);
                downLoadFile(curDownTest);// 继续下载
            } else {  //判断下载完成或是出现错误
                finished = true;
                if (checkDownFilesByPack(curDownTest.packName, filesSum) == 100) {
                    downloadStateListener.onFinishedListener((int) packInfo.Progress);
                } else {
                    downloadStateListener.onErrorListener(mContext.getString(R.string.down_data_error),
                            (int) packInfo.Progress);
                    LogUtil.e("下载出现错误！判断下载完成或是出现错误");
                }
            }
            return;
        }

        // else
        File fileTemp = new File(savePathString);// +".4ma"
        if (fileTemp.exists() && fileTemp.length() > 0) {// 如果文件存在并且文件大小大于0，则该文件未下载完成，需要重新下载
            fileTemp.delete();// 删除原文件
            // downTests.remove(0);
        }

        LogUtil.e("下载音频-网络地址", fileUrl);
        LogUtil.e("下载音频-本地名称", saveName);
        /*
         * 连接到服务器
         */
        if (CheckNetWork.isNetworkAvailable(mContext)) {
            try {
                URL url = new URL(fileUrl);
                connection = url.openConnection();
                if (connection.getReadTimeout() >= 5) {
                    //Log.e("---------->",
                    //		"当前网络有问题,网络连接时间" + connection.getReadTimeout());
                    // Toast.makeText(mContext,
                    // mContext.getString(R.string.net_disabled_wifi),
                    // Toast.LENGTH_SHORT).show();
                    downloadStateListener.onErrorListener(
                            mContext.getString(R.string.net_disabled_wifi),
                            (int) packInfo.Progress);
                    downloadStateListener.onPausedListener((int) packInfo.Progress);
                    return;
                }
                inputStream = connection.getInputStream();

                //Log.e("开始下载---------->connection.getReadTimeout()", "网络连接时间"+connection.getReadTimeout());

                this.downloadStateListener.onStartListener((int) packInfo.Progress);// 开始下载


            } catch (MalformedURLException e1) {
                //Log.e("下载音频", "error");
                // TODO Auto-generated catch block
                e1.printStackTrace();
                // downloadStateListener.onErrorListener(e1.getMessage());
                // downloadStateListener.onPausedListener();
            } catch (IOException e) {
                //Log.e("下载音频", "error");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            downloadStateListener.onErrorListener(
                    mContext.getString(R.string.net_disabled_wifi),
                    (int) packInfo.Progress);
            downloadStateListener.onPausedListener((int) packInfo.Progress);
        }

        /*
         * 文件的保存路径和和文件名其中Nobody.mp3是在手机SD卡上要保存的路径，如果不存在则新建
         */
        String savePAth = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                + "/" + packName;//curDownTest.packName;

        File file1 = new File(savePAth);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        Log.e("下载音频-本地名称(完整)", savePathString);
        File file = new File(savePathString);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //Log.e("writefilebbb", " "+FileLength);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            FileLength = connection.getContentLength();
            if (FileLength > 0)// 有连接
            {
                while (DownedFileLength < FileLength) {
                    //Log.e("writefile", " "+FileLength);
                    int length = inputStream.read(buffer);
                    DownedFileLength += length;
                    outputStream.write(buffer, 0, length);

                    // message1.what = 1;
                    // handler.sendEmptyMessage(1);
                }
                if (DownedFileLength > 0) {
                    handler.sendEmptyMessage(2);
                } else {
                    // 读取数据错误
                    downloadStateListener.onErrorListener(
                            mContext.getString(R.string.down_data_error),
                            (int) packInfo.Progress);
                    LogUtil.e("下载出现错误！读取数据错误");

                }

            } else {
                // 网络连接错误，没有连接，中断下载
                downloadStateListener.onErrorListener(
                        mContext.getString(R.string.net_disabled_wifi),
                        (int) packInfo.Progress);
            }

            // reNameFile(savePathString, savePathString+".mp3");
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 2:
                        Log.e("下载 完成----------->", savePathString);
                        //reNameFile(savePathString, savePathString + Constant.AUDIO_FORMATE);
                        reNameFile(savePathString, savePathString);
                        // 从请求队列中将当前请求删除
                        if (downTests.size() > 0) {
                            downTests.remove(0);
                        }
                        String packName;
                        if (curDownTest.titleNum / 1000 >= 8) {
                            packName = "2019" + curDownTest.packName;
                        } else {
                            packName = curDownTest.packName;
                        }
                        //设置下载进度
                        if (packInfo != null) {

                            packInfo.Progress = checkDownFilesByPack(packName, filesSum);
                        }
                        if (downloadStateListener != null
                                && downloadStateListener.isPausedListener()) {
                            LogUtil.e("暂停 下载----------->", savePathString);
                            downloadStateListener.onPausedListener((int) packInfo.Progress);
                            return;
                        } else {
                            if (downTests.size() > 0) {
                                // 从队列中获取当前下载文件
                                curDownTest = downTests.get(0);
                                // 从请求队列中将当前请求删除
                                // downTests.remove(0);
                                downLoadFile(curDownTest);// 继续下载
                            } else {
                                finished = true;
                                if (checkDownFilesByPack(packName, filesSum) == 100) {
                                    downloadStateListener.onFinishedListener((int) packInfo.Progress);
                                } else {
                                    downloadStateListener.onErrorListener(mContext
                                                    .getString(R.string.down_data_error),
                                            (int) packInfo.Progress);

                                    LogUtil.e("下载出现错误！handler");

                                }

                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        }

    };

    /**
     * 文件更名，从缓冲状态转换到完成状态
     *
     * @param oldFilePath
     * @param newFilePath
     * @return
     */
    public boolean reNameFile(String oldFilePath, String newFilePath) {
        File source = new File(oldFilePath);
        File dest = new File(newFilePath);
        Log.e("---------------文件更名", oldFilePath + "完成更名" + newFilePath);
        return source.renameTo(dest);
    }

    //
    public static int checkDownFilesByPack(String packName, int testSum) {
        int fileNum = 0;
        // 检测audio下该年份文件夹是否存在
        String savePAth = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                + "/" + packName;
        File file1 = new File(savePAth);
        if (!file1.exists()) {// 该年份文件夹不存在
            return fileNum;
        } else {// 检测该年份文件夹下文件数,过滤出mp3文件个数
            FilenameFilter mf = new ListFileUtil.MyFilenameFilter(".mp3");
            int mp3Num = ListFileUtil.listFilesByFilenameFilter(mf, savePAth);
            LogUtil.e("mp3文件数：" + mp3Num + "该年份题总数：" + testSum + "savePAth" + savePAth);
            if (mp3Num > testSum)
                mp3Num = testSum;
            fileNum = mp3Num * 100 / testSum;
        }
        return fileNum;
    }

    // 生成文件夹
    private void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            LogUtil.i("录音生成文件夹error:" + e);
        }
    }
}
