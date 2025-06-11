package com.iyuba.core.microclass.fragment;

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
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.DataManager;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshViewHeader;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshViewHeader.OnHeaderRefreshListener;
import com.iyuba.core.microclass.adapter.MobClassContentExpandListAdapter;
import com.iyuba.core.microclass.protocol.ContentListRequest;
import com.iyuba.core.microclass.protocol.ContentListResponse;
import com.iyuba.core.microclass.protocol.GetPayedCourseInfoRequest;
import com.iyuba.core.microclass.protocol.GetPayedCourseInfoResponse;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.FirstTitleInfo;
import com.iyuba.core.microclass.sqlite.mode.MbText;
import com.iyuba.core.microclass.sqlite.mode.PayedCourseRecord;
import com.iyuba.core.microclass.sqlite.mode.SecondTitleInfo;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.microclass.sqlite.op.MobClassResOp;
import com.iyuba.core.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ContentListFragment extends Fragment{

	private Context mContext;
	private View root;
	private int PackId;
	private int ClassNum = 100;
	private String appId;
	private String lesson;
	private String ProductId = "";
	private String UserAmount;
	private String UserBalanceAmount;
	private String MobClassBuyPackUrl;
	private double packPrice;
	private double CostedPrice = 0; // 购买课程已经花的钱
	private double curCourseCost;
	private CustomDialog wettingDialog;
	private MobClassResOp mobClassResOp;
	private CourseContentOp courseContentOp;
	private PullToRefreshViewHeader refreshCourseContentView;// 刷新列表
	private ExpandableListView expandableListView;
	private MobClassContentExpandListAdapter mobELAdapter;
	ArrayList<HashMap<String,Object>> groupData=null;
    ArrayList<ArrayList<HashMap<String,Object>>> childData=null; 
    private ArrayList<MbText> mbTextList = new ArrayList<MbText>();
    private ArrayList<CourseContent> courseContentList = new ArrayList<CourseContent>();
	private ArrayList<PayedCourseRecord> payedCourseRecord = new ArrayList<PayedCourseRecord>();
	private ArrayList<CourseContent> secReqCourseContentList = new ArrayList<CourseContent>();
	private ArrayList<FirstTitleInfo> courseContentFirTitle = new ArrayList<FirstTitleInfo>();
	private ArrayList<SecondTitleInfo> courseContentSecTitle = new ArrayList<SecondTitleInfo>();
	private ArrayList<CourseContent> btCourseContentList = new ArrayList<CourseContent>();

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
		root = inflater.inflate(R.layout.microclass_mobclass_contentlist, container,false);
		handlerRequest.sendEmptyMessage(0);
		initWidget();
		initData();
		setView();
		
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
		handlerRequest.sendEmptyMessage(0);
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
		PackId = MobManager.Instance().packid;
		packPrice = MobManager.Instance().curPackPrice;
		courseContentOp = new CourseContentOp(mContext);
		mobClassResOp = new MobClassResOp(mContext);
		wettingDialog = WaittingDialog.showDialog(mContext);
		refreshCourseContentView = (PullToRefreshViewHeader) root.findViewById(R.id.ptr_course_content);
		expandableListView = (ExpandableListView) root.findViewById(R.id.el_coursecontent_list);
		groupData=new ArrayList<HashMap<String,Object>>();
        childData=new ArrayList<ArrayList<HashMap<String,Object>>> ();
	}
	
	public void initData(){
		courseContentFirTitle = courseContentOp.findCourseContentFirTitleBySpecial(PackId + "");
        for(int i = 0; i < courseContentFirTitle.size();i++){
        	btCourseContentList = courseContentOp.findCourseContentBTidBySpecial(courseContentFirTitle.get(i).btid+"");
        	for(int j = 0;j < btCourseContentList.size();j++){
        		CourseTitleInfo cti = new CourseTitleInfo(courseContentFirTitle.get(i).btname, btCourseContentList.get(j));
        		addCourseTitle(cti);
        	}
        }
	}
	
	public void setView(){
		refreshCourseContentView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshViewHeader view) {
				// TODO Auto-generated method stub
				if (NetWorkState.isConnectingToInternet()) {// 开始刷新
					secReqCourseContentList = courseContentOp.findCourseContentDataBySpecial(PackId + "");
					DataManager.Instance().downloadCourseContentList = courseContentOp.findDownloadCourseContentDataBySpecial(PackId + "");
					//清空本地的课程记录
					courseContentList.clear();
					courseContentOp.deleteCourseContentDataByPackId(PackId);
					//重新请求服务器上的课程记录
					handlerRequest.sendEmptyMessage(0);
				} else {// 刷新失败
					handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(2);
					handler.sendEmptyMessage(7);
				}
			}
		});
		mobELAdapter = new MobClassContentExpandListAdapter(mContext, courseContentList,
				groupData, R.layout.microclass_item_group_mobclassactivity, 
				childData, R.layout.microclass_item_child_mobclassactivity);
		expandableListView.setAdapter(mobELAdapter);
		//遍历所有group,将所有项设置成默认展开
		int groupCount = expandableListView.getCount(); 
		for (int i=0; i< groupCount; i++)
		 { 
			expandableListView.expandGroup(i);
		 };
		handler.sendEmptyMessage(0);
	}
	Handler handlerRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				ExeProtocol.exe(
						new GetPayedCourseInfoRequest(AccountManagerLib
								.Instace(mContext).userId,
								MobManager.Instance().appId, MobManager
										.Instance().packid + ""),
						new ProtocolResponse() {
							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								Looper.prepare();
								GetPayedCourseInfoResponse res = (GetPayedCourseInfoResponse) bhr;
								if (res.result.equals("1")) {
									Log.w("ContentListFragment:", "GetPayedCourseInfo结果为1");
									payedCourseRecord.clear();
									payedCourseRecord.addAll(res.pcrList);
									CostedPrice = 0.0;
									// 设置当前课程的是否购买的标志,循环判断所有购买记录中是否包含当前课程对应的TitleId;判断是否是打包购买的
									for (int count = 0; count < payedCourseRecord
											.size(); count++) {
										CostedPrice += Double
												.parseDouble(payedCourseRecord
														.get(count).Amount);
									}
								}else{
									handler.sendEmptyMessage(1);
									handler.sendEmptyMessage(4);
									handler.sendEmptyMessage(7);
								}

								handlerRequest.sendEmptyMessage(1);
								Looper.loop();
							}
							@Override
							public void error() {
								// TODO Auto-generated method stub
								handler.sendEmptyMessage(1);
								handler.sendEmptyMessage(4);
								handler.sendEmptyMessage(7);
								Log.w("ContentListFragment:", "GetPayedCourseInfo Error!");
							}
						});
				break;
			case 1:
				try {
					ExeProtocol.exe(new ContentListRequest(
							MobManager.Instance().packid + "", "2"),
							new ProtocolResponse() {
								@Override
								public void finish(BaseHttpResponse bhr) {
									// TODO Auto-generated method stub
									Looper.prepare();
									ContentListResponse res = (ContentListResponse) bhr;
									if (res.result.equals("1")) {
										Log.w("ContentListFragment:", "ContentList结果为1");
										try {
											Message msgvc = new Message();
											msgvc.arg1 = res.cpdi.viewCount;
											msgvc.what = 4;
											handlerRequest.sendMessage(msgvc);
											Message msgdesc = new Message();
											msgdesc.what = 5;
											handlerRequest.sendMessage(msgdesc);
											courseContentOp.insertCourseContent(res.courseList);
											handlerRequest.sendEmptyMessage(7);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										if (courseContentList.size() == 0
												|| courseContentList == null) {
											mobELAdapter.clearList();
											mobELAdapter.addList(res.courseList);
										}
										courseContentList.clear();
										courseContentList
												.addAll(res.courseList);
										// 设置当前课程的是否购买的标志,循环判断所有购买记录中是否包含当前课程对应的TitleId;判断是否是打包购买的
										for (int count = 0; count < payedCourseRecord.size(); count++) {//逐个比较购买记录的每一项
											for (int index = 0; index < courseContentList.size(); index++) {//逐个与列表的每一项比较
												//如果购买记录中的一项的ID与当前列表中的某项的ID相同(单个购买)，
												//或者购买记录中的ProductId为0(打包购买)
												if (payedCourseRecord.get(count).ProductId.equals(courseContentList.get(index).id+ "")
														|| payedCourseRecord.get(count).ProductId.equals("0")) {
													courseContentList.get(index).IsFree = true;
													courseContentOp.setIsFree(courseContentList.get(index).id+ "", true);
												}
											}
										}
										//courseContentList未被购买的记录，IsFree字段设置为FALSE
										for(int tempIndex  = 0; tempIndex < courseContentList.size(); tempIndex++){
											if(courseContentList.get(tempIndex).IsFree != true){
												courseContentOp.setIsNotFree(courseContentList.get(tempIndex).id+ "", false);
											}
										}
										handlerRequest.sendEmptyMessage(2);
										handler.sendEmptyMessage(1);
										handler.sendEmptyMessage(7);
										handler.sendEmptyMessage(3);
									}else{
										handler.sendEmptyMessage(1);
										handler.sendEmptyMessage(4);
										handler.sendEmptyMessage(7);
									}
									Looper.loop();
								}

								@Override
								public void error() {
									// TODO Auto-generated method stub
									Log.d("ContentListResponse","ContentList error");
									handler.sendEmptyMessage(1);
									handler.sendEmptyMessage(4);
									handler.sendEmptyMessage(7);
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(4);
					handler.sendEmptyMessage(7);
					Log.w("ContentListFragment:", "ContentList Error!");
				}
			break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 7:
				if(DataManager.Instance().downloadCourseContentList != null 
					&&DataManager.Instance().downloadCourseContentList.size()>0){
					for(int i=0;i<DataManager.Instance().downloadCourseContentList.size();i++){
						if (DataManager.Instance().downloadCourseContentList.get(i).IsAllDownload == 1) {
							courseContentOp.setIsAllDownLoad(
									DataManager.Instance().downloadCourseContentList.get(i).id+"", 1);
						}else if(DataManager.Instance().downloadCourseContentList.get(i).IsAudioDownload == 1){
							courseContentOp.setIsAudioDownLoad(
									DataManager.Instance().downloadCourseContentList.get(i).id+"", 1);
						}else if(DataManager.Instance().downloadCourseContentList.get(i).IsVideoDownload == 1){
							courseContentOp.setIsVideoDownLoad(
									DataManager.Instance().downloadCourseContentList.get(i).id+"", 1);
						}
					}
				}
				DataManager.Instance().downloadCourseContentList.clear();
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
				wettingDialog.show();
				break;
			case 1:
				wettingDialog.dismiss();
				break;
			case 2:
				Toast.makeText(mContext, R.string.play_check_network, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if (mobELAdapter == null) {
					mobELAdapter = new MobClassContentExpandListAdapter(mContext, courseContentList,
							groupData, R.layout.microclass_item_group_mobclassactivity, 
							childData, R.layout.microclass_item_child_mobclassactivity);
					expandableListView.setAdapter(mobELAdapter);
					//遍历所有group,将所有项设置成默认展开
					int groupCountFresh = expandableListView.getCount(); 
					for (int i=0; i< groupCountFresh; i++){ 
						expandableListView.expandGroup(i);
					 }; 
				} else {
					mobELAdapter.clearGroupData();
					mobELAdapter.clearChildData();
			        courseContentFirTitle = courseContentOp.findCourseContentFirTitleBySpecial(PackId + "");
			        for(int i = 0; i < courseContentFirTitle.size();i++){
			        	btCourseContentList = courseContentOp.findCourseContentBTidBySpecial(courseContentFirTitle.get(i).btid+"");
			        	for(int j = 0;j < btCourseContentList.size();j++){
			        		CourseTitleInfo cti = new CourseTitleInfo(courseContentFirTitle.get(i).btname, btCourseContentList.get(j));
			        		addCourseTitle(cti);
			        	}
			        }
					mobELAdapter.notifyDataSetChanged();
				}
				break;
			case 4:
				Toast.makeText(mContext,"网络状况较差,请稍后重试……",Toast.LENGTH_SHORT).show();
				break;
			case 5:
				break;
			case 7:
				refreshCourseContentView.onHeaderRefreshComplete();
			}
		}
	};
	
	public class CourseTitleInfo {
		public String courseFirName=null;
	    public CourseContent courseContent=null;
	    public CourseTitleInfo(String firName, CourseContent course) {
	        super();
	        this.courseFirName = firName;
	        this.courseContent = course;
	    }
	}
	protected void addCourseTitle(CourseTitleInfo courseTitleInfo)
    {
        int i;
        for(i=0; i< groupData.size(); i++){
            if(groupData.get(i).get("groupName").toString().equals(courseTitleInfo.courseFirName)){
                break;
            }
        }
        if(i>=groupData.size()){
            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("groupName",courseTitleInfo.courseFirName );
            map.put("childCount", 0);
            groupData.add(map);
            ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
            childData.add(list);
        }
        HashMap<String,Object> courseData=new HashMap<String,Object>();
        courseData.put("childName",courseTitleInfo.courseContent );
        childData.get(i).add(courseData);
        Integer count=(Integer)groupData.get(i).get("childCount")+1;
        groupData.get(i).put("childCount", count);    
    }
}
