package com.iyuba.toeiclistening.sqlite;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.entity.User;

import timber.log.Timber;

public class ZDBHelper {

    // 常量
    public int TEST_TYPE_VALUE = 101;// 101是属于托业听力类型的旧试题 新题102
    public int TEST_TYPE_VALUE_2 = 102;// 101是属于托业听力类型的旧试题 新题102

    public String DBNAME = "zzaidb";// 数据库名
    public String TABLE_PACKINFO = "PackInfo";// 答案表名
    public String TABLE_TITLEINFO = "TitleInfo";// 文章句子的表名
    public String TABLE_FAVORITEWORD = "FavoriteWord";// 收藏单词表名
    public String TABLE_USER = "user";

    // packInfo表字段
    public String FIELD_PACKNAME = "PackName";
    public String FIELD_ISVIP = "IsVip";
    public String FIELD_ISDOWNLOAD = "IsDownload";
    public String FIELD_PROGRESS = "Progress";
    public String FIELD_ID = "id";
    public String FIELD_ISFREE = "IsFree";
    public String FIELD_PRODUCTID = "ProductID";
    public String FIELD_TEST_TYPE = "TestType";

    //ArticleInfo表的字段
    public String FIELD_TESTTYPE = "TestType";
    public String FIELD_PARTTYPE = "PartType";
    public String FIELD_TITLENUM = "TitleNum";
    public String FIELD_QUESNUM = "QuesNum";
    public String FIELD_SOUNDTIME = "SoundTime";
    public String FIELD_VIP = "Vip";
    public String FIELD_ENTEXT = "EnText";
    public String FIELD_CNTEXT = "CnText";
    public String FIELD_JPTEXT = "JpText";
    public String FIELD_ENEXPLAIN = "EnExplain";
    public String FIELD_CNEXPLAIN = "CnExplain";
    public String FIELD_JPEXPLAIN = "JpExplain";
    public String FIELD_TITLENAME = "TitleName";
    public String FIELD_HANDLE = "Handle";
    public String FIELD_FAVORITE = "Favorite";//Favorite
    public String FIELD_RIGHTNUM = "RightNum";
    public String FIELD_STUDYTIME = "StudyTime";

    // FavoriteWord表的字段
    public String FIELD_WORD = "Word";
    public String FIELD_AUDIO = "audio";
    public String FIELD_PRON = "pron";
    public String FIELD_DEF = "def";
    public String FIELD_ISCLOUD = "isCloud";
    public String FIELD_CREATEDATE = "CreateDate";
    public String FIELD_USERNAME = "userName";

    // user表的字段

    public String FIELD_DEADLINE = "deadline";
    public String FIELD_ISVIP_USER = "isvip";
    public String FIELD_CREATEDATE_USER = "createDate";
    //blog 表的字段
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

    private SQLiteDatabase mDB = null;

    private Context mContext;

    public ZDBHelper(Context context) {
        mContext = context;
        mDB = openDatabase();
    }


    /**
     * 功能：打开数据库
     */

    private SQLiteDatabase openDatabase() {

//		Log.d("ZDBHelper中打开数据库的地址", ZDBManager.DB_PATH + "/"
//				+ ZDBManager.DB_NAME);

        return SQLiteDatabase.openOrCreateDatabase(ZDBManager.DB_PATH + "/"
                + ZDBManager.DB_NAME, null);
    }

    /**
     * 修复数据库中的错误数据
     */
    public void resetError() {
//        if (!mDB.isOpen()) {
//            mDB = openDatabase();
//        }
//        String sqlSting = "select * from TitleInfo" + " where PackName='"
//                + packName + "' and TestType=" + TEST_TYPE_VALUE
//                + " order by TitleNum asc";
    }

