package com.iyuba.toeiclistening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.TitleInfo;

import java.util.ArrayList;

public class TestAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TitleInfo> testList;
    private ViewHolder viewHolder;

    public TestAdapter(Context context, ArrayList<TitleInfo> list) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        testList = list;
    }

    public void setData(ArrayList<TitleInfo> list) {
        testList = list;
    }

    @Override
    public int getCount() {
        return testList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return testList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        TitleInfo curTitleInfo;
        if (arg1 == null) {
            arg1 = mInflater.inflate(R.layout.test_in, null);
            viewHolder = new ViewHolder();
            viewHolder.testName = (TextView) arg1
                    .findViewById(R.id.test_in_title_name);
            viewHolder.testCond = (TextView) arg1
                    .findViewById(R.id.test_in_title_cond);
            viewHolder.relativeLayout = (RelativeLayout) arg1
                    .findViewById(R.id.RL_Test_In);

            //图片描述  应答问题  简短对话  谈话及语言  PART 1
            arg1.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

        curTitleInfo = testList.get(arg0);
        String type = "Part" + String.valueOf(curTitleInfo.PartType).substring(2);
        // 初始化viewHolder里面控件的值
        if (curTitleInfo.TitleName.length() > 8) {
            viewHolder.testName.setText(curTitleInfo.PackName + " - "
                    + type + " - " + curTitleInfo.TitleName.substring(8));
        } else {
            viewHolder.testName.setText("Test3 - Part3 - 71 to 73???");
        }

        int time;
        if (curTitleInfo.SoundTime == 0) {
            time = 60;
        } else {
            time = curTitleInfo.SoundTime;
        }
        if (curTitleInfo.RightNum >= curTitleInfo.QuesNum) {
            String testCondString = "正确比例：" + curTitleInfo.QuesNum + "/"
                    + curTitleInfo.QuesNum + "-时长:"
                    + transTime(time);
            viewHolder.testCond.setText(testCondString);
        } else {
            String testCondString = "正确比例：" + curTitleInfo.RightNum + "/"
                    + curTitleInfo.QuesNum + "-时长:"
                    + transTime(time);
            viewHolder.testCond.setText(testCondString);
        }


        //每个Item项之间的背景色不同
        if (arg0 % 2 == 0) {
            viewHolder.relativeLayout.setBackgroundDrawable(mContext
                    .getResources()
                    .getDrawable(R.drawable.test_list_background));
        } else {
            viewHolder.relativeLayout.setBackgroundDrawable(mContext
                    .getResources().getDrawable(
                            R.drawable.test_list_background1));
        }
        return arg1;
    }

    class ViewHolder {
        public TextView testName;
        public TextView testCond;
        public RelativeLayout relativeLayout;
    }

    public String transTime(int timeInSeconds) {
        String timeString = "";
        int minute = 0;
        int second = 0;
        minute = timeInSeconds / 60;
        second = timeInSeconds % 60;
        if (minute > 0) {
            timeString = minute + "分" + second + "秒";
        } else {
            timeString = second + "秒";
        }
        return timeString;
    }

}
