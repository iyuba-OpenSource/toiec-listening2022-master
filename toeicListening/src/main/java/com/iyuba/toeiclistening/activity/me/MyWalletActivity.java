package com.iyuba.toeiclistening.activity.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.activity.base.BaseActivity;
import com.iyuba.toeiclistening.adapter.MyWalletAdapter;
import com.iyuba.toeiclistening.databinding.ActivityMyWalletBinding;
import com.iyuba.toeiclistening.mvp.model.bean.RewardBean;
import com.iyuba.toeiclistening.mvp.presenter.MyWalletPresenter;
import com.iyuba.toeiclistening.mvp.view.MyWalletContract;
import com.iyuba.toeiclistening.util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 我的钱包
 */
public class MyWalletActivity extends BaseActivity<MyWalletContract.MyWalletView, MyWalletContract.MyWalletPresenter>
        implements MyWalletContract.MyWalletView {

    private ActivityMyWalletBinding binding;

    private MyWalletAdapter myWalletAdapter;
    private int page = 1;

    private int pageSize = 10;

    private SimpleDateFormat simpleDateFormat;

    /**
     * 正在加载数据
     */
    private boolean isLoading = false;

    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding.toolbar.toolbarIvTitle.setText("我的钱包");
        binding.toolbar.toolbarIvBack.setOnClickListener(v -> finish());

        uid = AccountManagerLib.Instace(MyWalletActivity.this).userId;
        binding.mywalletSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (myWalletAdapter == null) {

                    return;
                }
                if (isLoading) {

                    binding.mywalletSrl.setRefreshing(false);
                }

                binding.mywalletSrl.setRefreshing(true);
                page = 1;
                String sign = uid + "iyuba" + simpleDateFormat.format(new Date());
                presenter.getUserActionRecord(Integer.parseInt(uid), page, pageSize, MD5Util.MD5(sign));
            }
        });

        binding.mywalletRv.setLayoutManager(new LinearLayoutManager(this));

        myWalletAdapter = new MyWalletAdapter(R.layout.item_my_wallet, new ArrayList<>());
        binding.mywalletRv.setAdapter(myWalletAdapter);

        myWalletAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {


                isLoading = true;
                String sign = uid + "iyuba" + simpleDateFormat.format(new Date());
                presenter.getUserActionRecord(Integer.parseInt(uid), ++page, pageSize, MD5Util.MD5(sign));
            }
        }, binding.mywalletRv);

        simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");

        String sign = uid + "iyuba" + simpleDateFormat.format(new Date());
        presenter.getUserActionRecord(Integer.parseInt(uid), page, pageSize, MD5Util.MD5(sign));
    }

    @Override
    public View initLayout() {
        binding = ActivityMyWalletBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public MyWalletContract.MyWalletPresenter initPresenter() {
        return new MyWalletPresenter();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(ToeicApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void wallet(int pages, List<RewardBean.DataDTO> dataDTOS) {


        isLoading = false;
        if (pages == 1) {


            if (binding.mywalletSrl.isRefreshing()) {

                binding.mywalletSrl.setRefreshing(false);
            }

            if (dataDTOS == null) {

                toast("请求超时，请下拉刷新");
                return;
            }

            myWalletAdapter.setNewData(dataDTOS);
            if (dataDTOS.size() < pageSize) {

                myWalletAdapter.loadMoreEnd();
            } else {

                myWalletAdapter.loadMoreComplete();
            }


        } else {

            if (dataDTOS == null) {

                myWalletAdapter.loadMoreFail();
                return;
            }
            if (dataDTOS.size() < pageSize) {

                myWalletAdapter.addData(dataDTOS);
                myWalletAdapter.loadMoreEnd();
            } else {

                myWalletAdapter.addData(dataDTOS);
                myWalletAdapter.loadMoreComplete();
            }

        }
    }
}