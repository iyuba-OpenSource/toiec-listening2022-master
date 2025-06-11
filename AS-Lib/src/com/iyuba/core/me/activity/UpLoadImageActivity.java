/**
 *
 */
package com.iyuba.core.me.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.FileOpera;
import com.iyuba.core.common.util.SaveImage;
import com.iyuba.core.common.util.SelectPicUtils;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.util.Constant;
import com.iyuba.core.R;
import com.iyuba.module.user.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.ui.info.UploadImagePresenter;
import personal.iyuba.personalhomelibrary.utils.FileUtil;
import personal.iyuba.personalhomelibrary.utils.PathUtils;
import timber.log.Timber;

/**
 * 修改头像
 *
 * @author ct  zh2019.4.18 更新
 * @version 2.0
 * @para "regist" 是否来自注册 是则下一步补充详细信息
 */
@RuntimePermissions
public class UpLoadImageActivity extends BasisActivity {
    private ImageView image;
    private Button upLoad, photo, local, skip, back;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    private boolean fromRegist = false;
    private Context mContext;
    private CustomDialog waitingDialog;
    private boolean isSend = true;
    private boolean isSelectPhoto = false;

    private StringBuffer sbCutPath;
    private String strCutPath;

    public String size;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    public static final int PHOTO_RESULT = 4;// 结果

    private static final String TEMP = "temp.jpg";
    private static final String CROP = "temp_crop.jpg";
    private static final String TAG = "UpLoadImageActivity";

