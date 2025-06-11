package com.iyuba.core.common.base;

/**
 * 程序崩溃后操作
 *
 * @version 1.0
 * @author 陈彤
 * 修改日期    2014.3.29
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.R;
import com.iyuba.core.common.util.NetWorkHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class CrashApplication extends Application {
    private static final List<Activity> activityList = new LinkedList<>();

    // 全局volley请求队列队列
    private static com.android.volley.RequestQueue queue;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void init(Context context) {

        RuntimeManager.setApplicationContext(context);
        RuntimeManager.setApplication((Application) context);
        //没有开启智能验证
        initImageLoader(context);

        queue = Volley.newRequestQueue(context);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
        NetWorkHelper.init(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private static void initImageLoader(Context context) {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.nearby_no_icon) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.nearby_no_icon) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.nearby_no_icon) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)//线程池内加载的数量
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }

    // 程序加入运行列表
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 程序退出
    public static void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        //System.exit(0);
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static RequestQueue getQueue() {
        return queue;
    }
}