package com.iyuba.headnewslib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.iyuba.headnewslib.adapter.DetailAdapter;
import com.iyuba.headnewslib.model.Article;
import com.iyuba.headnewslib.model.ArticleDetail;
import com.iyuba.headnewslib.model.HeadlineTheme;
import com.iyuba.headnewslib.protocol.GetArticleDetailRequest;
import com.iyuba.headnewslib.protocol.GetArticleDetailResponse;
import com.iyuba.headnewslib.util.Constant;
import com.iyuba.headnewslib.util.NetWorkState;
import com.iyuba.headnewslib.widget.CustomToast;
import com.iyuba.http.BaseHttpResponse;
import com.iyuba.http.toolbox.ExeProtocol;
import com.iyuba.http.toolbox.ProtocolResponse;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReadActivity extends Activity implements OnClickListener {
    private static final String TAG = ReadActivity.class.getSimpleName();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.CHINA);
    private final static SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
    private static final String IMAGE = "http://cms."+Constant.IYBHttpHead+"/cms/news/image/";

    private Context mContext;

    View titleBar;
    ImageButton back_btn;
    ImageButton overflow_btn;
    TextView titleTv;

    ListView lv_details;
    private DetailAdapter detailAdapter;
    //ImageView iv_comment;
//	ImageView iv_share;
    //BadgeView commentBadge;
    ImageView newsImage;
    TextView articleTitletv;
    TextView articleUpdatedtv;
    TextView articleSourceTv;
