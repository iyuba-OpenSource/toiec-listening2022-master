package com.iyuba.toeiclistening.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageUtil {
	
	/**
	 * 将资源图片转化为bitmap
	 * @param context
	 * @param resId 引用的资源ID
	 * @return
	 */
	public static Bitmap getBitmap(Context context,int resId){
		Resources res = context.getResources();
		Bitmap bitmap=BitmapFactory.decodeResource(res,resId);
		return bitmap;
	}
	
	/**
	 * 
	 * 将bitmap图片裁剪为指定的圆角的bitmap图片
	 * @param bitmap src图片
	 * @param roundPx 裁剪的的圆角的半径
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);//指定  画布所画到的位图
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);//先用argb填充  相当于背景色
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//画一个裁剪过的矩形 在output的bitmap 图片上
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));//设置  当图片相交时的模式  保留src的相交部分图片
        canvas.drawBitmap(bitmap, rect, rect, paint);//bitmap为src output为destination  第一个参数为src的位置   第二个参数为 desc的位置
        return output;
	}
	
	
	/**
	 * 
	 * 调整bigmap到指定的大小
	 * @param bitmap
	 * @param width 调整后的width
	 * @param height 调整后的height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);//其x y都是指是原来的多少倍
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newBmp;
    }
	/**
	 * 
	 * 从assect里面读取图片
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getImageFromAssetsFile(Context context, String fileName)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = context.getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  }  
	
}
