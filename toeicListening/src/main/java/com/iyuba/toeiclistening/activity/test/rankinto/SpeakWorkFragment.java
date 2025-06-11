package com.iyuba.toeiclistening.activity.test.rankinto;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.core.common.network.SpeakRankWork;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.R;
import com.iyuba.widget.recycler.ListRecyclerView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

/**
 * 评测排行详情页Fragment
 * zh 2019-05-12
 */
@RuntimePermissions
public class SpeakWorkFragment extends Fragment implements SpeakWorkMvpView {

    public static final String UID_KEY = "uid_key";
    public static final String UNAME_KEY = "uname_key";
    public static final String TOPICID_KEY = "topic_id_key";
    public static final String TYPE_KEY = "type_key";

    public static Bundle buildArguments(int uid, String uName, int topicId, String type, String category) {
        Bundle b = new Bundle();
        b.putInt(UID_KEY, uid);
        b.putString(UNAME_KEY, uName);
        b.putInt(TOPICID_KEY, topicId);
        b.putString(TYPE_KEY, type);
        b.putString("category", category);
        return b;
    }

    public static SpeakWorkFragment newInstance(Bundle bundle) {
        SpeakWorkFragment fragment = new SpeakWorkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    SwipeRefreshLayout mContainer;
    ListRecyclerView mWorksRecyclerView;

    WorkAdapter mAdapter;
    SpeakWorkPresenter mPresenter;

    private int mUserId;
    private String mUsername;
    private int mTopicId;
    private String mType;
    private String mCategory;
    private static final String TAG = "SpeakWorkFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mUserId = getArguments().getInt(UID_KEY);
        mUsername = getArguments().getString(UNAME_KEY);
        mTopicId = getArguments().getInt(TOPICID_KEY);//ID 是文章ID textID titleNum
        mType = getArguments().getString(TYPE_KEY);
        mCategory = "toeic";


        mAdapter = new WorkAdapter(mCategory, getActivity(), mUserId, mUsername);
        mPresenter = new SpeakWorkPresenter();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_speak_work, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);

        initView(view);

        mAdapter.setPermissionRequester(mPermissionRequester);
        mAdapter.setShareReporter(mShareReporter);
        mContainer.setColorSchemeColors(getResources().getColor(com.iyuba.headlinelibrary.R.color.colorPrimary));
        mContainer.setOnRefreshListener(mRefreshListener);
        mWorksRecyclerView.setAdapter(mAdapter);

        mPresenter.getUserWorks(mUserId, mTopicId, mCategory);
    }

    private void initView(View view) {

        mContainer = view.findViewById(R.id.swipe_refresh_container);
        mWorksRecyclerView = view.findViewById(R.id.recycler);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdapter != null) {

            mAdapter.destroyMedia();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    mContainer.setRefreshing((boolean) msg.obj);
                    break;
                default:

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SpeakWorkFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestShare(WorkAdapter.WorkViewHolder holder) {
        holder.onSharePermissionGranted();
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void requestShareDenied() {
        CustomToast.showToast(getContext(), "申请权限失败,无法分享!");
    }

    @Override
    public void showMessage(String message) {
        CustomToast.showToast(getContext(), message);
    }

    @Override
    public void setSwipeContainer(boolean isRefreshing) {
        //mContainer.setRefreshing(isRefreshing);
        Message msg = new Message();
        msg.what = 1;
        msg.obj = isRefreshing;
        handler.sendMessage(msg);
    }

    @Override
    public void onUserWorksLoaded(List<SpeakRankWork> works) {
        mAdapter.setData(works);
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onShareCreditAdded(int creditChange, int totalCredit) {
        Timber.i("credit change : %s total : %s", creditChange, totalCredit);
        if (creditChange > 0 && totalCredit > 0) {
            String msg = "分享成功，增加了" + creditChange + "积分，共有" + totalCredit + "积分";
            CustomToast.showToast(getContext(), msg);
            //EventBus.getDefault().post(new UpdateUserCreditEvent(totalCredit)); //AccountManager.getInstance().updateCurrentUserCredit(totalCredit);
        } else {
            CustomToast.showToast(getContext(), "分享成功");
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.getUserWorks(mUserId, mTopicId, mCategory);
        }
    };

    private WorkAdapter.PermissionRequester mPermissionRequester = new WorkAdapter.PermissionRequester() {
        @Override
        public void requestShare(WorkAdapter.WorkViewHolder holder) {
            SpeakWorkFragmentPermissionsDispatcher.requestShareWithPermissionCheck
                    (SpeakWorkFragment.this, holder);
        }
    };

    private WorkAdapter.ShareReporter mShareReporter = new WorkAdapter.ShareReporter() {
        @Override
        public void report(int userId, int voaId, int srId) {
            mPresenter.addCredit(userId, voaId, srId);
        }
    };

}
