package com.iyuba.toeiclistening.activity;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.BlogContent;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IErrorReceiver;
import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.protocol.BlogContentRequest;
import com.iyuba.toeiclistening.protocol.BlogContentResponse;
import com.iyuba.toeiclistening.sqlite.BlogHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.widget.TitleBar;
import com.iyuba.toeiclistening.widget.WordCard;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.dialog.WaittingDialog;
import com.iyuba.toeiclistening.widget.subtitle.TextPage;
import com.iyuba.toeiclistening.widget.subtitle.TextPageSelectTextCallBack;

public class InformationActivity extends Activity{

	private TextView blogTitle,blogTime;
	private TextPage blogText;
	private BlogContent blogContent;
	private TitleBar title;
	private Context mContext;
	private WordCard wordCard;
	int position;//listView position 从1开始
	
	private String message = "";
	private BlogHelper bh;
	private Thread thread;
	
	private CustomDialog dialog;
	private Button btn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);			//设置为无标题样式
		setContentView(R.layout.activity_blog);
		mContext=this;
		position=getIntent().getIntExtra("position",0);
		Log.e("position",position+"");
		blogContent=DataManager.Instance().blogList.get(position-1);
		bh = new BlogHelper(mContext);
		message = blogContent.message;
		
		dialog = new WaittingDialog().wettingDialog(mContext);
		
		if(message != null){
			Log.d("查看日志内容:", message);
		}
		
		ini();
		
		if (message==null||message.equals("")) {
			dialog.show();
			ClientSession.Instace().asynGetResponse(new BlogContentRequest(blogContent.blogid), new IResponseReceiver() {
				@Override
				public void onResponse(BaseHttpResponse response, BaseHttpRequest request,
						int rspCookie) {
					// TODO 自动生成的方法存根
					BlogContentResponse rs = (BlogContentResponse) response ;
					if (rs.result.equals("1")) {
						Log.d("message", rs.message);
						message= rs.message;
						blogContent.message=rs.message;
						bh.saveBlogContentData(blogContent);
							
						Log.d("当Message为空时获取日志内容:", message);
						
						handler.sendEmptyMessage(0);
					}
				}
			},new IErrorReceiver() {
				@Override
				public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
						int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
			},new INetStateReceiver() {
				
				@Override
				public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onStartConnect(BaseHttpRequest request, int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onSendFinish(BaseHttpRequest request, int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onSend(BaseHttpRequest request, int rspCookie, int len) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onNetError(BaseHttpRequest request, int rspCookie,
						ErrorResponse errorInfo) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onConnected(BaseHttpRequest request, int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
				
				@Override
				public void onCancel(BaseHttpRequest request, int rspCookie) {
					// TODO 自动生成的方法存根
					
				}
			});
		}
		else {
			handler.sendEmptyMessage(0);
			handler.sendEmptyMessage(2);		//等待界面消失
		}
	}
	public void ini(){
//		title=(TitleBar)findViewById(R.id.title);
		btn_back =(Button)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		blogTitle=(TextView)findViewById(R.id.blog_title);
		blogTime=(TextView)findViewById(R.id.blog_time);
		blogText=(TextPage)findViewById(R.id.text);
	
		wordCard=(WordCard)findViewById(R.id.wordcard);
		
//		title.setTitleText("资讯");
		blogTitle.setText(blogContent.subject);
		long time=Long.parseLong(blogContent.dateline)*1000;
		blogTime.setText(DateFormat.format("yyyy-MM-dd kk:mm:ss", time));
		//图片和文字交叉的显示  设置文字
//		new Thread(){
//			public void run(){
//				super.run();
//				handler.sendEmptyMessage(0);
//			}
//		}.start();
		
		blogText.setTextSize(18);
		
		blogText.setTextpageSelectTextCallBack(new TextPageSelectTextCallBack() {
			
			@Override
			public void selectTextEvent(String selectText) {
				// TODO Auto-generated method stub
				wordCard.setVisibility(View.GONE);
				if (selectText.matches("^[a-zA-Z]*")) {
					if(CheckNetWork.isNetworkAvailable(mContext)){
						wordCard.setVisibility(View.VISIBLE);
						wordCard.searchWord(selectText);
					}else{
						Toast.makeText(mContext, R.string.play_check_network, 1000).show();
					}
				} else {
					
					CustomToast.showToast(mContext,
							R.string.play_please_take_the_word, 1000);
				}
			}
			@Override
			public void selectParagraph(int paragraph) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				blogText.setText(Html.fromHtml(blogContent.message, new ImageGetter() {
					
					@Override
					public Drawable getDrawable(String arg0) {
						// TODO Auto-generated method stub
						InputStream is = null;
						try {
							is = (InputStream) new URL(arg0).getContent();
							Drawable d = Drawable.createFromStream(is, "src");
							d.setBounds(0, 0, d.getIntrinsicWidth(),
									d.getIntrinsicHeight());
							is.close();
							return d;
						} catch (Exception e) {
							return null;
						}
					}
				}, null));
				
				dialog.dismiss();
				
				break;

			default:
				break;
			}
		}
	};
}
