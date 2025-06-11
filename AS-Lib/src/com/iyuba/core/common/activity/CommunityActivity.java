package com.iyuba.core.common.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.R;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.DividerItemDecoration;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.iyumooc.teacher.API.TeacherApiStores;
import com.iyuba.core.iyumooc.teacher.adapter.QuestionListAdapter;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.teacher.activity.QuesDetailActivity;
import com.iyuba.core.teacher.activity.QuezActivity;
import com.iyuba.core.teacher.activity.SelectQuestionType;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesRequest;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesResponse;
import com.iyuba.core.teacher.sqlite.op.QuestionOp;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommunityActivity extends BaseActivity {
    private static final int PRELOAD_SIZE = 6;

    private Context mContext;
    private TextView tvSelectQuesType, tvTitle;
    private ImageView btnEditQues;
    ContextMenu contextMenu;

    private RecyclerView mRecyclerView;

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRequestDataRefresh = false;
    private boolean mIsFirstTimeTouchBottom = true;
    private int mPage = 1;

    private QuestionListAdapter quesAdapter;
    //private View root;
    private ArrayList<QuestionListBean.QuestionDataBean> quesList = new ArrayList<QuestionListBean.QuestionDataBean>();
    private ArrayList<QuestionListBean.QuestionDataBean> localQuesList = new ArrayList<QuestionListBean.QuestionDataBean>();
    public int pageNum = 1;
    boolean isLast = false;
    private QuestionOp questionOp;
    private CustomDialog waitDialog;

    private static final String[] question_app_type_arr =
            {"全部", "VOA", "BBC", "听歌", "CET4", "CET6",
                    "托福", "N1", "N2", "N3", "微课", "雅思", "初中",
                    "高中", "考研", "新概念", "走遍美国", "英语头条"};

    private static final String[] question_ability_type_arr =
            {"全部", "口语", "听力", "阅读", "写作", "翻译",
                    "单词", "语法", "其他"};

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();

        initWidget();
    }*/

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_community;
    }

    @Override
    protected void initVariables() {
        mContext = this;
        questionOp = new QuestionOp(mContext);
        waitDialog = WaittingDialog.showDialog(mContext);
        localQuesList = questionOp.findDataLastTwenty();
        if (AccountManagerLib.Instace(mContext).userId == null) {
            AccountManagerLib.Instace(mContext).userId = "0";
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvSelectQuesType = (TextView) findViewById(R.id.tv_select_ques_type);
        tvTitle = (TextView) findViewById(R.id.tv_teacher_title);
        btnEditQues = (ImageView) findViewById(R.id.tinsert);
        contextMenu = (ContextMenu) findViewById(R.id.context_menu);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_ques_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetHeaderDataTask().execute();
            }
        });

        // 确定每个item的排列方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                // 这里要复写一下，因为默认宽高都是wrap_content
                // 这个不复写，你点击的背景色就只充满你的内容
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
//				StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.setHasFixedSize(true);
        quesAdapter = new QuestionListAdapter(mContext);
        quesAdapter.clearList();
        mRecyclerView.setAdapter(quesAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                mContext, R.drawable.recycler_rectangle, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });

        mRecyclerView.addOnScrollListener(getOnBottomListener(layoutManager));

        quesAdapter.setOnItemClickListener(new QuestionListAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                QuestionManager.getInstance().question = quesList.get(position);
                Intent intent = new Intent();
                intent.setClass(mContext, QuesDetailActivity.class);
                intent.putExtra("qid", quesList.get(position).getQuestionid() + "");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                QuestionManager.getInstance().question = quesList.get(position);

                if (quesList.get(position).getUid().equals(AccountManagerLib.Instace(mContext).userId)) {

                    final int theqid = quesList.get(position).getQuestionid();
                    final int num = position;
                    contextMenu.setText(mContext.getResources().getStringArray(
                            R.array.choose_delete));
                    contextMenu.setCallback(new ResultIntCallBack() {

                        @Override
                        public void setResult(int result) {
                            // TODO Auto-generated method stub
                            switch (result) {
                                case 0:
                                    delAlertDialog(theqid + "", num);
                                    break;
                                case 1:
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, QuesDetailActivity.class);
                                    intent.putExtra("qid", theqid + "");
                                    startActivity(intent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    contextMenu.show();
                } else {

                }
            }
        });

//		mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST));

        tvSelectQuesType.setOnClickListener(SelectQuesOnClickListener);

        btnEditQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //如果没登录则跳转登录
                if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {

                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(mContext, QuezActivity.class);
                startActivity(intent);
            }
        });
        //等待进度条
        handler.sendEmptyMessage(2);
        getHeaderData();

    }

    @Override
    protected void loadData() {

    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.lib_mooc_ques_list, container, false);
