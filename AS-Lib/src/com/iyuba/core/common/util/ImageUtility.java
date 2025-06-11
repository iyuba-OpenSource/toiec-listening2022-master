package com.iyuba.core.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowManager;

/**
 * ͼ��������
 * @Description: ͼ��������

 * @File: ImageUtility.java

 * @Package com.navigation.utility

 * @Author Hanyonglu

 * @Date 2012-7-30 ����10:52:49

 * @Version V1.0
 */
public class ImageUtility {
	/**
	 * ��ȡͼƬ�ĳߴ�
	 * @param context
	 * @param resource
	 * @return
	 */
	public static Point getImageDimension(Context context, int resource){
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource);
		Point point = new Point();
		point.x = bm.getWidth();
		point.y = bm.getHeight();
		bm.recycle();
		bm = null;
		
		return point;
	}
	
	/**
	 * ��ȡ����ͼƬ�����Ŀ�͸�
	 * @param context
	 * @param imgRes
	 * @return
	 */
	public static Point getImageMaxDimension(Context context,int[] imgRes){
		final Point point = new Point();
		
		for (int i = 0, length = imgRes.length; i < length; i++){
			Bitmap tmp = BitmapFactory.decodeResource(
					context.getResources(), 
					imgRes[i]);
			int width = tmp.getWidth();
			int height = tmp.getHeight();
			tmp.recycle();
			tmp = null;
			
			if (point.x < width){
				point.x = width;
			}
			
			if (point.y < height){
				point.y = height;
			}
		}
		
		return point;
	}
}
