package com.iyuba.toeiclistening.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.entity.EvaluateBean;
import com.iyuba.toeiclistening.entity.Answer;
import com.iyuba.toeiclistening.entity.DownTest;
import com.iyuba.toeiclistening.entity.Explain;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author toeiclistening表的数据库操作
 */
public class TEDBHelper {
    public String DBNAME = "com.iyuba.toeiclistening";// 数据库名
    public String TABLE_ANSWER = "Answer";// 答案表名
    public String TABLE_TEXT = "Text";// 文章句子的表名
    public String TABLE_REMEMBER = "remember";// 文章句子的表名

    // Answer表字段
    public String FIELD_TESTTYPE = "TestType";
    public String FIELD_PARTTYPE = "PartType";
    public String FIELD_TITLENUM = "TitleNum";
    public String FIELD_QUESINDEX = "QuesIndex";
    public String FIELD_QUESTEXT = "QuesText";
    public String FIELD_QUESIMAGE = "QuesImage";
    public String FIELD_ANSWERNUM = "AnswerNum";
    public String FIELD_ANSWERTEXT = "AnswerText";
    public String FIELD_ANSWER = "Answer";
    public String FIELD_SOUNDA = "Sound";// answer的语音
    public String FIELD_ISSINGLE = "IsSingle";
    public String FIELD_IS_EVALUATE = "IsEvaluate";//是否评测过

    private String EVALUATE_TEXTID = "TextId";
    private String EVALUATE_INDEX_ID = "IndexId";
    private String EVALUATE_TOTAL_SCORE = "totalScore";
    private String EVALUATE_TEXTNUM = "textNum";
    private String EVALUATE_SCORE = "textScore";
    private String EVALUATE_URL = "url";
    // text表的字段
    public String FIELD_TITLENAME = "TitleName";
    public String FIELD_SENINDEX = "SenIndex";
    public String FIELD_SENTENCE = "Sentence";
    public String FIELD_TIMING = "Timing";
    public String FIELD_SOUNDT = "Sound";// 文章的语音

    //Explain表的字段
    public static String FIELD_EXPLAIN = "Explain";

    //remember表的字段
    private String WORD_WORD = "word";
    private String WORD_PRON = "pron";
    private String WORD_EXPLAIN = "explain";
    private String WORD_EXPLAIN_EN = "explainEn";
    private String WORD_SENTENCE = "sentence";
    private String WORD_SENTENCE_CN = "sentenceCn";
    private String WORD_SENTENCE_SOUNED = "sentenceSound";
    private String WORD_SOUND = "sound";
    private String WORD_COLLECT = "collect";
    private String WORD_STATUES = "statues";
    private String WORD_STAGE = "stage";
    private String WORD_FLAG = "flag";


    private SQLiteDatabase mDB = null;
    private ZDBHelper zdHelper;
    private Context mContext;

    public TEDBHelper(Context context) {
        mContext = context;
        mDB = openDatabase();
        zdHelper = new ZDBHelper(context);
    }

