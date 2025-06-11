package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.FileSize;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.downloadprovider.jlpt1.downloads.ui.DownloadList;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.iyumooc.teacher.API.TeacherApiStores;
import com.iyuba.core.microclass.sqlite.op.CourseContentOp;
import com.iyuba.core.teacher.activity.AboutActivity;
import com.iyuba.core.teacher.activity.ExerciseModeActivity;
import com.iyuba.core.teacher.activity.Feedback;
import com.iyuba.core.teacher.activity.HelpUse;
import com.iyuba.core.util.ClearUserResponse;
import com.iyuba.core.util.JsonOrXmlConverterFactory;
import com.iyuba.core.util.LoginResponse;
import com.iyuba.core.util.PwdInputDialog;
import com.iyuba.core.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SettingActivity extends Activity  {
	
	private Context mContext;
	private Button backButton;
	
	private View aboutBtn, feedbackBtn,btn_help_use,newLogout,
	btn_clear_resource, recommendButton,language,checkdownload,download,set_exercise_mode;
	private TextView picSize, soundSize;
	
	private int appLanguage;
	private TextView languageText;
	
	private TextView downloadText;
	
	private CourseContentOp courseContentOp;
	private TextView set_exercise_mode_text;
	private ApiComService mApiComService;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_setting);
		mContext=this;
		courseContentOp = new CourseContentOp(mContext);
		initWidget();
		handler.sendEmptyMessage(5);
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
		Builder builder = new Builder(mContext);
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
	private void initWidget() {
		// TODO Auto-generated method stub
		backButton = (Button) findViewById(R.id.setting_button_back);
		
		soundSize = (TextView) findViewById(R.id.soundSize);
		
		btn_clear_resource = findViewById(R.id.clear_resource);
		checkdownload =  findViewById(R.id.check_download);
		btn_help_use =  findViewById(R.id.help_use_btn);
		aboutBtn =  findViewById(R.id.about_btn);//已经被隐藏
		feedbackBtn = findViewById(R.id.feedback_btn);//已经被隐藏
		recommendButton = findViewById(R.id.recommend_btn);
		
		download =  findViewById(R.id.download_option);
		downloadText = (TextView)  findViewById(R.id.curr_download);
		downloadText.setText(mContext.getResources().getStringArray(
				R.array.download)[Constant.download]);
		
		language =  findViewById(R.id.set_language);
		languageText = (TextView)  findViewById(R.id.curr_language);

		set_exercise_mode =  findViewById(R.id.set_exercise_mode);
		newLogout = findViewById(R.id.logout_view);
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
		set_exercise_mode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(mContext, ExerciseModeActivity.class);
                startActivity(intent);
            }
        });

		// TODO Auto-generated method stub
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		btn_clear_resource.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction("com.iyuba.toeiclistening.activity.DeleteAudioActivity");
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
				intent.setClass(mContext, AboutActivity.class);
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

		newLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
					TextView title = new TextView(mContext);
					title.setGravity(Gravity.CENTER);
					title.setText("提示");
					title.setTextColor(ContextCompat.getColor(mContext, R.color.black));
					title.setTextSize(20);
					title.setPadding(0, 20, 0, 0);
					new Builder(mContext)
							.setMessage("注销账号后将无法登录，并且将不再会保存个人信息，账户信息将会被清除，确定要注销账号吗？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									showContinueWrittenOffDialog();
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null)
							.setCustomTitle(title)
							.create()
							.show();
				} else {
					Toast.makeText(mContext, "未登录，请先登录", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	private void showContinueWrittenOffDialog() {
		final PwdInputDialog dialogTip = new PwdInputDialog(mContext);
		dialogTip.setTitleStr("输入密码注销账号");
		dialogTip.setCancelStr("取消");
		dialogTip.setConfirmStr("确认注销");
		dialogTip.setShowInputPwd(true);
		dialogTip.setListener(new PwdInputDialog.OnBtnClickListener() {
			@Override
			public void onCancelClick() {
				dialogTip.dismiss();
			}

			@Override
			public void onConfirmClick() {
				dialogTip.dismiss();
			}

			@Override
			public void onCheckInputPassWord(final String password) {

				String[] nameAndPwd = AccountManagerLib.Instace(mContext).getUserNameAndPwd();
				final String userName = nameAndPwd[0];
				final String userPwd = nameAndPwd[1];
				if (TextUtils.equals(password, userPwd)) {
					clearUser(userName, userPwd);
//                    ToastUtil.showToast(mContext, "frist vertify");
				} else {
					// 用 输入 的密码登录验证 输入的密码是否正确
					OkHttpClient client = new OkHttpClient.Builder()
							.connectTimeout(15, TimeUnit.SECONDS)
							.readTimeout(15, TimeUnit.SECONDS)
							.writeTimeout(15, TimeUnit.SECONDS)
							.build();

					Retrofit retrofit = new Retrofit.Builder()
							//这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
//							.baseUrl("http://api.iyuba.com.cn/")
							.baseUrl("http://api."+ WebConstant.COM_CN_SUFFIX)
							.client(client)
							.addConverterFactory(JsonOrXmlConverterFactory.create())
							.build();
					TeacherApiStores apiService = retrofit.create(TeacherApiStores.class);
					try {
						apiService.login("11001", URLEncoder.encode(userName, "UTF-8"), MD5.getMD5ofStr(password), "0", "0", Constant.APPID,
								MD5.getMD5ofStr("11001" + userName + MD5.getMD5ofStr(password) + "iyubaV2"), "json")
								.enqueue(new Callback<LoginResponse>() {
									@Override
									public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
										if (response.isSuccessful() && response.body() != null) {
											LoginResponse userMsg = response.body();
											if (TextUtils.equals("101", userMsg.getResult())) {
												// 登陆成功，证明输入的密码正确
//												Log.e(TAG, "second  "+"101" );
												dialogTip.dismiss();
												clearUser(userName, password);
//                                                ToastUtil.showToast(mContext, "验证中......");
											} else if (TextUtils.equals("102", userMsg.getResult())) {
												ToastUtil.showToast(mContext, "验证失败，请稍后重试！");
											} else if (TextUtils.equals("103", userMsg.getResult())) {
												ToastUtil.showToast(mContext, "输入的密码有误，请重试！");
											} else {
												ToastUtil.showToast(mContext, getString(R.string.login_fail));
											}
										}
									}

									@Override
									public void onFailure(Call<LoginResponse> call, Throwable t) {
										ToastUtil.showToast(mContext, "验证失败，请稍后重试！");
									}
								});
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		});
		dialogTip.show();
	}

	private void clearUser(String username, String password) {
//        InitPush.getInstance().unRegisterToken(mContext, Integer.parseInt(AccountManager.Instace(mContext).userId));
		AccountManagerLib.Instace(mContext).loginOut();
		Observable.create(new ObservableOnSubscribe<Boolean>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Boolean> observableEmitter) throws Exception {
				Response<ClearUserResponse> execute = null;
				execute = clearDataUser(username, password).execute();
				observableEmitter.onNext(execute != null);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
			@Override
			public void onSubscribe(@NonNull Disposable disposable) {

			}

			@Override
			public void onNext(@NonNull Boolean aBoolean) {
				if (aBoolean) {
					clearSuccess();
				}
			}

			@Override
			public void onError(@NonNull Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});


	}

	public void clearSuccess() {
		SettingConfig.Instance().setHighSpeed(false);
		ConfigManager.Instance().putString("userId", "");
		ConfigManager.Instance().putString("userName", "");
		ConfigManager.Instance().putInt("isvip", 0);
		EventBus.getDefault().post(new ChangeVideoEvent(true));

		CustomToast.showToast(mContext, R.string.loginout_success);
		new Builder(mContext)
				.setTitle("提示")
				.setMessage("账户被注销！账户信息清除")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				})
				.show();
	}

	public Call<ClearUserResponse> clearDataUser(String username, String password) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS);

		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		builder.addInterceptor(interceptor);

		OkHttpClient client = builder.build();
		SimpleXmlConverterFactory xmlFactory = SimpleXmlConverterFactory.create();
		GsonConverterFactory gsonFactory = GsonConverterFactory.create();
		RxJava2CallAdapterFactory rxJavaFactory = RxJava2CallAdapterFactory.create();

		mApiComService = ApiComService.Creator.createService(client,gsonFactory,rxJavaFactory);

		String protocol = "11005";
		String format = "json";
		String passwordMD5 = com.iyuba.module.toolbox.MD5.getMD5ofStr(password);
		String sign = buildV2Sign(protocol, username, passwordMD5, "iyubaV2");
		return mApiComService.clearUserInfo(protocol, username, passwordMD5, sign, format);

	}

	private String buildV2Sign(String... stuffs) {
		StringBuilder sb = new StringBuilder();
		for (String stuff : stuffs) {
			sb.append(stuff);
		}
		return com.iyuba.module.toolbox.MD5.getMD5ofStr(sb.toString());
	}

	public interface ApiComService {

//		String ENDPOINT = "http://api.iyuba.com.cn/";
		String ENDPOINT = "http://api."+WebConstant.COM_CN_SUFFIX;

		@GET("v2/api.iyuba")
		Call<ClearUserResponse> clearUserInfo(@Query("protocol") String protocol,
											  @Query("username") String username,
											  @Query("password") String password,
											  @Query("sign") String sign,
											  @Query("format") String format);


		class Creator {
			public static ApiComService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
				Retrofit retrofit = new Retrofit.Builder()
						.baseUrl(ENDPOINT)
						.client(client)
						.addConverterFactory(gsonFactory)
						.addCallAdapterFactory(rxJavaFactory)
						.build();
				return retrofit.create(ApiComService.class);
			}
		}
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
//				
//				//这里是新的显示已下载课程的Activity
//				intent = new Intent(mContext, DelCourseDataActivity.class);
//				startActivity(intent);
//				
//				break;
//				
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
		Builder builder = new Builder(mContext);
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
