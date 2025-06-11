package com.iyuba.toeiclistening.util.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.databinding.PopupSpeedBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class SpeedPopup extends BasePopupWindow {

    private PopupSpeedBinding popupSpeedBinding;

    private List<String> speedLesson;

    private Callback callback;

    public SpeedPopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_speed);
        popupSpeedBinding = PopupSpeedBinding.bind(view);
        setContentView(popupSpeedBinding.getRoot());

        initView();
    }


    private void initView() {

        //速度列表
        speedLesson = new ArrayList<>(Arrays.asList(getContext().getResources()
                .getStringArray(R.array.lessonSpeedChoose)));
        popupSpeedBinding.speedPvPickView.setData(speedLesson);

        popupSpeedBinding.speedTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    float speedFloat = Float.parseFloat(popupSpeedBinding.speedPvPickView.getChooseStr());
                    callback.getChoose(speedFloat);
                }
            }
        });
        popupSpeedBinding.speedTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    public void setChoosed(String txtSpeed) {

        String theSelectSpeed = txtSpeed.substring(0, txtSpeed.length() - 1);
        popupSpeedBinding.speedPvPickView.setSelected(speedLesson.indexOf(theSelectSpeed));
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void getChoose(float speedFloat);
    }

}
