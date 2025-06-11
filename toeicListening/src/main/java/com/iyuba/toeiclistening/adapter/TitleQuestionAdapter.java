package com.iyuba.toeiclistening.adapter;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.Answer;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.ImageUtil;
import com.iyuba.toeiclistening.util.Player;
import com.iyuba.toeiclistening.widget.subtitle.TextPage;
import com.iyuba.toeiclistening.widget.subtitle.TextPageSelectTextCallBack;

import java.util.ArrayList;
import java.util.List;

public class TitleQuestionAdapter extends BaseAdapter {

    private Context mContext;
    private boolean playing;
    public ArrayList<Answer> answerList;
    private LayoutInflater mInflater;
    private ViewHolder viewHolder;
    private List<DoQuestionCond> questionCondsList;// 记录每道题的作题情况
    private Player player;
    private String packNameString;
    private TextPageSelectTextCallBack tpstCallBack;// editext的选中后的监听器
    public Bitmap bitmap;

    //题目图片的空间
    private LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


    /**
     * @param context
     * @param list    AnswerList
     */
    public TitleQuestionAdapter(Context context, ArrayList<Answer> list,
                                TextPageSelectTextCallBack textPageSelectTextCallBack) {
        mContext = context;

        answerList = list;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        questionCondsList = new ArrayList<DoQuestionCond>();
        iniQuestionCondList();
        //packNameString = packName;
        tpstCallBack = textPageSelectTextCallBack;

    }

    public void iniQuestionCondList() {
        questionCondsList.clear();
        for (int i = 0; i < answerList.size(); i++) {
            DoQuestionCond questionCond = new DoQuestionCond();
            for (int j = 0; j < 8; j++) {
                questionCond.clickState[j] = 0;
            }
            questionCond.answerNum = answerList.get(i).AnswerNum;
            questionCondsList.add(i, questionCond);
        }
    }


    @Override
    public int getCount() {
        return answerList.size();
    }

    @Override
    public Object getItem(int position) {
        return answerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int curPosition;
        final Answer curAnswer;
        final ViewHolder curViewHolder;
        final DoQuestionCond curQuestionCond;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.title_question_content,
                    parent, false);
            viewHolder = new ViewHolder();
            findView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 初始化保存list单个item状态的变量
        curPosition = position;
        curAnswer = answerList.get(position);
        curViewHolder = viewHolder;
        curQuestionCond = questionCondsList.get(position);


        setView(curAnswer, curQuestionCond, curPosition);
        OnClickListener answerButtonOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.title_question_content_option_A:
                        if (curQuestionCond.clickState[0] == 0) {// 没有点击过
                            if (curAnswer.Answer.contains("1")) {// 答案选项中有1
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[0] = 1;
                                curViewHolder.aButton.setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[0] = 2;
                                curViewHolder.aButton.setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                                //setDBRightNum(curAnswer,1);//没有多选，都是1！！！
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                                //setDBRightNum(curAnswer,0);
                            }
                            // 更新questionCondList
                            questionCondsList.set(curPosition, curQuestionCond);
                        }

