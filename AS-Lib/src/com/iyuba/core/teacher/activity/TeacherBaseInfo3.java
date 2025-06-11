package com.iyuba.core.teacher.activity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.UploadFile;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.teacher.protocol.SubmitRequest;
import com.iyuba.core.teacher.protocol.SubmitResponse;
import com.iyuba.core.R;

public class TeacherBaseInfo3 extends Activity {
	private TextView quesCancel;

	private ContextMenu contextMenu;
	private Context mContext;
	
    public  String size;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	private String tempFilePath = Environment.getExternalStorageDirectory()
			+ "/fj.jpg";
	private File tempFile = new File(tempFilePath);

	private boolean hasPic = false;

	private CustomDialog cd;
	ImageView fj1,fj2,fj3;
	Button button1;
    int tu=1;
   
   private TextView next2;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_teacherbaseinfo3);
		mContext = this;
		cd=  WaittingDialog.showDialog(mContext);
		initWidget();
	}


	public void initWidget() {
		
		next2=(TextView) findViewById(R.id.next2);
		next2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
				
			}
		});
		
		
		
		button1=(Button) findViewById(R.id.button1);
		
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!hasPic){
					handler.sendEmptyMessage(13);
					return;
					
				}
				button1.setClickable(false);
				handler.sendEmptyMessage(1);
				new UploadThread().start();
				next2.setVisibility(View.VISIBLE);
				
			}
		});
		contextMenu = (ContextMenu) findViewById(R.id.context_menu3);
		quesCancel = (TextView) findViewById(R.id.tbutton_back3);
		fj1=(ImageView) findViewById(R.id.fj1);
		
		
		fj1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			setContextMenu();
				
			}
		});
        
		quesCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	
		
	}
 

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom1(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) {
			}
			setPicToView(data);
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
		tempFilePath = cursor.getString(column_index);
		Log.e("startPhotoZoom",tempFilePath);

	}
	
	
	
	
	

	
	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		 hasPic = true;
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			//SaveImage.saveImage(tempFilePath, photo);
			fj1.setBackgroundDrawable(drawable);
		}
	}

	 
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				cd.dismiss();
				break;
			case 1:
				cd.show();
				break;
			case 6:
				CustomToast.showToast(mContext, "请选择问题类型");
				break;
				
			case 13:
				CustomToast.showToast(mContext, "请添加图片!");
				break;
			case 11:
				 submit();
				break;
			case 12:
				CustomToast.showToast(mContext, "图片上传成功，点击完成，结束认证!");
				break;
			}
		}
	};
	public void  submit(){
	ExeProtocol.exe(
			new SubmitRequest(AccountManagerLib.Instace(mContext).userId + ""),
			new ProtocolResponse() {

				@Override
				public void finish(BaseHttpResponse bhr) {
					SubmitResponse tr = (SubmitResponse) bhr;
					handler.sendEmptyMessage(0);
					if ("1".equals(tr.result)) {
						handler.sendEmptyMessage(12);
					} 
				}

				@Override
				public void error() {
				}
			});
	
}
	 
	class UploadThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				String uri = "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/upLoad.jsp?from=attachment&uid="
						   +AccountManagerLib.Instace(mContext).userId+"&username="+TextAttr.encode(TextAttr.encode(TextAttr.encode(AccountManagerLib.Instace(mContext).userName)));
				Log.e("iyuba", uri);
                       Log.e("iyuba", "------图片");
				Bitmap bt=BitmapFactory.decodeFile(tempFilePath);
				File f=new File(tempFilePath);
				 FileInputStream fis= new FileInputStream(f);  
				 FileChannel   fc= fis.getChannel();  
				 
			    Log.e("iyuba",fc.size()/1024+"------------"+100);
				ByteArrayOutputStream stream=new ByteArrayOutputStream();
				int percent=20;
				bt.compress(Bitmap.CompressFormat.JPEG, percent, stream);
				size=stream.size()/1024+"+++1++"+fc.size()/1024;	
				Log.e("iyuba",stream.size()/1024+"------------"+percent);
				
				
				
				//讲压缩后的文件保存在temp2下
				String temp2=Constant.envir+"/temp2.jpg";
				FileOutputStream os=new FileOutputStream(temp2);
				os.write(stream.toByteArray());
				os.close();
				
				UploadFile.post(temp2, uri, new OperateCallBack() {

					@Override
					public void success(String message) {
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(3);
						handler.sendEmptyMessage(11);
					}

					@Override
					public void fail(String message) {
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(2);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	 
	
	 

	
	
	public void setContextMenu() {
		contextMenu.setText(mContext.getResources().getStringArray(
				R.array.choose_pic));
		contextMenu.setCallback(new ResultIntCallBack() {

			@Override
			public void setResult(int result) {
				Intent intent;
				switch (result) {
				case 0:
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(tempFile));
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
				contextMenu.dismiss();
			}
		});
		contextMenu.show();
	}
	
	
}
