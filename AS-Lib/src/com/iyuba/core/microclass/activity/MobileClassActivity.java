package com.iyuba.core.microclass.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.adapter.MainPagerAdapter;
import com.iyuba.core.common.adapter.ViewPagerAdapter;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;
import com.iyuba.core.microclass.adapter.MobClassCommentAdapter;
import com.iyuba.core.microclass.fragment.ContentCommentFragment;
import com.iyuba.core.microclass.fragment.ContentDescFragment;
import com.iyuba.core.microclass.fragment.ContentListFragment;
import com.iyuba.core.microclass.protocol.ContentListRequest;
import com.iyuba.core.microclass.protocol.ContentListResponse;
import com.iyuba.core.microclass.protocol.ScroingStarRequest;
import com.iyuba.core.microclass.protocol.ScroingStarResponse;
import com.iyuba.core.microclass.protocol.ViewCountPackRequest;
import com.iyuba.core.microclass.protocol.ViewCountPackResponse;
import com.iyuba.core.microclass.sqlite.mode.Comment;
import com.iyuba.core.microclass.sqlite.mode.CoursePackDescInfo;
import com.iyuba.core.microclass.sqlite.mode.TeacherInfo;
import com.iyuba.core.microclass.sqlite.op.CoursePackDescInfoOp;
import com.iyuba.core.microclass.sqlite.op.TeacherInfoOp;
import com.iyuba.core.R;
import com.iyuba.multithread.MultiThreadDownloadManager;

public class MobileClassActivity extends BasisActivity {

	private Context mContext;
	private Button backBtn;
	private RelativeLayout rlBack;
	private RelativeLayout rlCourseContentVideo;
	private RelativeLayout rlQQConsultation;
//	private ProgressDialog wettingDialog;

	private Button normalproblemBtn;
	private Button consultationBtn;
	private String backgroundUrl;
	private Bitmap bgBitmap;
	private Drawable bgDrawable; 
	private String QQSupport = "645051644";
	
	private ContextMenu contextMenu;
	private TextView listButton, descButton, commentButton;
	private View listView, descView,commentView;
	private View commentFooter;
	
	private TextView tvPackName;
	private TextView viewCounts;
	private TextView totalRating;
	private RatingBar ratingBarCourse;
	
	// 通用变量
	private int PackId;
	private int ownerid;
	private String appId;
	private int ClassNum;
	private double packPrice;
	private String desc;
	private String lesson;
	private String strPackName;
    private int stars;
    private int userCounts;
	private ArrayList<View> mList;
	private int currentPage = 1;
	private boolean isConnected;
	private boolean isvip;
	private Player mPlayer;
	private BackPlayer vv = null;
	private boolean isUpload = false;
	private String MobClassBuyPackUrl;
	
	private ViewPager container = null;
	private ViewPagerAdapter viewPagerAdapter;
	private LayoutInflater inflater;
	
    //Desc中用到的View和成员变量
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
	
    //Comment中用到的View和成员变量
    private ListView commentList;
    private MobClassCommentAdapter commentAdapter;
    private ArrayList<Comment> commentsList = new ArrayList<Comment>();
    private int curCommentPage = 1;
 	private Button expressButton;
 	private EditText expressEditText;
 	private String expressWord;
 	private View commentLoadMoreLayout;
 	private ListView listComment;
 	private boolean commentAll = false;
 	private boolean commentExist = false;
 	private TextView commentLoadMoreTextView;
 	private int commentMode = 0;// 0是文字评论，1是语音评论
 	private Drawable amps[] = new Drawable[8];
 	private Button btnScroing;
 	private RatingBar rb_curCourse;
    private float userScroing = -1f;
    private BigDecimal bd;  
    private float scroingStars = 4.0f;
    private float tempScroingStars;
    
