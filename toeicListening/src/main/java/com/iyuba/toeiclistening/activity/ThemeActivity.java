package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.util.Constant;

public class ThemeActivity extends Activity{
	private View backView;
	private Button backButton;
	private ImageView[] backColor = new ImageView[3];
	private ImageView[] textColor = new ImageView[6];
	private ImageView[] ok = new ImageView[9];
	private View[] textSize = new View[3];
	private CheckBox[] check = new CheckBox[3];
	private TextView textcolor;
	private int selected = ConfigManagerMain.Instance().loadInt("textSize");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.theme);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ok[3] = (ImageView) findViewById(R.id.ok4);
		ok[4] = (ImageView) findViewById(R.id.ok5);
		ok[5] = (ImageView) findViewById(R.id.ok6);
		ok[6] = (ImageView) findViewById(R.id.ok7);
		ok[7] = (ImageView) findViewById(R.id.ok8);
		ok[8] = (ImageView) findViewById(R.id.ok9);
		setTextSize();
		setTextColor();
	}


	private void setTextSize() {
		textSize[0] = findViewById(R.id.small);
		textSize[1] = findViewById(R.id.middle);
		textSize[2] = findViewById(R.id.large);
		check[0] = (CheckBox) findViewById(R.id.CheckBox_small);
		check[1] = (CheckBox) findViewById(R.id.CheckBox_middle);
		check[2] = (CheckBox) findViewById(R.id.CheckBox_large);
		switch (selected) {
		case 14:
			check[0].setChecked(true);
			break;
		case 18:
			check[1].setChecked(true);
			break;
		case 22:
			check[2].setChecked(true);
			break;
		default:
			break;
		}
		check[0].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				check[0].setChecked(true);
				check[1].setChecked(false);
				check[2].setChecked(false);
				ConfigManagerMain.Instance().putInt(Constant.TEXTSIZE, Constant.TEXTSIZE_SMALL);
			}
		});
		check[1].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				check[0].setChecked(false);
				check[1].setChecked(true);
				check[2].setChecked(false);
				ConfigManagerMain.Instance().putInt(Constant.TEXTSIZE, Constant.TEXTSIZE_MEDIUM);
			}
		});
		check[2].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				check[0].setChecked(false);
				check[1].setChecked(false);
				check[2].setChecked(true);
				ConfigManagerMain.Instance().putInt(Constant.TEXTSIZE, Constant.TEXTSIZE_BIG);
			}
		});
	}

	private void setTextColor() {
		int color= ConfigManagerMain.Instance().loadInt(Constant.TEXTCOLOR);
		switch (color) {
		case 0xff2983c1:
			setOKVisible(3, 1);
			break;
		case 0xffb982ec:
			setOKVisible(4, 1);
			break;
		case 0xffea3255:
			setOKVisible(5, 1);
			break;
		case 0xff1a8e35:
			setOKVisible(6, 1);
			break;
		case 0xffeb6e1b:
			setOKVisible(7, 1);
			break;
		case 0xff9c3c5c:
			setOKVisible(8, 1);
			break;
		default:
			break;
		}
		textcolor = (TextView) findViewById(R.id.highColor);
		textColor[0] = (ImageView) findViewById(R.id.text1);
		textColor[1] = (ImageView) findViewById(R.id.text2);
		textColor[2] = (ImageView) findViewById(R.id.text3);
		textColor[3] = (ImageView) findViewById(R.id.text4);
		textColor[4] = (ImageView) findViewById(R.id.text5);
		textColor[5] = (ImageView) findViewById(R.id.text6);
		textColor[0].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.skyBlue);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(3, 1);
			}
		});
		textColor[1].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.color1);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(4, 1);
			}
		});
		textColor[2].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.color2);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(5, 1);
			}
		});
		textColor[3].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.color3);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(6, 1);
			}
		});
		textColor[4].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.color4);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(7, 1);
			}
		});
		textColor[5].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int color=getResources().getColor(R.color.color5);
				ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR, color);
				textcolor.setTextColor(color);
				setOKVisible(8, 1);
			}
		});
	}

	private void setOKVisible(int i, int type) {
		if (type == 0) {
			for (int j = 0; j < 3; j++)
				ok[j].setVisibility(View.GONE);
			ok[i].setVisibility(View.VISIBLE);
		}
		if (type == 1) {
			for (int j = 3; j < 9; j++)
				ok[j].setVisibility(View.GONE);
			ok[i].setVisibility(View.VISIBLE);
		}
	}
}
