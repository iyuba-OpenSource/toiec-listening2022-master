package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;

public class AllAppActivity extends Activity{
	private Button backButton;
	private WebView web;
	private TextView textView;
	private CustomDialog wettingDialog;
	private TextView titleInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		backButton = (Button) findViewById(R.id.button_back);
		textView = (TextView) findViewById(R.id.play_title_info);
		web = (WebView) findViewById(R.id.webView);
		wettingDialog = new WaittingDialog().wettingDialog(AllAppActivity.this);
		titleInfo=(TextView)findViewById(R.id.play_title_info);
		titleInfo.setText(this.getIntent().getStringExtra("title"));
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl(this.getIntent().getStringExtra("url"));
		web.requestFocus();
		web.getSettings().setBuiltInZoomControls(true);// 显示放大缩小
		web.getSettings().setSupportZoom(true);// 可放大
		web.getSettings().setRenderPriority(RenderPriority.HIGH);// 提高渲染,加快加载速度
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setLoadWithOverviewMode(true);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				wettingDialog.show();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				wettingDialog.dismiss();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				wettingDialog.dismiss();
			}
		});
		web.setDownloadListener(new DownloadListener() {

			@Override
			// TODO Auto-generated method stub
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (web.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			web.goBack(); // goBack()表示返回webView的上一页面
			return true;
		} else if (!web.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void finish() {
		if (wettingDialog != null)
			wettingDialog.dismiss();
		super.finish();
	}
}
