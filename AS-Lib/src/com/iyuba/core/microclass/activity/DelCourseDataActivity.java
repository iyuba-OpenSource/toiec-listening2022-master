package com.iyuba.core.microclass.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.microclass.adapter.DeleCourseDataListAdapter;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.DelCourseDataInfo;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.util.GetFileSizeUtils;
import com.iyuba.core.R;

public class DelCourseDataActivity extends Activity {

    private Context mContext;
    private ArrayList<DelCourseDataInfo> delCourseDataInfos = new ArrayList<>();
    private ArrayList<CourseContent> allCourseContents = new ArrayList<>();
    private DeleCourseDataListAdapter deleDataListAdapter;
    private CourseContentOp courseContentOp;

    private ListView deldataList;
    private TextView TV_Size;

    private String strDataPath = Constant.APP_DATA_PATH + "audio";
    private String strDirSize = "";

    private Button backButton;
    private Button delAndEditButton;

    private File file;//存储课程资源的文件
    private ArrayList<File> resFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_course_data);

        initResFiles();

        courseContentOp = new CourseContentOp(mContext);
        allCourseContents = courseContentOp.findCourseContentDataByAll();

        backButton = (Button) findViewById(R.id.delete_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DelCourseDataActivity.this.finish();
            }
        });


        TV_Size = (TextView) findViewById(R.id.TV_Size);

        GetDataDirectory(strDataPath);
        GetFileSizeUtils.getInstance().getDirName(strDataPath);

        TV_Size.setText(strDirSize);

        deldataList = (ListView) findViewById(R.id.listViewData);
        deleDataListAdapter = new DeleCourseDataListAdapter(mContext, delCourseDataInfos, resFileList);
        deldataList.setAdapter(deleDataListAdapter);
        deldataList.setOnItemClickListener(mylistviewClick);

        delAndEditButton = (Button) findViewById(R.id.delete_coursedata_edit);
        delAndEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int state = deleDataListAdapter.getState();
                if (state == 1) {//如果不处在批量删除的状态
                    delAndEditButton.setText("完成");
                    deleDataListAdapter.setState(0);
                    deleDataListAdapter.notifyDataSetChanged();
                } else {//处在批量删除的状态
                    delAndEditButton.setText("编辑");
                    deleDataListAdapter.setState(1);
                    handlerRefreshSize.sendEmptyMessageDelayed(0, 500);
                }

            }
        });
    }

    public void initResFiles() {
        file = new File(Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH + "/");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                resFileList = new ArrayList<File>();
                Collections.addAll(resFileList, files);
            }
        }
    }

    // click
    AdapterView.OnItemClickListener mylistviewClick = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

