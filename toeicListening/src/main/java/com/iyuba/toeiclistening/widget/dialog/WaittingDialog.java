package com.iyuba.toeiclistening.widget.dialog;

import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;


/**
 * 自定义等待窗口
 * 
 * @author 李京蔚
 * 
 */
public class WaittingDialog {
	private TextView mTextView;
	private String topString = "友情提示:";
	private String hint[] = { 
			"文章可以进行后台播放", "点击单词可以查询释义", "可以在设置中设置选项",
			"设置中有更多软件等待您体验", "单词同步功能可以将您在其他应用中收藏的单词同步至本应用" };

	public WaittingDialog() {
	}

	/**
	 * 等待窗口
	 */
	public CustomDialog wettingDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.wetting, null);
		mTextView = (TextView) layout.findViewById(R.id.hint);
		mTextView.setText(getText());
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
		CustomDialog cDialog = customBuilder.setContentView(layout).create();
		return cDialog;
	}

	private String getText() {
		int i = new Random().nextInt(100) % hint.length;
		return topString + hint[i];
	}
}
