package com.iyuba.toeiclistening.util.popup;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.MoreAdapter;
import com.iyuba.toeiclistening.databinding.PopupMoreBinding;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class MorePopup extends BasePopupWindow {

    private PopupMoreBinding popupMoreBinding;

    private MoreAdapter moreAdapter;

    private Callback callback;

    /**
     * 0:paf导出
     * 1:将pdf发送到QQ或者微信
     */
    private int flag;

    public MorePopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_more);
        popupMoreBinding = PopupMoreBinding.bind(view);
        setContentView(popupMoreBinding.getRoot());
    }

    public void initOperation(List<String> strings) {

        popupMoreBinding.moreRv.setLayoutManager(new LinearLayoutManager(getContext()));
        moreAdapter = new MoreAdapter(R.layout.item_more, strings);
        popupMoreBinding.moreRv.setAdapter(moreAdapter);
        moreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (callback != null) {

                    callback.getString(moreAdapter.getItem(position));
                }
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public interface Callback {

        void getString(String s);
    }
}