    /**
     * 得到packInfo中的数据，绑定在testlib界面的lisview上
     *
     * @return
     */
    public ArrayList<PackInfo> getPackInfo() {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<PackInfo> packlist = new ArrayList<PackInfo>();
        Cursor sumC = null;
        Cursor packInfoC = null;
        String getSum = "select count(*) as TitleSum,sum(RightNum) as RightSum,sum(QuesNum) as QuesSum"
                + " from PackInfo,TitleInfo"
                + " where PackInfo.PackName=TitleInfo.PackName " +
                //"and PackInfo.TestType = "+ TEST_TYPE_VALUE //+
                "and TitleInfo.TestType=PackInfo.TestType "
                + " group by TitleInfo.PackName,PackInfo.TestType "
                + " order by id";

        String getPackInfoString = "select * from PackInfo"
                + " where PackInfo.PackName in"
                + " (select PackName from TitleInfo" + " where TestType="
                + TEST_TYPE_VALUE + " or TestType= " + TEST_TYPE_VALUE_2 + ")" + " order by id asc";
        try {
            sumC = mDB.rawQuery(getSum, null);
            packInfoC = mDB.rawQuery(getPackInfoString, null);
            if (sumC.getCount() > 0) {
                sumC.moveToFirst();
                packInfoC.moveToFirst();
                for (int i = 0; i < sumC.getCount(); i++) {
                    PackInfo pInfo = new PackInfo();
                    // 从cursor packInfoC中获取数据
                    pInfo.id = packInfoC.getInt(packInfoC
                            .getColumnIndex(FIELD_ID));
                    pInfo.IsDownload = Boolean.getBoolean(packInfoC
                            .getString(packInfoC
                                    .getColumnIndex(FIELD_ISDOWNLOAD)));
                    pInfo.IsFree = Boolean.parseBoolean(packInfoC
                            .getString(packInfoC.getColumnIndex(FIELD_ISFREE)));
                    pInfo.IsVip = Boolean.parseBoolean(packInfoC
                            .getString(packInfoC.getColumnIndex(FIELD_ISVIP)));
                    pInfo.PackName = packInfoC.getString(packInfoC
                            .getColumnIndex(FIELD_PACKNAME));
                    pInfo.ProductId = packInfoC.getString(packInfoC
                            .getColumnIndex(FIELD_PRODUCTID));
                    pInfo.Progress = packInfoC.getFloat(packInfoC
                            .getColumnIndex(FIELD_PROGRESS));
                    pInfo.TestType = packInfoC.getInt(packInfoC
                            .getColumnIndex(FIELD_TEST_TYPE));

                    // 从cursor sumC中获取数据
                    pInfo.RightSum = sumC.getInt(sumC
                            .getColumnIndex("RightSum"));
                    pInfo.TitleSum = sumC.getInt(sumC
                            .getColumnIndex("TitleSum"));
                    pInfo.QuestionSum = sumC.getInt(sumC
                            .getColumnIndex("QuesSum"));

                    packlist.add(i, pInfo);

                    // 指针移动到下一条数据
                    sumC.moveToNext();
                    packInfoC.moveToNext();
                }
            }
            sumC.close();
            packInfoC.close();
            mDB.close();

        } catch (Exception e) {
            LogUtil.e("获取试题列表数据异常" + e);
        } finally {
            if (sumC != null) {
                sumC.close();
            }
            if (packInfoC != null)
                if (mDB != null)
                    mDB.close();
        }
        return packlist;
    }

    /**
     * 从指定的包里面获取试题
     */

