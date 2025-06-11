package com.iyuba.toeiclistening.vocabulary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.WebConstant;
import com.iyuba.configation.util.ToastUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.ToeicApplication;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class StepAdapter extends BaseAdapter {


    private int stepPerLine;
    private int step;
    private int size;

    int convertPosition = 0;

    Context context;

    public StepAdapter(int stepPerLine, int step, int size) {
        this.stepPerLine = stepPerLine;
        this.step = step;
        this.size = size;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wordtest_step, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        TextView tv = holder.tv;
        CircleImageView iv = holder.img;
        ImageView iv_avtor = holder.img_avator;
        View vView = holder.lineVt;
        iv.setVisibility(View.GONE);
        iv_avtor.setVisibility(View.GONE);
        tv.setText(String.format("第%d关", position + 1));

        tv.setOnClickListener(v -> {

            boolean isLogin = AccountManagerLib.Instace(ToeicApplication.getApplication()).checkUserLogin();
            if (!isLogin) {

                Intent intent = new Intent(ToeicApplication.getApplication(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ToeicApplication.getApplication().startActivity(intent);
                Toast.makeText(ToeicApplication.getApplication(), "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            int index = ConfigManager.Instance().loadInt("stage", 1);
            if (position >= index) {
                ToastUtil.showToast(context, "尚未开启此关卡,请先学习前面的内容");
            } else {
                int vip = ConfigManager.Instance().loadInt("isvip");
                if (position == 0 || vip > 0) {
                    WordListActivity.startIntent(context, position + 1, false, true);
                } else {
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle("提醒");
                    dialog.setMessage("您还不是会员，开通会员可以继续学习！");
                    dialog.setPositiveButton("好的", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, VipCenterGoldActivity.class);//VipCenter
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }

        });

        if (step <= position) {
            tv.setBackground(context.getResources().getDrawable(R.drawable.word_step_bg_lock));
            vView.setBackgroundColor(context.getResources().getColor(R.color.gray));
            tv.setText("未解锁");
            //iv.setVisibility(View.VISIBLE);
            //iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_test_2));
        }

        vView.setVisibility(View.INVISIBLE);

        if (step == position + 1) {
            iv.setVisibility(View.VISIBLE);
            loadUserIcon(iv);
            iv_avtor.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void loadUserIcon(ImageView imageView) {

        if (AccountManagerLib.Instace(context).checkUserLogin()) {

            String userIconUrl = "http://api." + WebConstant.COM_CN_SUFFIX + "v2/api.iyuba?protocol=10005&uid="
                    + AccountManagerLib.Instace(context).userId + "&size=middle";
            Glide.with(context)
                    .asBitmap()
                    .load(userIconUrl)
                    .placeholder(R.drawable.noavatar_small)
                    .into(imageView);
        } else {

            Glide.with(context)
                    .asBitmap()
                    .load(R.drawable.noavatar_small)
                    .into(imageView);
        }
    }

    static class ViewHolder {
        TextView tv;
        View lineVt;
        CircleImageView img;
        ImageView img_avator;


        ViewHolder(View view) {

            tv = view.findViewById(R.id.tv_title);
            lineVt = view.findViewById(R.id.line_vt);
            img = view.findViewById(R.id.img);
            img_avator = view.findViewById(R.id.img_indicator);
        }
    }
}
