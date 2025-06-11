package com.iyuba.toeiclistening.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.R;

public class WebActivity extends BasisActivity {  //实现程序启动广告链接

    private Button backButton;
    private WebView web;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.lib_web);
        setProgressBarVisibility(true);
        backButton = (Button) findViewById(R.id.button_back);
        textView = (TextView) findViewById(R.id.play_title_info);
        web = (WebView) findViewById(R.id.webView);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        web.loadUrl(this.getIntent().getStringExtra("url"));
        textView.setText("爱语吧精品应用");
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setDomStorageEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if(url == null) return false;

                try {
                    if (url.startsWith("http:") || url.startsWith("https:"))
                    {
                        view.loadUrl(url);
                        return true;
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
            }
        });

//			web.setWebChromeClient(new WebChromeClient() {
//				// Set progress bar during loading
//				public void onProgressChanged(WebView view, int progress) {
//					web.this.setProgress(progress * 100);
//				}
//			});
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
    public void onBackPressed() {
//			if (web.canGoBack()) {
//				web.goBack(); // goBack()表示返回webView的上一页面
//			} else if (!web.canGoBack()) {
        Intent intent3 = new Intent(WebActivity.this,
                MainActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("isFirstInfo", 0);
        startActivity(intent3);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//			}
    }


}
