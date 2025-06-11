package com.iyuba.toeiclistening.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.mvp.model.bean.RewardBean;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyWalletAdapter extends BaseQuickAdapter<RewardBean.DataDTO, BaseViewHolder> {


    private DecimalFormat decimalFormat;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat hourSimpleDateFormat;

    private SimpleDateFormat timeSimpleDateFormat;


    public MyWalletAdapter(int layoutResId, @Nullable List<RewardBean.DataDTO> data) {
        super(layoutResId, data);
        decimalFormat = new DecimalFormat("#.##");

        simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");

        hourSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        hourSimpleDateFormat.applyPattern("HH:mm");

        timeSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        timeSimpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RewardBean.DataDTO item) {

        helper.setText(R.id.mywallet_tv_type, item.getType());
        float s = Integer.parseInt(item.getScore()) / 100.0f;
        helper.setText(R.id.mywallet_tv_money, decimalFormat.format(s) + "元");
        Date date;
        try {
            date = simpleDateFormat.parse(item.getTime());
            boolean isToday = simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(date));
            if (isToday) {

                helper.setText(R.id.mywallet_tv_date, hourSimpleDateFormat.format(timeSimpleDateFormat.parse(item.getTime())));
            } else {

                helper.setText(R.id.mywallet_tv_date, simpleDateFormat.format(date));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }
}
