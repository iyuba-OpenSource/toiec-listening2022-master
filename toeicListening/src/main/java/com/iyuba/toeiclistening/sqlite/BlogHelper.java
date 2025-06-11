package com.iyuba.toeiclistening.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iyuba.toeiclistening.entity.BlogContent;


public class BlogHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "db_blog";//数据库的名称
    public static final String TABLE_NAME_BLOG = "blog";
    public static final String _ID = "_id";
    public static final String BLOGID = "blogid";
    public static final String SUBJECT = "subject";
    public static final String VIEWNUM = "viewnum";
    public static final String REPLYNUM = "replynum";
    public static final String DATELINE = "dateline";
    public static final String NOREPLY = "noreply";
    public static final String FRIEND = "friend";
    public static final String PASSWORD = "password";
    public static final String FAVTIMES = "favtimes";
    public static final String SHARETIMES = "sharetimes";
    public static final String MESSAGE = "message";
    public static final String IDS = "ids";
    public static final String USERNAME = "username";
    public static final String UID = "uid";
    public static int VERSION = 1;//数据库升级时 更新 这个version
    private Context mContext;
    private SQLiteDatabase mDB;


    public BlogHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sqlString = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BLOG + " ("
                + _ID + " integer primary key autoincrement, " + BLOGID
                + " text unique, " + SUBJECT + " text, " + VIEWNUM + " text, " + REPLYNUM
                + " text," + DATELINE + " text," + NOREPLY + " text," + FRIEND + " text," + PASSWORD + " text," +
                FAVTIMES + " text," + SHARETIMES + " text," + MESSAGE + " text," + IDS + " text," + USERNAME + " text,"
                + UID + " text)";
        db.execSQL(sqlString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    //批量添加资讯  更新数据库里面的内容
    public void insertBlogs(List<BlogContent> blogs) {
        if (blogs != null && blogs.size() != 0) {
            String sqlString = "insert into " + TABLE_NAME_BLOG + " (" + BLOGID + ","
                    + SUBJECT + "," + VIEWNUM + "," + REPLYNUM + ","
                    + DATELINE + "," + NOREPLY + "," + FRIEND + ","
                    + PASSWORD + "," + FAVTIMES + "," + SHARETIMES + ","
                    + MESSAGE + "," + IDS + "," + USERNAME + ","
                    + UID + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            for (int i = 0; i < blogs.size(); i++) {
                BlogContent blog = blogs.get(i);
                Object[] objects = new Object[]{blog.blogid, blog.subject, blog.viewnum,
                        blog.replynum, blog.dateline, blog.noreply,
                        blog.friend, blog.password, blog.favtimes, blog.sharetimes,
                        blog.message, blog.ids, blog.username, blog.uid};
                exeSql(sqlString, objects);
            }
        }
    }

    /**
     * 删除  数据库里面的资讯
     */
    public boolean deleteBlogData() {
        String sqlString = "delete from blog";
        return exeSql(sqlString);
    }

    /**
     * @param startid 开始blogid
     * @param endid   结束blogid
     * @param type=1  选择前15条记录 type=2 选择从stattid到endid之间的记录
     * @return
     */
    public ArrayList<BlogContent> getBlogs(int startid, int endid, int type) {
        mDB = getReadableDatabase();
        String sqlSting = "";
        if (type == 2) {
            sqlSting = "select * from blog where blogid>=" + startid + " and blogid< " + endid
                    + " order by blogid desc";
            Log.e("sql", sqlSting);
        } else {//sqlite查询 选择前15条  要用limit这个方式
            sqlSting = "select * from blog order by blogid desc limit 0,10";
        }
        ArrayList<BlogContent> blogContents = new ArrayList<BlogContent>();
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    BlogContent blog = new BlogContent();
                    blog._id = cursor.getInt(0);
                    blog.blogid = cursor.getString(1);
                    blog.subject = cursor.getString(2);
                    blog.viewnum = cursor.getString(3);
                    blog.replynum = cursor.getString(4);
                    blog.dateline = cursor.getString(5);
                    blog.noreply = cursor.getString(6);
                    blog.friend = cursor.getString(7);
                    blog.password = cursor.getString(8);
                    blog.favtimes = cursor.getString(9);
                    blog.sharetimes = cursor.getString(10);
                    blog.message = cursor.getString(11);
                    blog.ids = cursor.getString(12);
                    blog.username = cursor.getString(13);
                    blog.uid = cursor.getString(14);
                    blogContents.add(i, blog);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDB != null) {
                mDB.close();
            }
        }
        Log.e("bb", blogContents.size() + "'");
        return blogContents;
    }

    /*
     * 根据日志的ID单条获取日志的内容
     * */
    public void saveBlogContentData(BlogContent blogs) {
        //deleteBlogData();

        if (blogs != null) {
            //Log.d("insert", "111111111111");
            try {
                String sqlString =
                        "update " + TABLE_NAME_BLOG + " set MESSAGE='" + blogs.message + "'" + " where blogid='"
                                + blogs.blogid + "'";
                exeSql(sqlString);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }

    /**
     * @return
     */
    public ArrayList<BlogContent> findDataByAll() {
        ArrayList<BlogContent> blogs = new ArrayList<BlogContent>();
        mDB = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(
                    "select *" + " from " + TABLE_NAME_BLOG + " ORDER BY " + BLOGID + " desc"
                    , new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                BlogContent blog = new BlogContent();
                blog._id = cursor.getInt(0);
                blog.blogid = cursor.getString(1);
                blog.subject = cursor.getString(2);
                blog.viewnum = cursor.getString(3);
                blog.replynum = cursor.getString(4);
                blog.dateline = cursor.getString(5);
                blog.noreply = cursor.getString(6);
                blog.friend = cursor.getString(7);
                blog.password = cursor.getString(8);
                blog.favtimes = cursor.getString(9);
                blog.sharetimes = cursor.getString(10);
                blog.message = cursor.getString(11);
                blog.ids = cursor.getString(12);
                blog.username = cursor.getString(13);
                blog.uid = cursor.getString(14);
                blogs.add(blog);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDB != null) {
                mDB.close();
            }
        }
        Log.e("bb", blogs.size() + "'");
        return blogs;
    }

    public boolean exeSql(String sqlString) {
        boolean flag;
        mDB = getReadableDatabase();
        try {
            mDB.execSQL(sqlString);
            mDB.close();
            flag = true;
        } catch (Exception e) {
            flag = false;
            // TODO: handle exception
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return flag;
    }

    public boolean exeSql(String sqlString, Object[] objects) {
        boolean flag;
        mDB = getReadableDatabase();
        try {
            mDB.execSQL(sqlString, objects);
            mDB.close();
            flag = true;
        } catch (Exception e) {
            flag = false;
            // TODO: handle exception
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return flag;
    }

    /*
     * findMaxBlogId():查找本地的blogId的最大值
     * */
    public int findMaxBlogId() {
        int maxBlogId = 0;
        mDB = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(
                    "select max(" + BLOGID + ") from " + TABLE_NAME_BLOG, new String[]{});
            cursor.moveToFirst();
            maxBlogId = cursor.getInt(0);
            Log.e("Test findMaxBlogId", maxBlogId + "");

            return maxBlogId;
                /*
                dbOpenHelper.getWritableDatabase().execSQL(
						"select max(" + BLOGID + ") from TABLE_NAME_BLOG");
				*/
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

}
