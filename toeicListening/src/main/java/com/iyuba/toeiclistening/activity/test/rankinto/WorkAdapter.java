package com.iyuba.toeiclistening.activity.test.rankinto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.SpeakRankWork;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.UserWorkType;
import com.iyuba.toeiclistening.manager.DataManager;
import com.lid.lib.LabelTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

    private List<SpeakRankWork> mWorks;

    private MediaPlayer mediaPlayer;

    private SpeakRankWork mPlayingWork;
    private final HLDBManager mDBManager;//DBManager

    private PermissionRequester mPermissionRequester;
    private ShareReporter mReporter;
    private int userId;
    private String type, userName;
    private Context mContext;

    public WorkAdapter(String type, Context context, int uId, String uName) {
        mWorks = new ArrayList<>();
        mDBManager = HLDBManager.getInstance();
        this.type = type;
        userId = uId;
        userName = uName;
        mContext = context;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();

                for (int i = 0; i < mWorks.size(); i++) {

                    SpeakRankWork speakRankWork = mWorks.get(i);
                    if (speakRankWork == mPlayingWork) {

                        speakRankWork.isAudioCommentPlaying = true;
                    } else {
                        speakRankWork.isAudioCommentPlaying = false;
                    }
                }
                notifyDataSetChanged();
            }
        });
        mediaPlayer.setOnCompletionListener(mp -> {

            for (int i = 0; i < mWorks.size(); i++) {

                SpeakRankWork speakRankWork = mWorks.get(i);
                if (speakRankWork == mPlayingWork) {

                    speakRankWork.isAudioCommentPlaying = false;
                } else {
                    speakRankWork.isAudioCommentPlaying = false;
                }
            }
            notifyDataSetChanged();
        });

    }

    public void setPermissionRequester(PermissionRequester requester) {
        mPermissionRequester = requester;
    }

    public void setShareReporter(ShareReporter reporter) {
        mReporter = reporter;
    }

    public void setData(List<SpeakRankWork> works) {
        mWorks = works;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_rank_work_common, parent, false);
        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        holder.setItem(mWorks.get(position));
    }

    @Override
    public int getItemCount() {
        return (mWorks == null) ? 0 : mWorks.size();
    }

    private void playAudioComment(SpeakRankWork work) {
        mPlayingWork = work;
//        mediaPlayer.setOnStateChangeListener(listener);
        // EventBus.getDefault().post(new PauseEvent());

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mPlayingWork.getShuoShuoUrl());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    interface PermissionRequester {
        void requestShare(WorkViewHolder holder);
    }

    interface ShareReporter {
        void report(int userId, int voaId, int srId);
    }


    public void destroyMedia() {

        if (mediaPlayer != null) {

            mediaPlayer.release();
        }
    }

    class WorkViewHolder extends RecyclerView.ViewHolder implements HolderMvpView {

        ImageView mCommentBodyIv;
        LabelTextView mLabelTv;
        TextView mReadSentenceTv;
        TextView mScoreTv;
        CircleImageView mUserImage;
        TextView mTimeTv;
        TextView mUsernameTv;
        TextView mUpvoteCountTv;
        TextView mDownvoteCountTv;
        ImageView mShareIv;

        ImageView image_upvote;

        ImageView image_downvote;

        ImageView image_share;

        int readColor;
        int broadcastColor;

        HolderPresenter mPresenter;

        SpeakRankWork mWork;

        public WorkViewHolder(View itemView) {
            super(itemView);
            mPresenter = new HolderPresenter();
            mPresenter.attachView(this);

            readColor = itemView.getResources().getColor(R.color.read_corner_stub);
            broadcastColor = itemView.getResources().getColor(R.color.broadcast_corner_stub);

            mCommentBodyIv = itemView.findViewById(R.id.image_body);
            mLabelTv = itemView.findViewById(R.id.text_label_stub);
            mReadSentenceTv = itemView.findViewById(R.id.text_read_sentence);
            mScoreTv = itemView.findViewById(R.id.text_score);
            mUserImage = itemView.findViewById(R.id.image_user_head);
            mTimeTv = itemView.findViewById(R.id.text_time);
            mUsernameTv = itemView.findViewById(R.id.text_username);
            mUpvoteCountTv = itemView.findViewById(R.id.text_upvote_count);
            mDownvoteCountTv = itemView.findViewById(R.id.text_downvote_count);
            mShareIv = itemView.findViewById(R.id.image_share);
            image_upvote = itemView.findViewById(R.id.image_upvote);
            image_downvote = itemView.findViewById(R.id.image_downvote);
            image_share = itemView.findViewById(R.id.image_share);

            mUserImage.setOnClickListener(clickUser);
            mCommentBodyIv.setOnClickListener(clickPlayVoiceComment);
            image_upvote.setOnClickListener(clickUpvote);
            image_downvote.setOnClickListener(clickDownvote);
            image_share.setOnClickListener(clickShare);
        }

        View.OnClickListener clickUser = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //点击用户头像，
                Intent intent;
                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    String uid = String.valueOf(mWork.userid);
                    //intent = ChattingActivity.buildIntent(itemView.getContext(), uid, mWork.username);
                } else {
                    //intent = new Intent(itemView.getContext(), LoginActivity.class);
                }
                //itemView.getContext().startActivity(intent);
            }
        };

        View.OnClickListener clickPlayVoiceComment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayingWork == null) {
                    playAudioComment(mWork);
                } else {
                    if (mPlayingWork != mWork) {
                        if (mPlayingWork.isAudioCommentPlaying) {
                            mediaPlayer.stop();
                        }
                        playAudioComment(mWork);
                    } else {
                        // do nothing
                    }
                }
            }
        };


        View.OnClickListener clickUpvote = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    int uid = IyuUserManager.getInstance().getUserId();
                    String workId = String.valueOf(mWork.id);
                    //boolean result = mDBManager.findUpVoteRecord(workId, uid);
                    boolean result = mDBManager.isClickZAN(String.valueOf(uid), workId);
                    Timber.i("点没点过 : %s", result);
                    if (!result) {
                        mPresenter.upvote(mWork, uid);
                    } else {
                        showMessage("您已经评论过该条了...");
                    }
                } else {
                    goToLogin(itemView.getContext());
                }
            }
        };

        View.OnClickListener clickDownvote = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                    int uid = IyuUserManager.getInstance().getUserId();
                    String workId = String.valueOf(mWork.id);
                    if (!mDBManager.isClickZAN(String.valueOf(uid), workId)) {
                        mPresenter.downvote(mWork, uid);
                    } else {
                        showMessage("您已经评论过该条了...");
                    }
                } else {
                    goToLogin(itemView.getContext());
                }
            }
        };

        View.OnClickListener clickShare = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPermissionRequester != null)
                    mPermissionRequester.requestShare(WorkViewHolder.this);
            }
        };

        private void goToLogin(Context context) {
            //context.startActivity(LoginActivity.buildIntent(context));
        }

        public void onSharePermissionGranted() {
            mPresenter.getVoaTitle(mWork.topicId, type);
        }

        public void setItem(SpeakRankWork work) {
            mWork = work;
            if (mWork.shuoshuoType == UserWorkType.BROADCAST) {
                //mShareIv.setVisibility(Constant.ENABLE_SHARE ? View.VISIBLE : View.GONE);
                //mShareIv.setVisibility(IHeadlineManager.ENABLE_SHARE ? View.VISIBLE : View.GONE);
                mLabelTv.setLabelBackgroundColor(broadcastColor);
                mLabelTv.setLabelText("播音");
            } else {
                mShareIv.setVisibility(View.GONE);
                //mLabelTv.setLabelBackgroundColor(itemView.getContext().getResources().getColor(readColor));
                mLabelTv.setLabelBackgroundColor(readColor);
                mLabelTv.setLabelText("跟读");
                String text = "";

                for (Text t : DataManager.Instance().textList) {

                    if (t.SenIndex == mWork.idindex) {

                        text = t.Sentence;
                        break;
                    }
                }
                mReadSentenceTv.setText(text);//mWork.readText;
            }
            if (userName.equals("")) {
                mUsernameTv.setText(String.valueOf(userId));//setVisibility(View.INVISIBLE);//
            } else {
                mUsernameTv.setText(userName);
            }
            mTimeTv.setText(mWork.createdate);
            mUpvoteCountTv.setText(mWork.getUpvoteCount());
            mDownvoteCountTv.setText(mWork.getDownvoteCount());
            if (mWork.isAudioCommentPlaying) {
                ((AnimationDrawable) mCommentBodyIv.getDrawable()).start();
//                mediaPlayer.setOnStateChangeListener(mListener);
            } else {
                AnimationDrawable animation = (AnimationDrawable) mCommentBodyIv.getDrawable();
                animation.stop();
                animation.selectDrawable(0);
            }
            mLabelTv.setVisibility(View.VISIBLE);
            mReadSentenceTv.setVisibility(View.VISIBLE);
            mScoreTv.setVisibility(View.VISIBLE);
            mScoreTv.setText(mWork.getScore());
            String photo = "http://api." + WebConstant.COM_CN_SUFFIX + "v2/api.iyuba?protocol=10005&uid=" + userId + "&size=middle";
            LogUtil.e("photo" + photo);
            Glide.with(itemView.getContext())
                    .load(photo)
                    .into(mUserImage);
        }



