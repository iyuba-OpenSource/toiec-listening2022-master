package com.iyuba.core.homepage.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.homepage.entity.NewsInfo;
import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 */
public class HeadCourseContent extends DatabaseService {

    public static final String TABLE_NAME_HEADCOURSECONTENT = "HeadCourseContent";
    public static final String ID = "id";
    public static final String APPID = "appid";
    public static final String TITLE = "title";
    public static final String TITLECN = "title_cn";
    public static final String DATE = "date";
    public static final String RECORD = "record";
    public static final String PICURL = "pic_url";

    public static final String TABLE_NAME_HEADIMAGE = "HeadImage";
    public static final String PRICE = "price";
    public static final String NAME = "name";
    public static final String OWNERID = "ownerid";
    public static final String PIC = "pic";
    public static final String DESC = "desc";


    public HeadCourseContent(Context context) {
        super(context);
    }

    public synchronized void insertNewsInfo(List<NewsInfo> courses) {
        if (courses != null && courses.size() != 0) {
            String sqlString = "insert into " + TABLE_NAME_HEADCOURSECONTENT + " (" + ID + ","
                    + APPID + "," + TITLE + "," + TITLECN + ","
                    + DATE + "," + RECORD + "," + PICURL + ") values(?,?,?,?,?,?,?)";
            if (courses.get(0).itemId == "3") {
                for (int i = 0; i < courses.size(); i++) {
                    NewsInfo course = courses.get(i);
                    Object[] objects = new Object[]{course.itemId, course.id, course.titleEn,
                            "歌手 "+course.singer+"\n"+"播音 "+course.announcer, course.time, course.readTimes, course.picUrl};
                    importDatabase.openDatabase().execSQL(sqlString, objects);

                    closeDatabase(null);
                }
            } else {
                for (int i = 0; i < courses.size(); i++) {
                    NewsInfo course = courses.get(i);
                    Object[] objects = new Object[]{course.itemId, course.id, course.titleEn,
                            course.title, course.time, course.readTimes, course.picUrl};
                    importDatabase.openDatabase().execSQL(sqlString, objects);

                    closeDatabase(null);
                }
            }
        }
    }

    public synchronized void insertSlideShowCourse(List<SlideShowCourse> courses) {
        if (courses != null && courses.size() != 0) {
            String sqlString = "insert into " + TABLE_NAME_HEADIMAGE + " (" + ID + ","
                    + PRICE + "," + NAME + "," + OWNERID + ","
                    + PIC + "," + DESC + ") values(?,?,?,?,?,?)";
                for (int i = 0; i < courses.size(); i++) {
                    SlideShowCourse course = courses.get(i);
                    Object[] objects = new Object[]{course.id, course.price,
                            course.name, course.ownerid, course.pic, course.desc1};
                    importDatabase.openDatabase().execSQL(sqlString, objects);

                    closeDatabase(null);
                }

        }
    }


    public synchronized boolean deleteHeadCourseContentData() {
        String sqlString = "delete from HeadCourseContent";
        try {
            importDatabase.openDatabase().execSQL(sqlString);
            closeDatabase(null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public synchronized boolean deleteHeadImage() {
        String sqlString = "delete from HeadImage";
        try {
            importDatabase.openDatabase().execSQL(sqlString);
            closeDatabase(null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public synchronized ArrayList<NewsInfo> findDataByAll() {
        ArrayList<NewsInfo> courses = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACKTYPE + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
                    "select *" + " from " + TABLE_NAME_HEADCOURSECONTENT
                    , new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                NewsInfo course = new NewsInfo();
                course.id = cursor.getInt(0);
                course.itemId = cursor.getString(1);
                course.titleEn = cursor.getString(2);
                course.title = cursor.getString(3);
                course.time = cursor.getString(4);
                course.readTimes = cursor.getString(5);
                course.picUrl = cursor.getString(6);
                courses.add(course);
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return courses;
    }


    public synchronized ArrayList<SlideShowCourse> findDataByAllHeadImage() {
        ArrayList<SlideShowCourse> courses = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACKTYPE + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
                    "select *" + " from " + TABLE_NAME_HEADIMAGE
                    , new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                SlideShowCourse course = new SlideShowCourse();
                course.id = cursor.getInt(0);
                course.price = cursor.getDouble(1);
                course.name = cursor.getString(2);
                course.ownerid = cursor.getInt(3);
                course.pic = cursor.getString(4);
                course.desc1 = cursor.getString(5);
                courses.add(course);
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return courses;
    }

}