    /**
     * 功能：打开数据库
     */
    private SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openOrCreateDatabase(TEDBManager.DB_PATH + "/"
                + TEDBManager.DB_NAME, null);
    }


    /**
     * 随机获取一个单词
     *
     * @return
     */
    public RememberWord getRandomWord() {

        String sqlStr = "SELECT * FROM remember ORDER BY RANDOM() limit 1";
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = mDB.rawQuery(sqlStr, null);
        if (cursor.moveToNext()) {

            RememberWord word = new RememberWord();
            word.word = cursor.getString(cursor.getColumnIndexOrThrow(WORD_WORD));
            word.pron = cursor.getString(cursor.getColumnIndexOrThrow(WORD_PRON));
            word.explain = cursor.getString(cursor.getColumnIndexOrThrow(WORD_EXPLAIN));
            word.explainEn = cursor.getString(cursor.getColumnIndexOrThrow(WORD_EXPLAIN_EN));
            word.sentence = cursor.getString(cursor.getColumnIndexOrThrow(WORD_SENTENCE));
            word.sentenceCn = cursor.getString(cursor.getColumnIndexOrThrow(WORD_SENTENCE_CN));
            word.sound = cursor.getString(cursor.getColumnIndexOrThrow(WORD_SOUND));
            word.sentenceSound = cursor.getString(cursor.getColumnIndexOrThrow(WORD_SENTENCE_SOUNED));
            int ben = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_COLLECT));
            word.isCollect = (ben != 0);
            word.statues = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_STATUES));
            word.stage = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_STAGE));
            word.flag = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_FLAG));

            return word;
        } else {

            return null;
        }
    }

    @SuppressLint("Range")
    public ArrayList<RememberWord> geWords(int stage) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<RememberWord> wordList = new ArrayList<>();
        Cursor cursor = null;
        String sqlString;
        if (stage == -1) {
            sqlString = "select * from remember" + " order by word COLLATE NOCASE asc";//忽略大小写排序
        } else {
            sqlString = "select * from remember" + " where stage=" + stage + " order by word COLLATE NOCASE asc";//忽略大小写排序
        }
        try {
            cursor = mDB.rawQuery(sqlString, null);

            while (cursor.moveToNext()) {

                RememberWord word = new RememberWord();
                word.word = cursor.getString(cursor.getColumnIndex(WORD_WORD));
                word.pron = cursor.getString(cursor.getColumnIndex(WORD_PRON));
                word.explain = cursor.getString(cursor.getColumnIndex(WORD_EXPLAIN));
                word.explainEn = cursor.getString(cursor.getColumnIndex(WORD_EXPLAIN_EN));
                word.sentence = cursor.getString(cursor.getColumnIndex(WORD_SENTENCE));
                word.sentenceCn = cursor.getString(cursor.getColumnIndex(WORD_SENTENCE_CN));
                word.sound = cursor.getString(cursor.getColumnIndex(WORD_SOUND));
                word.sentenceSound = cursor.getString(cursor.getColumnIndex(WORD_SENTENCE_SOUNED));
                int ben = cursor.getInt(cursor.getColumnIndex(WORD_COLLECT));
                word.isCollect = (ben != 0);
                word.statues = cursor.getInt(cursor.getColumnIndex(WORD_STATUES));
                word.stage = cursor.getInt(cursor.getColumnIndex(WORD_STAGE));
                word.flag = cursor.getInt(cursor.getColumnIndex(WORD_FLAG));
                wordList.add(word);
//                cursor.moveToNext();
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("获取rememberWord 失败" + e);
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        if (wordList.size() != 0) {

            RememberWord word = wordList.get(0);//(w)
            wordList.remove(0);
            wordList.add(1181, word);
        }
        return wordList;
    }

    /**
     * 获取对应Test的对应Sentence集合
     */
    public ArrayList<RememberWord> geWords() {
        return geWords(-1);
    }

    public void updateWords(List<RememberWord> words) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        for (RememberWord word : words) {
            if (word.isChange) {
                int collect = 0;
                if (word.isCollect) collect = 1;
                String sqlString = "update remember set collect= " + collect + ",statues= " + word.statues
                        + ",stage= " + word.stage
                        + "  where word=  '" + word.word
                        + " ' and explainEn = '" + word.explainEn + "'";
                try {
                    mDB.execSQL(sqlString);
                    mDB.close();
                } catch (Exception e) {
                    LogUtil.e("更新单词" + e);
                    e.printStackTrace();
                }
            }
        }
        if (mDB.isOpen()) {
            mDB.close();
        }
    }

    public void updateWord(RememberWord word) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        int collect = 0;
        if (word.isCollect) collect = 1;
        String sqlString = "update remember set collect= " + collect + ",statues= " + word.statues
                + ",stage= " + word.stage
                + "  where word=  '" + word.word
                + "' and sound = '" + word.sound + "'";
        try {
            LogUtil.e("更新单词----> 开始" + sqlString);
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("更新单词" + e);
            e.printStackTrace();
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }

    }

    /**
     * 更新本地单词数据库
     */

    public void updateWordFromServer(String explain, String answer, int statues) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "update remember set statues= " + statues
                + " where explain like '" + explain + "'" + " or word like '" + answer + "'";