    //使用Fragment添加测试
	private List<Fragment> fragments;
	private MainPagerAdapter mPagerAdapter;
	private FragmentManager fm;
    private ContentListFragment contentListFragment;
    private ContentDescFragment contentDescFragment;
    private ContentCommentFragment contentCommentFragment;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lib_microclass_mobclass_new_activity);

		mContext = this;
		isConnected = NetWorkState.isConnectingToInternet();
		
		PackId = MobManager.Instance().packid;
		ownerid = MobManager.Instance().ownerid;
		strPackName = getIntent().getStringExtra("packname");
		ClassNum = getIntent().getIntExtra("coursenum", 1);
		
		findView();
		//请求界面的背景图片
		handler.sendEmptyMessage(4);
		
		setView();
		
		desc = MobManager.Instance().desc;
		packPrice = MobManager.Instance().curPackPrice;
		appId = MobManager.Instance().appId;
		
		if (NetWorkState.isConnectingToInternet()) {// 开始刷新
			//请求列表的购买记录和列表数据
			handlerRequest.sendEmptyMessage(1);
			//增加浏览量(+1)
			handlerRequest.sendEmptyMessage(6);
		} else {// 刷新失败
			//提示无网络
			handler.sendEmptyMessage(1);
			handler.sendEmptyMessage(2);
		}
		
		//测试添加Fragment部分
		fragments = new ArrayList<Fragment>();
		contentListFragment = new ContentListFragment();
		contentDescFragment = new ContentDescFragment();
		contentCommentFragment = new ContentCommentFragment();
		fragments.add(contentListFragment);
		fragments.add(contentDescFragment);
		fragments.add(contentCommentFragment);

		fm = getSupportFragmentManager();
		mPagerAdapter = new MainPagerAdapter(fm, fragments);
		container.setAdapter(mPagerAdapter);
		container.setOffscreenPageLimit(1);
		container.setCurrentItem(0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		if (NetWorkState.isConnectingToInternet()) {// 开始刷新
			handlerRequest.sendEmptyMessage(0);
		} else {// 刷新失败
			handler.sendEmptyMessage(1);
			handler.sendEmptyMessage(2);
		}
		super.onResume();

	}

	protected void onDestroy() {
		super.onDestroy();
		if(MultiThreadDownloadManager.IsDowning()){
			MultiThreadDownloadManager.stopDownloads();
		}
		
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void findView(){
//		wettingDialog = new ProgressDialog(mContext);
		cpdInfoOp = new CoursePackDescInfoOp(mContext);
		teacherInfoOp = new TeacherInfoOp(mContext);
		
		backgroundUrl = Constant.MOB_CLASS_PACK_BGPIC+PackId+".png";
		
		rlBack = (RelativeLayout) findViewById(R.id.RL_button_contentlist_back);
		rlCourseContentVideo = (RelativeLayout) findViewById(R.id.course_content_videowindow);
		backBtn = (Button) findViewById(R.id.button_contentlist_back);
		rlQQConsultation = (RelativeLayout) findViewById(R.id.rl_qq_consultation);
		normalproblemBtn = (Button) findViewById(R.id.btn_normal_problem);
		consultationBtn = (Button) findViewById(R.id.btn_qq_consultation);
		tvPackName = (TextView) findViewById(R.id.tv_packname);
		viewCounts = (TextView) findViewById(R.id.tv_viewcounts);
		totalRating = (TextView) findViewById(R.id.tv_rating);
		ratingBarCourse = (RatingBar) findViewById(R.id.rb_course);
		contextMenu = (ContextMenu) findViewById(R.id.context_menu);
		container = (ViewPager) findViewById(R.id.mainBody);
		inflater = getLayoutInflater();
		
		listView = inflater.inflate(R.layout.microclass_mobclass_contentlist, null);
		descView = inflater.inflate(R.layout.lib_microclass_mobclass_coursedesc, null);
		commentView = inflater.inflate(R.layout.microclass_mobclass_contentcomment, null);
		commentFooter = inflater.inflate(R.layout.microclass_mobclass_contentcomment_footer, null);
		
		mList = new ArrayList<View>();
//		mList.add(listView);
//		mList.add(descView);
//		mList.add(commentView);
		// 初始化头部
		listButton = (TextView) findViewById(R.id.course_button_list);
		descButton = (TextView) findViewById(R.id.course_button_desc);
		commentButton = (TextView) findViewById(R.id.course_button_comment);
		
	}
	
	public void setView(){
		listButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				container.setCurrentItem(0);
			}
		});
		descButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				container.setCurrentItem(1);
			}
		});
		commentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				container.setCurrentItem(2);
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		consultationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url;
				if(curCoursePackDescInfo!=null && curCoursePackDescInfo.qq != ""){
					url="mqqwpa://im/chat?chat_type=wpa&uin="+curCoursePackDescInfo.qq+"&version=1";  
				}else if(firCoursePackDescInfo!=null&&firCoursePackDescInfo.qq != ""){
					url="mqqwpa://im/chat?chat_type=wpa&uin="+firCoursePackDescInfo.qq+"&version=1";  
				}
				else{
					url="mqqwpa://im/chat?chat_type=wpa&uin="+QQSupport+"&version=1";  
				}
				
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				} catch (ActivityNotFoundException e) {
					// TODO Auto-generated catch block
					Toast.makeText(mContext, "您的设备尚未安装QQ客户端", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} 
			}
		});
		normalproblemBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext,
						Web.class);
				intent.putExtra(
						"url",
						"http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/cq.jsp");
				intent.putExtra("title", "常见问题");
				startActivity(intent);
			}
		});
		
		curCoursePackDescInfo = cpdInfoOp.findDataByOwnerId(PackId+"");
		tvPackName.setText(strPackName);
		viewPagerAdapter = new ViewPagerAdapter(mList);
		container.setAdapter(viewPagerAdapter);
		container.setCurrentItem(0);
		currentPage = container.getCurrentItem();
		setBackGround(currentPage);
		container.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				currentPage = container.getCurrentItem();
				setBackGround(currentPage);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				if (arg0 == 2) {
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
	}
	
	private void setBackGround(int item) {
		listButton.setBackgroundResource(0);
		listButton.setTextColor(0xffd7d7d7);
		descButton.setBackgroundResource(0);
		descButton.setTextColor(0xffd7d7d7);
		commentButton.setBackgroundResource(0);
		commentButton.setTextColor(0xffd7d7d7);
		switch (item) {
		case 0:
			listButton.setTextColor(0xFF22b289);
			break;
		case 1:
			descButton.setTextColor(0xFF22b289);
			break;
		case 2:
			commentButton.setTextColor(0xFF22b289);
			break;
		}
	}
	
	
	Handler handlerRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				//请求Activity中和课程简介中用到的信息
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
										try {
											Message msgvc = new Message();
											msgvc.arg1 = res.cpdi.viewCount;
											msgvc.what = 4;
											handlerRequest.sendMessage(msgvc);
											
											firCoursePackDescInfo = res.cpdi;
											firTi = res.teacherInfo;
											
											teacherInfoOp.insertTeachers(res.teacherInfo);
											cpdInfoOp.insertCoursePackDescs(res.cpdi);
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
									Log.d("ContentListResponse", "Response error");
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
			case 2:
				ExeProtocol.exe(new ScroingStarRequest(String.valueOf(PackId),"1"), 
						new ProtocolResponse(){

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							Looper.prepare();

							ScroingStarResponse res = (ScroingStarResponse) bhr;

							if (res.resultCode.equals("511")) {
								stars = res.starCounts;
								userCounts = res.counts;
								tempScroingStars = (float)stars/userCounts;
//								scroingStars = (float)stars/userCounts;
								scroingStars =  (float)(Math.round(tempScroingStars*10))/10;
//								bd = new BigDecimal(tempScroingStars); 
//								scroingStars = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
								Log.e("starts:", stars+"");
								Log.e("userCounts:", userCounts+"");
								Log.e("scroingStars:", scroingStars+"");
								
								handlerRequest.sendEmptyMessage(3);
								handler.sendEmptyMessage(1);
								
							}
							Looper.loop();
						}

						@Override
						public void error() {
							// TODO Auto-generated method stub
							
						}
						
					});
				break;
			case 3:
				try {
					totalRating.setText(scroingStars+"分");
					ratingBarCourse.setRating(scroingStars/2);
//					viewCounts.setText(curCoursePackDescInfo.viewCount+"");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case 4:
				try {
					
//					tvPackName.setText();
					viewCounts.setText(msg.arg1+"");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			case 5:
				try {
//					setInfo();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
									Log.w("MobileClassActivity:", "ViewCount结果为1");
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
			case 4:
				new Thread(runnableClassBgImage).start();
				break;
			case 5:
				rlCourseContentVideo.setBackgroundDrawable(bgDrawable);
				break;
			case 6:
				rlCourseContentVideo.setBackgroundDrawable(getResources().getDrawable(R.drawable.coursevideowindow));
				break;
			case 7:
				break;
			}
		}
	};
	
	private Handler handler_scroingstar = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
