package com.iyuba.core.microclass.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.microclass.sqlite.mode.DelCourseDataInfo;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.R;

public class DeleCourseDataListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DelCourseDataInfo> mList = new ArrayList<DelCourseDataInfo>();
    private ArrayList<EditCond> ediCondList;
    private ArrayList<File> filesList = new ArrayList<File>();
    private CourseContentOp courseContentOp;

    int state = 1;

    public DeleCourseDataListAdapter(Context context,
                                     ArrayList<DelCourseDataInfo> List,
                                     ArrayList<File> filesList) {
        this.mContext = context;
        this.mList = List;
        this.filesList = filesList;

        courseContentOp = new CourseContentOp(mContext);

        iniEdiCondList();
    }

    public void iniEdiCondList() {
        ediCondList = new ArrayList<EditCond>();
        for (int i = 0; i < mList.size(); i++) {
            EditCond editCond = new EditCond();
            editCond.switchState = 0;
            ediCondList.add(editCond);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        // return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        // return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        // return 0;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final DelCourseDataInfo tmp = mList.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater viInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = viInflater.inflate(R.layout.microclass_item_delete_course_data, null);

            viewHolder = new ViewHolder();
            viewHolder.tvCourseName = (TextView) convertView.findViewById(R.id.delCourseName);
            viewHolder.tvDataSize = (TextView) convertView.findViewById(R.id.delCourseDataSize);
            viewHolder.ivDelCourse = (ImageView) convertView.findViewById(R.id.iv_delcourse_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvCourseName.setText(tmp.CourseName);
        viewHolder.tvDataSize.setText(tmp.strSize);

        if (position < ediCondList.size() && ediCondList.get(position).switchState == 0) {
            viewHolder.ivDelCourse.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.ivDelCourse.setVisibility(View.VISIBLE);
            if (position < ediCondList.size() && ediCondList.get(position).switchState == 1) {
                viewHolder.ivDelCourse.setImageResource(R.drawable.delete_course_normal);
            } else {
                viewHolder.ivDelCourse.setImageResource(R.drawable.delete_course_selected);
            }
        }

        return convertView;
    }

    public void updateView(int position) {
        if (ediCondList.get(position).switchState == 1) {
            ediCondList.get(position).switchState = 2;
        } else if (ediCondList.get(position).switchState == 2) {
            ediCondList.get(position).switchState = 1;
        }
        notifyDataSetChanged();
    }

    public void updateData() {
        // 利用Itreator进行操作，保障数据的安全，若根据位置变化进行操作，则要同时改变数据位置的偏移
        Iterator<File> filesIterator = filesList.iterator();
        Iterator<EditCond> editCondIterator = ediCondList.iterator();
        Iterator<DelCourseDataInfo> delCourseInfoIteratortor = mList.iterator();
        while (editCondIterator.hasNext()) {
            EditCond editCond = editCondIterator.next();
            File file = (File) filesIterator.next();
            DelCourseDataInfo dcdInfo = delCourseInfoIteratortor.next();
            if (editCond.switchState == 2) {

                delCourseInfoIteratortor.remove();
                editCondIterator.remove();
                filesIterator.remove();
                deleteFile(file);

                file.delete();// 删除文件本身

                Log.d("updateData file name:", file.getName());
                //文件名和TitleID相同，数据库操作

                courseContentOp.updateIsDownloadById(0, dcdInfo.CourseId);

//				boolean isNum = file.getName().matches("[0-9]+"); 
//				
//				try {
//					//如果文件名是该课程对应的文件夹（纯数字）
//					if(file.getName().matches("[0-9]+")){
//						courseContentOp.updateIsDownloadById(false,Integer.parseInt(file.getName()));
//					}else{
//						//否则截掉.mp4的后缀名
//						String tempStr = file.getName().substring(0, file.getName().length()-4);
//						courseContentOp.updateIsDownloadById(false,Integer.parseInt(tempStr));
//					}
//					
//				} catch (NumberFormatException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            }
        }
    }

    Handler delFileHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0:
                    DeleteFilesTask dfTask = new DeleteFilesTask();
                    dfTask.execute();
                    break;

                default:
                    break;
            }
        }

    };

    private class DeleteFilesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            updateData();
            return null;
        }

    }

    /**
     * 删除目录下的所有文件
     *
     * @param file
     */
    public void deleteFile(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
    }

    /**
     * 设置收藏文章是否可编辑的状态,并更新删除的状态
     *
     * @param state
     */
    public void setState(int state) {
        this.state = state;
        if (state == 0) {
            for (int i = 0; i < ediCondList.size(); i++) {
                ediCondList.get(i).switchState = 1;// 变为选择的界面
            }
        } else if (state == 1) {
            // 删除list中选定要删除的数据
            updateData();
//			DeleteFilesTask dfTask = new DeleteFilesTask();
//			dfTask.execute();
//			delFileHandler.sendEmptyMessage(0);
            for (int i = 0; i < ediCondList.size(); i++) {
                ediCondList.get(i).switchState = 0;
            }
        }
        notifyDataSetChanged();
    }

    public int getState() {
        return state;
    }

    class EditCond {
        public int switchState = 0;// 0不可见，2选中，1未选中
    }

    public class ViewHolder {

        TextView tvCourseName;
        TextView tvDataSize;
        ImageView ivDelCourse;
    }

}