//	TextView writeComment;
    //WordCard card;

    private Article article;

    private HeadlineTheme mTheme;
    //private RequestQueue requestQueue;

    //private String userId = "0";

	/*
    private TextPageSelectTextCallBack tpscb = new TextPageSelectTextCallBack() {

		@Override
		public void selectTextEvent(String selectText) {
			if (selectText.matches("^[a-zA-Z'-]*.{1}")) {
				String regEx = "[^a-zA-Z'-]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(selectText);
				selectText = m.replaceAll("").trim();
				card.setVisibility(View.VISIBLE);
				card.searchWord(selectText);
			} else {
				card.setVisibility(View.GONE);
				CustomToast.showToast(mContext, R.string.play_please_take_the_word);
			}
		}

		@Override
		public void selectParagraph(int paragraph) {
			// TODO Auto-generated method stub
		}
	};
	*/

    private Handler handler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // set article detail
                    @SuppressWarnings("unchecked")
                    List<ArticleDetail> alist = (List<ArticleDetail>) msg.obj;
                    detailAdapter = new DetailAdapter(mContext, alist);
                    lv_details.setAdapter(detailAdapter);
                    overflow_btn.setClickable(true);
                    break;
                case 2: // update comment number
                    //int commentNumber = (Integer) msg.obj;
                    //commentBadge.setText(commentNumber + "");
                    break;
                case 3:
                    CustomToast.showToast(getApplicationContext(), R.string.server_error);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.headnewslib_read_activity);

        article = getIntent().getParcelableExtra("article");
        //userId = String.valueOf(Account.uid);

        mTheme = getIntent().getParcelableExtra(HeadlineTheme.TAG);
        if (mTheme == null)
            mTheme = HeadlineTheme.DEFAULT_THEME;

        initTitlebar();
        lv_details = (ListView) findViewById(R.id.lv_details);
        initview();

        if (NetWorkState.isConnectingToInternet(this)) {
            ExeProtocol.exe(new GetArticleDetailRequest(article.getNewsId()), new ProtocolResponse() {

                @Override
                public void finish(BaseHttpResponse bhr) {
                    GetArticleDetailResponse response = (GetArticleDetailResponse) bhr;
                    Message msg = handler.obtainMessage(0);
                    msg.obj = response.details;
                    handler.sendMessage(msg);
                }

                @Override
                public void error() {
                    handler.sendEmptyMessage(3);
                }
            });
        }
    }

    private void initTitlebar() {
        titleBar = findViewById(R.id.read_titlebar);
        titleBar.setBackgroundColor(mTheme.titleBgColor);
        titleTv = (TextView) findViewById(R.id.titlebar_title);
        titleTv.setText(getLabel(article.getCategory()));
        titleTv.setTextColor(mTheme.titleTextColor);
        back_btn = (ImageButton) findViewById(R.id.titlebar_back_button);
        back_btn.setImageResource(mTheme.backBtnResId);
        back_btn.setOnClickListener(this);
        overflow_btn = (ImageButton) findViewById(R.id.titlebar_overflow_button);
        overflow_btn.setImageResource(R.drawable.headnews_mode_rotate);
        overflow_btn.setOnClickListener(this);
        overflow_btn.setClickable(false);
    }

    private void initview() {
//		commentBadge = new BadgeView(mContext);
//		commentBadge.setTextSize(8);
//		commentBadge.setBackground(7, Color.parseColor("#d3321b"));
//		commentBadge.setText("0");
        View headView = getLayoutInflater().inflate(R.layout.headview, null);
        lv_details.addHeaderView(headView, null, false);
        findsetview();
//		commentBadge.setTargetView(iv_comment);
        String url = IMAGE + article.getPic();
        ImageLoader.getInstance().displayImage(url, newsImage);
        articleTitletv.setText(article.getTitle());
        articleSourceTv.setText("(" + article.getSource() + ")");
        String createdtimestring;
        try {
            createdtimestring = sdf2.format(sdf.parse(article.getCreatTime()));
        } catch (ParseException e1) {
            createdtimestring = "";
        }
        articleUpdatedtv.setText(createdtimestring);
    }

    private void findsetview() {
        lv_details = (ListView) findViewById(R.id.lv_details);
        //iv_comment = (ImageView) findViewById(R.id.iv_comment);
        //iv_share = (ImageView) findViewById(R.id.iv_share);
        newsImage = (ImageView) findViewById(R.id.nwi_head);
        articleTitletv = (TextView) findViewById(R.id.article_title);
        articleUpdatedtv = (TextView) findViewById(R.id.article_updatetime);
        articleSourceTv = (TextView) findViewById(R.id.article_source);
        //writeComment = (TextView) findViewById(R.id.tv_write_comment);
        //card = (WordCard) findViewById(R.id.wordcard);
//		iv_comment.setOnClickListener(this);
//		iv_share.setOnClickListener(this);
//		writeComment.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void changeContentMode() {
        if (detailAdapter.getShowMode() == DetailAdapter.MODE_ENG) {
            detailAdapter.setShowMode(DetailAdapter.MODE_CHNENG);
        } else {
            detailAdapter.setShowMode(DetailAdapter.MODE_ENG);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.titlebar_back_button) {
            onBackPressed();
        } else if (id == R.id.titlebar_overflow_button) {
            changeContentMode();
        }/* else if (id == R.id.iv_share) {
			showShare();
		} else if (id == R.id.tv_write_comment) {
			showInputDialog();
		} else if (id == R.id.iv_comment) {
			showComments();
		}*/
    }

	/*
	void showShare() {
		String imageUrl = IMAGE + article.getPic();
		String text = "我正在读" + article.getTitle() + " " + article.getTitle_cn() + " "
				+ "[ http://news.iyuba.cn/m/essay/" + transformCreatedTime(article.getCreatTime())
				+ article.getNewsId() + ".html ]";
		String siteUrl = "http://news.iyuba.con/m/essay/"
				+ transformCreatedTime(article.getCreatTime()) + article.getNewsId() + ".html";

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(article.getTitle() + article.getTitle_cn());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(siteUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");
		// imageUrl是Web图片路径，sina需要开通权限
		oks.setImageUrl(imageUrl);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(siteUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("爱语吧的这款应用真的很不错啊~推荐！");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("爱语吧");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(siteUrl);
		oks.setDialogMode();
		oks.setSilent(false);
		// 启动分享GUI
		oks.show(this);
	}
	*/

    private String transformCreatedTime(String createdTime) {
        String result = null;
        try {
            SimpleDateFormat tsdf = new SimpleDateFormat("yyyy/MM/dd/", Locale.CHINA);
            result = tsdf.format(sdf.parse(createdTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//		updateCommentNumber();
    }

	/*
	private void updateCommentNumber() {
		GetCommentRequest rq = new GetCommentRequest(article.getNewsId(), 1, 1, new RequestCallBack() {

			@Override
			public void requestResult(Request request) {
				GetCommentRequest res = (GetCommentRequest) request;
				if (res.isRequestSuccessful()) {
					Message msg = handler.obtainMessage(2);
					msg.obj = res.counts;
					handler.sendMessage(msg);
				}
			}
		});
		rq.setTag(requestQueue);
		requestQueue.add(rq);
	}
	*/

	/*
	void showInputDialog() {
		View layout = getLayoutInflater().inflate(R.layout.input_dialog, null);
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		final CustomDialog dialog = builder.setContentView(layout).create();
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = getWindowManager().getDefaultDisplay().getWidth();
		dialogWindow.setGravity(Gravity.BOTTOM);
		dialogWindow.setAttributes(lp);
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				updateCommentNumber();
			}
		});
		dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		Button senBtn = (Button) layout.findViewById(R.id.send_btn);
		final EditText edt = (EditText) layout.findViewById(R.id.comment_et);
		senBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!verifyContent(edt.getText().toString())) {
					edt.setError(getString(R.string.comment_lengthlimit));
				} else {
					SendCommentRequest rq = new SendCommentRequest(userId, article.getNewsId(), edt.getText()
							.toString());
					rq.setTag(requestQueue);
					requestQueue.add(rq);
					dialog.dismiss();
				}
			}
		});
	}
	*/

	/*
	void showComments() {
		if (commentBadge.getText().equals("0")) {
			CustomToast.showToast(mContext, "暂无评论", 1000);
		} else {
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("articleId", article.getNewsId());
			startActivity(intent);
		}
	}
	*/

    private boolean verifyContent(String content) {
        return (content.length() != 0);
    }

    private String getLabel(int categoryId) {
        String result = "";
        String[] labels = getResources().getStringArray(R.array.headline_category_title);
        int[] ids = getResources().getIntArray(R.array.headline_category_id);
        int index;
        for (index = 0; index < ids.length; index++) {
            if (ids[index] == categoryId) {
                result = labels[index];
                break;
            }
        }
        return result;
    }

}
