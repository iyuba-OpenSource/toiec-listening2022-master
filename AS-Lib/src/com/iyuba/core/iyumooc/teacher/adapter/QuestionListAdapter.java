package com.iyuba.core.iyumooc.teacher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.core.R;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.teacher.activity.ShowLargePicActivity;
import com.iyuba.core.teacher.protocol.AgreeAgainstRequest;
import com.iyuba.core.teacher.sqlite.op.CommentAgreeOp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<QuestionListBean.QuestionDataBean> mList = new ArrayList<QuestionListBean.QuestionDataBean>();
    public HashMap<String, String> abilityTypeCatalog = new HashMap<String, String>();
    public HashMap<String, String> appTypeCatalog = new HashMap<String, String>();

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public QuestionListAdapter(Context context, ArrayList<QuestionListBean.QuestionDataBean> quesList) {
        mContext = context;
        mList = quesList;
        setAbilityTypeCatalog();
        setAppTypeCatalog();
    }

    public QuestionListAdapter(Context context) {
        mContext = context;
        setAbilityTypeCatalog();
        setAppTypeCatalog();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lib_list_item_question, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        final QuestionListBean.QuestionDataBean ques = mList.get(viewHolder.getAdapterPosition());

//		viewHolder.itemView.setOnClickListener(new RVOnClickListener(position, mOnItemClickListener));

        if (mOnItemClickListener != null) {
            MaterialRippleLayout rippleLayout = (MaterialRippleLayout) viewHolder.itemView;
            rippleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                }
            });
            rippleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onLongClick(viewHolder.itemView, pos);
                    return true;
                }
            });
        }

        GitHubImageLoader.Instace(mContext).setCirclePic(
                mList.get(position).getUid(), viewHolder.quesIcon);

        if (mList.get(position).getFlg() == 2) {
            viewHolder.quesRecommend.setVisibility(View.VISIBLE);
        } else {
            viewHolder.quesRecommend.setVisibility(View.INVISIBLE);
        }

        if (checkAgree("q" + mList.get(position).getQuestionid(),
                AccountManagerLib.Instace(mContext).userId) == 1) {
            viewHolder.agree.setImageResource(R.drawable.agree_press);
        } else {

            viewHolder.agree.setImageResource(R.drawable.agree_normal);

        }

        viewHolder.agree.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    return;
                }

                if (checkAgree("q" + mList.get(position).getQuestionid(),
                        AccountManagerLib.Instace(mContext).userId) == 1) {
                    handler.sendEmptyMessage(3);
                } else {

                    ExeProtocol.exe(
                            new AgreeAgainstRequest(AccountManagerLib
                                    .Instace(mContext).userId, AccountManagerLib
                                    .Instace(mContext).userName, "questionid",
                                    "" + mList.get(position).getQuestionid()),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    handler.sendEmptyMessage(4);
                                }

                                @Override
                                public void error() {

                                }
                            });

                    new CommentAgreeOp(mContext).saveData(
                            "q" + mList.get(position).getQuestionid(),
                            AccountManagerLib.Instace(mContext).userId, "agree");
                    mList.get(position).setAgreecount(mList.get(position).getAgreecount() + 1);
                    handler.sendEmptyMessage(0);
                }
            }
        });

        viewHolder.quesIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {

                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    return;
                }

//                Intent intent = new Intent();
                SocialDataManager.Instance().userid = ques.getUid();

                mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
                        Integer.valueOf(ques.getUid())
                        , SocialDataManager.Instance().userName, 0));//点击其他人头像时也可以进入
