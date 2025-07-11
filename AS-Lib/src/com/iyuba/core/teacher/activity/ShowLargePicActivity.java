package com.iyuba.core.teacher.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ShowLargePicActivity extends Activity {

    //	private ImageView btnBack;
    private ImageView discPic;
    String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lib_show_disc_pic);
        pic = getIntent().getStringExtra("pic");

        Log.d("ShowLargePicActivity:", pic);

        initWidget();
    }

    public void initWidget() {
//		btnBack = (ImageView) findViewById(R.id.btn_back);
        discPic = (ImageView) findViewById(R.id.disc_pic);
//		GitHubImageLoader.Instace(this).setPic(pic,
//				discPic, R.drawable.nearby_no_icon,0);
//		
        ImageLoader.getInstance().loadImage(pic, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);

                discPic.setImageBitmap(loadedImage);
                LayoutParams para;
                para = discPic.getLayoutParams();
                int height = loadedImage.getHeight();
                int width = loadedImage.getWidth();
//	                float f=mContext.getResources().getDisplayMetrics().density;
//	               float bit=width/300;
//	                height=(int)(height/bit);
//	               width=(int)(300/1.5*f);
//	               height=(int)(height/1.5*f);
                para.height = height;
                para.width = width;
                discPic.setLayoutParams(para);
            }
        });
//		btnBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
        discPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
}
