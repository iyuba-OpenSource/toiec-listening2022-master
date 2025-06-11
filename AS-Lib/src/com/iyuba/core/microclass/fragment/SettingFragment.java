package com.iyuba.core.microclass.fragment;
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.FileSize;
import com.iyuba.core.downloadprovider.jlpt1.downloads.ui.DownloadList;
import com.iyuba.core.microclass.activity.About;
import com.iyuba.core.microclass.activity.DelCourseDataActivity;
import com.iyuba.core.microclass.activity.Feedback;
import com.iyuba.core.microclass.activity.HelpUse;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.R;

public class SettingFragment extends Fragment {
	private Context mContext;
//	private Button backButton;
	
	private View view;
	
	private View aboutBtn, feedbackBtn,btn_help_use, 
	btn_clear_resource, recommendButton,language,checkdownload,download;
	private TextView picSize, soundSize;
	
	private int appLanguage;
	private TextView languageText;
	
	private TextView downloadText;
	
	private CourseContentOp courseContentOp;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		if (mContext == null) {
			mContext = (Context)activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		courseContentOp = new CourseContentOp(mContext);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.layout_setting, container,false);
		
		initWidget(view);
		handler.sendEmptyMessage(5);
		
		return view;
	}
	
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		initCacheSize();
		super.onResume();
	}

	/**
	 * 
	 */
	private void initLanguage() {
		// TODO Auto-generated method stub
		
		language.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setLanguage();
			}
		});
		
		// 根据设置初始化
		appLanguage = ConfigManager.Instance().loadInt("applanguage");
		String[] languages = mContext.getResources().getStringArray(
				R.array.language);
		languageText.setText(languages[appLanguage]);
	}

	private void setLanguage() {
		String[] languages = mContext.getResources().getStringArray(
				R.array.language);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.alert_title);
		builder.setSingleChoiceItems(languages, appLanguage,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int index) {
						switch (index) {
						case 0:// 系统默认语言
							appLanguage = 0;
							break;
						case 1:// 简体中文
							appLanguage = 1;
							break;
						case 2:// 英语
							appLanguage = 2;
							break;
						case 3:// 后续
							break;
						default:
							appLanguage = 0;
							break;
						}
					}
				});
		builder.setPositiveButton(R.string.alert_btn_set,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ConfigManager.Instance().putInt("applanguage",
								appLanguage);
						Intent intent = new Intent();
						intent.setAction("changeLanguage");
						mContext.sendBroadcast(intent);
					}
				});
		builder.setNegativeButton(R.string.alert_btn_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.create().show();
	}
	
	/**
	 * 
	 */
	private void initWidget(View view) {
		// TODO Auto-generated method stub
//		backButton = (Button) view.findViewById(R.id.setting_button_back);
		soundSize = (TextView) view.findViewById(R.id.soundSize);
		
		btn_clear_resource = view.findViewById(R.id.clear_resource);
		checkdownload = view.findViewById(R.id.check_download);
		btn_help_use = view.findViewById(R.id.help_use_btn);
		aboutBtn = view.findViewById(R.id.about_btn);
		feedbackBtn = view.findViewById(R.id.feedback_btn);
		recommendButton = view.findViewById(R.id.recommend_btn);
		
		download = view.findViewById(R.id.download_option);
		downloadText = (TextView) view.findViewById(R.id.curr_download);
		downloadText.setText(mContext.getResources().getStringArray(
				R.array.download)[Constant.download]);
		
		language = view.findViewById(R.id.set_language);
		languageText = (TextView) view.findViewById(R.id.curr_language);
		
		initCacheSize();
		initListener();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
//				String strings = (String) msg.obj;
//				picSize.setText(strings);
				break;
			case 1:
				String string = (String) msg.obj;
				soundSize.setText(string);
				break;
			case 2:
//				((TextView) findViewById(R.id.sleep_state))
//						.setText(R.string.setting_sleep_state_off);
				break;
			case 3:
//				wakeupTimeTextView.setText(String.format("%02d:%02d",
//						wakeup_hour, wakeup_minute));
				break;
			case 4:
				soundSize.setText("0B");
				break;
			case 5:
				initLanguage();
				break;
			case 6:
//				initWidget();
				break;
			case 7:
//				initCheckBox();
				break;
			case 8:
//				initSleep();
//				initWakeUp();
				break;
			case 9:
//				AccountManagerLib.Instace(mContext).loginOut();
//				CustomToast.showToast(mContext,
//						R.string.setting_loginout_success);
//				SettingConfig.Instance().setHighSpeed(false);
//				checkBox_Download.setChecked(false);
//				logout.setText(R.string.no_login);
				break;
			default:
				break;
			}
		}

	};
	
	private void initCacheSize() {
		
		new Thread(new Runnable() {
			// 获取图片大小
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				String strings = getSize(0);
//				handler.obtainMessage(0, strings).sendToTarget();
//				strings = getSize(1);
				handler.obtainMessage(1, strings).sendToTarget();
			}
		}).start();
	}

	/**
	 * 
	 */
	private void initListener() {
		// TODO Auto-generated method stub
//		backButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
////				mContext.finish();
//			}
//		});
		btn_clear_resource.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent(mContext, DelCourseDataActivity.class);
				startActivity(intent);
			}
		});
		checkdownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent(mContext, DownloadList.class);
				startActivity(intent);
			}
		});
		btn_help_use.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent(mContext, HelpUse.class);
				intent.putExtra("source", "set");
				startActivity(intent);
			}
		});
		recommendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				prepareMessage();
			}
		});
		aboutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent();
				intent.setClass(mContext, About.class);
				startActivity(intent);
			}
		});
		feedbackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent();
				intent.setClass(mContext, Feedback.class);
				startActivity(intent);
			}
		});
		download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				createDialog(R.string.setting_download_option, R.array.download,
						Constant.download, "download");
			}
		});
	}

	class CleanBufferAsyncTask extends AsyncTask<Void, Void, Void> {
		private String filepath = Constant.envir+"res";
		public String result;

		public boolean Delete() {
			File file = new File(filepath);
			if (file.isFile()) {
				file.delete();
				return true;
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				if (files != null && files.length == 0) {
					return false;
				} else {
					for (int i = 0; i < files.length; i++) {
						files[i].delete();
					}
					return true;
				}
			} else {
				return false;
			}
		}
		
		public void deleteFile(File f) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				if (files != null && files.length > 0) {
					for (int i = 0; i < files.length; ++i) {
						deleteFile(files[i]);
					}
				}
			}
			f.delete();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO 自动生成的方法存根
