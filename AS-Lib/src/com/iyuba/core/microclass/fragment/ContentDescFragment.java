package com.iyuba.core.microclass.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;
import com.iyuba.core.microclass.protocol.ContentListRequest;
import com.iyuba.core.microclass.protocol.ContentListResponse;
import com.iyuba.core.microclass.protocol.ViewCountPackRequest;
import com.iyuba.core.microclass.protocol.ViewCountPackResponse;
import com.iyuba.core.microclass.sqlite.mode.CoursePackDescInfo;
import com.iyuba.core.microclass.sqlite.mode.TeacherInfo;
import com.iyuba.core.microclass.sqlite.op.CoursePackDescInfoOp;
import com.iyuba.core.microclass.sqlite.op.TeacherInfoOp;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContentDescFragment extends Fragment{

	private Context mContext;
	private View root;
	private int PackId;
//	private ProgressDialog wettingDialog;
	
	private TextView descContent,descTeacher,nameTeacher,descStudent;
  	private CircularImageView teacherImageView;
  	private int teacherId,viewCount,recommendId;
  	private ArrayList<CoursePackDescInfo> cpdiList = new ArrayList<CoursePackDescInfo>();
	private CoursePackDescInfo curCoursePackDescInfo = new CoursePackDescInfo();
	private TeacherInfo ti = new TeacherInfo();
	private CoursePackDescInfo firCoursePackDescInfo = new CoursePackDescInfo();
	private TeacherInfo firTi = new TeacherInfo();
	private CoursePackDescInfoOp cpdInfoOp;
	private TeacherInfoOp teacherInfoOp;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		root = inflater.inflate(R.layout.lib_microclass_mobclass_coursedesc, container,false);
		initWidget();
		handlerRequest.sendEmptyMessage(0);
		setInfo();
		return root;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	private void initWidget(){
//		wettingDialog = new ProgressDialog(mContext);
		PackId = MobManager.Instance().packid;
		cpdInfoOp = new CoursePackDescInfoOp(mContext);
		teacherInfoOp = new TeacherInfoOp(mContext);
		descContent = (TextView) root.findViewById(R.id.tv_courseContent_desc_content);
		descTeacher = (TextView) root.findViewById(R.id.tv_courseTeacher_desc);
		nameTeacher = (TextView) root.findViewById(R.id.tv_courseTeacher_name);
		teacherImageView = (CircularImageView) root.findViewById(R.id.teaher_image);
		descStudent = (TextView) root.findViewById(R.id.tv_courseStudent_desc);
		curCoursePackDescInfo = cpdInfoOp.findDataByOwnerId(PackId+"");
		
	}
	private void setInfo() {
		
		if(curCoursePackDescInfo!=null){
			descContent.setText(curCoursePackDescInfo.detail);
			descStudent.setText(curCoursePackDescInfo.condition);
			
			teacherId = curCoursePackDescInfo.tid;
			viewCount = curCoursePackDescInfo.viewCount;
			recommendId = curCoursePackDescInfo.recommendId;
			
			ti = teacherInfoOp.findDataByOwnerId(teacherId+"");
			descTeacher.setText(ti.tdes);
			nameTeacher.setText(ti.tname);
			ImageLoader.getInstance().displayImage(
					"http://static3."+com.iyuba.core.util.Constant.IYBHttpHead+"/resource/teacher/"
							+ ti.timg, teacherImageView);
		}else{
			descContent.setText(firCoursePackDescInfo.detail);
			descStudent.setText(firCoursePackDescInfo.condition);
			
			teacherId = firCoursePackDescInfo.tid;
			viewCount = firCoursePackDescInfo.viewCount;
			recommendId = firCoursePackDescInfo.recommendId;
			
			ti = teacherInfoOp.findDataByOwnerId(teacherId+"");
			descTeacher.setText(firTi.tdes);
			nameTeacher.setText(firTi.tname);
			ImageLoader.getInstance().displayImage(
					"http://static3."+com.iyuba.core.util.Constant.IYBHttpHead+"/resource/teacher/"
							+ firTi.timg, teacherImageView);
		}
		
	}
	Handler handlerRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				handlerRequest.sendEmptyMessage(1);
				break;
			case 1:
				try {
					handler.sendEmptyMessage(0);
					ExeProtocol.exe(new ContentListRequest(
							MobManager.Instance().packid + "", "2"),
							new ProtocolResponse() {
								@Override
								public void finish(BaseHttpResponse bhr) {
									// TODO Auto-generated method stub
									Looper.prepare();
									ContentListResponse res = (ContentListResponse) bhr;
									if (res.result.equals("1")) {
										try {
											Message msgvc = new Message();
											msgvc.arg1 = res.cpdi.viewCount;
											msgvc.what = 4;
											handlerRequest.sendMessage(msgvc);
											Message msgdesc = new Message();
											msgdesc.what = 5;
											handlerRequest.sendMessage(msgdesc);
											firCoursePackDescInfo = res.cpdi;
											firTi = res.teacherInfo;
											handlerRequest.sendEmptyMessage(7);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										handlerRequest.sendEmptyMessage(2);
										handler.sendEmptyMessage(1);
										handler.sendEmptyMessage(7);
										handler.sendEmptyMessage(3);
									}
									Looper.loop();
								}
								@Override
								public void error() {
									// TODO Auto-generated method stub
									Log.d("ContentListResponse","Response error");
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				setInfo();
				break;
			//增加包浏览量
			case 6:
				try {
					ExeProtocol.exe(new ViewCountPackRequest(String.valueOf(PackId)), 
							new ProtocolResponse(){
							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								Looper.prepare();
								ViewCountPackResponse res = (ViewCountPackResponse) bhr;
								if (res.ResultCode.equals("1")) {
									Toast.makeText(mContext, "获取浏览量正确！",Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(mContext, "获取浏览量出错！",Toast.LENGTH_SHORT).show();
								}
								Looper.loop();
							}
							@Override
							public void error() {
								// TODO Auto-generated method stub
							}
						});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 7:
				break;
			}
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
//				wettingDialog.show();
				break;
			case 1:
//				wettingDialog.dismiss();
				break;
			case 2:
				Toast.makeText(mContext, R.string.play_check_network, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			}
		}
	};
}
