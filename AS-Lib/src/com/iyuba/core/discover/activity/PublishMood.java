package com.iyuba.core.discover.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.util.TakePictureUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.teacher.protocol.RequestPublishMood;
import com.iyuba.core.teacher.protocol.ResponsePublishMood;
import com.iyuba.core.util.Constant;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 发表爱语？？？
 * 
 * @author lmy
 * 
 */

public class PublishMood extends BasisActivity implements OnClickListener {
	private Context mContext;

	private TextView button_back, publish_m;
	private RelativeLayout rlShow;
	private GridView emotion_GridView;
	private EditText content;
	private ImageView iface, ivAddPic;
	private int[] imageIds = new int[30];

	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	private String actionUrl = "http://api."+Constant.IYBHttpHead2+"/v2/avatar/photo?uid=";
	public static final int NONE = 0;
	private String uploadFile;
	private String newName;
	private Boolean isChangePor = false;
	
	private boolean hasDiscPic = false;
	
    public  String size;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mood);
		mContext = this;
		 CrashApplication.addActivity(this);
		initView();
		
//		String action = getIntent().getStringExtra("action");
//		if (action.equals("2"))
//			showListDia();

	}

	public void initView() {
		button_back = (TextView) findViewById(R.id.button_back2);
		button_back.setOnClickListener(this);
		publish_m = (TextView) findViewById(R.id.publish_m);
		publish_m.setOnClickListener(this);
		rlShow = (RelativeLayout) findViewById(R.id.rl_show);
		emotion_GridView = (GridView) rlShow.findViewById(R.id.grid_emotion);
		content = (EditText) findViewById(R.id.edit_mood);
		iface = (ImageView) findViewById(R.id.iface);
		iface.setOnClickListener(this);
		ivAddPic = (ImageView) findViewById(R.id.iv_add_picture);
		ivAddPic.setOnClickListener(this);
	}

	public void showListDia() {

		final String[] mList = { "相机拍摄", "手机相册" };
		AlertDialog.Builder listDia = new AlertDialog.Builder(PublishMood.this);
		listDia.setTitle("选择图片");
		listDia.setItems(mList, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/* 下标是从0开始的 */
				showClickMessage(which);
			}

		});
		listDia.create().show();

	}

	private void showClickMessage(int which) {
	// TODO Auto-generated method stub
		Intent intent;
		switch (which) {
		case 0:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(TakePictureUtil.getPhotoFile(mContext)));
			
			startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
			break;
		case 1:
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(new File(TakePictureUtil.photoPath)), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom1(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) {
				hasDiscPic = true;
				isChangePor = true;
				setPicToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private void startPhotoZoom1(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
		
		Bitmap photo = intent.getExtras().getParcelable("data");
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(intent.getData(), proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		TakePictureUtil.photoPath = cursor.getString(column_index);
		Log.e("startPhotoZoom",TakePictureUtil.photoPath);

	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			//SaveImage.saveImage(tempFilePath, photo);
			ivAddPic.setBackgroundDrawable(drawable);
		}
	}
	
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 40, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 40;
		while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	private Bitmap getImageZoomed(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
		
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	String success;
	String failure;

	// 上传头像、文件到服务器上
	private void uploadFile() {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url;
			if (content.getText().toString() == null
					|| content.getText().toString().equals("")) {
				url = new URL(actionUrl
						+ AccountManagerLib.Instace(mContext).userId);
			} else {
				url = new URL(actionUrl
						+ AccountManagerLib.Instace(mContext).userId
						+ "&iyu_describe="
						+ URLEncoder.encode(URLEncoder.encode(content.getText()
								.toString(), "UTF-8"), "UTF-8"));
				Log.e("userId", actionUrl
						+ AccountManagerLib.Instace(mContext).userId
						+ "&iyu_describe=" + content.getText().toString());
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设定传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// con.setRequestProperty("iyu_describe",
			// URLEncoder.encode(mood_content.getText().toString(),"utf-8"));
			/* 设定DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			String describe = "iyu_describe="
					+ URLEncoder.encode(content.getText().toString(), "utf-8");
			Log.e("describe", "----" + describe);
			// ds.writeBytes(describe);
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			Log.e("iyuba", uploadFile + "----------------------wenjian");
			
//			Bitmap bmp = BitmapFactory.decodeFile(uploadFile);
			Bitmap bmp = BitmapFactory.decodeFile(TakePictureUtil.photoPath);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0
			Log.e("iyuba", stream.toByteArray().length / 1024
					+ "stream------------------------");
//			String temp2 = Environment.getExternalStorageDirectory()
//					+ "/temp2.jpg";
			String temp2 = TakePictureUtil.photoPath;
			FileOutputStream os = new FileOutputStream(temp2);
			os.write(stream.toByteArray());
			os.close();
			FileInputStream fStream = new FileInputStream(temp2);
			/* 设定每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据到缓冲区 */
			while ((length = fStream.read(buffer)) != -1) { /* 将数据写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(describe);
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			success = b.toString().trim();
			JSONObject jsonObject = new JSONObject(success.substring(
					success.indexOf("{"), success.lastIndexOf("}") + 1));
			System.out.println("cc=====" + jsonObject.getString("status"));
			if (jsonObject.getString("status").equals("0")) {// status 为0则修改成功
				if(jsonObject.getString("jiFen")!=null
						&& Integer.parseInt(jsonObject.getString("jiFen")) > 0){
					Message msg = new Message();
					msg.what = 4;
					msg.arg1 = Integer.parseInt(jsonObject.getString("jiFen"));
					handler.sendMessage(msg);
				}else{
					handler.sendEmptyMessage(1);
				}
				finish();
			} else {
				handler.sendEmptyMessage(2);
			}
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
			failure = e.getMessage();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	boolean doing = false;

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.publish_m) {
			if (!NetWorkState.isConnectingToInternet()) {// 开始刷新

				handler.sendEmptyMessage(3);

			} else {

				if (doing) {
					handler.sendEmptyMessage(6);
					return;
				}
				if (isChangePor) {
					doing = true;
					handler.sendEmptyMessage(6);
					Thread upload = new Thread() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							uploadFile();
						}
					};
					upload.start();
				} else {

					if (content.getText().toString().trim().equals("")) {
						handler.sendEmptyMessage(0);
						return;
					}

					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(PublishMood.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					ExeProtocol.exe(
							new RequestPublishMood(AccountManagerLib
									.Instace(mContext).userId, AccountManagerLib
									.Instace(mContext).userName, content
									.getText().toString().trim()),
							new ProtocolResponse() {

								@Override
								public void finish(BaseHttpResponse bhr) {
									// TODO Auto-generated method stub
									ResponsePublishMood responseUpdateState = (ResponsePublishMood) bhr;
									int code = Integer
											.parseInt(responseUpdateState.result);
									if (code == 351) {
										if(responseUpdateState.jiFen != null &&
												Integer.parseInt(responseUpdateState.jiFen) > 0){
											Message msg = new Message();
											msg.what = 4;
											msg.arg1 = Integer.parseInt(responseUpdateState.jiFen);
											handler.sendMessage(msg);
										}else{
											handler.sendEmptyMessage(1);
										}
									} else {
										handler.sendEmptyMessage(2);
									}
									doing = false;
								}

								@Override
								public void error() {
									// TODO Auto-generated method stub
									// handler.sendEmptyMessage(3);
								}
							});
				}

			}
		}
		if (arg0.getId() == R.id.button_back2) {
			finish();

		}

		if (arg0.getId() == R.id.iv_add_picture) {
			showListDia();
		}

		if (arg0.getId() == R.id.iface) {

			if (rlShow.getVisibility() == View.GONE) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(PublishMood.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				rlShow.setVisibility(View.VISIBLE);
				initEmotion();
				emotion_GridView.setVisibility(View.VISIBLE);
				emotion_GridView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								Bitmap bitmap = null;
								bitmap = BitmapFactory.decodeResource(
										getResources(), imageIds[arg2
												% imageIds.length]);
								ImageSpan imageSpan = new ImageSpan(mContext,
										bitmap);
								String str = "image" + arg2;
								SpannableString spannableString = new SpannableString(
										str);
								String str1 = Emotion.express[arg2];
								SpannableString spannableString1 = new SpannableString(
										str1);
								if (str.length() == 6) {
									spannableString.setSpan(imageSpan, 0, 6,
											Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								} else if (str.length() == 7) {
									spannableString.setSpan(imageSpan, 0, 7,
											Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								} else {
									spannableString.setSpan(imageSpan, 0, 5,
											Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
								content.append(spannableString1);
							}
						});
			} else {
				rlShow.setVisibility(View.GONE);
			}
		}

	}

	private void initEmotion() {
		SimpleAdapter simpleAdapter = new SimpleAdapter(this,
				Emotion.initEmotion(),
				R.layout.team_layout_single_expression_cell,
				new String[] { "image" }, new int[] { R.id.image });
		emotion_GridView.setAdapter(simpleAdapter);
		emotion_GridView.setNumColumns(7);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			int jiFen = 0;
			super.handleMessage(msg);
			jiFen = msg.arg1;
			switch (msg.what) {
			case 0:
				CustomToast.showToast(mContext, "写点什么在发表吧！");
				break;
			case 1:
				CustomToast.showToast(mContext, "发表成功");
				finish();
				break;
			case 2:
				CustomToast.showToast(mContext, "网络原因发表失败，请稍后再试！");
				break;
			case 3:
				CustomToast.showToast(mContext, "请打开网络！");
				break;
			case 4:
				Toast.makeText(mContext, "发表成功"+"+"+jiFen+"积分！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 6:
				CustomToast.showToast(mContext, "正在发表....");
				break;
			}
		}
	};
}