//			if (Delete()) {
				deleteFile(new File(filepath));
				courseContentOp.deleteCourseContentData();
//				courseContentOp.updateIsDownload(false);
				soundSize.post(new Runnable() {
					@Override
					public void run() {
						// TODO 自动生成的方法存根
						soundSize.setText("0B");
					}
				});
//			}
			return null;
		}
	}
	
	private void prepareMessage() {
		String text = getResources().getString(R.string.setting_share1)
				+ Constant.APP_NAME
				+ getResources().getString(R.string.setting_share2)
				+ "：http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android/androidDetail.jsp?id="
				+ Constant.APPID;
		Intent shareInt = new Intent(Intent.ACTION_SEND);
		shareInt.setType("text/*");
		shareInt.putExtra(Intent.EXTRA_TEXT, text);
		shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareInt.putExtra("sms_body", text);
		startActivity(Intent.createChooser(shareInt,
				getResources().getString(R.string.setting_share_ways)));
	}
	
	private String getSize(int type) {
//		if (type == 0) {
//			return FileSize.getInstance().getFormatFolderSize(
//					new File(Constant.picAddr));
//		} else {
//			return FileSize.getInstance().getFormatFolderSizeAudio(
//					new File(Constant.videoAddr));
//		}
		return FileSize.getInstance().getFormatFolderSize(new File(Constant.envir+"res"));
	}
	
//	OnClickListener ocl = new OnClickListener() {
//
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			Intent intent;
//			Dialog dialog;
//			switch (arg0.getId()) {
//			case R.id.clear_resource:
//				//这里是新的显示已下载课程的Activity
//				intent = new Intent(mContext, DelCourseDataActivity.class);
//				startActivity(intent);
//				
//				break;
//				
//			case R.id.check_download:
//				intent = new Intent(mContext, DownloadList.class);
//				startActivity(intent);
//				break;
//			case R.id.help_use_btn:
//				intent = new Intent(mContext, HelpUse.class);
//				intent.putExtra("source", "set");
//				startActivity(intent);
//				break;
//			case R.id.recommend_btn:
//				prepareMessage();
//				break;
//			case R.id.about_btn:
//				intent = new Intent();
//				intent.setClass(mContext, About.class);
//				startActivity(intent);
//				break;
//			case R.id.feedback_btn:
//				intent = new Intent();
//				intent.setClass(mContext, Feedback.class);
//				startActivity(intent);
//				break;
//			case R.id.download_option:
//				createDialog(R.string.setting_download_option, R.array.download,
//						Constant.download, "download");
//				break;
//			default:
//				break;
//			}
//		}
//	};

	private void createDialog(int title, int array, int select,
			final String sign) {
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setSingleChoiceItems(array, select,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stud
						if (sign.equals("download")) {
							Constant.download = which;
							downloadText.setText(mContext.getResources()
									.getStringArray(R.array.download)[which]);
							ConfigManager.Instance().putInt("download",
									Constant.download);
						}
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.alert_btn_cancel, null);
		builder.create().show();
	}
	
	
	
}