//        return root;
//    }

    // 顶部今日头条事件监听器
    private View.OnClickListener SelectQuesOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 跳转到筛选Activity
            Intent intent = new Intent();
            intent.setClass(mContext, SelectQuestionType.class);
            startActivityForResult(intent, 0);
        }
    };

    public void initWidget() {
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> orfl = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            // TODO Auto-generated method stub

            if (NetWorkState.isConnectingToInternet()) {// 开始刷新

                new GetHeaderDataTask().execute();

            } else {// 刷新失败
                if (localQuesList.size() > 0) {
                    binderAdapterLocalDataHandler.post(binderAdapterLocalDataRunnable);
                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(4);
                } else {
                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(9);
                    handler.sendEmptyMessage(4);
                }
            }

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            // TODO Auto-generated method stub

            if (NetWorkState.isConnectingToInternet()) {// 开始刷新

                new GetFooterDataTask().execute();

            } else {// 刷新失败
                handler.sendEmptyMessage(3);
                handler.sendEmptyMessage(9);
                handler.sendEmptyMessage(4);
            }

        }
    };

    //	RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
    //		return new RecyclerView.OnScrollListener() {
    RecyclerView.OnScrollListener getOnBottomListener(final LinearLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                boolean isBottom =
//						layoutManager.findLastCompletelyVisibleItemPositions(
//								new int[2])[1] >=
//								quesAdapter.getItemCount() -
//										PRELOAD_SIZE;
                        layoutManager.findFirstVisibleItemPosition() >=
                                quesAdapter.getItemCount() -
                                        PRELOAD_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                    if (!mIsFirstTimeTouchBottom) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        mPage += 1;
//						loadData();
                        new GetFooterDataTask().execute();
                    } else {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        };
    }

    private void delAlertDialog(final String id, final int num) {
        AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle(R.string.alert_title);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage(mContext.getString(R.string.alert_delete));
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        ExeProtocol.exe(new DeleteAnswerQuesRequest("0", id, AccountManagerLib.Instace(mContext).userId), new ProtocolResponse() {

                            @Override
                            public void finish(BaseHttpResponse bhr) {
                                // TODO Auto-generated method stub
                                DeleteAnswerQuesResponse tr = (DeleteAnswerQuesResponse) bhr;
                                if (tr.result.equals("1")) {
                                    quesList.remove(num);

//									handler.sendEmptyMessage(1);
                                    binderAdapterDataHandler.post(binderAdapterDataRunnable);
                                    handler.sendEmptyMessage(8);

                                } else {
                                    quesList.remove(num);

//									handler.sendEmptyMessage(1);
                                    binderAdapterDataHandler.post(binderAdapterDataRunnable);
                                    handler.sendEmptyMessage(8);
                                }
                            }

                            @Override
                            public void error() {
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    quesAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    waitDialog.show();
                    break;
                case 3:
                    waitDialog.dismiss();
                    break;
                case 4:
//					refreshView.onRefreshComplete();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 5:
                    CustomToast.showToast(mContext, "已经是最新数据了哦~");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 6:
                    CustomToast.showToast(mContext, "暂时没有数据,下拉刷新数据！");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 7:
                    CustomToast.showToast(mContext, "已是最后一页");
                    break;
                case 8:
                    CustomToast.showToast(mContext, "删除成功!");
                case 9:
                    CustomToast.showToast(mContext, "请检查网络连接！");
                    break;
            }
        }
    };

    private Handler binderAdapterDataHandler = new Handler();
    private Runnable binderAdapterDataRunnable = new Runnable() {
        public void run() {
            quesAdapter.clearList();
            quesAdapter.addList(quesList);
            quesAdapter.notifyDataSetChanged();
            handler.sendEmptyMessage(3);
        }
    };

    private Handler binderAdapterLocalDataHandler = new Handler();
    private Runnable binderAdapterLocalDataRunnable = new Runnable() {
        public void run() {
            quesList.clear();
            quesList.addAll(localQuesList);
            quesAdapter.clearList();
            quesAdapter.addList(localQuesList);
            quesAdapter.notifyDataSetChanged();
            handler.sendEmptyMessage(3);
        }
    };

    public void getHeaderData() {
        int quesAppType;
        quesAppType = ConfigManager.Instance().loadInt("quesAppType");
        if (quesAppType == 0) {
            quesAppType = 105;
        }
//        System.out.println(quesAppType+"**********************************");
        int quesAbilityType = ConfigManager.Instance().loadInt("quesAbilityType");
//        int quesAbilityType = 105;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
                .baseUrl("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TeacherApiStores apiStores = retrofit.create(TeacherApiStores.class);
        Call<QuestionListBean> call = apiStores.getQuesList("json", 3, quesAbilityType, quesAppType, 1, -1);
        call.enqueue(new Callback<QuestionListBean>() {
            @Override
            public void onResponse(Call<QuestionListBean> v,Response<QuestionListBean> response) {

                if (response.body().getData() != null && response.body().getData().size() != 0) {
                    quesList.clear();
                    quesList.addAll(response.body().getData());
                    binderAdapterDataHandler.post(binderAdapterDataRunnable);
                    questionOp.insertQuestions(response.body().getData());
                    pageNum = 2;
                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(4);
                    if (response.body().getData().size() < 20) isLast = true;
                    else isLast = false;
                } else {
                    handler.sendEmptyMessage(4);
                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(5);
                }

            }

            @Override
            public void onFailure(Call<QuestionListBean> v,Throwable t) {
                if (t != null) {
                    if (localQuesList.size() > 0) {
                        binderAdapterLocalDataHandler.post(binderAdapterLocalDataRunnable);
                        handler.sendEmptyMessage(3);
                        handler.sendEmptyMessage(4);
                    } else {
                        handler.sendEmptyMessage(3);
                        handler.sendEmptyMessage(6);
                        handler.sendEmptyMessage(4);
                    }
                }
            }
        });

    }

    public void getFooterData() {
        int quesAppType = 105;
//        quesAppType = ConfigManager.Instance().loadInt("quesAppType");
        int quesAbilityType = ConfigManager.Instance().loadInt("quesAbilityType");
//        int quesAbilityType = 105;

        if (isLast) {
            handler.sendEmptyMessage(4);
            handler.sendEmptyMessage(7);
            return;
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                //这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
                .baseUrl("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/")
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        TeacherApiStores apiStores = retrofit.create(TeacherApiStores.class);
        Call<QuestionListBean> call = apiStores.getQuesList("json", 3, quesAbilityType, quesAppType, pageNum, -1);
        call.enqueue(new Callback<QuestionListBean>() {
            @Override
            public void onResponse(Call<QuestionListBean> v,Response<QuestionListBean> response) {

                if (response.body().getData() != null && response.body().getData().size() != 0) {
                    quesList.addAll(response.body().getData());
                    questionOp.insertQuestions(response.body().getData());
                    pageNum++;
                    binderAdapterDataHandler.post(binderAdapterDataRunnable);

                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(4);
                    if (response.body().getData().size() < 20) isLast = true;
                    else isLast = false;
                } else {
                    handler.sendEmptyMessage(4);
                    handler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onFailure(Call<QuestionListBean> v,Throwable t) {
                if (t != null) {
                    if (localQuesList.size() > 0) {
                        binderAdapterLocalDataHandler.post(binderAdapterLocalDataRunnable);
                        handler.sendEmptyMessage(3);
                        handler.sendEmptyMessage(4);
                    } else {
                        handler.sendEmptyMessage(3);
                        handler.sendEmptyMessage(6);
                        handler.sendEmptyMessage(4);
                    }
                }
            }
        });

    }

    private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
            getHeaderData();
            return null;
        }
    }

    private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
            getFooterData();
            return null;
        }
    }
}
