package com.iyuba.toeiclistening.util;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.iyuba.toeiclistening.R;


public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {
	ImageView imageView;
	Context mContext;

	public DownLoadImage(Context mContext, ImageView imageView) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		// TODO Auto-generated method stub
		String url = urls[0];
		Bitmap tmpBitmap = null;
		NetWorkState netWorkState = new NetWorkState(mContext);
		if (netWorkState.getAPNType() != 2) {
			tmpBitmap = BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.userimg);
		} else {
			try {
				InputStream is = new java.net.URL(url).openStream();
				if (is == null)
					tmpBitmap = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.userimg);
				else
					tmpBitmap = BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				tmpBitmap = BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.userimg);
				e.printStackTrace();
			}
		}
		return tmpBitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		imageView.setImageBitmap(result);
	}
}
