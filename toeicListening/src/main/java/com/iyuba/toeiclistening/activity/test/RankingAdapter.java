package com.iyuba.toeiclistening.activity.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.network.SpeakRank;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.activity.holder.FragmentHolderActivity;
import com.iyuba.toeiclistening.activity.test.rankinto.SpeakWorkFragment;
import com.iyuba.toeiclistening.entity.UserWorkType;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private interface Type {
        int TOP_THREE = 0;
        int CURRENT_USER = 1;
        int NORMAL = 2;
    }

    private List<SpeakRank> mRankings;
    private SpeakRank mRank1st, mRank2nd, mRank3rd;
    private SpeakRank mCurrentUserRanking;

    private int mRankVoaId;

    private int offset;
    private String mCategory;

    public RankingAdapter(String type) {
        mRankings = new ArrayList<>();
        mCategory = type;
        offset = 0;
    }

    public boolean isEmpty() {
        return mRank1st == null && mRank2nd == null && mRank3rd == null && mRankings.size() == 0
                && mCurrentUserRanking == null;
    }

    public void setRankVoaId(int id) {
        mRankVoaId = id;
    }

    public void setData(List<SpeakRank> rankings, @Nullable SpeakRank currentUserRanking) {
        Timber.e("rankings size : %s", rankings.size());
        LogUtil.e("排行数据刷新！rankings size : %s", rankings.size());
        if (rankings.size() <= 3) {
            switch (rankings.size()) {
                case 1:
                    mRank1st = rankings.get(0);
                    mRank2nd = null;
                    mRank3rd = null;
                    break;
                case 2:
                    mRank1st = rankings.get(0);
                    mRank2nd = rankings.get(1);
                    mRank3rd = null;
                    break;
                case 3:
                    mRank1st = rankings.get(0);
                    mRank2nd = rankings.get(1);
                    mRank3rd = rankings.get(2);
                    break;
            }
        } else {
            mRank1st = rankings.remove(0);
            mRank2nd = rankings.remove(0);
            mRank3rd = rankings.remove(0);
            if (currentUserRanking != null) {
                for (SpeakRank rank : rankings) {
                    if (rank.uid == currentUserRanking.uid) {
                        rankings.remove(rank);
                        break;
                    }
                }
                mCurrentUserRanking = currentUserRanking;
                offset = 2;
            } else {
                offset = 1;
            }
            mRankings = rankings;
            Timber.i("mRankings size : %s", mRankings.size());
        }
    }

    public void addData(List<SpeakRank> rankings) {
        if (mCurrentUserRanking != null) {
            for (SpeakRank rank : rankings) {
                if (rank.uid == mCurrentUserRanking.uid) {
                    rankings.remove(rank);
                    break;
                }
            }
        }
        mRankings.addAll(rankings);
        notifyDataSetChanged();//这样写
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case Type.TOP_THREE:
                return new TopThreeHolder(inflater.inflate(R.layout.item_top3_read_rank, parent,
                        false));
            case Type.CURRENT_USER:
                return new SelfHolder(inflater.inflate(R.layout.item_current_read_rank_common, parent,
                        false));
            case Type.NORMAL:
            default:
                return new NormalHolder(inflater.inflate(R.layout.item_normal_read_rank_common, parent,
                        false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mCurrentUserRanking == null) {
            if (position == 0) {
                return Type.TOP_THREE;
            } else {
                return Type.NORMAL;
            }
        } else {
            if (position == 0) {
                return Type.TOP_THREE;
            } else if (position == 1) {
                return Type.CURRENT_USER;
            } else {
                return Type.NORMAL;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Type.TOP_THREE:
                ((TopThreeHolder) holder).setData(mRank1st, mRank2nd, mRank3rd);
                break;
            case Type.CURRENT_USER:
                ((SelfHolder) holder).setData(mCurrentUserRanking);
                break;
            case Type.NORMAL:
                ((NormalHolder) holder).setData(mRankings.get(position - offset));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mRank1st != null) {
            count = 1;
            if (mCurrentUserRanking != null) {
                count = 2;
            }
            count += mRankings.size();
        }
        return count;
    }

    public int getLastRank() {
        if (mRankings.size() > 0) {
            return mRankings.get(mRankings.size() - 1).ranking;
        } else {
            if (mRank1st == null) {
                return 0;
            } else if (mRank2nd == null) {
                return mRank1st.ranking;
            } else if (mRank3rd == null) {
                return mRank2nd.ranking;
            } else {
                return mRank3rd.ranking;
            }
        }
    }

    private void seeDetail(Context context, SpeakRank rank) {
        StringBuilder sb = new StringBuilder();
        sb.append(UserWorkType.EVALUATION).append(",").append(UserWorkType.BROADCAST);
        Bundle bundle = SpeakWorkFragment.buildArguments(rank.uid, rank.name, mRankVoaId, sb.toString(), mCategory);
        Intent intent = new Intent(context, FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.FRAGMENT_NAME, FragmentHolderActivity.Names.SPEAK_WORK);
        intent.putExtra(FragmentHolderActivity.ARGUMENTS, bundle);
        intent.putExtra("Category", mCategory);
        context.startActivity(intent);
    }

    class TopThreeHolder extends RecyclerView.ViewHolder {

        LinearLayout mFirstContainer;
        LinearLayout mSecondContainer;
        LinearLayout mThirdContainer;
        CircleImageView mFirstHeadIv;
        CircleImageView mSecondHeadIv;
        CircleImageView mThirdHeadIv;
        ImageView mFirstVipIv;
        ImageView mSecondVipIv;
        ImageView mThirdVipIv;
        TextView mFirstUsernameTv;
        TextView mFirstSentenceCountTv;
        TextView mFirstAverageScoreTv;
        TextView mFirstTotalScoreTv;
        TextView mSecondUsernameTv;
        TextView mSecondSentenceCountTv;
        TextView mSecondAverageScoreTv;
        TextView mSecondTotalScoreTv;
        TextView mThirdUsernameTv;
        TextView mThirdSentenceCountTv;
        TextView mThirdAverageScoreTv;
        TextView mThirdTotalScoreTv;

        SpeakRank rank1st, rank2nd, rank3rd;

        public TopThreeHolder(View itemView) {
            super(itemView);


            mFirstContainer = itemView.findViewById(R.id.linear_first_container);
            mSecondContainer = itemView.findViewById(R.id.linear_second_container);
            mThirdContainer = itemView.findViewById(R.id.linear_third_container);
            mFirstHeadIv = itemView.findViewById(R.id.image_first_user_head);
            mSecondHeadIv = itemView.findViewById(R.id.image_second_user_head);
            mThirdHeadIv = itemView.findViewById(R.id.image_third_user_head);
            mFirstVipIv = itemView.findViewById(R.id.image_first_vip);
            mSecondVipIv = itemView.findViewById(R.id.image_second_vip);
            mThirdVipIv = itemView.findViewById(R.id.image_third_vip);
            mFirstUsernameTv = itemView.findViewById(R.id.text_first_username);

            mFirstSentenceCountTv = itemView.findViewById(R.id.text_first_sentence_count);
            mFirstAverageScoreTv = itemView.findViewById(R.id.text_first_average_score);
            mFirstTotalScoreTv = itemView.findViewById(R.id.text_first_total_score);
            mSecondUsernameTv = itemView.findViewById(R.id.text_second_username);
            mSecondSentenceCountTv = itemView.findViewById(R.id.text_second_sentence_count);

            mSecondAverageScoreTv = itemView.findViewById(R.id.text_second_average_score);
            mSecondTotalScoreTv = itemView.findViewById(R.id.text_second_total_score);
            mThirdUsernameTv = itemView.findViewById(R.id.text_third_username);
            mThirdSentenceCountTv = itemView.findViewById(R.id.text_third_sentence_count);

            mThirdAverageScoreTv = itemView.findViewById(R.id.text_third_average_score);
            mThirdTotalScoreTv = itemView.findViewById(R.id.text_third_total_score);

            mFirstContainer.setOnClickListener(clickFirst);
            mSecondContainer.setOnClickListener(clickSecond);
            mThirdContainer.setOnClickListener(clickThrid);
        }

        public void setData(@NonNull SpeakRank rank1st, SpeakRank rank2nd, SpeakRank rank3rd) {
            this.rank1st = rank1st;
            this.rank2nd = rank2nd;
            this.rank3rd = rank3rd;
            setContainer(mFirstContainer, true);
            setContent(rank1st, mFirstHeadIv, mFirstVipIv, mFirstUsernameTv,
                    mFirstAverageScoreTv, mFirstTotalScoreTv, mFirstSentenceCountTv);
            if (rank2nd != null) {
                setContainer(mSecondContainer, true);
                setContent(rank2nd, mSecondHeadIv, mSecondVipIv, mSecondUsernameTv,
                        mSecondAverageScoreTv, mSecondTotalScoreTv, mSecondSentenceCountTv);
                if (rank3rd != null) {
                    setContainer(mThirdContainer, true);
                    setContent(rank3rd, mThirdHeadIv, mThirdVipIv, mThirdUsernameTv,
                            mThirdAverageScoreTv, mThirdTotalScoreTv, mThirdSentenceCountTv);
                } else {
                    setContainer(mThirdContainer, false);
                }
            } else {
                setContainer(mSecondContainer, false);
                setContainer(mThirdContainer, false);
            }
        }

        private void setContainer(LinearLayout layout, boolean able) {
            layout.setVisibility(able ? View.VISIBLE : View.INVISIBLE);
            layout.setClickable(able);
        }

        private void setContent(SpeakRank rank, CircleImageView head, ImageView vip, TextView name,
                                TextView average, TextView total, TextView count) {
            name.setText(rank.name);
            count.setText(String.valueOf(rank.count));
            total.setText(String.valueOf(rank.score));
            average.setText(String.valueOf(rank.getAverageScore()));
            vip.setVisibility(rank.isVip() ? View.VISIBLE : View.INVISIBLE);
            Glide.with(itemView.getContext())
                    .load(rank.imgSrc)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(head);
        }


        View.OnClickListener clickFirst = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seeDetail(itemView.getContext(), rank1st);
            }
        };

        View.OnClickListener clickSecond = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seeDetail(itemView.getContext(), rank2nd);
            }
        };

        View.OnClickListener clickThrid = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seeDetail(itemView.getContext(), rank3rd);
            }
        };
    }

    class BaseHolder extends RecyclerView.ViewHolder {

        TextView mRankTv;
        CircleImageView mUserHeadIv;
        ImageView mVipIv;
        TextView mUsernameTv;
        TextView mAverageScoreTv;
        TextView mSentenceCountTv;
        TextView mTotalScoreTv;
        SpeakRank rank;

        public BaseHolder(View itemView) {
            super(itemView);

            mRankTv = itemView.findViewById(R.id.text_rank);
            mUserHeadIv = itemView.findViewById(R.id.image_user_head);
            mVipIv = itemView.findViewById(R.id.image_vip);
            mUsernameTv = itemView.findViewById(R.id.text_username);
            mAverageScoreTv = itemView.findViewById(R.id.text_average_score);
            mSentenceCountTv = itemView.findViewById(R.id.text_sentence_count);
            mTotalScoreTv = itemView.findViewById(R.id.text_score);
        }

        public void setData(@NonNull final SpeakRank rank) {
            this.rank = rank;
            mRankTv.setText(String.valueOf(rank.ranking));
            mUsernameTv.setText(rank.name);
            mTotalScoreTv.setText(String.valueOf(rank.score));
            mSentenceCountTv.setText(String.valueOf(rank.count));
            mAverageScoreTv.setText(String.valueOf(rank.getAverageScore()));
            mVipIv.setVisibility(rank.isVip() ? View.VISIBLE : View.INVISIBLE);

            Glide.with(itemView.getContext())
                    .load(rank.imgSrc)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(mUserHeadIv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rank.count > 0) {
                        seeDetail(itemView.getContext(), rank);
                    } else {
                        Toast.makeText(itemView.getContext(), "暂无评测", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

//        @OnClick(R2.id.relative_container)
//        void click() {
//            if (rank.count > 0) {
//                seeDetail(itemView.getContext(), rank);
//            } else {
//                Toast.makeText(itemView.getContext(), "暂无评测", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    class SelfHolder extends BaseHolder {

        public SelfHolder(View itemView) {
            super(itemView);
        }

    }

    class NormalHolder extends BaseHolder {

        public NormalHolder(View itemView) {
            super(itemView);
        }
    }
}