//
//
//                intent.setClass(mContext, PersonalHome.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                mContext.startActivity(intent);
            }
        });

        if (ques.getUid() != null &&
                !"".equals(ques.getUid()) &&
                !ques.getUid().equals("0")
                && Integer.parseInt(ques.getUid()) < 50000000) {
            viewHolder.quesName.setText(ques.getUsername());
        } else {//临时账户
            viewHolder.quesName.setText(ques.getUid());
        }

        if (ques.getCreatetime() == null || "null".equals(ques.getCreatetime())) {
            ques.setCreatetime("");
        }

        if (ques.getLocation() == null || "null".equals(ques.getLocation())) {
            ques.setLocation("");
        }
        ques.setCreatetime(ques.getCreatetime().substring(0, 19));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = sdf.parse(ques.getCreatetime()).getTime();
            viewHolder.quesInfo.setText(formatTime(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // viewHolder.quesInfo.setText(ques.time);

        if (ques.getAnswercount() == 0) {
            viewHolder.answerIcon.setVisibility(View.INVISIBLE);
            viewHolder.isAnswer.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.answerIcon.setVisibility(View.VISIBLE);
            viewHolder.isAnswer.setVisibility(View.VISIBLE);
        }

        if (ques.getQuestion() != null && !ques.getQuestion().trim().equals("")) {
            viewHolder.quesDisc.setText(ques.getQuestion());
            viewHolder.quesDisc.setVisibility(View.VISIBLE);
        } else {
            viewHolder.quesDisc.setVisibility(View.INVISIBLE);
        }

        if (ques.getImg() != null && !ques.getImg().trim().equals("")) {
            viewHolder.quesPic.setVisibility(View.VISIBLE);
            viewHolder.quesPic.setAdjustViewBounds(true);
            viewHolder.quesPic.setMaxHeight(360);
            viewHolder.quesPic.setMaxWidth(240);

            ImageLoader.getInstance().displayImage("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"
                    + ques.getImg().replaceAll("_b.jpg", "_s.jpg"), viewHolder.quesPic);


            viewHolder.quesPic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ShowLargePicActivity.class);
                    intent.putExtra("pic", "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"
                            + ques.getImg());
                    mContext.startActivity(intent);
                }
            });
        } else {
            viewHolder.quesPic.setVisibility(View.GONE);
        }

        viewHolder.quesSource.setText(appTypeCatalog.get(ques.getCategory2())
                + " " + abilityTypeCatalog.get(ques.getCategory1()));

        viewHolder.answerNum.setText(ques.getAnswercount() + "");
        viewHolder.commentNum.setText(ques.getCommentcount() + "");
        viewHolder.agreeNum.setText(ques.getAgreecount() + "");
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    // 查看此用户是否已点赞
    private int checkAgree(String commentId, String uid) {
        return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
    }

    public void setData(ArrayList<QuestionListBean.QuestionDataBean> quesList) {
        mList = quesList;
    }

    public void addList(ArrayList<QuestionListBean.QuestionDataBean> quesList) {
        mList.addAll(quesList);
    }

    public void clearList() {
        mList.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class RVOnClickListener implements View.OnClickListener {
        final int mPosition;
        final OnRecyclerViewItemClickListener mListener;

        public RVOnClickListener(int i, OnRecyclerViewItemClickListener listener) {
            mPosition = i;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, mPosition);
                mListener.onLongClick(v, mPosition);
            }
        }
    }

    // 用来设置每个item的接听
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int position);
    }


    private String formatTime(long time) {
        Date date = new Date(time);
        Date date2 = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar.setTime(date);
        calendar2.setTime(date2);

        if (calendar2.get(Calendar.YEAR)
                - calendar.get(Calendar.YEAR) > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        } else if (calendar2.get(Calendar.DAY_OF_YEAR)
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

    public void setAbilityTypeCatalog() {
        abilityTypeCatalog = new HashMap<String, String>();
        abilityTypeCatalog.put("0", "其他");
        abilityTypeCatalog.put("1", "口语");
        abilityTypeCatalog.put("2", "听力");
        abilityTypeCatalog.put("3", "阅读");
        abilityTypeCatalog.put("4", "写作");
        abilityTypeCatalog.put("5", "翻译");
        abilityTypeCatalog.put("6", "单词");
        abilityTypeCatalog.put("7", "语法");
        abilityTypeCatalog.put("8", "其他");
    }

    public void setAppTypeCatalog() {
        appTypeCatalog = new HashMap<String, String>();
        appTypeCatalog.put("0", "其他");
        appTypeCatalog.put("101", "VOA");
        appTypeCatalog.put("102", "BBC");
        appTypeCatalog.put("103", "听歌");
        appTypeCatalog.put("104", "CET4");
        appTypeCatalog.put("105", "CET6");
        appTypeCatalog.put("106", "托福");
        appTypeCatalog.put("107", "N1");
        appTypeCatalog.put("108", "N2");
        appTypeCatalog.put("109", "N3");
        appTypeCatalog.put("110", "微课");
        appTypeCatalog.put("111", "雅思");
        appTypeCatalog.put("112", "初中");
        appTypeCatalog.put("113", "高中");
        appTypeCatalog.put("114", "考研");
        appTypeCatalog.put("115", "新概念");
        appTypeCatalog.put("116", "走遍美国");
        appTypeCatalog.put("117", "英语头条");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View user_inf;
        public ImageView quesRecommend;
        public ImageView quesIcon;
        public TextView quesName;
        public TextView quesInfo;
        public ImageView answerIcon;
        public TextView isAnswer;
        public TextView answerName;
        public TextView quesDisc;
        public ImageView quesPic;
        public TextView quesSource;
        public TextView answerNum;
        public TextView commentNum;
        public TextView agreeNum;
        public ImageView agree;

        public ViewHolder(View itemView) {
            super(itemView);
            quesRecommend = (ImageView) itemView
                    .findViewById(R.id.iv_ques_recommend);
            quesIcon = (ImageView) itemView
                    .findViewById(R.id.ques_icon);
            quesName = (TextView) itemView
                    .findViewById(R.id.ques_name);
            quesInfo = (TextView) itemView
                    .findViewById(R.id.ques_info);
            answerIcon = (ImageView) itemView
                    .findViewById(R.id.answer_icon);
            isAnswer = (TextView) itemView
                    .findViewById(R.id.is_answer);
            answerName = (TextView) itemView
                    .findViewById(R.id.answer_name);
            quesDisc = (TextView) itemView
                    .findViewById(R.id.ques_disc);
            quesPic = (ImageView) itemView
                    .findViewById(R.id.ques_pic);
            quesSource = (TextView) itemView
                    .findViewById(R.id.ques_source);
            answerNum = (TextView) itemView
                    .findViewById(R.id.answer_num);
            commentNum = (TextView) itemView
                    .findViewById(R.id.comment_num);
            agreeNum = (TextView) itemView
                    .findViewById(R.id.agree_num);
            user_inf = itemView.findViewById(R.id.user_inf);
            agree = (ImageView) itemView.findViewById(R.id.agree);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    notifyDataSetChanged();
                    break;

                case 1:
                    notifyDataSetChanged();
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.agree_already);
                    break;
                case 4:
                    CustomToast.showToast(mContext, R.string.comment_agree);
                    break;

            }

        }
    };

}
