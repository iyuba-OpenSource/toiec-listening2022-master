package com.iyuba.core.teacher.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.onekeyshare.OnekeyShare;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.manager.RecordManager;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.thread.UploadFile;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.CustomExpandableListView;
import com.iyuba.core.common.widget.MyScrollView;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.teacher.adapter.ChatAdapter;
import com.iyuba.core.teacher.adapter.CommentListAdapter;
import com.iyuba.core.teacher.protocol.AnswerFollowRequest;
import com.iyuba.core.teacher.protocol.AnswerFollowResponse;
import com.iyuba.core.teacher.protocol.AnswerQuesRequest;
import com.iyuba.core.teacher.protocol.AnswerQuesResponse;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesRequest;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesResponse;
import com.iyuba.core.teacher.protocol.GetChatListRequest;
import com.iyuba.core.teacher.protocol.GetChatListResponse;
import com.iyuba.core.teacher.protocol.GetCommentListRequest;
import com.iyuba.core.teacher.protocol.GetCommentListResponse;
import com.iyuba.core.teacher.sqlite.mode.AnswerInfo;
import com.iyuba.core.teacher.sqlite.mode.AnswerType;
import com.iyuba.core.teacher.sqlite.mode.Chat;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class QuesDetailActivity extends Activity {
    private Context mContext;
    AnswerType answerType = new AnswerType();
    private ImageView btnBack;
    private ImageView btnShare;
    private ImageView userIcon;
    private TextView userName;
    private TextView questInfo;
    private TextView attention;
    private TextView quesDisc;
    //	private TextView questiondetail;
    private ImageView quesPic;
    private View chatView;
    private TextView teacherTag;
    private CustomExpandableListView chatListview;
    private View commentView;
    private TextView studentTag;
    private ListView commentListview;

    private int inputMode = 0;// 默认为文字输入，0文字，1语音
    private ImageView setMode;
    private View expressLayout;
    private EditText express;
    private ImageView holdSpeak;
    private Button sendBtn;
    private ImageView showMore;
    private TextView photoAlbum;
    private TextView takePhoto;
    private View bottomView;

    private QuestionListBean.QuestionDataBean question = new QuestionListBean.QuestionDataBean();
    private List<List<Chat>> childChatList;
    private List<AnswerInfo> groupChatList;
    private List<AnswerInfo> commentList;

    private ChatAdapter chatAdapter;
    private CommentListAdapter commentListAdapter;

    private CustomDialog waitDialog;

    private String uid;
    private String username;
    private int qid;

    private String tmpFilepath;
    private String tmpPhotoFilePath = Environment.getExternalStorageDirectory()
            + "/temp_photo.jpg";
    private File tmpPhotoFile = new File(tmpPhotoFilePath);
    private String tmpSoundFilePath = Environment.getExternalStorageDirectory()
            + "/temp_sound.jpg";
    private File tmpSoundFile = new File(tmpSoundFilePath);

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private View voiceView;
    private ImageView voiceValue;
    private Drawable amps[] = new Drawable[8];
    private RecordManager rManager;
    int show = 0;// ==2用于控制是否取消加载中的提示

    MyScrollView scrollView;
    View linear_express;

    private ImageView setCommentModeButton;
    private int commentMode = 0;// 0是文字评论，1是语音评论
    Button voiceExpressButton;
    private Player mPlayer;
    EditText expressEditText;
    Button voiceListenButton;
    Button button_express;
    ContextMenu contextMenu;

    private static String shareQuestionTitleUrl;
    private static String shareCourseTitleImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lib_ques_detail);
        mContext = this;
        waitDialog = WaittingDialog.showDialog(mContext);

        initData();
        initWidget();
        init();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (commentListAdapter != null) {
            commentListAdapter.stopVoice();
        }
    }

    public void initData() {
        if (AccountManagerLib.Instace(mContext).userId == null) {
            uid = "0";
        }
        uid = AccountManagerLib.Instace(mContext).userId;
        username = AccountManagerLib.Instace(mContext).userName;
        Intent intent = getIntent();
        String theQid = intent.getStringExtra("qid");
        if (theQid == null)
            theQid = "1";
        qid = Integer.parseInt(theQid);

        shareQuestionTitleUrl = "http://m."+com.iyuba.core.util.Constant.IYBHttpHead+"/teacher/qdetail.jsp?qid=" + qid;
        shareCourseTitleImageUrl = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android/images/iyuba/iyuba.png";
    }

    public void initWidget() {
        mPlayer = new Player(mContext, null);
        scrollView = (MyScrollView) findViewById(R.id.scrollq);
        linear_express = findViewById(R.id.linear_express);
        linear_express.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
        contextMenu = (ContextMenu) findViewById(R.id.context_menu);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnShare = (ImageView) findViewById(R.id.btn_share);
        btnShare.setVisibility(View.GONE);
        userIcon = (ImageView) findViewById(R.id.user_icon);
        userName = (TextView) findViewById(R.id.user_name);
        questInfo = (TextView) findViewById(R.id.quest_info);
        attention = (TextView) findViewById(R.id.attention);
        quesDisc = (TextView) findViewById(R.id.ques_disc);
//		questiondetail = (TextView) findViewById(R.id.questiondetail);
        quesPic = (ImageView) findViewById(R.id.ques_detail_pic);
        chatView = findViewById(R.id.answer_view);
        teacherTag = (TextView) findViewById(R.id.teacher_tag);
        chatListview = (CustomExpandableListView) findViewById(R.id.answer_listview);
        commentView = findViewById(R.id.comment_view);
        studentTag = (TextView) findViewById(R.id.student_tag);
        commentListview = (ListView) findViewById(R.id.comment_listviewq);

        OnChildClickListener occl = new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1,
                                        int arg2, int arg3, long arg4) {

                final int theaid = childChatList.get(arg2).get(arg3).answerid;
                final int num = arg2;
                final int num2 = arg3;
                final String auid = childChatList.get(arg2).get(arg3).fromid
                        + "";

                final String type = "2";

                return false;
            }
        };

        chatListview.setOnChildClickListener(occl);

        setMode = (ImageView) findViewById(R.id.set_mode);
        express = (EditText) findViewById(R.id.express);
        expressLayout = findViewById(R.id.express_layout);
        holdSpeak = (ImageView) findViewById(R.id.hold_speak);
        sendBtn = (Button) findViewById(R.id.send);
        showMore = (ImageView) findViewById(R.id.show);
        photoAlbum = (TextView) findViewById(R.id.phone_album);
        takePhoto = (TextView) findViewById(R.id.take_photo);
        bottomView = findViewById(R.id.bottom_view);
        voiceView = findViewById(R.id.voice_view);
        voiceValue = (ImageView) findViewById(R.id.mic_value);
        button_express = (Button) findViewById(R.id.button_express);
        setCommentModeButton = (ImageButton) findViewById(R.id.setcommentmode);
        setCommentModeButton.setOnClickListener(ocl_comment);
        setCommentModeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (commentMode == 1) {
                    switchToText();
                } else {

                    switchToVoice();
                }
            }
        });

        // 发表评论回复的功能
        button_express.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                    if (answerType.sub == 0) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入评论！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                answerQuestion(expressionInput.toString()
                                        .toString(), 2);
                                expressEditText.setText("");
                                button_express.setEnabled(false);
                            }
                        } else {
                            // 发送语音
                            new CommentThread().start();

                        }
                    } else if (answerType.sub == 1) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入回复！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                answerQuestion(expressionInput.toString()
                                        .toString(), 1);
                                expressEditText.setText("");
                                button_express.setEnabled(false);
                            }
                        } else {
                            // 发送语音
                            new answerThread().start();
                        }
                    } else if (answerType.sub == 2) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入内容！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                // answerQuestion(expressionInput.toString().toString());
                                answerFollowQuestion(expressionInput.toString()
                                        .trim());
                                expressEditText.setText("");
                                button_express.setEnabled(false);
                            }
                        } else {
                            // 发送语音
                            new UploadVoice().start();
                            // new UploadFollow().start();
                        }
                    }

                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 录音
        voiceExpressButton = (Button) findViewById(R.id.voice_express);
        voiceExpressButton.setOnTouchListener(voice_otl);

        expressEditText = (EditText) findViewById(R.id.editText_express);

        voiceListenButton = (Button) findViewById(R.id.voice_express_listen);

        voiceListenButton.setOnClickListener(ocl_comment);
        voiceListenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                mPlayer.reset();
                mPlayer.playUrl(Constant.voiceCommentAddr);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        btnShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    showShare();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        userIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SocialDataManager.Instance().userid = question.getUid();
