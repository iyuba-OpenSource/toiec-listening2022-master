package com.iyuba.toeiclistening.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.entity.EvaluateWord;

import java.util.ArrayList;
import java.util.List;

public class ResultParse {
    private static final String TAG = ResultParse.class.getSimpleName();

    private static int parseIndex;
    private static SpannableStringBuilder spannable;
    private static String sen;


    public static SpannableStringBuilder getSenResultLocal(String[] style, String s) {
        sen = s;
        Log.e(TAG, sen);
        spannable = new SpannableStringBuilder(sen);
        parseIndex = 0;


        String[] words = s.split(" ");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {

            if (!"".equals(words[i])) {
                stringList.add(words[i]);
            }
        }
        if (stringList.size() > style.length) {
            LogUtil.e("数组越界异常" + " " + stringList + "  " + style.length);
        }
        LogUtil.e("stringList " + stringList + " " + stringList.size());
        LogUtil.e("stringList style[i]" + style + " " + style.length);
        for (int j = 0; j < stringList.size(); j++) {
            EvaluateWord word = new EvaluateWord();
            word.content = stringList.get(j);
            if (j < style.length) {
                if (style[j].isEmpty()) {
                    word.total_score = 0;
                } else {
                    try {
                        word.total_score = Float.parseFloat(style[j]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                setWord(word);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("stringList " + e);
            }
        }


        return spannable;
    }

    public static void setWord(EvaluateWord word) {
        String wordStr = word.content;
        // wordStr=wordStr.replaceAll("\\p{P}", "");
        LogUtil.e(TAG, "Setting Word : " + wordStr);
        if (word.total_score < 3 || word.total_score > 4) {
            int start = -1;
            int end = -1;
            int i = parseIndex;
            LogUtil.e(TAG, "pareseIndex is : " + parseIndex);
            for (int j = 0; i < sen.length() && j < wordStr.length(); i++) {
                LogUtil.e(TAG, sen.charAt(i) + "___" + wordStr.charAt(j));
                if (start == -1) {
                    if (sen.charAt(i) == wordStr.charAt(j)
                            || sen.charAt(i) + 32 == wordStr.charAt(j)) {
                        start = i;
                        j++;
                    }
                } else {
                    if (sen.charAt(i) == wordStr.charAt(j)) {
                        j++;
                    } else {
                        return;
                    }
                }
            }


            if (start != -1) {
                end = i;
                parseIndex = i;
                //LogUtil.e("单词*******" + wordStr + "+start : " + start + " end : " + end);
                try {
                    String s_start = wordStr.substring(0, 1);
                    // String s_end = wordStr.substring(wordStr.length() - 1,wordStr.length());
                    //
                    String s_end = wordStr.substring(wordStr.length() - 1);
                    LogUtil.e("单词==" + wordStr + "开头" + s_start + "结尾" + s_end + "***");
                    LogUtil.e("单词 " + wordStr + "分数" + word.total_score);
                    if (check(s_start)) {
                        start = start + 1;
                    }
                    if (check(s_end) || s_end.equals("") || s_end.length() == 0) {
                        end = end - 1;
                    } else if (s_end.indexOf('\r') >= 0) {//判断是否包含转义字符
                        end = end - 2;
                    }
                } catch (Exception e) {
                    LogUtil.e("异常" + e);
                }

                if (word.total_score < (float) 3) {
                    setRed(start, end);

                } else if (word.total_score >= (float) 3) {
                    try {
                        setGreen(start, end);
                    } catch (Exception e) {
                        LogUtil.e("异常" + e);
                    }
                }
            }
        } else {
            parseIndex += wordStr.length() + 1;
        }
        while (parseIndex < sen.length() && !isAlphabeta(sen.charAt(parseIndex))) {
            parseIndex++;
        }

    }

    private static boolean isAlphabeta(char c) {
        boolean result = false;
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            result = true;
        return result;
    }

    public static void setRed(int start, int end) {
//        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    public static void setGreen(int start, int end) {
        spannable.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     *
     * @param s
     * @return
     */
    public static boolean check(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }

        return b;
    }
}
