package com.iyuba.core.teacher.activity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.thread.UploadFile;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.SaveImage;
import com.iyuba.core.common.util.SelectPicUtils;
import com.iyuba.core.common.util.TakePictureUtil;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.teacher.protocol.GetTeacherInfoRequest;
import com.iyuba.core.teacher.protocol.GetTeacherInfoResponse;
import com.iyuba.core.teacher.protocol.UpdateBasicRequest;
import com.iyuba.core.teacher.protocol.UpdateBasicResponse;
import com.iyuba.core.teacher.sqlite.mode.Teacher;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TeacherBaseInfo extends Activity {
	private TextView quesCancel;
	private ContextMenu contextMenu;
	private Context mContext;
	
    public  String size;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	public static final int PHOTO_RESULT = 4;// 结果


	private boolean isSend = true;
	private boolean isSelectPhoto = false;
	private StringBuffer sbCutPath;

	private String tempFilePath = Environment.getExternalStorageDirectory()
			+ "/teacher.jpg";
	private File tempFile = new File(tempFilePath);

	private CustomDialog cd;

	private ImageView teaacherhead;
	private String  attachment="";
   private TextView next2,editteachername,editteachermobile,editteacheremail,editteacherweixin,editteacherdesc;
   RadioButton  RadioButton1,RadioButton2;
   private Teacher teacher=new Teacher();
   Spinner  spteachercert;
   
   private ArrayAdapter<String> adapter;

   private List<String> list;

   private String[] items = {"专科","本科","硕士","博士","其他"};
   
   public void intiSpiner(Spinner spinner ){
	    
	   list = new ArrayList<String>();

	   for(int i = 0; i < items.length; i++)

	   {

	   list.add(items[i]);

	   }

	   adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);

	   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	   spinner.setAdapter(adapter);
	   
   }
   
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lib_teacherbaseinfo);
		mContext = this;
		cd=  WaittingDialog.showDialog(mContext);
	
		initWidget();
		handler.sendEmptyMessage(1);
	}

	public void initData() {
		 cd.dismiss();
		 if(!teacher.timg.equals(""))	handler.sendEmptyMessage(12);
		 editteachername.setText(teacher.tname);
		 editteachermobile.setText(teacher.tphone);
		 editteacherdesc.setText(teacher.tonedesc);
		 editteacherweixin.setText(teacher.tweixin);
		 editteacheremail.setText(teacher.temail);
		 if(teacher.tsex.trim().equals("女"))RadioButton2.setChecked(true);
		
		 if(!teacher.topedu.equals("")){
			 int theNum=0;
			 for(int i=0;i<items.length;i++){
				 if(items[i].equals(teacher.topedu)){
					theNum=i; 
				 break;
				 }
				 
			 }
			 
			 
			 spteachercert.setSelection(theNum,true);
			 
			 
		 }
		 
		 
	}

	public void initWidget() {
		
		
		
		spteachercert=(Spinner) findViewById(R.id.spteachercert);
		intiSpiner(spteachercert);
		
		
		
		contextMenu = (ContextMenu) findViewById(R.id.context_menu1);
		 teaacherhead=(ImageView) findViewById(R.id.teaacherhead);
		 teaacherhead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				setContextMenu();
			}
		});
		 
		 
		 
		editteacheremail=(TextView) findViewById(R.id.editteacheremail);
		 editteacherweixin=(TextView) findViewById(R.id.editteacherweixin);
		 editteacherdesc=(TextView) findViewById(R.id.editteacherdesc);
	
		 editteachermobile=(TextView) findViewById(R.id.editteachermobile);
		RadioButton1=(RadioButton) findViewById(R.id.RadioButton1);
		RadioButton2=(RadioButton) findViewById(R.id.RadioButton2);
		
		
		editteachername=(TextView) findViewById(R.id.editteachername);
		quesCancel = (TextView) findViewById(R.id.tbutton_back);
		
		next2 = (TextView) findViewById(R.id.next2);
		
		next2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//获取老师的基本信息
				teacher.uid=AccountManagerLib.Instace(mContext).userId;
				teacher.username=AccountManagerLib.Instace(mContext).userName;
				teacher.tname=editteachername.getText().toString().trim();
				teacher.tphone=editteachermobile.getText().toString().trim();
				teacher.temail=editteacheremail.getText().toString().trim();
				teacher.tweixin=editteacherweixin.getText().toString().trim();
				teacher.tonedesc= editteacherdesc.getText().toString().trim();
				if(!attachment.equals(""))teacher.timg=attachment;
				if(RadioButton2.isChecked())
				{
					teacher.tsex="女";
				}else{
				    teacher.tsex="男";
				}
			      teacher.topedu=spteachercert.getSelectedItem().toString();
			      //判断是否上传老师头像
			      if(teacher.timg.equals("")) {
						handler.sendEmptyMessage(16);
						return;
					}
			      //判断是否有必填项没有填
					if(editteachername.getText().toString().trim().equals("")||editteachermobile.getText().toString().trim().equals("")
							||editteacheremail.getText().toString().trim().equals("")||editteacherweixin.getText().toString().trim().equals("")
							||editteacherdesc.getText().toString().trim().equals("")
							){
						handler.sendEmptyMessage(17);
						return;
					}
			      
			      
				updateBasic();
				Intent intent = new Intent(mContext, TeacherBaseInfo2.class);
				startActivity(intent);
				finish();
			}
		});
        
		quesCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	
	
		
		
		
		
		
		
	}
       //设置菜单
	public void setContextMenu() {
		contextMenu.setText(mContext.getResources().getStringArray(
				R.array.choose_pic));
		contextMenu.setCallback(new ResultIntCallBack() {

			@Override
			public void setResult(int result) {
				Intent intent;
				switch (result) {
				case 0:
//					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//					intent.putExtra(MediaStore.EXTRA_OUTPUT,
//							Uri.fromFile(tempFile));
//					startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (Build.VERSION.SDK_INT >= 24) {
						intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					}
					intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
					startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					break;
				case 1:
//					intent = new Intent(Intent.ACTION_PICK, null);
//					intent.setDataAndType(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//							"image/*");
//					startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
					intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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

	private Uri getUri() {
		File file = new File(Environment.getExternalStorageDirectory(), "teacher_header.jpg");//temp
		Uri uri;
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			//startPhotoZoom(Uri.fromFile(tempFile), 150);
			SelectPicUtils.cropPicture(this, getUri(), PHOTO_RESULT, "teacher_header.jpg");
			break;

		case PHOTO_REQUEST_GALLERY:
//			if (data != null)
//				startPhotoZoom1(data.getData(), 150);
			if (data != null){
				String path = SelectPicUtils.getPath(this, data.getData());
				Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(path));
				SelectPicUtils.cropPicture(this, uri, PHOTO_RESULT, "teacher_header.jpg");// Uri.fromFile(new File(path)),
			}
			break;

		case PHOTO_REQUEST_CUT:
			if (resultCode == RESULT_CANCELED) {
				return;
			}
			if (data != null) {
				isSend = false;
				isSelectPhoto = true;
				GitHubImageLoader.Instace(mContext).clearCache();
				setPicToView(data);

				Bundle extras = data.getExtras();
				Bitmap photo = extras.getParcelable("data");

				if (TakePictureUtil.photoCutPath != null && !TakePictureUtil.photoCutPath.equals("")) {
					SaveImage.saveImage(TakePictureUtil.photoCutPath, photo);
				} else if (TakePictureUtil.photoPath != null && !TakePictureUtil.photoPath.equals("")) {
					sbCutPath = new StringBuffer(TakePictureUtil.photoPath);
					sbCutPath.insert(TakePictureUtil.photoPath.length() - 4, "cut");
					TakePictureUtil.photoCutPath = sbCutPath.toString();
					SaveImage.saveImage(TakePictureUtil.photoCutPath, photo);
				} else {
					return;
				}
			}
			break;

			case PHOTO_RESULT:
				// 处理结果
				if (requestCode == PHOTO_RESULT && data != null) {
					isSend = false;
					isSelectPhoto = true;
					File cropFile = new File(Environment.getExternalStorageDirectory() + "/teacher_header.jpg");
					Bitmap bitmap = BitmapFactory.decodeFile(cropFile.getAbsolutePath(), getBitmapOption(2));

					if (Build.VERSION.SDK_INT >= 24) {
						try {
							bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getUri()));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						bitmap = BitmapFactory.decodeFile(getUri().getPath());
					}
					teaacherhead.setImageBitmap(bitmap);
					ImageLoader.getInstance().cancelDisplayTask(teaacherhead);
					SaveImage.saveImage(Environment.getExternalStorageDirectory() + "/teacher_header.jpg", bitmap);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private BitmapFactory.Options getBitmapOption(int inSampleSize) {
		System.gc();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = inSampleSize;
		return options;
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
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			teaacherhead.setImageDrawable(drawable);
			SaveImage.saveImage(tempFilePath, photo);
			teaacherhead.setImageDrawable(drawable);
			//teaacherhead.setBackgroundDrawable(drawable);
			new  UploadThread().start();
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
				getTeacherData();
				break;
			case 2:
				CustomToast.showToast(mContext, R.string.ask_question_fail);
				break;
			case 3:
				CustomToast.showToast(mContext, R.string.ask_question_success);
				finish();
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.question_tip);
				break;
				
			case 6:
				CustomToast.showToast(mContext, "请选择问题类型");
				break;
				
			case 10:
				CustomToast.showToast(mContext, size);
				break;
			case 12:
				GitHubImageLoader.Instace(mContext).setPic("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"+teacher.timg,
						 teaacherhead, R.drawable.photo_pic,0);
				break;
			case 13:
				CustomToast.showToast(mContext, attachment);
				break;
			case 16:
				CustomToast.showToast(mContext, "请上传您的头像!");
				break;
			case 17:
				CustomToast.showToast(mContext, "请填写必填信息！");
				break;
			case 18:
               initData();
				break;
			}
			
		}
	};

 //上传老师的头像
	class UploadThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				String uri="http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/upLoad.jsp?format=json";
				Log.e("iyuba", uri);
				UploadFile.postHead(tempFilePath, uri, new OperateCallBack() {

					@Override
					public void success(String message) {
					    attachment=message;
					}

					@Override
					public void fail(String message) {
						attachment=message;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	//获取老师的基本信息
	public void getTeacherData() {
		
		ExeProtocol.exe(new GetTeacherInfoRequest(AccountManagerLib.Instace(mContext).userId), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				GetTeacherInfoResponse tr = (GetTeacherInfoResponse) bhr;
				teacher=tr.item;
				handler.sendEmptyMessage(18);
				//initData();
			}
			@Override
			public void error() {
				
			}
		});
	}
	
	//提交或更新老师基本信息	
	public void updateBasic() {
		ExeProtocol.exe(new UpdateBasicRequest(teacher), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				UpdateBasicResponse tr = (UpdateBasicResponse) bhr;
				if(tr.result.equals("1")) 	;
			}
			@Override
			public void error() {
				
			}
		});
	}
	
	
}