                        break;
                    case R.id.title_question_content_option_B:
                        if (curQuestionCond.clickState[1] == 0) {
                            if (curAnswer.Answer.indexOf("2") != -1) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[1] = 1;
                                curViewHolder.bButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[1] = 2;
                                curViewHolder.bButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_C:
                        if (curQuestionCond.clickState[2] == 0) {

                            if (curAnswer.Answer.contains("3")) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[2] = 1;
                                curViewHolder.cButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[2] = 2;
                                curViewHolder.cButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_D:
                        if (curQuestionCond.clickState[3] == 0) {

                            if (curAnswer.Answer.contains("4")) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[3] = 1;
                                curViewHolder.dButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[3] = 2;
                                curViewHolder.dButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_E:
                        if (curQuestionCond.clickState[4] == 0) {

                            if (curAnswer.Answer.indexOf("5") != -1) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[4] = 1;
                                curViewHolder.eButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[4] = 2;
                                curViewHolder.eButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_F:
                        if (curQuestionCond.clickState[5] == 0) {

                            if (curAnswer.Answer.indexOf("6") != -1) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[5] = 1;
                                curViewHolder.fButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[5] = 2;
                                curViewHolder.fButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_G:
                        if (curQuestionCond.clickState[6] == 0) {

                            if (curAnswer.Answer.indexOf("7") != -1) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[6] = 1;
                                curViewHolder.gButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[6] = 2;
                                curViewHolder.gButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                    case R.id.title_question_content_option_H:
                        if (curQuestionCond.clickState[7] == 0) {

                            if (curAnswer.Answer.indexOf("8") != -1) {
                                curQuestionCond.rightNum++;
                                curQuestionCond.clickState[7] = 1;
                                curViewHolder.hButton
                                        .setBackgroundResource(R.drawable.button_answer_right);
                            } else {
                                curQuestionCond.wrongNum++;
                                curQuestionCond.clickState[7] = 2;
                                curViewHolder.hButton
                                        .setBackgroundResource(R.drawable.button_answer_wrong);
                            }
                            if (curQuestionCond.answerNum == curQuestionCond.answerNum
                                    && curQuestionCond.wrongNum == 0) {
                                // 选项完整，且从来没有选错过，则这道题就作对了
                                curQuestionCond.result = true;
                            } else {
                                // 否则，错了
                                curQuestionCond.result = false;
                            }
                            questionCondsList.set(curPosition, curQuestionCond);
                        }
                        break;
                }

            }
        };

        viewHolder.aButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.bButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.cButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.dButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.eButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.fButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.gButton.setOnClickListener(answerButtonOnClickListener);
        viewHolder.hButton.setOnClickListener(answerButtonOnClickListener);
//		viewHolder.playQuestionImageView
//				.setOnClickListener(videoPlayOnClickListener);
        viewHolder.quesContenTextPage
                .setTextpageSelectTextCallBack(tpstCallBack);

        return convertView;

    }


    public void findView(View convertView) {
        viewHolder.aButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_A);
        viewHolder.bButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_B);
        viewHolder.cButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_C);
        viewHolder.dButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_D);
        viewHolder.eButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_E);
        viewHolder.fButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_F);
        viewHolder.gButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_G);
        viewHolder.hButton = (Button) convertView
                .findViewById(R.id.title_question_content_option_H);
        viewHolder.playQuestionImageView = (LinearLayout) convertView
                .findViewById(R.id.title_question_content_sound_switch);
        viewHolder.quesContenTextPage = (TextPage) convertView
                .findViewById(R.id.title_question_title);
        viewHolder.answerSelectTextPage = (LinearLayout) convertView
                .findViewById(R.id.Title_Question_Content_Option);
        viewHolder.questionContentImage = (LinearLayout) convertView
                .findViewById(R.id.question_content_image);
        viewHolder.relativeLayout = (RelativeLayout) convertView
                .findViewById(R.id.RL_Title_Question_Content);
        viewHolder.ivTable = convertView
                .findViewById(R.id.iv_table);

    }

    public void setView(Answer curAnswer, DoQuestionCond curQuestionCond,
                        int curPosition) {


        // 根据问题选项的个数设置选项Button的可见性
        if (curAnswer.AnswerNum >= 1) {
            viewHolder.aButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[0] == 0) {
                viewHolder.aButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[0] == 1) {
                viewHolder.aButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[0] == 2) {
                viewHolder.aButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 2) {
            viewHolder.bButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[1] == 0) {
                viewHolder.bButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[1] == 1) {
                viewHolder.bButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[1] == 2) {
                viewHolder.bButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }

        }
        if (curAnswer.AnswerNum >= 3) {
            viewHolder.cButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[2] == 0) {
                viewHolder.cButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[2] == 1) {
                viewHolder.cButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[2] == 2) {
                viewHolder.cButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 4) {
            viewHolder.dButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[3] == 0) {
                viewHolder.dButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[3] == 1) {
                viewHolder.dButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[3] == 2) {
                viewHolder.dButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 5) {
            viewHolder.eButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[4] == 0) {
                viewHolder.eButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[4] == 1) {
                viewHolder.eButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[4] == 2) {
                viewHolder.eButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 6) {
            viewHolder.eButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[5] == 0) {
                viewHolder.fButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[5] == 1) {
                viewHolder.fButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[5] == 2) {
                viewHolder.fButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 7) {
            viewHolder.eButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[6] == 0) {
                viewHolder.gButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[6] == 1) {
                viewHolder.gButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[6] == 2) {
                viewHolder.gButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }
        if (curAnswer.AnswerNum >= 8) {
            viewHolder.eButton.setVisibility(View.VISIBLE);
            if (curQuestionCond.clickState[7] == 0) {
                viewHolder.hButton
                        .setBackgroundResource(R.drawable.button_answer);
            } else if (curQuestionCond.clickState[7] == 1) {
                viewHolder.hButton
                        .setBackgroundResource(R.drawable.button_answer_right);
            } else if (curQuestionCond.clickState[7] == 2) {
                viewHolder.hButton
                        .setBackgroundResource(R.drawable.button_answer_wrong);
            }
        }

        if (curAnswer.AnswerNum == 3) {
            viewHolder.dButton.setVisibility(View.GONE);
        }

        // 带图的题目
        if ((curAnswer.PartType == 403 || curAnswer.PartType == 404) && !curAnswer.QuesImage.equals("")) {
            viewHolder.ivTable.setVisibility(View.VISIBLE);
            String imageNameString = curAnswer.QuesImage;
            String imagePathString = Constant.ASSETS_IMAGE_PATH + "/" + imageNameString;
            try {
                bitmap = ImageUtil.getImageFromAssetsFile(mContext, imagePathString);
//            if (bitmap != null) {
//                bitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2);
//            }
                viewHolder.ivTable.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("内存溢出！");
            }
        } else {
            viewHolder.ivTable.setVisibility(View.GONE);
        }

        //如果是图片问题
        if (curAnswer.PartType == 401) {
            viewHolder.questionContentImage.setVisibility(View.VISIBLE);
            addQuestionImage(curAnswer);
            Log.d("exits picture:", "题目存在图片");
        } else {
            viewHolder.questionContentImage.setVisibility(View.GONE);
            Log.d("not exits picture:", "题目不存在图片");
        }

        if (curAnswer.PartType != 401) {
            getNormalAnswerString(curAnswer);
        } else {
            getNormalAnswerStringForImage("");
        }

        //根据托业的不同题型判断是否要显示题目的内容（401和402型不显示题目内容）
        if (curAnswer.PartType == 403 || curAnswer.PartType == 404) {
            int num = curAnswer.TitleNum % 100;
            if (curPosition == 1) {
                num = num + 1;
            } else if (curPosition == 2) {
                num = num + 2;
            }
            String questionContentString = (num) + ". " + curAnswer.QuesText;
            viewHolder.quesContenTextPage.setText(questionContentString);
        } else {
            viewHolder.quesContenTextPage.setText("");
        }

        if (curPosition % 2 == 0) {
            viewHolder.relativeLayout.setBackgroundResource(R.color.spale);
        } else {
            viewHolder.relativeLayout.setBackgroundResource(R.color.grey);
        }
    }

    public void addQuestionImage(Answer cuAnswer) {
        viewHolder.questionContentImage.removeAllViews();
        String images = cuAnswer.QuesImage;
        //String prefix=String.valueOf(cuAnswer.TitleNum).substring(0,4);
        ImageView imageViews = new ImageView(mContext);

        params.width = 600;//400
        params.height = 450;//350
//		String tranString="";
//		for(int i=0;i<images[0].length();i++){
//			String subString=images[0].substring(i,i+1);
//			tranString=tranString+subString;
//			if (subString.equals("_")) {
//				break;
//			}
//		}
        imageViews = getImageView(images);

        viewHolder.questionContentImage.addView(imageViews);
    }

    /**
     * 从assects中获取图片资源 并设置其格式
     *
     * @param image
     * @return
     */
    public ImageView getImageView(String image) {
        String imageNameString = image;
        String imagePathString = Constant.ASSETS_IMAGE_PATH + "/" + imageNameString;
        Bitmap bitmap = ImageUtil.getImageFromAssetsFile(mContext, imagePathString);

        Log.e("显示图片路径：", imagePathString);

        if (bitmap != null) {
            Log.e("ggggggggg", bitmap.getWidth() + "  " + bitmap.getHeight());
            bitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2);
        }
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(params);
        //imageView.set
        imageView.setScaleType(imageView.getScaleType().FIT_XY);
        return imageView;
    }

    public void getNormalAnswerStringForImage(String answer) {
        viewHolder.answerSelectTextPage.removeAllViews();
    }

    // 将数据库里面存的answer的字符串转化为标准选项的字符串
    public void getNormalAnswerString(Answer curAnswer) {
        String answer = curAnswer.AnswerText;
        viewHolder.answerSelectTextPage.removeAllViews();
        String[] answers = answer.split("\\+\\+");// 分隔是对字符串模式进行匹配，用++会抛出异常
        int answerNum = curAnswer.AnswerNum;
        if (curAnswer.PartType == 402) {
            if (answerNum >= 1) {
                addText("A) ");
            }
            if (answerNum >= 2) {
                addText("B) ");
            }
            if (answerNum >= 3) {
                addText("C) ");
            }
            if (answerNum >= 4) {
                addText("D) ");
            }
        } else {
            if (answers.length >= 1) {//answers.length
                String normalAnswerString = "A) " + answers[0];
                addText(normalAnswerString);
            }
            if (answers.length >= 2) {
                String normalAnswerString = "B) " + answers[1];
                addText(normalAnswerString);
            }
            if (answers.length >= 3) {
                String normalAnswerString = "C) " + answers[2];
                addText(normalAnswerString);
            }
            if (answers.length >= 4) {
                String normalAnswerString = "D) " + answers[3];
                addText(normalAnswerString);
            }
            if (answers.length >= 5) {
                String normalAnswerString = "E) " + answers[4];
                addText(normalAnswerString);
            }
            if (answers.length >= 6) {
                String normalAnswerString = "F) " + answers[5];
                addText(normalAnswerString);
            }
            if (answers.length >= 7) {
                String normalAnswerString = "G) " + answers[6];
                addText(normalAnswerString);
            }
            if (answers.length >= 8) {
                String normalAnswerString = "H) " + answers[7] + "\n";
                addText(normalAnswerString);
            }
        }
    }

    public void addText(String normalAnswerString) {
        TextPage textPage = new TextPage(mContext);
        textPage.setText(normalAnswerString);
        textPage.setTextSize(20);
        textPage.setPadding(0, 0, 0, 0);
        textPage.setBackgroundResource(color.transparent);
        LayoutParams layoutParams = new LayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 2;
        textPage.setLayoutParams(layoutParams);
        textPage.setTextpageSelectTextCallBack(tpstCallBack);
        viewHolder.answerSelectTextPage.addView(textPage);
    }

    public int getRightNum() {
        int rightNum = 0;
        for (int i = 0; i < questionCondsList.size(); i++) {
            if (questionCondsList.get(i).result) {
                rightNum = rightNum + 1;
            }
        }
        return rightNum;
    }

    public List<DoQuestionCond> getResult() {
        return questionCondsList;
    }

    private void setDBRightNum(Answer answer, int rightNum) {
        int titleNum = answer.TitleNum;
        ZDBHelper zHelper = new ZDBHelper(mContext);
        if (answer.PartType == 404 || answer.PartType == 403) {
            int oldNum = zHelper.getRightNum(answer.TitleNum);
            if (rightNum == 1 && oldNum < 3) {//正确
                rightNum = zHelper.getRightNum(answer.TitleNum) + 1;
            } else {//错误
                if (oldNum > 0) {
                    rightNum = oldNum - 1;
                }
            }
        } else {//单选
            int oldNum = zHelper.getRightNum(answer.TitleNum);
            if (rightNum == 1 && oldNum == 0) {//正确
                rightNum = oldNum + 1;
            } else {//错误
                if (oldNum > 0) {
                    rightNum = oldNum - 1;
                }
            }
        }
        zHelper.setRightNum(titleNum, rightNum);
    }


    class ViewHolder {
        public LinearLayout playQuestionImageView;
        public ImageView quesImageImageView;
        public TextPage quesContenTextPage;                //存放问题题目的TextPage
        public LinearLayout answerSelectTextPage;        //存放问题选项文本内容的LinearLayout
        public RelativeLayout relativeLayout;            //整个大的面板
        public LinearLayout questionContentImage;        //图片题目
        public ImageView ivTable;

        public Button aButton;
        public Button bButton;
        public Button cButton;
        public Button dButton;
        public Button eButton;
        public Button fButton;
        public Button gButton;
        public Button hButton;
    }

    // 记录每道题作题的情况
    public class DoQuestionCond {
        int rightNum = 0;// 正确的次数
        int wrongNum = 0;// 错误的次数
        int answerNum = 0;// 答案的个数
        public boolean result = false;// 该题是否答对
        int[] clickState = new int[8];// 0代表没有选过，1代表选过选对了，2代表选错了
    }

    @Override
    public void notifyDataSetChanged() {
        iniQuestionCondList();
        super.notifyDataSetChanged();
    }
}
