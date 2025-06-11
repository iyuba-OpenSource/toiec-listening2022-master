package com.iyuba.core.teacher.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.R;
import com.iyuba.configation.Constant;

public class ExerciseModeActivity extends Activity implements OnClickListener {
    private Context mContext;
    private int curMode = 1;
    private Button backButton;
    private RelativeLayout tqRelativeLayout;
    private RelativeLayout tRelativeLayout;
    private RelativeLayout qRelativeLayout;
    private ImageView image1, image2, image3;
    private TextView textView1, textView2, textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exercise_mode);
        mContext = this;
        curMode = ConfigManager.Instance().loadInt(Constant.EXERCISE_MODE);

        getView();
        setView();
    }


    public void getView() {
        backButton = (Button) findViewById(R.id.exercise_mode_button_back);
        tqRelativeLayout = (RelativeLayout) findViewById(R.id.exercice_mode_tq);
        qRelativeLayout = (RelativeLayout) findViewById(R.id.exercice_mode_q);
        tRelativeLayout = (RelativeLayout) findViewById(R.id.exercice_mode_t);
        image1 = (ImageView) findViewById(R.id.exercise_mode_image1);
        image2 = (ImageView) findViewById(R.id.exercise_mode_image2);
        image3 = (ImageView) findViewById(R.id.exercise_mode_image3);
        textView1 = (TextView) findViewById(R.id.exercice_mode_text1);
        textView2 = (TextView) findViewById(R.id.exercice_mode_text2);
        textView3 = (TextView) findViewById(R.id.exercice_mode_text3);
    }

    public void setView() {
        backButton.setOnClickListener(this);
        tqRelativeLayout.setOnClickListener(this);
        tRelativeLayout.setOnClickListener(this);
        qRelativeLayout.setOnClickListener(this);
        setSelected();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.exercise_mode_button_back) {
            finish();
        } else if (v.getId() == R.id.exercice_mode_tq) {
            //0为练习环境，1为精听环境，2为模考环境
            ConfigManager.Instance().putInt(Constant.EXERCISE_MODE, 0);
            curMode = 0;
            setSelected();
        } else if (v.getId() == R.id.exercice_mode_t) {
            //0为练习环境，1为精听环境，2为模考环境
            ConfigManager.Instance().putInt(Constant.EXERCISE_MODE, 1);
            curMode = 1;
            setSelected();
        } else if (v.getId() == R.id.exercice_mode_q) {
            //0为练习环境，1为精听环境，2为模考环境
            ConfigManager.Instance().putInt(Constant.EXERCISE_MODE, 2);
            curMode = 2;
            setSelected();
        }
//		switch (v.getId()) {
//		case R.id.exercise_mode_button_back:
//			finish();
//			break;
//		case R.id.exercice_mode_tq:
//			//0为练习环境，1为精听环境，2为模考环境
//			ConfigManager.Instance().putInt(Constant.EXERCISE_MODE,0 );
//			curMode=0;
//			setSelected();
//			break;
//		case R.id.exercice_mode_t:
//			ConfigManager.Instance().putInt(Constant.EXERCISE_MODE,1 );
//			curMode=1;
//			setSelected();
//			break;
//		case R.id.exercice_mode_q:
//			ConfigManager.Instance().putInt(Constant.EXERCISE_MODE,2 );
//			curMode=2;
//			setSelected();
//			break;
//		default:
//			break;
//		}
    }

    public void setSelected() {
        image1.setImageResource(R.color.transparent);
        image2.setImageResource(R.color.transparent);
        image3.setImageResource(R.color.transparent);
        textView1.setTextColor(getResources().getColor(R.color.black));
        textView2.setTextColor(getResources().getColor(R.color.black));
        textView3.setTextColor(getResources().getColor(R.color.black));
        if (curMode == 0) {
            image1.setImageResource(R.drawable.selected);
            textView1.setTextColor(getResources().getColor(R.color.skyBlue));
        } else if (curMode == 1) {
            image2.setImageResource(R.drawable.selected);
            textView2.setTextColor(getResources().getColor(R.color.skyBlue));
        } else if (curMode == 2) {
            image3.setImageResource(R.drawable.selected);
            textView3.setTextColor(getResources().getColor(R.color.skyBlue));
        }
    }
}