//				new Thread(new scroingStarThread()).start();
				ExeProtocol.exe(new ScroingStarRequest(String.valueOf(PackId),"1"), 
						new ProtocolResponse(){

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							Looper.prepare();

							ScroingStarResponse res = (ScroingStarResponse) bhr;

							if (res.resultCode.equals("511")) {
								stars = res.starCounts;
								userCounts = res.counts;
								scroingStars = (float)stars/userCounts;
								Log.e("starts:", stars+"");
								Log.e("userCounts:", userCounts+"");
								Log.e("scroingStars:", scroingStars+"");
							}
							Looper.loop();
						}

						@Override
						public void error() {
							// TODO Auto-generated method stub
							
						}
						
					});
				break;
			default:
				break;
			}
		}
	};
	private class scroingStarThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ExeProtocol.exe(new ScroingStarRequest(String.valueOf(PackId),"1"), 
				new ProtocolResponse(){

				@Override
				public void finish(BaseHttpResponse bhr) {
					// TODO Auto-generated method stub
					Looper.prepare();

					ScroingStarResponse res = (ScroingStarResponse) bhr;

					if (res.resultCode.equals("511")) {
						stars = res.starCounts;
						Log.d("starts:", stars+"");
					}
					Looper.loop();
				}

				@Override
				public void error() {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	Runnable runnableClassBgImage = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			Bitmap bitmap = null;
			InputStream in = null;
			BufferedOutputStream out = null;
			try{
				in = new BufferedInputStream(new URL(backgroundUrl).openStream(), Constant.IO_BUFFER_SIZE);
				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, Constant.IO_BUFFER_SIZE);
				copy(in, out);
				out.flush();
				byte[] data = dataStream.toByteArray();
				bgBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				data = null;
				if(bgBitmap!=null){
					bgDrawable = new BitmapDrawable(bgBitmap);
				}else{
					bgDrawable = getResources().getDrawable(R.drawable.coursevideowindow);
				}
				handler.sendEmptyMessage(5);
				
			}
			catch (IOException e){
				e.printStackTrace();
				handler.sendEmptyMessage(6);
			}
		}
	};
	/**
	 * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
	 * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
	 * B.本地路径:url="file://mnt/sdcard/photo/image.png";
	 * C.支持的图片格式 ,png, jpg,bmp,gif等等
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap GetLocalOrNetBitmap(String url){
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try{
			in = new BufferedInputStream(new URL(url).openStream(), Constant.IO_BUFFER_SIZE);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, Constant.IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			return bitmap;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}
	private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[Constant.IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
}
