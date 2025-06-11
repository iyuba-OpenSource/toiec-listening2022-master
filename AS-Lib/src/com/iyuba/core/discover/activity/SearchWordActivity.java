package com.iyuba.core.discover.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.iyuba.core.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.WordDBOp;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.discover.adapter.WordListAdapter;

import java.util.ArrayList;

/**
 * 查单词界面
 *
 * @author chentong
 * @version 1.0
 */
public class SearchWordActivity extends BasisActivity {
    private Context mContext;
    private Button backBtn;
    private ListView wordList;
    private WordListAdapter wordListAdapter;
    private EditText searchText;
    private Button search;
    private View clear;
    private ArrayList<Word> words = new ArrayList<Word>();
    private WordDBOp wOp;
    private String tempWord;
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_word);
        mContext = this;
         CrashApplication.addActivity(this);
        wOp = new WordDBOp(mContext);
        Intent intent = getIntent();
        word = intent.getStringExtra("wordPart");
        initWidget();
    }

    /**
     *
     */
    private void initWidget() {
        // TODO Auto-generated method stub
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        searchText = (EditText) findViewById(R.id.search_text);
        searchText.setText(word);
        clear = findViewById(R.id.clear);
        search = (Button) findViewById(R.id.search);
        searchText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                searchAppointWord(v.getText().toString());
                searchText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                return true;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                tempWord = arg0.toString();
                fuzzySearchWord(tempWord);
            }
        });
        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                searchText.setText("");
                handler.sendEmptyMessage(0);
            }
        });
        search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String word = searchText.getText().toString();
                if (!word.equals("") && word.length() != 0) {
                    searchAppointWord(word);
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        });
        wordList = (ListView) findViewById(R.id.word_list);
        wordList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                searchAppointWord(words.get(arg2).key);
            }
        });
        handler.sendEmptyMessage(0);
    }

    /**
     * //	 * @param string
     * 根据String模糊查询
     */
    private void fuzzySearchWord(String word) {
        // TODO Auto-generated method stub
        handler.sendEmptyMessage(1);
    }

    /**
     * //	 * @param string
     * 根据String精确查询 同时跳转内容界面
     */
    private void searchAppointWord(String word) {
        // TODO Auto-generated method stub
        wOp.updateWord(word);
        Intent intent = new Intent(mContext, WordContentActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    words = wOp.findDataByView();
                    if (wordListAdapter == null) {
                        wordListAdapter = new WordListAdapter(mContext, words);
                        wordList.setAdapter(wordListAdapter);
                    } else {
                        wordListAdapter.setData(words);
                        wordListAdapter.notifyDataSetChanged();
                    }
                    break;
                case 1:
                    words = wOp.findDataByFuzzy(tempWord);
                    wordListAdapter.setData(words);
                    wordListAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.word_null);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(0);
    }

}
