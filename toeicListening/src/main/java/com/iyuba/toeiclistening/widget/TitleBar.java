package com.iyuba.toeiclistening.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;

/**
 * 
 * 
 * 自定义控件
 * 1.重载构造函数 (Context context, AttributeSet attributeSet) 比较重要
 * 2.实例化 自定义布局
 * 2.增加设置属性借口，设置其监听器
 * @author tuomao
 *
 */
public class TitleBar extends RelativeLayout{
	private Context mContext;
	private Button back;
	private TextView title;
	public TitleBar(Context context) {
		super(context);
		mContext=context;
		ini();
		// TODO Auto-generated constructor stub
	}
	//当没有设置自定义属性时，会调用这个构造函数,此构造函数必不可少
	public TitleBar(Context context, AttributeSet attributeSet){ 
		super(context, attributeSet);
		mContext=context;
		ini();
	}
	public TitleBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		ini();
	}
	
	public void setTitleText(String text){
		if(text!=null){
			title.setText(text);
		}
	}
	public void setButtonVisible(int visibility){
		back.setVisibility(visibility);	
	}
	public void ini(){
		((Activity)mContext).getLayoutInflater().inflate(R.layout.title_bar, this);
		back=(Button)findViewById(R.id.button_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((Activity)mContext).finish();
			}
		});
		title=(TextView)findViewById(R.id.play_title_info);
	}
}
