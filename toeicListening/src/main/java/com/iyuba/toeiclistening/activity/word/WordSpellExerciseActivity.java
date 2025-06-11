package com.iyuba.toeiclistening.activity.word;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.activity.base.BaseActivity;
import com.iyuba.toeiclistening.databinding.ActivityWordSpellBinding;
import com.iyuba.toeiclistening.entity.RememberWord;
import com.iyuba.toeiclistening.entity.WordQuestion;
import com.iyuba.toeiclistening.mvp.presenter.WordAnswerPresenter;
import com.iyuba.toeiclistening.mvp.view.WordAnswerContract;
import com.iyuba.toeiclistening.util.popup.AnalysisPopup;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 单词拼写页面
 */
public class WordSpellExerciseActivity extends BaseActivity<WordAnswerContract.WordAnswerView, WordAnswerContract.WordAnswerPresenter>
        implements WordAnswerContract.WordAnswerView {

    private ActivityWordSpellBinding binding;

    private List<RememberWord> jpConceptWordList;

    private List<WordQuestion> wordQuestions;

    private Random random;
    private int position;

    private AnalysisPopup analysisPopup;

    private DecimalFormat decimalFormat;


    /**
     * type
     * 0：中文
     * 1：英文
     */
    private int questionType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = 0;
        random = new Random();
        decimalFormat = new DecimalFormat("##.0");
        getBundle();


        binding.toolbar.toolbarIvRight.setVisibility(View.GONE);
        initOperation();
        initData();

    }

    @Override
    public View initLayout() {

        binding = ActivityWordSpellBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public WordAnswerContract.WordAnswerPresenter initPresenter() {
        return new WordAnswerPresenter();
    }

    private void initOperation() {

        binding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        binding.toolbar.toolbarIvTitle.setText("单词拼写");
        binding.toolbar.toolbarIvRight.setVisibility(View.INVISIBLE);

        //进度
        binding.wsPbIndex.setMax(jpConceptWordList.size());
        //下一个
        binding.waButNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == (wordQuestions.size() - 1)) {

                    showResultAlert();
                    return;
                }
                if (binding.wsEtWord.getText().toString().trim().length() == 0) {

                    toast("请填写答案");
                    return;
                }
                WordQuestion wordQuestion = wordQuestions.get(position);
                if (wordQuestion.gettPosition() == -1) {//没有作答

                    binding.waButAnalysis.setVisibility(View.VISIBLE);
                    binding.waButNext.setVisibility(View.VISIBLE);
                    //拼写只使用tPosition，使用1代表正确，使用0代表错误
                    if (wordQuestion.getWord().word.equalsIgnoreCase(binding.wsEtWord.getText().toString())) {

                        wordQuestion.settPosition(1);
                        position++;
                        showData();
                    } else {

                        wordQuestion.settPosition(0);
                    }
                } else {

                    position++;
                    //设置答题开始时间
//                    wordQuestions.get(position).setBeginTime(getCurrentTime());

//                    binding.toolbar.toolbarIvTitle.setText((position + 1) + "/" + jpConceptWordList.size());
                    showData();
                }
            }
        });
        binding.waButAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.wsEtWord.getWindowToken(), InputMethodManager.SHOW_FORCED);
                initAnalysisPopup(wordQuestions.get(position).getWord());
            }
        });
    }

    /**
     * 检测闯关是否失败
     */
    private void showResultAlert() {

        int totalDoQuestion = 0;//共做多少道题
        int tNUM = 0;//已做题正确数量
        int fNum = 0;//已做题错误数量

        for (int i = 0; i < wordQuestions.size(); i++) {

            WordQuestion wq = wordQuestions.get(i);
            totalDoQuestion++;
            if (wq.gettPosition() == 1) {
                tNUM++;
            } else {
                fNum++;
            }
        }
//        double fPercentage = 100.0 * fNum / wordQuestions.size();//总的错误率
//        double questionPercentage = 100.0 * totalDoQuestion / wordQuestions.size();//总做题进度

        double tPercentage = 100.0 * tNUM / totalDoQuestion;

        AlertDialog alertDialog = new AlertDialog.Builder(WordSpellExerciseActivity.this)
                .setTitle("训练结果")
                .setMessage("共做：" + totalDoQuestion + "题，\n做对：" + tNUM + "题，\n正确比例" + decimalFormat.format(tPercentage) + "%")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }


    /**
     * 解释、解析弹窗
     *
     * @param conceptWord
     */
    private void initAnalysisPopup(RememberWord conceptWord) {

        if (analysisPopup == null) {

            analysisPopup = new AnalysisPopup(this);
        }

        analysisPopup.setJpWord(conceptWord);
        analysisPopup.showPopupWindow();
    }

    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String strCurrTime = formatter.format(curDate);
        return strCurrTime;
    }

    private void initData() {


        new Thread(() -> {

            wordQuestions = new ArrayList<>();
            for (int i = 0; i < jpConceptWordList.size(); i++) {

                RememberWord conceptWord = jpConceptWordList.get(i);
                WordQuestion wordQuestion = new WordQuestion();
                wordQuestion.setWord(conceptWord);
//                wordQuestion.setAnswerList(getAnswerData());
//                int tPosition = random.nextInt(4);
//                wordQuestion.getAnswerList().set(tPosition, conceptWord);
                wordQuestion.settPosition(-1);//-1为没有作答
                wordQuestion.setType(questionType);
                wordQuestions.add(wordQuestion);
            }

            runOnUiThread(this::showData);
            //设置开始时间
//            wordQuestions.get(0).setBeginTime(getCurrentTime());
        }).start();
    }


    private void showData() {

        //index
        binding.wsPbIndex.setProgress((position + 1));
        binding.wsTvIndex.setText((position + 1) + "/" + jpConceptWordList.size());
        //隐藏按钮
        binding.waButAnalysis.setVisibility(View.INVISIBLE);
        binding.waButNext.setVisibility(View.VISIBLE);

        WordQuestion wordQuestion = wordQuestions.get(position);
        if (wordQuestion.getType() == 0) {

            binding.waTvQuestion.setText(wordQuestion.getWord().explain);
        } else {

            binding.waTvQuestion.setText(wordQuestion.getWord().word);
        }
        binding.wsEtWord.setText("");

        if ((position + 1) == jpConceptWordList.size()) {

            binding.waButNext.setText("完成");
        }
    }


    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            jpConceptWordList = (List<RememberWord>) bundle.getSerializable("DATAS");
            questionType = bundle.getInt("QUESTION_TYPE");
        }
    }

    /**
     * 此页面questionType没什么用，传不传值都行
     *
     * @param activity
     * @param jpConceptWordList
     * @param questionType
     */
    public static void startActivity(Activity activity, List<RememberWord> jpConceptWordList, int questionType) {

        Intent intent = new Intent(activity, WordSpellExerciseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATAS", (Serializable) jpConceptWordList);
        bundle.putInt("QUESTION_TYPE", questionType);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(ToeicApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}