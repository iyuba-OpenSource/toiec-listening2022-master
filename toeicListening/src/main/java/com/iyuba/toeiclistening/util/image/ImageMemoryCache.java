package com.iyuba.toeiclistening.util.image;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;

public class ImageMemoryCache {
	
	public static final int SOFT_CACHE_SIZE =50;
	private static LruCache<String, Bitmap> mLruCache;
	private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;
	 public ImageMemoryCache(Context context) {
	        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	        int cacheSize = 1024 * 1024 * memClass / 4;  //纭紩鐢ㄧ紦瀛樺閲忥紝涓虹郴缁熷彲鐢ㄥ唴瀛樼殑1/4
	        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
	            @Override
	            protected int sizeOf(String key, Bitmap value) {
	                if (value != null)
	                    return value.getRowBytes() * value.getHeight();
	                else
	                    return 0;
	            }
	                                                                                          
	            @Override
	            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
	                if (oldValue != null)
	                    // 纭紩鐢ㄧ紦瀛樺閲忔弧鐨勬椂鍊欙紝浼氭牴鎹甃RU绠楁硶鎶婃渶杩戞病鏈夎浣跨敤鐨勫浘鐗囪浆鍏ユ杞紩鐢ㄧ紦瀛?
	                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
	            }
	        };
	        mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {
	            private static final long serialVersionUID = 6040103833179403725L;
	            @Override
	            protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
	                if (size() > SOFT_CACHE_SIZE){    
	                    return true;  
	                }  
	                return false; 
	            }
	        };
	    }
	                                                                                  
	    /**
	     * 浠庣紦瀛樹腑鑾峰彇鍥剧墖
	     */
	    public Bitmap getBitmapFromCache(String url) {
	        Bitmap bitmap;
	        //鍏堜粠纭紩鐢ㄧ紦瀛樹腑鑾峰彇
	        synchronized (mLruCache) {
	        	if (url!=null) {
					bitmap = mLruCache.get(url); 
					if (bitmap != null) {
	                //濡傛灉鎵惧埌鐨勮瘽锛屾妸鍏冪礌绉诲埌LinkedHashMap鐨勬渶鍓嶉潰锛屼粠鑰屼繚璇佸湪LRU绠楁硶涓槸鏈?悗琚垹闄?
	                mLruCache.remove(url);
	                mLruCache.put(url, bitmap);
	                return bitmap;
	            }
				}
	           
	        }
	        //濡傛灉纭紩鐢ㄧ紦瀛樹腑鎵句笉鍒帮紝鍒拌蒋寮曠敤缂撳瓨涓壘
	        synchronized (mSoftCache) { 
	            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
	            if (bitmapReference != null) {
	                bitmap = bitmapReference.get();
	                if (bitmap != null) {
	                    //灏嗗浘鐗囩Щ鍥炵‖缂撳瓨
	                    mLruCache.put(url, bitmap);
	                    mSoftCache.remove(url);
	                    return bitmap;
	                } else {
	                    mSoftCache.remove(url);
	                }
	            }
	        }
	        return null;
	    } 
	                                                                                  
	    /**
	     * 娣诲姞鍥剧墖鍒扮紦瀛?
	     */
	    public void addBitmapToCache(String url, Bitmap bitmap) {
	        if (bitmap != null) {
	            synchronized (mLruCache) {
	                mLruCache.put(url, bitmap);
	            }
	        }
	    }
	                                                                                  
	    public void clearCache() {
	        mSoftCache.clear();
	    }
	}


