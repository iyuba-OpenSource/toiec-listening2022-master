package com.iyuba.toeiclistening.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.toeiclistening.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 功能：拷贝res/raw目录下数据库文件到data中，防止用户误删
 * 参考：http://www.cnblogs.com/xiaowenji/archive/2011/01/03/1925014.html
 */
public class TEDBManager {
    private final int BUFFER_SIZE = 1048576;//1MB
    public static final String DB_NAME = "toeiclistening.sqlite3"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.iyuba.toeiclistening";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //在手机里存放数据库的位置

    private SQLiteDatabase database;
    private Context context;
    private int VERSION = 1;
    private int lastVersion, currentVersion;

    public TEDBManager(Context context) {
        this.context = context;
        Log.d("12121212121212121212", "测试连接数据库操作");
    }

    //设置版本啊
    public void setVersion(int lastVeision, int curVersion) {
        this.lastVersion = lastVeision;
        this.currentVersion = curVersion;
    }

    /**
     * 功能：初始化时调用
     */
    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
        Log.d("34343434343434343434", "测试连接数据库操作");
    }

    /**
     * 打开数据库 根据版本判断是否需要更新
     *
     * @param dbfile
     */
    public SQLiteDatabase openDatabase(String dbfile) {
        //两个数据库都用这个来标识了，更新就一起更新 toeiclistening.sqlite3；zzaidb.sqlite
        lastVersion = ConfigManager.Instance().loadInt("toeiclistening_database_version");
        File database = new File(dbfile);
        LogUtil.e("toeiclistening旧数据库版本：" + lastVersion + "新数据库版本：" + currentVersion);
        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
            }

            ConfigManager.Instance().putInt("toeiclistening_database_version", currentVersion);

            return loadDataBase(dbfile);
        } else {
            return loadDataBase(dbfile);
        }
    }

    /**
     * @param dbfile
     * @return 功能：操作指定地址的数据库文件时调用
     */
    private SQLiteDatabase loadDataBase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(R.raw.toeiclistening); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }//do something else here

    public void closeDatabase() {
        this.database.close();
    }
}