/*        private OnStateChangeListener mListener = new OnStateChangeListener() {
            @Override
            public void onStateChange(int newState) {
                switch (newState) {
                    case State.PLAYING:
                        if (mWork == mPlayingWork) {
                            mWork.isAudioCommentPlaying = true;
                            ((AnimationDrawable) mCommentBodyIv.getDrawable()).start();
                        } else {
                            mPlayingWork.isAudioCommentPlaying = true;
                        }
                        break;
                    case State.ERROR:
                    case State.PAUSED:
                    case State.INTERRUPTED:
                    case State.COMPLETED:
                        if (mWork == mPlayingWork) {
                            mWork.isAudioCommentPlaying = false;
                            AnimationDrawable animation = (AnimationDrawable) mCommentBodyIv.getDrawable();
                            animation.stop();
                            animation.selectDrawable(0);
                        } else {
                            mPlayingWork.isAudioCommentPlaying = false;
                        }
                        mPlayingWork = null; // maybe unnecessary?
                        break;
                }
            }
        };*/

//        private void showShare() {
//            new ShareDialog(itemView.getContext())
//                    .setShareTitle("学习，登录，分享等可获积分，积分可兑换话费，VIP卡，VOA和四六级考试书等。")
//                    .setShareStuff(mWork)
//                    .setPlatformActionListener(new PlatformActionListener() {
//                        @Override
//                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                            int srid = 46;
//                            String name = platform.getName();
//                            if (name.equals(QQ.NAME) || name.equals(Wechat.NAME)
//                                    || name.equals(WechatFavorite.NAME)) {
//                                srid = 45;
//                            } else if (name.equals(QZone.NAME) || name.equals(WechatMoments.NAME)
//                                    || name.equals(SinaWeibo.NAME)) {
//                                srid = 46;
//                            }
//                            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
//                                int uid = IHeadlineManager.userId;
//                                if (mReporter != null) mReporter.report(uid, mWork.topicId, srid);
//                            } else {
//                                CustomToast.showToast(itemView.getContext(), "登陆后分享可获取积分!");
//                                //itemView.getContext().startActivity(LoginActivity.buildIntent(itemView.getContext()));
//                            }
//                        }
//
//                        @Override
//                        public void onError(Platform platform, int i, Throwable throwable) {
//                            ToastUtil.showToast(itemView.getContext(), "分享失败");
//                        }
//
//                        @Override
//                        public void onCancel(Platform platform, int i) {
//                            ToastUtil.showToast(itemView.getContext(), "分享已取消");
//                        }
//                    })
//                    .show();
//        }

        @Override
        public void showMessage(String message) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = message;
            handler.sendMessage(msg);
            //ToastUtil.showToast(itemView.getContext(), message);
        }

        @Override
        public void onVoaTitleLoaded(Text text) {
            mWork.title = text.Sentence;
            mWork.titleCn = "";
            mWork.description = "";
            mWork.imgsrc = "";

            //showShare();
        }

        @Override
        public void onUpvoteSuccess(SpeakRankWork work, int userId) {
            //mDBManager.saveUpVoteRecord(String.valueOf(work.id), userId);
            mDBManager.saveClickZan(String.valueOf(userId), String.valueOf(work.id));
            work.agreeCount += 1;
            if (mWork == work) {
                //mUpvoteCountTv.setText(mWork.getUpvoteCount());
                handler.sendEmptyMessage(1);
            }
        }

        @Override
        public void onDownvoteSuccess(SpeakRankWork work, int userId) {
            //mDBManager.saveUpVoteRecord(String.valueOf(work.id), userId);
            mDBManager.saveClickZan(String.valueOf(userId), String.valueOf(work.id));
            work.againstCount += 1;
            if (mWork == work) {
                //mDownvoteCountTv.setText(mWork.getDownvoteCount());
                handler.sendEmptyMessage(2);
            }
        }

        @SuppressLint("HandlerLeak")
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        CustomToast.showToast(itemView.getContext(), (String) msg.obj);
                        break;
                    case 1:
                        mUpvoteCountTv.setText(mWork.getUpvoteCount());
                        break;
                    case 2:
                        mDownvoteCountTv.setText(mWork.getDownvoteCount());
                        break;
                    default:
                }
            }
        };
    }

}
