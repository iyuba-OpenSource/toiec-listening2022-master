/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.core.R;


/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class TabText extends LinearLayout {
	private TextView text;
	private ImageView image;
	private View root;

	public TabText(Context context) {
		super(context);
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = vi.inflate(	R.layout.tab_text, this);
		init();
		// TODO Auto-generated constructor stub
	}

	public TabText(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = vi.inflate(R.layout.tab_text, this);
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {
		// TODO Auto-generated method stub
		text = (TextView) root.findViewById(R.id.text);
		image = (ImageView) root.findViewById(R.id.pic);
	}

	public void setText(String name) {
		text.setText(name);
	}

	public void getFocus() {
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(300);
		image.startAnimation(animation);
		text.setTextColor(0xfffa3f2b);
		text.setTextSize(22);
		image.setVisibility(View.VISIBLE);
	}

	public void loseFocus() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(300);
		image.startAnimation(animation);
		text.setTextColor(Color.BLACK);
		text.setTextSize(18);
		image.setVisibility(View.INVISIBLE);
	}

	public void show() {
		text.setTextColor(0xfffa3f2b);
		text.setTextSize(22);
		image.setVisibility(View.VISIBLE);
	}

	public void dismiss() {
		text.setTextColor(Color.BLACK);
		text.setTextSize(18);
		image.setVisibility(View.INVISIBLE);
	}
}
