package com.iyuba.toeiclistening.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.DeleteAudioFileEvent;
import com.iyuba.core.util.GetFileSizeUtils;
import com.iyuba.core.widget.Button;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Author：Howard on 2016/8/3 14:23
 * Email：Howard9891@163.com
 */

public class DeleteAudioActivity extends Activity {
    List<String> fileNames;
    private ListView lv_audio_delete;
    private DelAdapter mAdapter;
    //    private DataBaseManager mDBManager;
    private ZDBHelper zhelper;
    private CustomDialog mWaittingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_audio);
        fileNames = GetFileSizeUtils.getInstance().getDirName(Constant.videoAddr);
//        mDBManager = DataBaseManager.getInstance(this);
        lv_audio_delete = (ListView) findViewById(R.id.lv_audio_delete);
        Button button = (Button) findViewById(R.id.buttonTitleBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mWaittingDialog = WaittingDialog.showDialog(this);

        mAdapter = new DelAdapter();
        mAdapter.setData(fileNames);
        lv_audio_delete.setAdapter(mAdapter);
        zhelper = new ZDBHelper(this);
        lv_audio_delete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlertDialog(position);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAlertDialog(final int position) {
        final String fileName = fileNames.get(position);
        System.out.println(fileName + "=======--------=========");
        Dialog dialog = new AlertDialog.Builder(this).setTitle("确认删除吗?").setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // mWaittingDialog.show();
                        GetFileSizeUtils.getInstance().deleteDir(new File(Constant.videoAddr + "/" + fileNames.get(position)));
//                        mDBManager.delete(DownloadManager.createKey(Constant.soundBaseUrl + fileNames.get(position) + ".zip"));
                        String name=fileNames.get(position);
                        fileNames.remove(position);
                        changeState(fileName);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(DeleteAudioActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        int index;
                        try {
                            index=Integer.valueOf(name.substring(name.length()-1,name.length()));
                            DataManager.Instance().packInfoList.get(index-1).IsDownload=false;//删除
                            DataManager.Instance().packInfoList.get(index-1).Progress=0f;//删除
                            DataManager.Instance().packInfoList.get(index-1).IsFree=false;//删除
                            DataManager.Instance().packInfoList.get(index-1).downloadStatus=0;//删除
                            DataManager.Instance().packInfoList.get(index-1).RightSum=0;//删除
                            EventBus.getDefault().post(new DeleteAudioFileEvent());//over!
                        }catch (Exception e){
                            LogUtil.e("删除异常 最后一个是d?"+name);
                        }


                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();
        dialog.show();
    }

    private void changeState(String fileName) {
        boolean isChange = zhelper.setDownloadState(fileName, false);
        System.out.println(isChange + "-------------------");
        //mWaittingDialog.dismiss();

    }

//    private String getRealPackName(String fileName) {
//        String year;
//        String month;
//        if (fileName.length() > 4) {
//            year = fileName.substring(0, 4);
//            month = fileName.substring(4, 6);
//            Log.e("yyyyyy", year + "年" + Integer.parseInt(month) + "月");
//            return year + "年" + Integer.parseInt(month) + "月";
//        } else {
//            year = fileName;
//            Log.e("yyyyyy", year + "年");
//            return year + "年";
//        }
//    }

    class DelAdapter extends BaseAdapter {
        private List<String> fileNames;

        public void setData(List<String> fileNames) {
            this.fileNames = fileNames;
        }

        @Override
        public int getCount() {
            return fileNames.size();
        }

        @Override
        public Object getItem(int position) {
            return fileNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.item_delete_listen, null);
                viewHolder.tv_delete_listen = (TextView) convertView.findViewById(R.id.tv_delete_listen);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String str = fileNames.get(position);
            viewHolder.tv_delete_listen.setText(str);
//            if (str.length() > 6) {
//                viewHolder.tv_delete_listen.setText(str.substring(0, 6) + "(" + str.substring(str.length() - 1, str.length()) + ")");
//            } else {
//                viewHolder.tv_delete_listen.setText(str.substring(0, 4) + "(1)");
//            }

            return convertView;
        }
    }


    class ViewHolder {
        public TextView tv_delete_listen;
    }
}