//                Intent intent = new Intent();
//                intent.setClass(mContext, PersonalHome.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                mContext.startActivity(intent);
                startActivity(PersonalHomeActivity.buildIntent (mContext,
                        Integer.valueOf(SocialDataManager.Instance().userid),
                        SocialDataManager.Instance().userName, 0));
            }
        });

        attention.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        quesPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mContext, ShowLargePicActivity.class);
                intent.putExtra("pic", "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"
                        + question.getImg());
                mContext.startActivity(intent);
            }
        });

        setMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                inputMode = 1 - inputMode;
                if (inputMode == 0) {
                    sendBtn.setVisibility(View.VISIBLE);
                    showMore.setVisibility(View.GONE);
                    expressLayout.setVisibility(View.VISIBLE);
                    holdSpeak.setVisibility(View.GONE);
                    setMode.setImageDrawable(getResources().getDrawable(
                            R.drawable.chatting_setmode_keyboard_btn));
                    bottomView.setVisibility(View.GONE);
                } else {

                    sendBtn.setVisibility(View.GONE);
                    showMore.setVisibility(View.VISIBLE);
                    expressLayout.setVisibility(View.GONE);
                    holdSpeak.setVisibility(View.VISIBLE);
                    setMode.setImageDrawable(getResources().getDrawable(
                            R.drawable.chatting_setmode_keyboard_btn));
                }
            }
        });

        holdSpeak.setOnTouchListener(voice_otl);

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(express.getWindowToken(), 0);
                    String expressStr = express.getText().toString();
                    if ("".equals(expressStr.trim())) {
                        handler_chat.sendEmptyMessage(0);

                        answerFollowQuestion(expressStr.trim());

                    } else {
                        Message msg = handler_chat.obtainMessage();
                        msg.obj = expressStr;
                        handler.sendEmptyMessage(3);
                        handler_chat.handleMessage(msg);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        showMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bottomView.getVisibility() == View.VISIBLE) {
                    bottomView.setVisibility(View.GONE);
                } else {
                    bottomView.setVisibility(View.VISIBLE);
                }
            }
        });

        photoAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        takePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tmpPhotoFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            }
        });

    }

    public static void setExpandableListViewHeight(Context context,
                                                   ExpandableListView expandableListView) {

        ExpandableListAdapter listAdapter = expandableListView.getExpandableListAdapter();

        ViewGroup.LayoutParams params = expandableListView
                .getLayoutParams();

        if (listAdapter == null) {
            // pre -condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {

            View listGroupItem = listAdapter.getGroupView(i, true, null, expandableListView);
            listGroupItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            listGroupItem.measure(0, 0);
            totalHeight += listGroupItem.getMeasuredHeight();

            for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                View listChildItem = listAdapter.getChildView(i, j, false, null, expandableListView);
                listChildItem.measure(0, 0);
                totalHeight += listChildItem.getMeasuredHeight();
            }

        }

        float f = context.getResources().getDisplayMetrics().density;
        totalHeight = (int) (totalHeight / 1.5 * f);
        params.height = totalHeight;
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }

    private void setListViewHeight(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        float f = mContext.getResources().getDisplayMetrics().density;
        params.height = (int) (params.height / 1.5 * f);

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void answerQuestion(final String answer, final int userType) {

        username = AccountManagerLib.Instace(mContext).userName;
        show = 3;

        ClientSession.Instace().asynGetResponse(
                new AnswerQuesRequest(AccountManagerLib.Instace(mContext).userId
                        + "", AccountManagerLib.Instace(mContext).userName,
                        userType, qid, answer),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                        AnswerQuesResponse tr = (AnswerQuesResponse) response;
                        if ("1".equals(tr.result) || "0".equals(tr.result)) {
                            handler.sendEmptyMessage(4);
                            if (tr.jiFen != null && Integer.parseInt(tr.jiFen) > 0) {
                                Message msg = new Message();
                                msg.what = 9;
                                msg.arg1 = Integer.parseInt(tr.jiFen);
                                handler.sendMessage(msg);
                            } else {
                                handler_chat.sendEmptyMessage(1);
                            }
                            if (userType == 1) {
                                new GetChatDataTask().execute();
                            } else {

                                Log.e("iyuba", "提交--------");
                                new GetCommentDataTask().execute();
                            }
                        } else {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(2);
                            Message msg = handler_chat.obtainMessage();
                            msg.obj = answer;
                            msg.what = 4;
                            handler_chat.sendMessage(msg);
                        }
                    }
                },
                new IErrorReceiver() {
                    @Override
                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                        if (errorResponse != null) {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(2);
                            Message msg = handler_chat.obtainMessage();
                            msg.obj = answer;
                            msg.what = 4;
                            handler_chat.sendMessage(msg);
                        }
                    }
                });
    }

    // 提交追问或者老师的追问回答
    public void answerFollowQuestion(final String answer) {

        ClientSession.Instace().asynGetResponse(
                new AnswerFollowRequest(
                        AccountManagerLib.Instace(mContext).userId, answerType.aid
                        + "", answer),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                        AnswerFollowResponse tr = (AnswerFollowResponse) response;
                        if ("1".equals(tr.result)) {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(1);
                            new GetChatDataTask().execute();
                        } else {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(2);
                            Message msg = handler_chat.obtainMessage();
                            msg.obj = answer;
                            msg.what = 4;
                            handler_chat.sendMessage(msg);
                        }
                    }
                },
                new IErrorReceiver() {
                    @Override
                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {

                    }
                });

    }

    private void showShare() {

        //先隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(express.getWindowToken(), 0);

        //ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareQuestionTitleUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(Constant.APP_NAME);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APP_NAME);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareQuestionTitleUrl);
        //图片的网络路径，新浪微博、人人、QQ空间和Linked-in
        oks.setImageUrl(shareCourseTitleImageUrl);

        oks.setText("小伙伴们快来用爱语吧客户端交流问题吧～");
        oks.setTitle("爱语吧客户端");
        oks.setUrl(shareQuestionTitleUrl);

//		 oks.setCallback(new PlatformActionListener() {
//
//			@Override
//			public void onError(Platform arg0, int arg1, Throwable arg2) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onCancel(Platform arg0, int arg1) {
//				// TODO Auto-generated method stub
//
//			}
//		});

//		 oks.setText("测试分享文字 http://www.baidu.com");
//		 oks.setTitle("分享");
//		 oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//		 oks.setUrl("http://www.baidu.com");

        // 启动分享GUI
        oks.show(this);
    }


    private void delAlertDialog(final String id, final int num, final int num2,
                                final String type) {
        AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle(R.string.alert_title);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage(mContext.getString(R.string.alert_delete));
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        ClientSession.Instace().asynGetResponse(
                                new DeleteAnswerQuesRequest(type, id,
                                        AccountManagerLib.Instace(mContext).userId),
                                new IResponseReceiver() {
                                    @Override
                                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                                        DeleteAnswerQuesResponse tr = (DeleteAnswerQuesResponse) response;
                                        if (tr.result.equals("1")) {
                                            childChatList.get(num).remove(num2);

                                            handler.sendEmptyMessage(1);
                                            handler.sendEmptyMessage(8);
                                        } else {
                                            childChatList.get(num).remove(num2);

                                            handler.sendEmptyMessage(1);
                                            handler.sendEmptyMessage(8);
                                        }
                                    }
                                },
                                new IErrorReceiver() {
                                    @Override
                                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {

                                    }
                                });
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                    }
                });
        alert.show();
    }

    Handler handler_chat = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.input_content);
                    break;
                case 1:
                    CustomToast.showToast(mContext, R.string.send_success);
                    button_express.setEnabled(true);
                    expressEditText.setText("");
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.send_fail);
                    button_express.setEnabled(true);
                    break;
                case 3:
                    express.setText("");
                    break;
                case 4:
                    String text = (String) msg.obj;
                    express.setText(text);
                    break;
                case 5:
                    String answer = (String) msg.obj;
                    // answerQuestion(answer);
                    break;
            }
        }
    };

    // 发送回复语音
    class answerThread extends Thread {
        File file = null;

        @Override
        public void run() {
            super.run();
            try {

                // 用户名转码
                try {
                    username = AccountManagerLib.Instace(mContext).userName;
                    username = new String(username.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                username = TextAttr.encode(TextAttr.encode(TextAttr
                        .encode(username)));

                String uri = "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/answerQuestion.jsp?"
                        + "&format=json"
                        + "&authorid="
                        + uid
                        + "&username="
                        + username + "&questionid=" + qid + "&authortype=" + 1;
                Map<String, String> textParams = new HashMap<String, String>();
                Map<String, File> fileParams = new HashMap<String, File>();
                Log.e("iyuba",
                        "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/answerQuestion.jsp?"
                                + "&format=json" + "&authorid=" + uid
                                + "&username=" + username + "&questionid" + qid
                                + "&authortype=" + 1);
                file = new File(Constant.voiceCommentAddr);
                fileParams.put("file1", file);

                if (file != null && file.exists()) {
                    UploadFile.post(uri, textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetChatDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    // 发送评论语音
    class CommentThread extends Thread {
        File file = null;

        @Override
        public void run() {
            super.run();
            try {

                // 用户名转码
                try {
                    username = AccountManagerLib.Instace(mContext).userName;
                    username = new String(username.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                username = TextAttr.encode(TextAttr.encode(TextAttr
                        .encode(username)));

                String uri = "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/answerQuestion.jsp?"
                        + "&format=json"
                        + "&authorid="
                        + uid
                        + "&username="
                        + username + "&questionid=" + qid + "&authortype=" + 2;

                Map<String, String> textParams = new HashMap<String, String>();
                Map<String, File> fileParams = new HashMap<String, File>();
                Log.e("iyuba",
                        "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/answerQuestion.jsp?"
                                + "&format=json" + "&authorid=" + uid
                                + "&username=" + username + "&questionid" + qid
                                + "&authortype=" + 2);
                file = new File(Constant.voiceCommentAddr);
                fileParams.put("file1", file);

                if (file != null && file.exists()) {
                    UploadFile.post(uri, textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetCommentDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    // 发送语音追问
    public class UploadVoice extends Thread {
        File file = null;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            StringBuffer sb = new StringBuffer(
                    "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/zaskQuestion.jsp?"
                            + "format=json"
                            + "&answerid="
                            + answerType.aid
                            + "&fromid="
                            + AccountManagerLib.Instace(mContext).userId
                            + "&zcontent="
                            + TextAttr.encode(TextAttr.encode(TextAttr
                            .encode(""))));
            Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileParams = new HashMap<String, File>();
            file = new File(Constant.voiceCommentAddr);
            fileParams.put("file1", file);
            Log.e("iyuba", sb.toString());
            if (file != null && file.exists()) {
                try {

                    UploadFile.post(sb.toString(), textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetChatDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
        }
    }

    private OnTouchListener voice_otl = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            if (v.getId() == R.id.voice_express) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.comment_speak_pressed));

                    amps[1] = getResources().getDrawable(R.drawable.amp1);
                    amps[2] = getResources().getDrawable(R.drawable.amp2);
                    amps[3] = getResources().getDrawable(R.drawable.amp3);
                    amps[4] = getResources().getDrawable(R.drawable.amp4);
                    amps[5] = getResources().getDrawable(R.drawable.amp5);
                    amps[6] = getResources().getDrawable(R.drawable.amp6);
                    amps[7] = getResources().getDrawable(R.drawable.amp7);

                    handler_sound.sendEmptyMessageDelayed(1, 500);

                    File file = new File(Constant.voiceCommentAddr);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    rManager = new RecordManager(file, voiceValue);
                    rManager.startRecord();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    v.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.comment_speak_normal));

                    handler_sound.removeMessages(1);
                    handler_sound.sendEmptyMessage(0);
                    rManager.stopRecord();
                }
            } else {
                handler_sound.removeMessages(1);
                handler_sound.sendEmptyMessage(0);
                rManager.stopRecord();
            }
            return true;
        }
    };

    Handler handler_sound = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    voiceView.setVisibility(View.GONE);
                    voiceListenButton.setVisibility(View.VISIBLE);

                    break;
                case 1:
                    voiceView.setVisibility(View.VISIBLE);

                    break;
                case 3:
                    voiceListenButton.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void init() {
        new GetChatDataTask().execute();
        new GetCommentDataTask().execute();
    }

    public void initQuestion() {
        // question = QuestionManager.getInstance().question;
        GitHubImageLoader.Instace(mContext)
                .setCirclePic(question.getUid(), userIcon);
        userName.setText(question.getUsername() + "");
        // questInfo.setText(question.time.substring(0,19)+ " " );

        question.setCreatetime(question.getCreatetime().substring(0, 19));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = sdf.parse(question.getCreatetime()).getTime();
            questInfo.setText(formatTime(time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        quesDisc.setText(question.getQuestion() + "");
//		if (question.getQuestion() != null) {
//			question.setQuestion(question.getQuestion().replace("null", "")) ;
//			if (!question.getQuestion().equals("")) {
//				questiondetail.setText(question.getQuestion());
//				questiondetail.setVisibility(View.VISIBLE);
//			}
//
//		}
        if (question.getImg().equals("")) {
            quesPic.setVisibility(View.GONE);
        } else {
            quesPic.setVisibility(View.VISIBLE);

            ImageLoader.getInstance().displayImage("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/" + question.getImg(),
                    quesPic);

        }
    }

    private String formatTime(long time) {
        Date date = new Date(time);
        Date date2 = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar.setTime(date);
        calendar2.setTime(date2);

        if (calendar2.get(Calendar.DAY_OF_YEAR)
                - calendar.get(Calendar.DAY_OF_YEAR) > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        } else if (System.currentTimeMillis() - time > 60 * 60 * 1000) {
            return (System.currentTimeMillis() - time) / (60 * 60 * 1000)
                    + "小时之前";
        } else if (System.currentTimeMillis() - time > 60 * 1000) {
            return (System.currentTimeMillis() - time) / (60 * 1000) + "分钟之前";
        } else if (System.currentTimeMillis() - time > 60) {

            return (System.currentTimeMillis() - time) / (1000) + "秒之前";

        } else if (System.currentTimeMillis() - time == 0) {
            return "1秒之前";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int jiFen = 0;
            super.handleMessage(msg);
            jiFen = msg.arg1;
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.send_success);
                    break;
                case 1:
                    // 判断此老师如果 没有回复此问题则他只能

                    // if(answerType.isanswer==0&&AccountManagerLib.Instace(mContext).isteacher.equals("1")&&!question.uid.equals(AccountManagerLib.Instace(mContext).userId)){
                    // expressEditText.setHint("回复问题");
                    // answerType.sub=1;
                    // }

                    if (chatAdapter != null) {
                        chatAdapter.notifyDataSetChanged();

                        setExpandableListViewHeight(mContext, chatListview);
                    }

                    Message msg2 = handler.obtainMessage();
                    msg2.what = 23;
                    handler.sendMessageDelayed(msg2, 100);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    break;
                case 2:
                    if (commentListAdapter != null) {
                        studentTag.setVisibility(View.VISIBLE);
                        studentTag.setText("讨论区(" + commentList.size() + ")");
                        commentListview.setAdapter(commentListAdapter);
                        commentListAdapter.notifyDataSetChanged();
                        ViewGroup.LayoutParams params = commentListview
                                .getLayoutParams();

                        int height = 0;
                        height = 160 * commentList.size();
                        float f = mContext.getResources().getDisplayMetrics().density;
                        height = (int) (height / 1.5 * f);
                        params.height = height;
                        commentListview.setLayoutParams(params);
                        Message msg1 = handler.obtainMessage();
                        msg1.what = 22;
                        handler.sendMessageDelayed(msg1, 100);

                    }
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    break;
                case 3:
                    waitDialog.show();
                    break;
                case 4:
                    if (show >= 2)
                        waitDialog.dismiss();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    // scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.network_error);
                    break;
                case 6:
                    // chatView.setVisibility(View.GONE);
                    break;
                case 7:
                    chatView.setVisibility(View.VISIBLE);
                    teacherTag.setVisibility(View.VISIBLE);
                    teacherTag.setText("老师(" + groupChatList.size() + ")");
                    chatAdapter = new ChatAdapter(mContext, groupChatList,
                            childChatList, expressEditText, answerType);
                    chatListview.setAdapter(chatAdapter);

//				setListViewHeightBasedOnChildren(chatListview);

                case 8:
                    initChatList();
                    break;
                case 9:
                    Toast.makeText(mContext, "提交成功+" + jiFen + "积分！", Toast.LENGTH_SHORT).show();
                    break;
                case 21:
                    initQuestion();
                    break;
                case 22:
                    int ch = 0;
                    for (int cl = 0; cl < commentList.size(); cl++) {

                        if (commentListview.getChildAt(cl) != null) {
                            ch = ch + commentListview.getChildAt(cl).getHeight();
                        }
                    }
                    ViewGroup.LayoutParams params = commentListview
                            .getLayoutParams();

                    params.height = ch + 20;
                    commentListview.setLayoutParams(params);
                    break;

                case 23:
                    // CustomToast.showToast(mContext,
                    // "881---"+answerType.isanswer);
                    if (answerType.isanswer == 0
                            && AccountManagerLib.Instace(mContext).isteacher
                            .equals("1")
                            && !question.getUid().equals(AccountManagerLib
                            .Instace(mContext).userId)) {
                        expressEditText.setHint("回复问题");
                        answerType.sub = 1;
                    } else {

                        if (expressEditText.getText().toString().trim().equals("")) {
                            expressEditText.setHint("评论");
                            answerType.sub = 0;
                        }
                    }
                    float f = mContext.getResources().getDisplayMetrics().density;
                    int it = (int) (250 / 1.5 * f);
                    int sh = 0;
                    for (int cl = 0; cl < childChatList.size(); cl++) {
                        sh = sh + chatListview.getChildAt(cl).getHeight() + it;

                        Log.e("iyuba", "" + chatListview.getChildAt(cl).getHeight());

                    }
                    ViewGroup.LayoutParams paramss = chatListview.getLayoutParams();

                    break;
                case 30:
                    commentListAdapter = new CommentListAdapter(mContext,
                            commentList);
                    break;
                case 31:
                    commentView.setVisibility(View.VISIBLE);
                    break;
                case 32:
                    commentView.setVisibility(View.GONE);
                    break;
                case 35:
                    CustomToast.showToast(mContext, "问题已被删除!");
                    finish();
                    break;
            }
        }
    };

    public void getChatList() {
        ClientSession.Instace().asynGetResponse(
                new GetChatListRequest(qid),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                        GetChatListResponse tr = (GetChatListResponse) response;
                        show++;// 加载完毕值加1
                        if (tr.result.equals("1")) {
                            // 获取问题内容并使其显示
                            question = tr.item;
                            QuestionManager.getInstance().question = question;
                            handler.sendEmptyMessage(21);
                            childChatList = tr.chatList;
                            groupChatList = tr.infoList;

                            if (groupChatList.size() != 0) {
                                handler.sendEmptyMessage(7);
                                handler.sendEmptyMessage(8);
                            } else {
                                handler.sendEmptyMessage(6);
                            }

                            handler.sendEmptyMessage(1);
                            handler.sendEmptyMessage(4);
                        } else {

                            handler.sendEmptyMessage(4);
                            handler.sendEmptyMessage(35);
                        }
                    }
                },
                new IErrorReceiver() {
                    @Override
                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                        handler.sendEmptyMessage(4);
                    }
                });
    }

    public void getCommentList() {
        ClientSession.Instace().asynGetResponse(
                new GetCommentListRequest(qid),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                        GetCommentListResponse tr = (GetCommentListResponse) response;
                        show++;// 加载完毕值加1

                        if (tr.result.equals("1")) {
                            commentList = tr.infoList;
                            if (commentList.size() != 0) {

                                handler.sendEmptyMessage(31);
                                handler.sendEmptyMessage(30);
                                // commentListAdapter = new CommentListAdapter(mContext,
                                // commentList);

                            } else {
                                handler.sendEmptyMessage(32);
                            }

                            handler.sendEmptyMessage(2);
                            handler.sendEmptyMessage(4);
                        } else {
                            handler.sendEmptyMessage(4);
                            handler.sendEmptyMessage(35);
                        }
                    }
                },
                new IErrorReceiver() {
                    @Override
                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                        handler.sendEmptyMessage(4);
                    }
                });
    }

    // 获取老师回答和追问
    private class GetChatDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            handler.sendEmptyMessage(3);
            getChatList();
            return null;
        }
    }

    // 获取评论
    private class GetCommentDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            handler.sendEmptyMessage(3);
            getCommentList();
            return null;
        }
    }

    public void initChatList() {
        chatListview.setGroupIndicator(null);
        for (int i = 0; i < groupChatList.size(); i++) {
            chatListview.expandGroup(i);
            setExpandableListViewHeight(mContext, chatListview);
        }
    }

    private void switchToText() {
        findViewById(R.id.voicebutton).setVisibility(View.GONE);
        findViewById(R.id.edittext).setVisibility(View.VISIBLE);
        // 显示软键盘
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.showSoftInput(express, InputMethodManager.RESULT_SHOWN);

        setCommentModeButton
                .setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
        commentMode = 0;
    }

    private void switchToVoice() {
        findViewById(R.id.voicebutton).setVisibility(View.VISIBLE);
        findViewById(R.id.edittext).setVisibility(View.GONE);
        // 关闭软键盘
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(express.getWindowToken(), 0);
        setCommentModeButton
                .setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
        commentMode = 1;
    }

    private OnClickListener ocl_comment = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            show = 2;
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case 2:
                    if (commentMode == 1) {
                        switchToText();
                    } else {

                        switchToVoice();
                    }
                    break;
                case 4:
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                    }
                    mPlayer.reset();
                    mPlayer.playUrl(Constant.voiceCommentAddr);
                    break;

                case 1:
                    if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

                        if (answerType.sub == 0) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入评论！");
                                    expressEditText.setText("");
                                } else {
                                    answerQuestion(expressionInput.toString()
                                            .toString(), 2);
                                    expressEditText.setText("");
                                    button_express.setEnabled(false);
                                }
                            } else {
                                // 发送语音
                                // new UploadVoice().start();

                            }
                        } else if (answerType.sub == 1) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入回复！");
                                    expressEditText.setText("");
                                } else {
                                    answerQuestion(expressionInput.toString()
                                            .toString(), 1);
                                    expressEditText.setText("");
                                    button_express.setEnabled(false);
                                }
                            } else {
                                // 发送语音
                                new answerThread().start();
                            }
                        } else if (answerType.sub == 2) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入内容！");
                                    expressEditText.setText("");
                                } else {
                                    if (expressionInput.toString().trim().length() > 100) {
                                        CustomToast.showToast(mContext,
                                                "最多只能输入100字，已超啦!");
                                        return;
                                    }
                                    // answerQuestion(expressionInput.toString().toString());
                                    answerFollowQuestion(expressionInput.toString()
                                            .trim());
                                }
                            } else {
                                // 发送语音
                                new UploadVoice().start();
                                // new UploadFollow().start();

                            }
                        }

                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            // 触摸屏幕时候关闭软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(express.getWindowToken(), 0);

            // 判断此老师如果 没有回复此问题则他只能回复问题
            if (answerType.isanswer == 0
                    && AccountManagerLib.Instace(mContext).isteacher.equals("1")
                    && !question.getUid()
                    .equals(AccountManagerLib.Instace(mContext).userId)) {
                expressEditText.setHint("回复问题");
                answerType.sub = 1;
            } else {

                if (expressEditText.getText().toString().trim().equals("")) {
                    expressEditText.setHint("评论");
                    answerType.sub = 0;
                    switchToText();
                }
            }
        }
        return true;
    }

}
