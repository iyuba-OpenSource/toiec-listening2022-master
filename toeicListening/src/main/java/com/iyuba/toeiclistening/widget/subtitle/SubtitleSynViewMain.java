package com.iyuba.toeiclistening.widget.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 字幕同步View
 *
 * @author lijingwei
 */
public class SubtitleSynViewMain extends ScrollView implements
        TextPageSelectTextCallBack {
    public boolean syncho = true;
    private Context context;
    private LinearLayout subtitleLayout;
    private ArrayList<Text> textsList;
    private List<View> subtitleViews;
    private int currParagraph;
    private TextPageSelectTextCallBack tpstcb;
    private boolean enableSelectText = true;
    private int textColor;
    private int textSize;
    private int blankColor;

    public void setTpstcb(TextPageSelectTextCallBack tpstcb) {
        if (enableSelectText)
            this.tpstcb = tpstcb;
    }

    public SubtitleSynViewMain(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initWidget(context);
    }

    public SubtitleSynViewMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initWidget(context);
    }

    public SubtitleSynViewMain(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initWidget(context);
    }

    private void initWidget(Context context) {
        this.context = context;
        subtitleLayout = new LinearLayout(this.context);
        subtitleLayout.setOrientation(LinearLayout.VERTICAL);
        textColor = ConfigManagerMain.Instance().loadInt(Constant.TEXTCOLOR);
        textSize = ConfigManagerMain.Instance().loadInt(Constant.TEXTSIZE);
        blankColor = getResources().getColor(R.color.blank);
    }

    /**
     * 设置帮顶的textList，即文章的内容
     *
     * @param textsList
     */
    public void setTextList(ArrayList<Text> textsList) {
        this.textsList = textsList;
    }

    /**
     * 初始化T界面上的文章的内容，全文显示
     */
    public void initSubtitleSum() {
        subtitleLayout.removeAllViews();
        removeAllViews();
        if (textsList != null && textsList.size() != 0) {
            subtitleViews = new ArrayList<View>();
            for (int i = 0; i < textsList.size(); i++) {
                TextPage tp = new TextPage(this.context);
                tp.setBackgroundColor(Color.TRANSPARENT);
                tp.setTextColor(blankColor);
                tp.setTextSize(textSize);
                if (currParagraph == i) {
                    // tp.setTextColor(Constant.textColor);
                }
                tp.setText(textsList.get(i).Sentence);
                tp.setTextpageSelectTextCallBack(this);
                final int current = i;
                tp.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View arg0) {
                        //tpstcb.selectParagraph(current);
                        return true;
                    }
                });
                subtitleViews.add(tp);
                subtitleLayout.addView(tp);
            }
        }
        addView(subtitleLayout);
    }

    /**
     * 初始化T&Q文章界面上的内容，只显示当前播放的句子内容
     */
    public void initSubtitleOneSectence() {
        subtitleLayout.removeAllViews();
        removeAllViews();
        if (textsList != null && textsList.size() != 0) {
            subtitleViews = new ArrayList<View>();
            for (int i = 0; i < textsList.size(); i++) {
                TextPage tp = new TextPage(this.context);
                tp.setBackgroundColor(Color.TRANSPARENT);
                tp.setTextColor(blankColor);
                tp.setTextSize(textSize);
                if (currParagraph == i) {
                    //tp.setTextColor(textColor);
                }
                tp.setText(textsList.get(i).Sentence);

//				Log.d("初始化T&Q文章界面上的内容时：", currParagraph + ":" + textsList.get(i).Sentence);

                tp.setTextpageSelectTextCallBack(this);
                final int current = i;
                tp.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View arg0) {
                        //tpstcb.selectParagraph(current);
                        return true;
                    }
                });
                subtitleViews.add(tp);

                // 测试tp中的内容
//				 Log.d("!!!!!!!!tp:", tp.getText().toString());
            }
        }
        // 开始只显示第一句话
        if (subtitleViews != null && subtitleViews.size() > 0) {
            subtitleLayout.addView(subtitleViews.get(0));
//			 Log.d("tag:", "~~~~~~~~~~");
        }
        addView(subtitleLayout);
    }

    @Override
    public void selectTextEvent(String selectText) {
        tpstcb.selectTextEvent(selectText);
    }

    /**
     * T界面上的原文同步
     *
     * @param paragraph 目前播放到第几句话
     */
    public void snyParagraphT(int paragraph) {
        currParagraph = paragraph;
        handler.sendEmptyMessage(0);

    }

    /**
     * TQ界面的原文同步
     *
     * @param paragraph
     */
    public void snyParagraphTQ(int paragraph) {
        currParagraph = paragraph;
        subtitleLayout.removeAllViews();

//		Log.e("currParagraph44444444444444",currParagraph+" ");

        ((TextPage) subtitleViews.get(paragraph - 1)).setTextColor(textColor);

//		Log.d("!!!!!!!!subtitleViews:",subtitleViews.get(paragraph - 1).toString());

        subtitleLayout.addView(subtitleViews.get(paragraph - 1));

//		Log.d("测试subtitleLayout","");
    }

    public void unsnyParagraph() {
        handler.removeMessages(0);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 0:
                    if (subtitleViews != null && subtitleViews.size() != 0) {
                        int center = 0;
                        for (int i = 0; i < subtitleViews.size(); i++) {
                            TextView textView = (TextView) subtitleViews.get(i);
                            if (currParagraph == i + 1) {
                                textView.setTextColor(textColor);

                                center = textView.getTop() + textView.getHeight()
                                        / 2;

                            } else {
                                textView.setTextColor(blankColor);
                            }
                        }
                        if (ConfigManagerMain.Instance()
                                .loadBoolean(Constant.TEXT_SYNS)) {
                            center -= getHeight() / 2;
                            if (center > 0) {
                                smoothScrollTo(0, center);
                            } else {
                                smoothScrollTo(0, 0);
                            }
                        }
                    }

                    break;
                case 1:
                    break;

            }
            return false;
        }
    });

    public void updateSubtitleViewT() {
        if (textsList != null && textsList.size() != 0 && subtitleViews != null
                && subtitleViews.size() != 0) {
            for (int i = 0; i < textsList.size(); i++) {
                TextPage tp = (TextPage) subtitleViews.get(i);
                tp.setText(textsList.get(i).Sentence);
            }
            snyParagraphT(currParagraph);
        }
    }

    public int getCurrParagraph() {
        return currParagraph;
    }

    @Override
    public void selectParagraph(int paragraph) {
        tpstcb.selectParagraph(paragraph);
    }
}