    private String imagePath;
    private File imageFile;
    UploadImagePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.upload_image);
         CrashApplication.addActivity(this);
        mContext = this;
        fromRegist = getIntent().getBooleanExtra("regist", fromRegist);
        waitingDialog = WaittingDialog.showDialog(mContext);
        mPresenter = new UploadImagePresenter();
        initWidget();
        UpLoadImageActivityPermissionsDispatcher.readPermissionWithPermissionCheck(this);
    }

    private void initWidget() {
        image = (ImageView) findViewById(R.id.userImage);
        back = (Button) findViewById(R.id.upload_back_btn);
        upLoad = (Button) findViewById(R.id.upLoad);
        photo = (Button) findViewById(R.id.photo);
        local = (Button) findViewById(R.id.local);
        skip = (Button) findViewById(R.id.skip);

        if (!fromRegist) {
            skip.setVisibility(View.GONE);
        }

        GitHubImageLoader.Instace(mContext).setCirclePic(AccountManagerLib.Instace(mContext).userId,
                image);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent;
//				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				intent.putExtra(MediaStore.EXTRA_OUTPUT,

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (Build.VERSION.SDK_INT >= 24) {
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                }
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
//                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri uri = FileUtil.getFileUri(mContext, new File(PathUtils.getAvatarPath(mContext), TEMP));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                addIntentFlag(intent);
//                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                Uri uri = FileUtil.getFileUri(mContext, new File(PathUtils.getPublishPath(getApplicationContext()), "temp.jpg"));
                intent.putExtra("output", uri);
                addIntentFlag(intent);
                startActivityForResult(intent, 1);

            }
        });
        local.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectPicUtils.selectPictureNew(UpLoadImageActivity.this, PHOTO_REQUEST_GALLERY);
                // 手机相册中选择
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });
        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File croppedHead = new File(PathUtils.getPublishPath(getApplicationContext()), "temp_crop_8965236958.jpg");
                if (croppedHead.exists()) {
                    if (isSelectPhoto) {
                        if (!isSend) {
                            isSend = true;
                            handler.sendEmptyMessage(0);
                            CustomToast.showToast(mContext, R.string.uploadimage_uploading);
                            new uploadThread().start();
                        } else {
                            CustomToast.showToast(mContext, R.string.submitting);
                        }
                    } else {
                        CustomToast.showToast(mContext, "请先选择图片！");
                    }
                }

            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromRegist) {
                    Intent intent = new Intent(mContext,
                            EditUserInfoActivity.class);
                    intent.putExtra("regist", fromRegist);
                    startActivity(intent);
                } else {
                    onBackPressed();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File input;
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
//                File input1 = new File(PathUtils.getAvatarPath(this), TEMP);
//                File output1 = new File(PathUtils.getAvatarPath(this), CROP);
//                crop(this, input1, PHOTO_REQUEST_CUT, output1);
                imageFile = new File(personal.iyuba.personalhomelibrary.utils.PathUtils.getPublishPath(getApplicationContext()), "temp.jpg");
                input = new File(PathUtils.getPublishPath(getApplicationContext()), "temp_crop_8965236958.jpg");
                this.crop(this, imageFile, 3, input);


                //SelectPicUtils.cropPicture(this, getUri(), PHOTO_RESULT, "header.jpg");
                break;

            case PHOTO_REQUEST_GALLERY:
//                if (resultCode == RESULT_CANCELED) {
//                    return;
//                }
//                String path = SelectPicUtils.getPath(this, data.getData());
//                if (path != null) {
//                    File file = new File(PathUtils.getAvatarPath(this));
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                    File input = new File(path);
//                    File output = new File(PathUtils.getAvatarPath(this), CROP);
//                    creatFile(input.getPath());
//                    creatFile(output.getPath());
//                    Timber.e("input:" + input.getAbsolutePath());
//                    Timber.e("output:" + output.getPath());
//                    crop(this, input, PHOTO_REQUEST_CUT, output);
//                }
                String path = personal.iyuba.personalhomelibrary.utils.SelectPicUtils.getPath(this, data.getData());
                if (path == null) {
                    return;
                }

                input = new File(path);
                File output = new File(personal.iyuba.personalhomelibrary.utils.PathUtils.getPublishPath(getApplicationContext()), "temp_crop_8965236958.jpg");
                crop(this, input, 3, output);
                break;

            case PHOTO_REQUEST_CUT:
//                imageFile = new File(PathUtils.getAvatarPath(UpLoadImageActivity.this), CROP);
//                imagePath = imageFile.getPath();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Timber.i("imageFile : %s %s", imageFile.getAbsolutePath(), imageFile.length());
//                    Glide.with(UpLoadImageActivity.this)
//                            .load(imageFile)
//                            .skipMemoryCache(true)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .into(image);
//                } else {
//                    if (data != null) {
//                        Bitmap bm = data.getParcelableExtra("data");
//                        image.setImageBitmap(bm);
//                        SaveImage.save(PathUtils.getAvatarPath(this) + "/" + CROP, bm);
//                    }
//                }
//                isSelectPhoto = true;
//                isSend = false;
                isSelectPhoto = true;
                isSend = false;
                if (Build.VERSION.SDK_INT >= 24) {
                    imagePath = personal.iyuba.personalhomelibrary.utils.PathUtils.getPublishPath(getApplicationContext());
                    imageFile = new File(imagePath, "temp_crop_8965236958.jpg");
                    Timber.i("imageFile : %s %s", imageFile.getAbsolutePath(), imageFile.length());
                    Glide.with(this).load(imageFile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(this.image);
                } else if (data != null) {
                    Bitmap bm = (Bitmap) data.getParcelableExtra("data");
                    this.image.setImageBitmap(bm);
                    personal.iyuba.personalhomelibrary.utils.SaveImage.save(personal.iyuba.personalhomelibrary.utils.PathUtils.getAvatarPath(this) + "/" + "temp_crop_8965236958.jpg", bm);
                }
                break;
            case PHOTO_RESULT:
                // 处理结果
                if (requestCode == PHOTO_RESULT && data != null) {
                    isSend = false;
                    isSelectPhoto = true;
                    File cropFile = new File(Environment.getExternalStorageDirectory() + "/header.jpg");
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
                    image.setImageBitmap(bitmap);
                    ImageLoader.getInstance().cancelDisplayTask(image);
                    SaveImage.saveImage(Environment.getExternalStorageDirectory() + "/header.jpg", bitmap);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void crop(Activity activity, File inputFile, int requestCode, File outputFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SelectPicUtils.cropPicture24(activity, inputFile, requestCode, outputFile);
        } else {
            Timber.e("裁剪开始");
            Uri uri = Uri.fromFile(inputFile);
            SelectPicUtils.cropPicture(activity, uri, requestCode);
        }
    }

    private void addIntentFlag(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    private void creatFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    private Uri getUri() {
        File file = new File(Environment.getExternalStorageDirectory(), "header.jpg");//temp
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    // 将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            image.setImageDrawable(drawable);
        }
    }


    private void showDialog(String mess, OnClickListener ocl) {
        new AlertDialog.Builder(UpLoadImageActivity.this)
                .setTitle(R.string.alert_title).setMessage(mess)
                .setNegativeButton(R.string.alert_btn_ok, ocl).show();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int jiFen = 0;
            String picUrl = "";
            String fileUrl = "";
            super.handleMessage(msg);
            jiFen = msg.arg1;
            switch (msg.what) {
                case 0:
                    waitingDialog.show();
                    break;
                case 1:
                    waitingDialog.dismiss();
                    break;
                case 2:
                    isSend = false;
                    fileUrl = Environment.getExternalStorageDirectory() + "/header.jpg";//TakePictureUtil.photoCutPath;
                    new FileOpera(mContext).deleteFile(fileUrl);
                    GitHubImageLoader.Instace(mContext).clearCache();
//                    showDialog(getResources().getString(R.string.uploadimage_success),
//                            new OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    if (fromRegist) {
//                                        Intent intent = new Intent(mContext,
//                                                EditUserInfoActivity.class);
//                                        intent.putExtra("regist", fromRegist);
//                                        startActivity(intent);
//                                    } else {
//                                        onBackPressed();
//                                    }
//                                }
//                            });
                    break;
                case 3:
                    isSend = false;
                    showDialog(
                            getResources().getString(
                                    R.string.uploadimage_failupload),
                            new OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            });
                    waitingDialog.dismiss();
                    break;
                case 4:
                    isSend = false;
                    picUrl = "http://api." + Constant.IYBHttpHead2 + "/v2/api.iyuba?protocol=10005&uid="
                            + AccountManagerLib.Instace(mContext).userId
                            + "&size=middle";
                    fileUrl = Environment.getExternalStorageDirectory() + "/header.jpg";//TakePictureUtil.photoCutPath;
                    new FileOpera(mContext).deleteFile(fileUrl);
                    GitHubImageLoader.Instace(mContext).clearCache();
//                    showDialog(
//                            getResources().getString(R.string.uploadimage_success) + "+" + jiFen + "积分！",
//                            new OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    if (fromRegist) {
//                                        Intent intent = new Intent(mContext,
//                                                EditUserInfoActivity.class);
//                                        intent.putExtra("regist", fromRegist);
//                                        startActivity(intent);
//                                    } else {
//                                        onBackPressed();
//                                    }
//                                }
//                            });
                    break;
                case 5:
                    ToastUtil.showToast(mContext, "刷新成功！");
                    EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布
                    onBackPressed();
                    break;
                case 6:
                    ToastUtil.showToast(mContext, "刷新失败！");
                    break;
                case 7:
                    ToastUtil.showToast(mContext, "账户丢失！");
                    break;
                default:
                    break;
            }
        }
    };

    class uploadThread extends Thread {
        @Override
        public void run() {
            super.run();
            uploadFile();
        }
    }

    ;

    String success;
    String failure;

    // 上传头像、文件到服务器上
    private void uploadFile() {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL("http://api." + Constant.IYBHttpHead2 + "/v2/avatar?uid="
                    + AccountManagerLib.Instace(mContext).userId);

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
            //File files = new File(Environment.getExternalStorageDirectory() + "/header.jpg");//TakePictureUtil.photoCutPath
            File files = new File(imagePath);//TakePictureUtil.photoCutPath
//				File files = new File(TakePictureUtil.photoPath);
            Log.d(TAG, "uploadFile: " + imageFile.getAbsolutePath() + " " + imagePath);
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\""
                    + imageFile + "\"" + ";filename=\""
                    + System.currentTimeMillis() + ".jpg\"" + end);
            ds.writeBytes(end);
            /* 取得文件的FileInputStream */

            //Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/header.jpg");
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//				Bitmap bmp = BitmapFactory.decodeFile(TakePictureUtil.photoPath);