    public ArrayList<TitleInfo> getTitleInfos(String packName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<TitleInfo> titlelist = new ArrayList<TitleInfo>();
        String sqlSting = "select * from TitleInfo" + " where PackName='"
                + packName + "' and TestType= " + testType + " order by TitleNum asc";
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    TitleInfo titleInfo = new TitleInfo();
                    // 获取titleInfo表中的数据
//                    titleInfo.CnExplain = Boolean.parseBoolean(cursor
//                            .getBlob(cursor.getColumnIndex(FIELD_CNEXPLAIN)));
                    titleInfo.CnExplain = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_CNEXPLAIN)));
                    titleInfo.CnText = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_CNTEXT)));
                    titleInfo.EnExplain = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_ENEXPLAIN)));
                    titleInfo.Favorite = Boolean.parseBoolean(cursor.getString(cursor
                            .getColumnIndex(FIELD_FAVORITE)));
                    titleInfo.Handle = cursor.getInt(cursor
                            .getColumnIndex(FIELD_HANDLE));
                    titleInfo.JpExplain = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_JPEXPLAIN)));
                    titleInfo.JpText = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_JPTEXT)));
                    titleInfo.PackName = cursor.getString(cursor
                            .getColumnIndex(FIELD_PACKNAME));
                    titleInfo.PartType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_PARTTYPE));
                    titleInfo.SoundTime = cursor.getInt(cursor
                            .getColumnIndex(FIELD_SOUNDTIME));
                    titleInfo.TestType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TESTTYPE));
                    titleInfo.QuesNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_QUESNUM));
                    titleInfo.RightNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_RIGHTNUM));
                    titleInfo.TitleName = cursor.getString(cursor
                            .getColumnIndex(FIELD_TITLENAME));
                    titleInfo.TitleNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TITLENUM));
                    titleInfo.Vip = Boolean.parseBoolean(cursor
                            .getString(cursor.getColumnIndex(FIELD_VIP)));
                    titlelist.add(i, titleInfo);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("读取数据异常;");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDB != null) {
                mDB.close();
            }
        }
        return titlelist;
    }

    /**
     * 得到收藏的试题
     */
    public ArrayList<TitleInfo> getFavTitleInfos() {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<TitleInfo> titlelist = new ArrayList<TitleInfo>();
//        String sqlSting = "select * from TitleInfo" + " where Favorite='true'"
//                + " and (TestType=" + TEST_TYPE_VALUE + " or TestType= "+TEST_TYPE_VALUE_2 +") order by TitleNum asc";

        String sqlSting = "select * from TitleInfo" + " where Favorite='true'"
                + " order by TitleNum asc";

        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    TitleInfo titleInfo = new TitleInfo();
                    // 获取titleInfo表中的数据
                    titleInfo.CnExplain = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_CNEXPLAIN))));
                    titleInfo.CnText = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_CNTEXT))));
                    titleInfo.EnExplain = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_ENEXPLAIN))));
                    titleInfo.Favorite = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_FAVORITE))));
                    titleInfo.Handle = cursor.getInt(cursor
                            .getColumnIndex(FIELD_HANDLE));
                    titleInfo.JpExplain = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_JPEXPLAIN))));
                    titleInfo.JpText = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_JPTEXT))));
                    titleInfo.PackName = cursor.getString(cursor
                            .getColumnIndex(FIELD_PACKNAME));
                    titleInfo.PartType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_PARTTYPE));
                    titleInfo.SoundTime = cursor.getInt(cursor
                            .getColumnIndex(FIELD_SOUNDTIME));
                    titleInfo.TestType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TESTTYPE));
                    titleInfo.QuesNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_QUESNUM));
                    titleInfo.RightNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_RIGHTNUM));
                    titleInfo.TitleName = cursor.getString(cursor
                            .getColumnIndex(FIELD_TITLENAME));
                    titleInfo.TitleNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TITLENUM));
                    titleInfo.Vip = Boolean.parseBoolean(Arrays.toString(cursor
                            .getBlob(cursor.getColumnIndex(FIELD_VIP))));
                    titlelist.add(i, titleInfo);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("收藏数据查询异常！" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDB != null) {
                mDB.close();
            }
        }
        return titlelist;
    }

    /**
     * 重置是否收藏文章
     *
     * @param titleNum
     * @param state
     * @return
     */
    public boolean setFavoriteTitle(int titleNum, boolean state) {
//        String sqlString = "update TitleInfo" + " set Favorite='" + state + "'"
//                + " where TestType=" + TEST_TYPE_VALUE + " and TitleNum="
//                + titleNum;
        String text = String.valueOf(state);
        String sqlString = "update TitleInfo" + " set Favorite='" + text + "'"
                + " where TitleNum=" + titleNum;
        return exeSql(sqlString);
        // 更新titleInfoList中的数据
    }

    /**
     * 更新PackInfo中的状态
     *
     * @param packName
     * @param progress
     * @return
     */
    public boolean setState(String packName, boolean isDownload, boolean isFree, float progress) {
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update PackInfo"
                + " set IsDownLoad='" + isDownload + "', Progress=" + progress + ", IsFree='" + isFree + "'"
                + " where PackName='" + packName + "'";
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    /**
     * 更新PackInfo中的下载状态
     */

    public boolean setDownloadState(String packName, boolean state) {
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update PackInfo"
                + " set IsDownLoad='" + state + "'" + ",isFree='false', Progress=0"
                + " where PackName='" + packName + "'";
        System.out.println(sqlString);
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    /**
     * 更新PackInfo中的下载进度
     *
     * @param packName
     * @param progress
     * @return
     */
    public boolean setProgress(String packName, float progress) {
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update PackInfo"
                + " set IsDownLoad='true', Progress=" + progress
                + " where PackName='" + packName + "'";
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    public boolean setProgress(String packName, float progress, boolean isdownload) {
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update PackInfo"
                + " set IsDownLoad='" + isdownload + "', Progress=" + progress
                + " where PackName='" + packName + "'";
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    /**
     * 更新Title中的正确题目信息
     *
     * @param titleNum
     * @param rightNum
     * @return
     */
    public boolean setRightNum(int titleNum, int rightNum) {
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update TitleInfo" + " set RightNum=" + rightNum
                + " where TitleNum=" + titleNum;
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    /**
    * 更新Title中学习时间
    * */
    public boolean updateStudyTime(int titleNum, int studyTime){
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update TitleInfo" + " set StudyTime=" + studyTime
                + " where TitleNum=" + titleNum;
        try {
            result = exeSql(sqlString);
//            Timber.e("更新Title中学习时间 ==== " + sqlString);
            mDB.close();
        } catch (Exception e) {
        }
        finally {
//            if (mDB != null) {
//                mDB.close();
//            }
        }
        return result;
    }

    /**
     * 获取TitleInfo中的学习时间
    */
    public int getStudyTime(int titleNum){
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select StudyTime from TitleInfo " +
                    "where TitleNum=" + titleNum;
//            Timber.e("获取TitleInfo中的学习时间 === " + sqlString);
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
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
        return sum;
    }

    /**
     * 获取TitleInfo中的单项录音时间
     */
    public int getSoundTime(int titleNum){
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select SoundTime from TitleInfo " +
                    "where TitleNum=" + titleNum;
//            Timber.e("获取TitleInfo中的单项录音时间 === " + sqlString);
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
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
        return sum;
    }

    /**
     * 更新Title中的题目学习进度信息
     *
     * @param titleNum
     * @param studyTime
     * @return
     */
    public boolean setStudyTime(int titleNum, int studyTime){
        boolean result = false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update TitleInfo" + " set StudyTime=" + studyTime
                + " where TitleNum=" + titleNum;
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
        return result;
    }

    //获得某一测试的总时间
    public int getTotalTime(String packName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select sum (SoundTime) from TitleInfo " +
                    "where PackName=" + "'" + packName + "'" + " and TestType=" + testType;
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
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
        return sum;
    }

    //获得某一测试的总学习时间
    public int getStudyTime(String packName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select sum(StudyTime) from TitleInfo " +
                    "where PackName=" + "'" + packName +"'" + " and TestType=" + testType;
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
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
        return sum;
    }

    //获取某一题的正确个数
    public int getRightNum(int titleNum) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select RightNum from TitleInfo " +
                    "where TitleNum=" + titleNum;
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (mDB != null) {
//                mDB.close();
//            }
        }
        return sum;
    }

    /**
     * 获取Title中的正确题目信息
     *
     * @return
     */
    public int getRightNum(String packName, int TestType) {
//        String sqlString = "select Sum(RightNum) from TitleInfo where PackName=" + packName;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString1 = "select Sum(RightNum) from TitleInfo " +
                    "where PackName='" + packName + "'and TestType= " + TestType;
            cursor = mDB.rawQuery(sqlString1, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
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
//        Log.d("ZDBHelper中获取PackInfo的名称:", packName);
//        Log.d("ZDBHelper中获取PackInfo的编号:", packNum + "");

        return sum;
    }

    /**
     * 更新Title中做题正确数
     * */
    public boolean updateRightNum(int titleNum, int rightNum){
        boolean result=false;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update TitleInfo set RightNum=" + rightNum
                + " where TitleNum=" + titleNum;
        try {
            result = exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
//            if (mDB != null) {
//                mDB.close();
//            }
        }
        return result;

    }


    /**
     * 添加到收藏的单词
     *
     * @param newWord
     * @return
     */
    public boolean saveNewWord(NewWord newWord) {
        String sqlString = "insert into FavoriteWord(word,audio,pron,def,userName,CreateDate)"
                + " values(?,?,?,?,?,?)";
        Object[] objects = new Object[]{newWord.Word,
                newWord.audio, newWord.pron, newWord.def, newWord.userName, newWord.CreateDate};

        //未添加用户名的查询命令
//		String sqlString = "insert into FavoriteWord(word,audio,pron,def,CreateDate)"
//				+" values(?,?,?,?,?)";
//		Object[] objects=new Object[]{newWord.Word,
//				newWord.audio,newWord.pron,newWord.def,newWord.CreateDate};

//		Log.d("NewWord Content:", "word: " + newWord.Word + " audio:" 
//				+ newWord.audio + " pron:" + newWord.pron + " def:" 
//				+ newWord.def + " userName:" + newWord.userName + " CreateDate"
//				+ newWord.CreateDate);

        return exeSql(sqlString, objects);
    }

    /**
     * 移除收藏的单词
     */
    public boolean deleteNewWord(String word) {
        String sqlString = "	delete from FavoriteWord" + " where Word='" + word
                + "'";
        return exeSql(sqlString);
    }


    /**
     * 包装的辅助执行数据库操作
     */
    public boolean exeSql(String sqlString) {
        boolean flag;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        try {
            mDB.execSQL(sqlString);
            mDB.close();
            flag = true;
        } catch (Exception e) {
            flag = false;
            LogUtil.e("数据库操作失败" + e);
            LogUtil.e("数据库操作失败" + sqlString);
            e.printStackTrace();
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return flag;
    }

    public boolean exeSql(String sqlString, Object[] objects) {
        boolean flag;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
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

    /**
     * 返回收藏的单词
     */
    public ArrayList<NewWord> getNewWords(String userName) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<NewWord> favWordlist = new ArrayList<NewWord>();
        String sqlSting = "select * from FavoriteWord where userName='" + userName + "'";
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    NewWord newWord = new NewWord();
                    newWord.audio = cursor.getString(cursor
                            .getColumnIndex(FIELD_AUDIO));
                    newWord.CreateDate = cursor.getString(cursor
                            .getColumnIndex(FIELD_CREATEDATE));
                    newWord.def = cursor.getString(cursor
                            .getColumnIndex(FIELD_DEF));
                    newWord.id = cursor.getInt(cursor
                            .getColumnIndex(FIELD_ID));
                    newWord.pron = cursor.getString(cursor
                            .getColumnIndex(FIELD_PRON));
                    newWord.Word = cursor.getString(cursor
                            .getColumnIndex(FIELD_WORD));
                    newWord.userName = cursor.getString(cursor
                            .getColumnIndex(FIELD_USERNAME));
                    favWordlist.add(i, newWord);

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
        return favWordlist;
    }

    /*
     * 获取PackInfo的编号
     * */
    public int getDownPackNum(String packName) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int packNum = 0;
        try {
            String sqlString1 = "select id from PackInfo" + " where PackName = '" + packName + "'";
            cursor = mDB.rawQuery(sqlString1, null);
            cursor.moveToFirst();
            packNum = cursor.getInt(0);
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
        LogUtil.d("ZDBHelper中获取PackInfo的名称:", packName);
        LogUtil.d("ZDBHelper中获取PackInfo的编号:", packNum + "");

        return packNum;
    }

    /**
     * 增加用户
     */
    public void addUser(User user) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = " insert into user" + " values('" + user.userName
                + "'," + user.isvip + ",'" + user.createDate + "','"
                + user.deadline + "')";
        try {
            exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
    }

    /**
     * 修改用户信息
     *
     * @param user
     */
    public void updateUser(User user) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update user" + " set isvip=" + user.isvip
                + ",createDate='" + user.createDate + "',deadline='"
                + user.deadline + "'" + " where userName='" + user.userName + "'";
        try {
            exeSql(sqlString);
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (mDB != null) {
                mDB.close();
            }
        }
    }

    /**
     *获取用户信息
     *
     *
     */
    public User getUser(String userName) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlSting = "select * from user " + " where userName='" + userName
                + "'";
        User user = null;
        Cursor cursor = null;
        try {

            cursor = mDB.rawQuery(sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {

                user = new User();
                user.createDate = cursor.getString(cursor
                        .getColumnIndex(FIELD_CREATEDATE_USER));
                user.deadline = cursor.getString(cursor
                        .getColumnIndex(FIELD_DEADLINE));
                user.isvip = cursor.getInt(cursor
                        .getColumnIndex(FIELD_ISVIP_USER));
                user.userName = userName;
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
        return user;
    }

}
