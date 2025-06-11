package com.iyuba.core.common.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.R;
import com.iyuba.core.common.sqlite.db.DBOpenHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库管理
 *
 * @author chentong
 */
public class ImportLibDatabase {
    private final int BUFFER_SIZE = 1048576;
    private static final String DB_NAME = "lib_database.sqlite"; // 保存的数据库文件名
    private String PACKAGE_NAME;
    private String DB_PATH;
    public static DBOpenHelper mdbhelper = new DBOpenHelper(
            RuntimeManager.getContext());
    private static SQLiteDatabase database = null;
    private static Context mContext;
    private int lastVersion, currentVersion;

    public ImportLibDatabase(Context context) {
        mContext = context;
    }

    public String getDBPath() {
        return DB_PATH + "/" + DB_NAME;
    }

    /*
     * 传入报名 导入数据库
     */
    public void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
        DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/" + PACKAGE_NAME + "/" + "databases";
    }

    public void setVersion(int lastVeision, int curVersion) {
        this.lastVersion = lastVeision;
        this.currentVersion = curVersion;
    }

    public synchronized SQLiteDatabase openDatabase() {
        this.database = mdbhelper.getWritableDatabase();
        return this.database;
    }

    /**
     * 打开数据库 根据版本判断是否需要更新
     *
     * @param dbfile
     */
    public synchronized void openDatabase(String dbfile) {
        lastVersion = ConfigManager.Instance().loadInt("lib_database_version");//存储的以前版本
        File database = new File(dbfile);
        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
            }
            loadDataBase(dbfile);
            ConfigManager.Instance().putInt("lib_database_version", currentVersion);
        }
    }

    public void closeDatabase() {

    }

    /**
     * 更换数据库
     *
     * @param dbfile
     */
    private void loadDataBase(String dbfile) {
        try {
            InputStream is = mContext.getResources().openRawResource(
                    R.raw.lib_database);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (!(new File(DB_PATH).exists())) {
                new File(DB_PATH).mkdir();
            }
            FileOutputStream fos = new FileOutputStream(dbfile);
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = bis.read(buffer)) > 0) {
                bfos.write(buffer, 0, count);
            }
            bfos.flush();
            fos.close();
            is.close();
            bis.close();
            bfos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