//				Bitmap bmp = getImageZoomed(TakePictureUtil.photoPath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0
            LogUtil.e("iyuba", stream.toByteArray().length / 1024 + "stream------------------------");

            String temp2 = imageFile.getAbsolutePath();

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
                handler.sendEmptyMessage(1);
                isSend = false;
                try {
                    if (jsonObject.getString("jiFen") != null
                            && Integer.parseInt(jsonObject.getString("jiFen")) > 0) {
                        Message msg = new Message();
                        msg.what = 4;
                        msg.arg1 = Integer.parseInt(jsonObject.getString("jiFen"));
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //需要重新登录
                autoLogin(callBack);
            } else {
                isSend = false;
                handler.sendEmptyMessage(3);
            }
            /* 关闭DataOutputStream */
            ds.close();
        } catch (Exception e) {
            isSend = false;
            e.printStackTrace();
            failure = e.getMessage();
        }
    }

    private OperateCallBack callBack = new OperateCallBack() {
        @Override
        public void success(String message) {
            Timber.e("刷新登录成功");
            handler.sendEmptyMessage(5);
        }

        @Override
        public void fail(String message) {
            Timber.e("刷新登录失败");
            //自动登录失败无法刷新
            handler.sendEmptyMessage(6);
        }
    };

    public void autoLogin(OperateCallBack back) {
        String[] nameAndPwd = AccountManagerLib.Instace(mContext).getUserNameAndPwd();
        String userName = nameAndPwd[0];
        String userPwd = nameAndPwd[1];
        if (!TextUtils.isEmpty(userName)) {
            if (back == null) {
                AccountManagerLib.Instace(mContext).login(userName, userPwd, callBack);
                AccountManagerLib manager = AccountManagerLib.Instace(mContext);
//                PersonalManager.getInstance().setUserId(manager.getUserId());//修改
//                PersonalManager.getInstance().userName=manager.userName;
//                PersonalManager.getInstance().setVipState(manager.getVipStringStatus());//新的修改！！！
                PersonalHome.setSaveUserinfo(manager.getUserId(), manager.userName, manager.getVipStringStatus());

                User user = new User();
                user.vipStatus = String.valueOf(manager.getVipStringStatus());
                user.uid = manager.getUserId();
                user.name = manager.userName;
            } else {
                AccountManagerLib.Instace(mContext).login(userName, userPwd, back);
                AccountManagerLib manager = AccountManagerLib.Instace(mContext);
//                PersonalManager.getInstance().setUserId(manager.getUserId());//修改
//                PersonalManager.getInstance().userName=manager.userName;
//                PersonalManager.getInstance().setVipState(manager.getVipStringStatus());//新的修改！！！
                PersonalHome.setSaveUserinfo(0, null, "0");

                User user = new User();
                user.vipStatus = String.valueOf(manager.getVipStringStatus());
                user.uid = manager.getUserId();
                user.name = manager.userName;
            }
        } else {
            handler.sendEmptyMessage(7);
        }
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA})
    public void readPermission() {
        Timber.i("READ_PHONE_STATE request");
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA})
    public void readPermissionDenied() {
        Timber.w("the permission is denied, READ_PHONE_STATE not change!");
        ToastUtil.showToast(mContext, "请先开启权限！");
        finish();
    }
}