//			DelCourseDataInfo tmp = new DelCourseDataInfo();
//			tmp.dirPath = delCourseDataInfos.get(arg2).dirPath;
//			tmp.CourseName = delCourseDataInfos.get(arg2).CourseName;
//			tmp.CourseId = delCourseDataInfos.get(arg2).CourseId;
//			tmp.strSize = delCourseDataInfos.get(arg2).strSize;
//			
//
//			TextView coursename = (TextView) v.findViewById(R.id.delCourseName);
//			TextView datasize = (TextView) v.findViewById(R.id.delCourseDataSize);
//
//			String time = coursename.getText().toString();
//			String data = datasize.getText().toString();
//
//			showSelectedInfo(tmp);

            int state = deleDataListAdapter.getState();
            if (state == 0) {
                deleDataListAdapter.updateView(arg2);
            }
        }
    };

    public void showSelectedInfo(DelCourseDataInfo deldatainfo) {

        final String strCourseName = deldatainfo.CourseName;
        final String strSize = deldatainfo.strSize;
        final String strDirPath = deldatainfo.dirPath;
        final int CourseId = deldatainfo.CourseId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除数据");
        builder.setMessage(strCourseName + " - " + strSize);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                DeleDataByPath(strDirPath);

//				courseContentOp.deleteCourseContentData(CourseId);
                courseContentOp.updateIsDownloadById(0, CourseId);

                if (!delCourseDataInfos.isEmpty()) {
                    delCourseDataInfos.clear();
                }

                GetDataDirectory(strDataPath);
                TV_Size.setText(strDirSize);

                deleDataListAdapter = new DeleCourseDataListAdapter(mContext,
                        delCourseDataInfos, resFileList);
                deldataList.setAdapter(deleDataListAdapter);
            }
        }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).show();

    }

    public void showInfo(String coursename, String datasize) {

        final String strCourseName = coursename;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除数据");
        builder.setMessage(coursename + " - " + datasize);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                DeleDataByPath(strDataPath + "/" + strCourseName);

                if (!delCourseDataInfos.isEmpty()) {
                    delCourseDataInfos.clear();
                }

                GetDataDirectory(strDataPath);
                TV_Size.setText(strDirSize);

                deleDataListAdapter = new DeleCourseDataListAdapter(mContext,
                        delCourseDataInfos, resFileList);
                deldataList.setAdapter(deleDataListAdapter);
            }
        }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).show();

    }

    /*
     * 获取数据文件夹
     */
    public void GetDataDirectory(String strPath) {

        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (null == files) {
            return;
        }

        Double dDirSize = 0.0;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {

                Double dsize = GetDirSizeDouble(files[i]);
                DecimalFormat dFormat = new DecimalFormat("0.00");
                String filesize = dFormat.format(dsize);

                dDirSize += dsize;

                DelCourseDataInfo tmp = new DelCourseDataInfo();
                tmp.CourseId = Integer.parseInt(files[i].getName());
                for (int j = 0; j < allCourseContents.size(); j++) {
                    if (tmp.CourseId == allCourseContents.get(j).id) {
                        tmp.CourseName = allCourseContents.get(j).titleName;
                    }
                }
                tmp.strSize = filesize + " M";
                tmp.dirPath = files[i].getAbsolutePath();

                delCourseDataInfos.add(tmp);
                GetDataDirectory(files[i].getAbsolutePath());
            } else {

            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String allsize = df.format(dDirSize);
        strDirSize = allsize + " M";
    }

	/*
     * 文件夹大小
	 */

    private long GetDirSize(File dir) {
        if (null == dir) {
            return 0;
        }

        if (!dir.isDirectory()) {
            return 0;
        }

        long dirsize = 0;

        File[] files = dir.listFiles();
        for (File file : files) {

            if (file.isFile()) {
                dirsize += file.length();
            } else if (file.isDirectory()) {
                dirsize += file.length();
                dirsize += GetDirSize(file);
            }
        }
        return dirsize;
    }


    private double GetDirSizeDouble(File dir) {
        if (null == dir) {
            return 0;
        }

        if (!dir.isDirectory()) {
            return 0;
        }

        long dirsize = 0;

        File[] files = dir.listFiles();
        for (File file : files) {

            if (file.isFile()) {
                dirsize += file.length();
            } else if (file.isDirectory()) {
                dirsize += file.length();
                dirsize += GetDirSize(file);
            }
        }

        double size = 0;
        size = (dirsize + 0.0) / (1024 * 1024);
        return size;
    }

    /*
     * 删除指定时间的数据
     */
    private void DeleDataByPath(String strpath) {

        Log.e("path", strpath);
        File file = new File(strpath);
        if (file.exists() && file.isDirectory()) {

            if (file.listFiles().length == 0) {
                file.delete();
            } else {
                File delfFile[] = file.listFiles();
                int i = file.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delfFile[j].isDirectory()) {
                        DeleDataByPath(delfFile[j].getAbsolutePath());
                    }
                    delfFile[j].delete();
                }

                file.delete();

            }

        }

    }

    Handler handlerRefreshSize = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0:
                    if (!delCourseDataInfos.isEmpty()) {
                        delCourseDataInfos.clear();
                    }
                    GetDataDirectory(strDataPath);
                    TV_Size.setText(strDirSize);
                    break;

                default:
                    break;
            }
        }

    };

}
