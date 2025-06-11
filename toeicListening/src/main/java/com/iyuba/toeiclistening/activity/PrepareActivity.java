package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.util.ShareHelper;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.toeiclistening.R;
import com.umeng.commonsdk.UMConfigure;

public class PrepareActivity extends Activity {
    String TAG = "PrepareActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        Log.e(TAG, "onCreate: -- ");
        if (ConfigManager.Instance().loadBoolean("PrivacyDialog", false)) {
            Log.e(TAG, "onCreate: init");
            goToInit();
        } else {
            Log.e(TAG, "onCreate: dialog");
            showDialog(this);
        }
    }

    public void showDialog(Context context) {
        String privacy1 = "1.为了更方便您使用我们的软件，我们回根据您使用的具体功能时申请必要的权限，如摄像头，存储权限，录音权限等。\n";
        String privacy2 = "2.使用本app需要您了解并同意";
        String privacy3 = "隐私政策及用户协议";
        String privacy4 = "，点击同意即代表您已阅读并同意该协议";

        ClickableSpan secretString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToSecret(context);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(context, R.color.app_color));
            }
        };

        ClickableSpan policyString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToPolicy(context);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(context, R.color.app_color));
            }
        };
        int start = privacy1.length() + privacy2.length();
        int end = start + privacy3.length();

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(privacy1);
        strBuilder.append(privacy2);
        strBuilder.append(privacy3);
        strBuilder.append(privacy4);
        strBuilder.setSpan(secretString, start, start + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(policyString, start + 5, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacy, null);
        dialog.setView(view);
        dialog.show();

        TextView textView = view.findViewById(R.id.text_link);

        textView.setText(strBuilder);

        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView agreeNo = view.findViewById(R.id.text_no_agree);
        TextView agree = view.findViewById(R.id.text_agree);

        agreeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        agree.setOnClickListener(v -> {
            ConfigManager.Instance().putBoolean("PrivacyDialog", true);
            dialog.dismiss();
            PrivacyInfoHelper.getInstance().putApproved(true);

            ShareHelper.init(getApplicationContext());
            goToInit();
        });

    }

    private void goToInit() {

        ShareHelper.init(getApplicationContext());
        UMConfigure.init(getApplicationContext(), "", "", UMConfigure.DEVICE_TYPE_PHONE, "");
        startActivity(new Intent(PrepareActivity.this, IniActivity.class));
        finish();
    }


    private void goToSecret(Context context) {
        com.iyuba.core.common.activity.Web.start(context, WebConstant.HTTP_SPEECH_ALL + "/api/protocolpri.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=1", "用户隐私政策");
    }

    private void goToPolicy(Context context) {
        com.iyuba.core.common.activity.Web.start(context, WebConstant.HTTP_SPEECH_ALL + "/api/protocoluse666.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=爱语吧", "用户使用协议");
    }
}