//        Timber.e("更新本地单词数据库 === " + sqlString);

        try {
//            LogUtil.e("从服务器更新单词----> 开始"+sqlString);
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("更新单词出现问题 === " + e);
            e.printStackTrace();
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }

    }

    /**
     * 获取已背单词的数量
     */
    public int getTotalStudiedWordsNum() {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select count() from remember " +
                    "where statues=2";
            Timber.e("获取已背单词的数量 " + sqlString);
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取已评测数量失败" + e);
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
     * 获取对应Test的对应Sentence集合
     */
    @SuppressLint("Range")
    public ArrayList<Text> geTexts(int titleNum) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<Text> textlist = new ArrayList<Text>();
        Cursor cursor = null;
        String sqlString = "select * from Text" + " where TitleNum=" + titleNum
                + " order by SenIndex asc";
        try {
            cursor = mDB.rawQuery(sqlString, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    Text text = new Text();
                    text.PartType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_PARTTYPE));
                    text.SenIndex = cursor.getInt(cursor
                            .getColumnIndex(FIELD_SENINDEX));
                    text.Sentence = cursor.getString(cursor
                            .getColumnIndex(FIELD_SENTENCE));
                    text.Sound = cursor.getString(cursor
                            .getColumnIndex(FIELD_SOUNDT));
                    text.TestType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TESTTYPE));
                    text.Timing = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TIMING));
                    text.TitleName = cursor.getString(cursor
                            .getColumnIndex(FIELD_TITLENAME));
                    text.TitleNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TITLENUM));
                    text.isEvaluateDB = cursor.getInt(cursor
                            .getColumnIndex(FIELD_IS_EVALUATE));
                    textlist.add(i, text);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("获取test 失败" + e);
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return textlist;
    }

    /**
     * 设置 评测过标识
     *
     * @param
     * @return
     */
    public void setIsEvaluate(int titleNum, int SenIndex) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "update Text set IsEvaluate=" + 1 + " where TitleNum=" + titleNum
                + " and SenIndex =" + SenIndex;
        try {
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("评测标识失败" + e);
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
    }

    /**
     * 获取已评测的数量
     */
    public int getTotalIsEvaluateNum(String titleName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select sum (IsEvaluate) from Text " +
                    "where TitleName like" + "'" + titleName + "%'" + " and TestType=" + testType;
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取已评测数量失败" + e);
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
     * 获取总评测数量
     */
    public int getTotalEvaluateNum(String titleName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select count() from Text " +
                    "where TitleName like" + "'" + titleName + "%'" + " and TestType=" + testType;
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("获取总评测数量失败" + e);
            e.printStackTrace();
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
     * 更新评测数据
     *
     * @param textId
     * @param indexId
     * @param totalScore
     * @param textNum
     * @param textScore
     * @param url
     */
    public void updateEvaluate(int textId, int indexId, int totalScore, int textNum, String textScore, String url) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "update Evaluate set totalScore= " + totalScore + ",textNum= " + textNum
                + ",textScore= '" + textScore + "',url= '" + url
                + "' where TextId= " + textId
                + " and IndexId = " + indexId;
        try {
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("更新评测数据失败" + e);
            e.printStackTrace();
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
    }

    /**
     * 删除评测记录
     */
    public void deleteEvaluate() {

        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlStr = "DELETE FROM Evaluate";
        mDB.execSQL(sqlStr);
        if (mDB.isOpen()) {
            mDB.close();
        }
    }


    /**
     * 重置所有IsEvaluate标志位
     */
    public void resetEvaluate() {

        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlStr = "update Text set IsEvaluate = 0";
        mDB.execSQL(sqlStr);
        if (mDB.isOpen()) {
            mDB.close();
        }
    }


    /**
     * 插入评测数据
     *
     * @param textId
     * @param indexId
     * @param totalScore
     * @param textNum
     * @param textScore
     * @param url
     */
    public void setEvaluate(int textId, int indexId, int totalScore, int textNum, String textScore, String url) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "insert into Evaluate(TextId,IndexId,totalScore,textNum,textScore,url)"
                + " values(?,?,?,?,?,?)";
        Object[] objects = new Object[]{textId, indexId, totalScore, textNum, textScore, url};

        try {
            mDB.execSQL(sqlString, objects);
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("插入评测数据失败" + e);
        } finally {
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
    }

    /**
     * 获取对应Test的对应Sentence集合
     */
    @SuppressLint("Range")
    public ArrayList<EvaluateBean> getEvaluate(int textId, int indexId) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<EvaluateBean> evaluateBeans = new ArrayList<EvaluateBean>();
        Cursor cursor = null;
        String sqlString = "select * from Evaluate" + " where TextId=" + textId + " and IndexId=" + indexId
                + " order by IndexId asc";
        try {
            cursor = mDB.rawQuery(sqlString, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    EvaluateBean bean = new EvaluateBean();
                    bean.voaId = cursor.getInt(cursor
                            .getColumnIndex(EVALUATE_TEXTID)) + "";
                    bean.IdIndex = cursor.getInt(cursor
                            .getColumnIndex(EVALUATE_INDEX_ID)) + "";
                    bean.total_score = cursor.getInt(cursor
                            .getColumnIndex(EVALUATE_TOTAL_SCORE)) + "";
                    if (cursor.getInt(cursor.getColumnIndex(EVALUATE_TEXTNUM)) != 0) {
                        bean.textNum = cursor.getInt(cursor
                                .getColumnIndex(EVALUATE_TEXTNUM));
                    } else {
                        bean.textNum = 0;
                    }
                    if (cursor.getString(cursor.getColumnIndex(EVALUATE_SCORE)) != null) {
                        bean.textScore = cursor.getString(cursor
                                .getColumnIndex(EVALUATE_SCORE));
                    } else {
                        bean.textScore = " ";
                    }

                    bean.URL = cursor.getString(cursor
                            .getColumnIndex(EVALUATE_URL));
                    evaluateBeans.add(i, bean);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e(" " + e);
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return evaluateBeans;
    }

    /**
     * 获取已背单词的数量
     */
    public int getTotalDoneWordsNum() {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select count() from remember" +
                    " where statues=1" + " or statues=2";
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取已评测数量失败" + e);
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
     * 获取总单词数量
     */
    public int getTotalWordsNum() {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select count() from remember";
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            sum = cursor.getInt(0);
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("获取总单词数量失败" + e);
            e.printStackTrace();
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
     * 获取title中的题目信息
     */
    @SuppressLint("Range")
    public ArrayList<Answer> getAnswers(int titleNum) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        ArrayList<Answer> answerlist = new ArrayList<Answer>();
        Cursor cursor = null;
        String sqlString = "select * from Answer" + " where TitleNum="
                + titleNum + " order by QuesIndex asc";
        try {
            cursor = mDB.rawQuery(sqlString, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    Answer answer = new Answer();
                    answer.Answer = cursor.getString(cursor
                            .getColumnIndex(FIELD_ANSWER));
                    answer.AnswerNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_ANSWERNUM));
                    answer.AnswerText = cursor.getString(cursor
                            .getColumnIndex(FIELD_ANSWERTEXT));
                    answer.IsSingle = cursor.getInt(cursor
                            .getColumnIndex(FIELD_ISSINGLE));
                    answer.PartType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_PARTTYPE));
                    answer.QuesImage = cursor.getString(cursor
                            .getColumnIndex(FIELD_QUESIMAGE));
                    answer.QuesText = cursor.getString(cursor
                            .getColumnIndex(FIELD_QUESTEXT));
                    answer.QuesIndex = cursor.getInt(cursor
                            .getColumnIndex(FIELD_QUESINDEX));
                    answer.Sound = cursor.getString(cursor
                            .getColumnIndex(FIELD_SOUNDA));
                    answer.TestType = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TESTTYPE));
                    answer.TitleNum = cursor.getInt(cursor
                            .getColumnIndex(FIELD_TITLENUM));
                    answerlist.add(i, answer);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (mDB.isOpen()) {
                mDB.close();
            }
        }
        return answerlist;
    }

    /**
     * 从指定的包里面获取要下载的音频信息
     */
    @SuppressLint("Range")
    public ArrayList<DownTest> getDownTests(String packName, int packNum, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        if (zdHelper == null) {
            zdHelper = new ZDBHelper(mContext);//频繁操作后引起异常
        }
        ArrayList<TitleInfo> titlelist = zdHelper.getTitleInfos(packName, testType);
        ArrayList<PackInfo> packList = zdHelper.getPackInfo();

//		Log.e("判断" + packName +"中title的数目：",packName + " 的大小为：" + titlelist.size() );

        Cursor cursor = null;
        ArrayList<DownTest> testlist = new ArrayList<DownTest>();

        try {
            for (int i = 0; i < titlelist.size(); i++) {
                int titleNum = titlelist.get(i).TitleNum;
                int type = titlelist.get(i).TestType;
                String sqlString1 = "select distinct sound from Text"
                        + " where TitleNum=" + titleNum + " and TestType=" + type;
                String sqlString2 = "select distinct * from Answer"
                        + " where TitleNum=" + titleNum;

                cursor = mDB.rawQuery(sqlString1, null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    for (int j = 0; j < cursor.getCount(); j++) {
                        DownTest tDownTest = new DownTest();
                        tDownTest.packNum = packNum;
                        tDownTest.packName = packName;
                        tDownTest.titleNum = titleNum;
                        tDownTest.testType = testType;
                        tDownTest.sound = cursor.getString(cursor
                                .getColumnIndex(FIELD_SOUNDT));
                        testlist.add(tDownTest);
                        cursor.moveToNext();
                    }
                }
            }
            cursor.close();
            mDB.close();
        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDB != null) {
                mDB.close();
            }
        }

        return testlist;
    }

    public int getDownTestsNum(String packName, int testType) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        if (zdHelper == null) {
            zdHelper = new ZDBHelper(mContext);//频繁操作后引起异常
        }
        ArrayList<TitleInfo> titlelist = zdHelper.getTitleInfos(packName, testType);
        Cursor cursor = null;
        int sum = 0;

        try {
            for (int i = 0; i < titlelist.size(); i++) {
                int titleNum = titlelist.get(i).TitleNum;
                String sqlString1 = "select distinct sound from Text"
                        + " where TitleNum=" + titleNum;
                String sqlString2 = "select distinct * from Answer"
                        + " where TitleNum=" + titleNum;
                cursor = mDB.rawQuery(sqlString1, null);
                sum += cursor.getCount();
                cursor = mDB.rawQuery(sqlString2, null);
                sum += cursor.getCount();
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
        return sum;
    }

    @SuppressLint("Range")
    public Explain getExplain(int titleNum) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        Explain explain = new Explain();
        Cursor cursor = null;
        int sum = 0;
        try {
            String sqlString = "select * from Explain where TitleNum=" + titleNum;
            cursor = mDB.rawQuery(sqlString, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                explain.Explain = cursor.getString(cursor.getColumnIndex(FIELD_EXPLAIN));
                explain.PartType = cursor.getInt(cursor.getColumnIndex(FIELD_PARTTYPE));
                explain.TestType = cursor.getInt(cursor.getColumnIndex(FIELD_TESTTYPE));
                explain.TitleNum = cursor.getInt(cursor.getColumnIndex(FIELD_TITLENUM));
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
        return explain;

    }

    /**
     * 更新本地评测记录
     */
    public void updateEvaluateFromServer(int textId, int indexId, int totalScore, String url) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "update Evaluate set totalScore= " + totalScore + ","
                + "url= '" + url
                + "' where TextId= " + textId
                + " and IndexId= " + indexId;
        try {
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("更新评测数据失败" + e);
            e.printStackTrace();
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }
    }

    /**
     * 插入服务器评测记录
     */
    public void insertEvaluateFromServer(int textId, int indexId, int totalScore, String url) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }
        String sqlString = "insert into Evaluate(TextId,IndexId,totalScore,url)"
                + " values(?,?,?,?)";
        Object[] objects = new Object[]{textId, indexId, totalScore, url};

        try {
            mDB.execSQL(sqlString, objects);
            mDB.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("插入评测数据失败" + e);
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }
    }

    /**
     * 查找服务器评测详细记录是否在本地
     */
    public boolean findEvaluateRecord(int textId, int indexId) {
        boolean result = false;
        Cursor cursor;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "select exists" + "(select * from Evaluate "
                + "where TextId= " + textId
                + " and IndexId = " + indexId + ")";
        try {
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                result = false;
            } else if (cursor.getInt(0) == 1) {
                result = true;
            }

//            Timber.e("查找服务器评测记录是否在本地cursor" + cursor.getInt(0));
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("查找" + e);
            e.printStackTrace();
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }
        return result;
    }

    /**
     * 查找服务器已经评测的记录是否在本地
     */
    public boolean findIsEvaluate(int textId, int indexId) {
        boolean result = false;
        Cursor cursor;
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "select IsEvaluate from Text "
                + "where TitleNum = " + textId
                + " and SenIndex = " + indexId;
        try {
            cursor = mDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                result = false;
            } else if (cursor.getInt(0) == 1) {
                result = true;
            }

//            Timber.e("查找服务器评测记录是否在本地cursor" + cursor.getInt(0));
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("查找" + e);
            e.printStackTrace();
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }
        return result;
    }

    /**
     * 更新本地评测记录
     */
    public void updateIsEvaluate(int textId, int indexId, int isEvaluate) {
        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        String sqlString = "update Text set IsEvaluate= " + isEvaluate
                + " where TitleNum = " + textId
                + " and SenIndex = " + indexId;
        try {
            mDB.execSQL(sqlString);
            mDB.close();
        } catch (Exception e) {
            LogUtil.e("更新评测数据失败" + e);
            e.printStackTrace();
        } finally {
//            if (mDB.isOpen()) {
//                mDB.close();
//            }
        }
    }


    /**
     * 存储评测的单词
     *
     * @param evaluateBean
     */
    public void saveEvalWord(EvaluateBean evaluateBean) {

        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        List<EvaluateBean.WordsBean> wordsBeanList = evaluateBean.getWords();
        String sqlString = "replace into EvalWord(TitleNum,SenIndex,content,word_index,pron,pron2,score,user_pron,user_pron2)" +
                " values(?,?,?,?,?,?,?,?,?)";

        for (int i = 0; i < wordsBeanList.size(); i++) {

            EvaluateBean.WordsBean wordsBean = wordsBeanList.get(i);
            mDB.execSQL(sqlString, new Object[]{evaluateBean.getVoaId(), evaluateBean.getIdIndex(),
                    wordsBean.getContent(), wordsBean.getIndex(), wordsBean.getPron(), wordsBean.getPron2(),
                    wordsBean.getScore(), wordsBean.getUserPron(), wordsBean.getUserPron2()});
        }
        mDB.close();
    }


    /**
     * 获取评测的单词
     * @param titleNum
     * @param senIndex
     * @return
     */
    public List<EvaluateBean.WordsBean> getEvalWord(String titleNum, String senIndex) {

        if (!mDB.isOpen()) {
            mDB = openDatabase();
        }

        List<EvaluateBean.WordsBean> wordsBeanList = new ArrayList<>();

        String sqlStr = "select * from EvalWord where TitleNum = ? and SenIndex = ? order by word_index ASC";
        Cursor cursor = mDB.rawQuery(sqlStr, new String[]{titleNum, senIndex});
        while (cursor.moveToNext()) {

            String titleNumStr = cursor.getString(cursor.getColumnIndexOrThrow("TitleNum"));
            int senIndexInt = cursor.getInt(cursor.getColumnIndexOrThrow("SenIndex"));
            String contentStr = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            int word_index = cursor.getInt(cursor.getColumnIndexOrThrow("word_index"));
            String pron = cursor.getString(cursor.getColumnIndexOrThrow("pron"));
            String pron2 = cursor.getString(cursor.getColumnIndexOrThrow("pron2"));
            String score = cursor.getString(cursor.getColumnIndexOrThrow("score"));
            String user_pron = cursor.getString(cursor.getColumnIndexOrThrow("user_pron"));
            String user_pron2 = cursor.getString(cursor.getColumnIndexOrThrow("user_pron2"));

            EvaluateBean.WordsBean wordsBean = new EvaluateBean.WordsBean();
            wordsBean.setContent(contentStr);
            wordsBean.setIndex(word_index);
            wordsBean.setPron(pron);
            wordsBean.setPron2(pron2);
            wordsBean.setScore(Double.parseDouble(score));
            wordsBean.setUserPron(user_pron);
            wordsBean.setUserPron2(user_pron2);
            wordsBeanList.add(wordsBean);
        }
        return wordsBeanList;
    }

}
