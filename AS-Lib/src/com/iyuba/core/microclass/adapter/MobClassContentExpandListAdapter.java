package com.iyuba.core.microclass.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.util.ZipUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.downloadprovider.jlpt1.downloads.DownloadInfoSimp;
import com.iyuba.core.downloadprovider.jlpt1.downloads.DownloadManagerProxy;
import com.iyuba.core.downloadprovider.jlpt1.downloads.DownloadState;
import com.iyuba.core.downloadprovider.jlpt1.downloads.providers.DownloadManager;
import com.iyuba.core.microclass.activity.MobClassBase;
import com.iyuba.core.microclass.activity.MobClassVideoBase;
import com.iyuba.core.microclass.protocol.MbTextRequest;
import com.iyuba.core.microclass.protocol.MbTextResponse;
import com.iyuba.core.microclass.protocol.PayCourseAmountRequest;
import com.iyuba.core.microclass.protocol.PayCourseAmountResponse;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.DownloadFile;
import com.iyuba.core.microclass.sqlite.mode.MbText;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.microclass.sqlite.op.MobClassResOp;
import com.iyuba.core.R;
import com.iyuba.multithread.MultiThreadDownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipException;

public class MobClassContentExpandListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	int groupLayout = 0;
	int childLayout = 0;
	DownloadManager mDownloadManager;
	DownloadManagerProxy proxy;
	private CourseContentOp courseContentOp;
	private MobClassResOp mobClassResOp;
	private String UserAmount;
	private String UserBalanceAmount;
	private double CostedPrice = 0;    //购买课程已经花的钱
	private double curCourseCost;        //当前课程的费用
	private double packPrice;            //课程的打包价格
	private int ClassNum;                //课程的讲数
	private int PackId;                    //课程包的ID
	private String appId;
	private String ProductId = "";
	private String MobClassBuyPackUrl;
	private String fileSaveDir;            //保存文件的目录
	private String fileSaveZipDir;        //保存压缩文件的目录
	private String strFileUnPackDir;    //解压的文件夹
	private String strFileVideoDownloadDir;    //视频文件下载目录
	private String strVideoFinalDir;        //视频文件最终存放目录
	private String strDestDir;                //目标目录
	private String strTargetVideoPathName;    //目标视频目录
	private ArrayList<MbText> mbTextList = new ArrayList<MbText>();    //音频PPT对应的切的数据记录
	private ArrayList<CourseContent> mList = new ArrayList<CourseContent>();//包内所有的小讲的记录
	private ArrayList<HashMap<String, Object>> groupData = null;            //group数据
	private ArrayList<ArrayList<HashMap<String, Object>>> childData = null;//child数据
	private HashMap<String, RoundProgressBar> progresses = new HashMap<String, RoundProgressBar>(); //进度条的集合
	private ExecutorService executorService = Executors.newFixedThreadPool(3);

	public MobClassContentExpandListAdapter(Context mContext) {
		this.mContext = mContext;
		proxy = DownloadManagerProxy.getInstance(mContext);
	}

	public MobClassContentExpandListAdapter(Context mContext, ArrayList<CourseContent> list,
											ArrayList<HashMap<String, Object>> groupData, int groupLayout,
											ArrayList<ArrayList<HashMap<String, Object>>> childData,
											int childLayout) {
		super();
//		Constant.download = 1;
		this.mContext = mContext;
		this.mList = list;
		this.groupData = groupData;
		this.groupLayout = groupLayout;
		this.childData = childData;
		this.childLayout = childLayout;

		this.packPrice = MobManager.Instance().curPackPrice;
		this.ClassNum = MobManager.Instance().CourseNum;
		this.PackId = MobManager.Instance().packid;
		this.appId = MobManager.Instance().appId;

		mDownloadManager = new DownloadManager(mContext.getContentResolver(),
				mContext.getPackageName());
		proxy = DownloadManagerProxy.getInstance(mContext);
		courseContentOp = new CourseContentOp(mContext);
		mobClassResOp = new MobClassResOp(mContext);

	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * position与id一样，都是从0开始计数的，
	 * 这里返回的id也是从0开始计数的
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		int id = 0;
		for (int i = 0; i < groupPosition; i++) {
			id += childData.get(i).size();
		}
		id += childPosition;
		return id;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public void addList(ArrayList<CourseContent> courseList) {
		mList.addAll(courseList);
	}

	public void clearList() {
		mList.clear();
	}

	public void addGroupData(ArrayList<HashMap<String, Object>> gData) {
		groupData.addAll(gData);
	}

	public void clearGroupData() {
		groupData.clear();
	}

	public void addChildData(ArrayList<ArrayList<HashMap<String, Object>>> cData) {
		childData.addAll(cData);
	}

	public void clearChildData() {
		childData.clear();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		//
		GroupViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.microclass_item_group_mobclassactivity, null);
			holder = new GroupViewHolder();
			holder.firstTitleName = (TextView) convertView.findViewById(R.id.microclass_content_first_title);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		holder.firstTitleName.setText(groupData.get(groupPosition).get("groupName").toString());

		return convertView;
		/**
		 * 不要在适配器中调用适配器的内部方法，不然会出现奇怪的异常
		 *
		 */
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		/**
		 * 这里isLastChild目前没用到，如果出现异常再说
		 */
		final ChildViewHolder holder;
		final CourseContent cc;
		cc = (CourseContent) childData.get(groupPosition).get(childPosition).get("childName");
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.microclass_item_child_mobclassactivity, null);
			//感觉这里需要把root设置成ViewGroup 对象
			/**
			 * ERROR!!这里不能把null换成parent，否则会出现异常退出，原因不太确定，可能是inflate方法获得的这个item的View
			 * 并不属于某个控件组，所以使用默认值null即可
			 */
			holder = new ChildViewHolder();
			holder.titleName = (TextView) convertView.findViewById(R.id.courseCotent_title);
			holder.titleTimeLong = (TextView) convertView.findViewById(R.id.courseContent_timelong);
			holder.rlBuy = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_buy_state_pic);
			holder.rlPlay = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_play_state_pic);
			holder.rlDownload = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_download_state_pic);
			holder.rlDownloadPause = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_download_pause_pic);
			holder.ivLock = (ImageView) convertView.findViewById(R.id.courseContent_lock_pic);
			holder.ivAudio = (ImageView) convertView.findViewById(R.id.courseContent_audio_pic);
			holder.ivVideo = (ImageView) convertView.findViewById(R.id.courseContent_video_pic);
			holder.rlTitleName = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_list_image);
			holder.rlItem = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_list);

			holder.rlProgressBar = (RelativeLayout) convertView.findViewById(R.id.RL_courseContent_roundProgressBar);
			holder.progressBar = (RoundProgressBar) convertView.findViewById(R.id.courseRoundProgressBar);
			holder.progressBar.setTextSize(14);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		holder.titleName.setText(cc.titleName);
		holder.titleTimeLong.setText(FormatTimeLong(cc.totaltime));

		holder.rlBuy.setVisibility(View.VISIBLE);
		holder.ivLock.setVisibility(View.VISIBLE);
		holder.ivAudio.setVisibility(View.INVISIBLE);
		holder.ivVideo.setVisibility(View.INVISIBLE);
		holder.rlDownload.setVisibility(View.INVISIBLE);
		holder.rlDownloadPause.setVisibility(View.INVISIBLE);
		holder.rlPlay.setVisibility(View.INVISIBLE);
		holder.rlProgressBar.setVisibility(View.INVISIBLE);

		//课程免费或已经购买
		if (cc.IsFree == true
				|| cc.cost == 0.0) {
			holder.rlBuy.setVisibility(View.INVISIBLE);
			holder.ivLock.setVisibility(View.INVISIBLE);
			//下载完成
			if (getDownloadState(cc) == 1) {
				holder.rlDownloadPause.setVisibility(View.INVISIBLE);
				holder.rlDownload.setVisibility(View.INVISIBLE);
				holder.rlPlay.setVisibility(View.VISIBLE);
				holder.ivAudio.setVisibility(View.VISIBLE);
				//是否包含视频文件
				if (cc.video == 1) {
					holder.ivVideo.setVisibility(View.VISIBLE);
				} else {
					holder.ivVideo.setVisibility(View.INVISIBLE);
				}
				holder.rlProgressBar.setVisibility(View.INVISIBLE);
				holder.titleName.setTextColor(mContext.getResources().getColor(R.color.course_green));
			}
			//未下载
			else if (getDownloadState(cc) == 0) {
				holder.rlDownload.setVisibility(View.VISIBLE);
				holder.rlDownloadPause.setVisibility(View.INVISIBLE);
				holder.rlPlay.setVisibility(View.INVISIBLE);
				holder.ivAudio.setVisibility(View.INVISIBLE);
				holder.ivVideo.setVisibility(View.INVISIBLE);
				holder.rlProgressBar.setVisibility(View.INVISIBLE);
				holder.titleName.setTextColor(mContext.getResources().getColor(R.color.course_content_title));
			}
			//正在下载
			else if (getDownloadState(cc) == 2) {
				holder.rlDownload.setVisibility(View.VISIBLE);
				holder.rlDownloadPause.setVisibility(View.INVISIBLE);
				holder.rlPlay.setVisibility(View.INVISIBLE);
				holder.ivAudio.setVisibility(View.INVISIBLE);
				holder.ivVideo.setVisibility(View.INVISIBLE);
				holder.rlProgressBar.setVisibility(View.INVISIBLE);
				holder.titleName.setTextColor(mContext.getResources().getColor(R.color.course_content_title));
			}
			//还未购买课程
		} else {
			holder.rlBuy.setVisibility(View.VISIBLE);
			holder.ivLock.setVisibility(View.VISIBLE);
			holder.ivVideo.setVisibility(View.INVISIBLE);
			holder.titleName.setTextColor(mContext.getResources().getColor(R.color.course_content_title));
		}


		//判断并处理进度条的状态
		DownloadInfoSimp info = proxy.query(cc.id);
		if (info == null) {
			holder.progressBar.setProgress(0);
			holder.progressBar.setMax(100);
		} else {
			switch (info.state) {
				case DownloadState.STATUS_PENDING:
					break;
				case DownloadState.STATUS_RUNNING:
					holder.progressBar.setProgress(info.percent);
					holder.progressBar.setMax(100);
					//notify
//					handler.sendEmptyMessage(0);
					if (info.percent == 100) {
						Message msg = new Message();
						if (Constant.download == 0 && cc.video == 1) {
							//下载PPT和视频
							msg.what = 12;
						} else if (Constant.download == 2 && cc.video == 1) {
							//下载视频
							msg.what = 11;
						} else {
							//下载PPT
							msg.what = 9;
						}
						msg.obj = cc;
						handler.sendMessage(msg);
					}
					break;
				case DownloadState.STATUS_PAUSED:
					break;
				case DownloadState.STATUS_SUCCESSFUL:
					Message msg = new Message();
					if (Constant.download == 0 && cc.video == 1) {
						//下载PPT和视频
						msg.what = 12;
					} else if (Constant.download == 2 && cc.video == 1) {
						//下载视频
						msg.what = 11;
					} else {
						//下载PPT
						msg.what = 9;
					}
					msg.obj = cc;
					handler.sendMessage(msg);
					break;
				default:
					holder.progressBar.setProgress(0);
					holder.progressBar.setMax(100);
					break;
			}
		}
		holder.rlDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//缓存，同时跳转在线播放课程
				checkNetWork(cc, holder.progressBar, Constant.download);

				startOnlineActicity(cc, groupPosition, childPosition);
			}
		});

		//响应Item项被点击的事件，下载或播放
		holder.rlItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cc.IsFree == true
						|| cc.cost == 0.0) {
					// 控制下载标志是否显示，当前课程包中是否已经下载完成
					//当下载时，首先全部和视频要同时判断音频的情况，因为可能选择优先的是全部或者视频，但是并没有视频文件，只能下载音频;
					//当视频优先，但是未下载视频时，下载视频
					//1、优先下载全部，但是全部下载和音频下载的字段都为0，且当前课程有视频（下载视频）要下载；
					//2、优先下载视频，视频和音频下载的字段都为0，要下载（音频或视频，根据cc.video字段确定）
					//3、优先下载音频，音频下载字段为0，要下载（下载音频）；
					//4、优先下载视频，有视频，且视频下载字段为0，要下载（下载视频）。
					//此时，要进行下载
					if ((Constant.download == 0 && cc.IsAllDownload == 0 && cc.IsAudioDownload == 0)
							|| (Constant.download == 0 && cc.IsVideoDownload == 0 && cc.IsAllDownload == 0 && cc.video == 1)
							|| (Constant.download == 1 && cc.IsAudioDownload == 0 && cc.IsAllDownload == 0)
							|| (Constant.download == 2 && cc.IsAudioDownload == 0 && cc.IsAllDownload == 0 && cc.video == 0)
							|| (Constant.download == 2 && cc.IsVideoDownload == 0 && cc.IsAllDownload == 0 && cc.video == 1)) {
						checkNetWork(cc, holder.progressBar, Constant.download);

						startOnlineActicity(cc, groupPosition, childPosition);

						//此时直接进到播放页面
					} else if (cc.IsAllDownload == 1 || cc.IsVideoDownload == 1 || cc.IsAudioDownload == 1) {
						int id = 0;
						for (int i = 0; i < groupPosition; i++) {
							id += childData.get(groupPosition).size();
						}
						id += childPosition;
						startBaseActicity(cc, id, Constant.download);
					} else {
						startOnlineActicity(cc, groupPosition, childPosition);
					}
				}
				//执行购买
				else {
					if (NetWorkState.isConnectingToInternet()) {
						if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
							ProductId = cc.id + "";
							if (packPrice == CostedPrice) {
								handlerBuy.sendEmptyMessage(5);
							} else if (CostedPrice < packPrice) {
								curCourseCost = packPrice - CostedPrice;
								ProductId = "0";
								Message message = handlerBuy.obtainMessage(0);
								handlerBuy.sendMessage(message);
							} else {
								handlerBuy.sendEmptyMessage(7);
							}
						} else {
							Intent intent;
							intent = new Intent();
							intent.setClass(mContext, LoginActivity.class);
							mContext.startActivity(intent);
						}
					} else {
						handler.sendEmptyMessage(7);
					}

				}
			}
		});

		holder.rlPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int id = 0;
				for (int i = 0; i < groupPosition; i++) {
					id += childData.get(groupPosition).size();
				}
				id += childPosition;
				Intent intent = new Intent();
				intent.setClass(mContext, MobClassBase.class);
				intent.putExtra("titleid", cc.id + "");
				intent.putExtra("title", cc.titleName);
				intent.putExtra("position", id);
				intent.putExtra("lesson", cc.lesson);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	public class UnzipRunnable implements Runnable {

		String uFileSaveDir, uFileUnpackDir;
		CourseContent cc;
		int type;

		public UnzipRunnable(CourseContent cc, String uFileSaveDir, String uFileUnpackDir) {

			this.type = type;
			this.cc = cc;
			this.uFileSaveDir = uFileSaveDir;
			this.uFileUnpackDir = uFileUnpackDir;
		}
		final String command = "chmod 777 "+uFileSaveDir+ "\n"
				+ "exit\n";

		@Override
		public void run() {

			File upFile = new File(uFileSaveDir);
			try {
				if(upFile.exists()){
					Process su = Runtime.getRuntime().exec("/system/bin/su");
					su.getOutputStream().write(command.getBytes());
					ZipUtil.upZipFile(upFile, uFileUnpackDir);
				}
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String FormatTimeLong(String totalSeconds) {
		int intTotalSeconds;
		if (totalSeconds.equals("") || totalSeconds == null) {
			intTotalSeconds = 0;
		} else {
			intTotalSeconds = Integer.parseInt(totalSeconds);
		}
		long hours = (intTotalSeconds % (60 * 60 * 24)) / (60 * 60);
		long minutes = (intTotalSeconds % (60 * 60)) / (60);
		long seconds = (intTotalSeconds % 60);
		if (hours == 0) {
			return minutes + "'"
					+ seconds + "''";
		} else {
			return hours + "h" + minutes + "'"
					+ seconds + "''";
		}
	}

	public void checkNetWork(CourseContent cc, RoundProgressBar progressBar, int downloadType) {
		if (MultiThreadDownloadManager.DowningTaskNum() >= 0
				&& MultiThreadDownloadManager.DowningTaskNum() <= 1) {
			int isConnect = NetWorkState.getAPNType();
			if (isConnect == 0) {
				handler.sendEmptyMessage(7);
			} else {
				handler.sendEmptyMessage(6);
				progresses.put(String.valueOf(cc.id), progressBar);

				if (Constant.download == 0 && cc.video == 1) {
					//下载PPT和视频
					cc.IsAllDownload = 2;
				} else if (Constant.download == 2 && cc.video == 1) {
					//下载视频
					cc.IsVideoDownload = 2;
				} else {
					//下载PPT
					cc.IsAudioDownload = 2;
				}
				handler.sendEmptyMessage(0);
				Message msg = new Message();
				msg.what = 10;
				msg.arg1 = cc.id;
				msg.arg2 = cc.PackId;
				handler.sendMessage(msg);
				//下载地址
				String url = getZipUrl(cc);
				//保存到本地的文件夹
				String fileSaveDir = mergeAudioPath(cc.id);
				//解压缩的路径
				strFileUnPackDir = Constant.envir + "res";
				File fileUpDir = new File(strFileUnPackDir);
				if (!fileUpDir.exists()) {
					fileUpDir.mkdirs();
				}
				strFileVideoDownloadDir = Constant.envir + "res/" + cc.id;
				File fileVideoDownloadDir = new File(strFileVideoDownloadDir);
				if (!fileVideoDownloadDir.exists()) {
					fileVideoDownloadDir.mkdirs();
				}
				File file = new File(Constant.envir + "res");
				if (!file.exists()) {
					file.mkdirs();
				}
				Uri srcUri = Uri.parse(url);
				if (downloadType == 0 && cc.video == 1) {
					//下载PPT和视频
					proxy.addDownloadAll(cc.id, getZipUrl(cc), getVideoUrl(cc));
				} else if (downloadType == 2 && cc.video == 1) {
					//下载视频
					proxy.addDownload(cc.id, getVideoUrl(cc));
				} else {
					//下载PPT
					proxy.addDownload(cc.id, getZipUrl(cc));
				}
				//延迟发送Handler消息时为了让下载开始后再刷新界面，防止下载还没开始，已经刷新完了界面，而界面无法更新
				handler.sendEmptyMessageDelayed(0, 800);
			}
		} else {
			Toast.makeText(mContext, "超过当前最大下载任务数，请稍后下载！", Toast.LENGTH_SHORT).show();
		}
	}

	private int getDownloadState(CourseContent cc) {
		//优先全部下载
		if (Constant.download == 0) {
			if (cc.video == 1) {
				if (cc.IsAllDownload == 1) {
					return 1;
				} else if (cc.IsAllDownload == 2) {
					return 2;
				} else {
					return 0;
				}
			} else {
				if (cc.IsAudioDownload == 1) {
					return 1;
				} else if (cc.IsAudioDownload == 2) {
					return 2;
				} else {
					return 0;
				}
			}
			//优先下载视频
		} else if (Constant.download == 2) {
			if (cc.video == 1) {
				if (cc.IsAllDownload == 1 || cc.IsVideoDownload == 1) {
					return 1;
				} else if (cc.IsVideoDownload == 2) {
					return 2;
				} else {
					return 0;
				}
			} else {
				if (cc.IsAudioDownload == 1) {
					return 1;
				} else if (cc.IsAudioDownload == 2) {
					return 2;
				} else {
					return 0;
				}
			}
			//优先下载音频
		} else {
			if (cc.IsAllDownload == 1 || cc.IsAudioDownload == 1) {
				return 1;
			} else if (cc.IsAudioDownload == 2) {
				return 2;
			} else {
				return 0;
			}
		}
	}

	private void startOnlineActicity(CourseContent cc, int groupPosition, int childPosition) {

		int id = 0;
		for (int i = 0; i < groupPosition; i++) {
			id += childData.get(groupPosition).size();
		}
		id += childPosition;
		Intent intent = new Intent();
		intent.setClass(mContext, MobClassBase.class);
		intent.putExtra("titleid", cc.id + "");
		intent.putExtra("title", cc.titleName);
		intent.putExtra("position", id);
		intent.putExtra("lesson", cc.lesson);
		mContext.startActivity(intent);
	}

	private void startBaseActicity(CourseContent cc, int position, int priorDownload) {
		Intent intent = new Intent();
		intent.putExtra("PackId", MobManager.Instance().packid);
		intent.putExtra("ownerid", MobManager.Instance().ownerid);
		intent.putExtra("titleid", cc.id + "");
		intent.putExtra("title", cc.titleName);
		intent.putExtra("position", position);
		intent.putExtra("lesson", cc.lesson);
		intent.putExtra("hasVideo", cc.video);
		intent.putExtra("IsAllDownload", cc.IsAllDownload);
		intent.putExtra("IsAudioDownload", cc.IsAudioDownload);
		intent.putExtra("IsVideoDownload", cc.IsVideoDownload);
		if (cc.video == 1 && (priorDownload == 0 || priorDownload == 2) && isVideoFileExist(cc)) {
			intent.setClass(mContext, MobClassVideoBase.class);
		} else {
			intent.setClass(mContext, MobClassBase.class);
		}
		mContext.startActivity(intent);
	}

	private boolean isVideoFileExist(CourseContent cc) {
		String videoDir = Constant.envir + "res" + "/" + cc.id + "/" + cc.id + ".mp4";

		File videoFileLocal = new File(videoDir);
		if (videoFileLocal.exists()) {
			return true;
		} else {
			cc.IsAllDownload = 0;
			courseContentOp.setIsAllDownLoad(cc.id + "", 0);
			return false;
		}
	}

	private String getZipUrl(CourseContent cc) {
		String url;
		url = Constant.MOB_CLASS_DOWNLOAD_PATH + "package/" + cc.id + ".zip";
		return url;
	}

	private String getVideoUrl(CourseContent cc) {
		String url;
//		url = Constant.MOB_CLASS_DOWNLOAD_PATH + Constant.MicroClassReqPackId +"/"+ cc.PackId + "/"+cc.id + "/" + cc.id + ".mp4";
		url = Constant.MOB_CLASS_DOWNLOAD_PATH + MobManager.Instance().ownerid + "/" + cc.PackId + "/" + cc.id + "/" + cc.id + ".mp4";
		Log.d("getVideoUrl:", url);
		return url;
	}

	private String mergeAudioPath(int courseId) {
		return Constant.envir + "res" + "/" + courseId + ".zip";
	}

	private String mergeVideoPath(int courseId) {
		return Constant.envir + "res" + "/" + courseId + ".mp4";
	}

	private void getMbTextInfo(int titleid, int packid) {
		ExeProtocol.exe(new MbTextRequest(titleid + "", packid + ""),
				new ProtocolResponse() {
					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						Looper.prepare();
						Log.d("MbText response得到结果", "MbText的内容");
						MbTextResponse res = (MbTextResponse) bhr;
						if (res.result.equals("1")) {
							mbTextList.clear();
							mbTextList.addAll(res.mbTextList);
							Log.d("mbTextList size3:",
									mbTextList.size() + "");
							mobClassResOp.insertMbText(res.mbTextList);
						}
						Log.d("mbTextList size4:",
								mbTextList.size() + "");
						Looper.loop();
					}

					@Override
					public void error() {
						// TODO Auto-generated method stub
						Log.d("MbTextResponse", "Response error");
					}
				});
	}

	private class CopyVideoTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//这里做文件拷贝
			File videoFile = new File(params[0]);
			try {
				if (videoFile.exists()) {
					FileUtils.copyFile(videoFile, new File(params[1]));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	private class UnzipFileTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//这里做文件解压
			File videoFile = new File(params[0]);
			try {
				if (videoFile.exists()) {
					FileUtils.copyFile(videoFile, new File(params[1]));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}


	Handler handler = new Handler() {
		int max;
		int curr;
		DownloadFile file;
		RoundProgressBar tempBar;
		CourseContent tempcc;
		CopyVideoTask cvTask;
		UnzipFileTask unzipTask;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			RoundProgressBar progressBar;
			switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;
				case 1:
					progressBar = progresses.get(String.valueOf(msg.arg1));
					progressBar.setProgress((Integer) msg.obj);
					Log.d("handler 1", msg.obj.toString());
					break;
				case 2:
					progressBar = progresses.get(String.valueOf(msg.arg1));
					progressBar.setMax((Integer) msg.obj);
					Log.d("handler 2", msg.obj.toString());
					break;
				case 3:
					file = (DownloadFile) msg.obj;
					Log.d("handler 3", msg.obj.toString());
					Message message = new Message();
					if (file.downloadState.equals("start")) {
						tempBar = progresses.get(String.valueOf(file.id));
						tempBar.setCricleProgressColor(0xff87c973);
						if (file.fileSize != 0 && file.downloadSize != 0) {
							tempBar.setMax(file.fileSize);
							tempBar.setProgress(file.downloadSize);
						} else {
							tempBar.setMax(1);
							tempBar.setProgress(0);
						}
						message.what = 3;
						message.obj = file;
						handler.sendMessageDelayed(message, 1500);
					} else if (file.downloadState.equals("half_finish")) {
						tempBar = progresses.get(String.valueOf(file.id));
						tempBar.setCricleProgressColor(0xff7ea4e2);
						if (file.fileSize != 0 && file.downloadSize != 0) {
							tempBar.setMax(file.fileSize);
							tempBar.setProgress(file.downloadSize);
						} else {
							tempBar.setMax(1);
							tempBar.setProgress(0);
						}
						message.what = 3;
						message.obj = file;
						handler.sendMessageDelayed(message, 1500);
					} else if (file.downloadState.equals("finish")) {
						message.what = 4;
						message.obj = file;
						handler.sendMessage(message);
					}
					break;
				case 4:
					file = (DownloadFile) msg.obj;
					tempBar = progresses.get(String.valueOf(file.id));
					tempBar.setVisibility(View.GONE);
					handler.sendEmptyMessage(0);
					handler.obtainMessage(10, file.id, 0).sendToTarget();
					break;
				case 6:
					CustomToast.showToast(mContext,
							R.string.courseslist_downloading);
					break;
				case 7:
					CustomToast.showToast(mContext, R.string.check_network);
					break;
				case 8:
					Dialog dialog = new AlertDialog.Builder(mContext)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("提示")
							.setMessage(
									"请首先点击屏幕右上角的按钮购买课程~")
							.setPositiveButton("确定", null).create();
					dialog.show();// 如果要显示对话框，一定要加上这句
					break;
				case 9:
					tempcc = (CourseContent) msg.obj;
					fileSaveDir = mergeAudioPath(tempcc.id);

					final String command = "chmod 777 "+fileSaveDir+ "\n"
							+ "exit\n";

					strFileUnPackDir = Constant.envir + "res" + "/" + tempcc.id + "/";
					File fileUpDir = new File(strFileUnPackDir);
					if (!fileUpDir.exists()) {
						fileUpDir.mkdirs();
					}

					File resFile = new File(Constant.envir + "res");
					if (!resFile.exists()) {
						resFile.mkdirs();
					}

					File upFile = new File(fileSaveDir);
					try {
//						Process su = Runtime.getRuntime().exec("/system/bin/su");
//						su.getOutputStream().write(command.getBytes());
						if(upFile.exists()) {
							ZipUtil.upZipFile(upFile, strFileUnPackDir);
						}
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//					UnzipRunnable unzipRunnable = new UnzipRunnable(tempcc, fileSaveDir, strFileUnPackDir);
//					new Thread(unzipRunnable).start();
//					executorService.execute(unzipRunnable);

					tempcc.IsAudioDownload = 1;
					courseContentOp.setIsAudioDownLoad(tempcc.id + "", 1);
					File file = new File(fileSaveDir);
					if (file.exists()) {
						file.delete();
					}
					if(proxy != null){
						proxy.removeDownload(tempcc.id);
					}
					handler.sendEmptyMessage(0);
					break;
				case 10:
					getMbTextInfo(msg.arg1, msg.arg2);
					break;
				case 11:
					//下载视频
					tempcc = (CourseContent) msg.obj;
					fileSaveDir = mergeVideoPath(tempcc.id);
					strVideoFinalDir = Constant.envir + "res" + "/" + tempcc.id + "/";
					strTargetVideoPathName = Constant.envir + "res" + "/" + tempcc.id + "/" + tempcc.id + ".mp4";
					File videoFileDir = new File(strVideoFinalDir);
					if (!videoFileDir.exists()) {
						videoFileDir.mkdirs();
					}
					File resFile2 = new File(Constant.envir + "res");
					if (!resFile2.exists()) {
						resFile2.mkdirs();
					}
					File videoFile = new File(fileSaveDir);
					//这里做文件拷贝
					try {
						if (videoFile.exists()) {
							FileUtils.copyFile(videoFile, new File(strTargetVideoPathName));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tempcc.IsVideoDownload = 1;
					courseContentOp.setIsVideoDownLoad(tempcc.id + "", 1);
					File file2 = new File(fileSaveDir);
					if (file2.exists()) {
						file2.delete();
					}
					if(proxy != null){
						proxy.removeDownload(tempcc.id);
					}
					handler.sendEmptyMessage(0);
					break;
				case 12:
					//下载音频和视频
					tempcc = (CourseContent) msg.obj;
					fileSaveDir = mergeVideoPath(tempcc.id);
					fileSaveZipDir = mergeAudioPath(tempcc.id);
					strDestDir = Constant.envir + "res" + "/" + tempcc.id + "/";
					strTargetVideoPathName = Constant.envir + "res" + "/" + tempcc.id + "/" + tempcc.id + ".mp4";
					File destFileDir = new File(strDestDir);
					if (!destFileDir.exists()) {
						destFileDir.mkdirs();
					}
					File resFile3 = new File(Constant.envir + "res");
					if (!resFile3.exists()) {
						resFile3.mkdirs();
					}

					File upFile2 = new File(fileSaveZipDir);
					try {
						if (upFile2.exists()) {
							ZipUtil.upZipFile(upFile2, strDestDir);
						}
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//这里做文件拷贝
					try {
						unzipTask = new UnzipFileTask();
						unzipTask.execute(fileSaveDir, strDestDir);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						unzipTask.cancel(true);
					}

//					UnzipRunnable unzipRunnable2 = new UnzipRunnable(0,tempcc, fileSaveDir, strDestDir);
//					new Thread(unzipRunnable2).start();

					//视频文件直接下载到TitleID对应的文件夹，不再拷贝(这样处理不行，因为解压缩的时候还是会把TitleID文件夹中的视频文件覆盖掉)
					File videoFile2 = new File(fileSaveDir);

					//这里做文件拷贝
					try {
						cvTask = new CopyVideoTask();
						cvTask.execute(fileSaveDir, strTargetVideoPathName);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						cvTask.cancel(true);
					}

					tempcc.IsAllDownload = 1;
					courseContentOp.setIsAllDownLoad(tempcc.id + "", 1);

					File file3 = new File(fileSaveZipDir);
					if (file3.exists()) {
						file3.delete();
					}
					if(proxy != null){
						proxy.removeDownload(tempcc.id);
					}
					handler.sendEmptyMessage(0);
					break;

			}
		}
	};


	Handler handlerBuy = new Handler() {
		int curNeedCost;
		int userAmount;

		int recommendCourseId;
		CourseContent tempCourse;

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

				case 0:

					UserAmount = AccountManagerLib.Instace(mContext).userInfo.iyubi;
					Log.d("UserAmount4444444:", UserAmount);
					if (Double.parseDouble(UserAmount) >= curCourseCost
							&& curCourseCost <= packPrice
							&& curCourseCost > 0) {
						handlerBuy.sendEmptyMessage(1);
					} else if (Double.parseDouble(UserAmount) < curCourseCost) {
						Message msg2 = new Message();
						msg2.arg1 = (int) curCourseCost;
						msg2.arg2 = Integer.parseInt(UserAmount);
						msg2.what = 3;
						handlerBuy.sendMessage(msg2);
					} else {
						handlerBuy.sendEmptyMessage(7);
					}
					break;
				case 1:
					Dialog dialog = new AlertDialog.Builder(mContext)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("提示")
							.setMessage(
									"您需要支付" + curCourseCost + "爱语币购买全部" + ClassNum + "节课程，确定要购买么")
							.setPositiveButton("购买",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int whichButton) {
											handlerBuy.sendEmptyMessage(2);
										}
									}).setNeutralButton("取消", null).create();
					dialog.show();// 如果要显示对话框，一定要加上这句
					break;
				case 2:

					String usrId;
					if (AccountManagerLib.Instace(mContext).userId == null) {
						usrId = "0";
					} else {
						usrId = AccountManagerLib.Instace(mContext).userId;
					}
					MobClassBuyPackUrl = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/payClassApi.jsp?userId="
							+ usrId
							+ "&amount="
							+ (int) curCourseCost
							+ "&appId="
							+ appId
							+ "&productId="
							+ ProductId
							+ "&packageId="
							+ PackId
							+ "&sign="
							+ MD5.getMD5ofStr((int) curCourseCost + appId + usrId
							+ PackId + ProductId + "iyuba");
					Log.d("MobClassBuyPackUrl:", MobClassBuyPackUrl);
					ExeProtocol.exe(new PayCourseAmountRequest(MobClassBuyPackUrl),
							new ProtocolResponse() {
								@Override
								public void finish(BaseHttpResponse bhr) {
									// TODO Auto-generated method stub
									Looper.prepare();
									Log.d("PayCourseAmountResp", "PayCourseAmountResponse的内容");
									PayCourseAmountResponse res = (PayCourseAmountResponse) bhr;
									if (res.result.equals("1")) {
										Log.d("PayCourseAmountResp", "结果为1");
										UserBalanceAmount = res.amount;
										Log.d("UserAmountBalance55555:", UserBalanceAmount);
										handlerBuy.sendEmptyMessage(6);
										handlerBuy.sendEmptyMessage(4);
										if (ProductId.equals("0")) {
											courseContentOp.setIsFreeForPack(PackId + "", true);
										} else {
											courseContentOp.setIsFree(ProductId, true);
										}
										handlerBuy.sendEmptyMessage(8);
									} else {
										handlerBuy.sendEmptyMessage(7);
									}
									Looper.loop();
								}

								@Override
								public void error() {
									// TODO Auto-generated method stub
									Log.d("PayCourseAmountResponse",
											"Response error");
								}
							});
					break;
				case 3:
					curNeedCost = msg.arg1;
					userAmount = msg.arg2;
					Dialog dialog1 = new AlertDialog.Builder(mContext)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("提示")
							.setMessage("当前购买需要支付" + curNeedCost + "爱语币,您的余额为" + userAmount + ",不足以支付，是否充值？")
							.setPositiveButton("充值",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int whichButton) {
											Intent intent = new Intent();
											intent.setClass(mContext, Web.class);
											intent.putExtra(
													"url",
													"http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/wap/index.jsp?uid="
															+ AccountManagerLib
															.Instace(mContext).userId);
											intent.putExtra("title", "购买爱语币");
											mContext.startActivity(intent);
										}
									}).setNeutralButton("取消", null).create();
					dialog1.show();// 如果要显示对话框，一定要加上这句
					break;
				case 4:
					Toast.makeText(mContext, "支付成功，您现在的余额是：" + UserBalanceAmount,
							Toast.LENGTH_SHORT).show();
					break;
				case 5:
					Toast.makeText(mContext, "您已购买本课程", Toast.LENGTH_SHORT).show();
				case 6:
					break;
				case 7:
					Toast.makeText(mContext, "购买错误，请稍后再试！", Toast.LENGTH_SHORT)
							.show();
				case 8:
					for (int i = 0; i < getGroupCount(); i++) {
						for (int j = 0; j < getChildrenCount(i); j++) {
							tempCourse = (CourseContent) (childData.get(i).get(j).get("childName"));
							tempCourse.IsFree = true;

							childData.get(i).get(j).remove("childName");
							childData.get(i).get(j).put("childName", tempCourse);

						}
					}
					notifyDataSetChanged();
					break;
				case 9:
					break;

				default:
					break;
			}
		}
	};

	Handler handlerProxy = new Handler() {
		int max;
		int curr;
		DownloadInfoSimp file;
		RoundProgressBar tempBar;
		CourseContent tempcc;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			RoundProgressBar progressBar;

			switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;
				case 1:
					progressBar = progresses.get(String.valueOf(msg.arg1));
					progressBar.setProgress((Integer) msg.obj);

					Log.d("handler 1", msg.obj.toString());
					break;
				case 2:
					progressBar = progresses.get(String.valueOf(msg.arg1));
					progressBar.setMax((Integer) msg.obj);

					Log.d("handler 2", msg.obj.toString());
					break;
				case 3:
					break;
				case 4:
					break;
			}
		}

	};

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * ChildViewHolder内部类
	 **/
	class ChildViewHolder {
		TextView titleName;
		TextView titleTimeLong;
		RelativeLayout rlBuy;
		RelativeLayout rlPlay;
		RelativeLayout rlDownload;
		RelativeLayout rlDownloadPause;
		ImageView ivLock;
		ImageView ivAudio;
		ImageView ivVideo;
		RelativeLayout rlTitleName;
		RelativeLayout rlItem;

		RelativeLayout rlProgressBar;
		RoundProgressBar progressBar;
	}

	/**
	 * GroupViewHolder内部类
	 **/
	class GroupViewHolder {
		TextView firstTitleName = null;
	}

}
