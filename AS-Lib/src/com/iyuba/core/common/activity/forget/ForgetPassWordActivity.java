package com.iyuba.core.common.activity.forget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.core.R;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestChangePassWord;
import com.iyuba.core.common.protocol.message.ResponseChangePassWord;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.widget.dialog.CustomToast;

public class ForgetPassWordActivity extends AppCompatActivity {

    private Button buttonBack;
    private TextView playTitleInfo;
    private ImageView ivUserPhoto;
    private TextView tvUserName,tvUserPhone;
    private EditText registPhonePaswd;
    private EditText registPhonePaswdAgain;
    private  Button registPhoneSubmit;

    private String username;
    private String userPhoto;
    private String telPhone;
    private Context mContext;
    private static final String TAG = "ForgetPassWordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);

        mContext =this;

        initView();
        initData();
        submit();
    }

    private void initView(){
        buttonBack=findViewById(R.id.button_back);
        playTitleInfo=findViewById(R.id.play_title_info);
        ivUserPhoto=findViewById(R.id.imageView1);
        tvUserName=findViewById(R.id.tv_user_name);
        registPhonePaswd=findViewById(R.id.regist_phone_paswd);
        registPhonePaswdAgain=findViewById(R.id.regist_phone_paswd_again);
        registPhoneSubmit=findViewById(R.id.regist_phone_submit);
        tvUserPhone=findViewById(R.id.tv_user_phone);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        username = getIntent().getStringExtra("userName");
        userPhoto = getIntent().getStringExtra("userPhoto");
        telPhone = getIntent().getStringExtra("phoneNumb");

        GitHubImageLoader.Instace(mContext).setCircleImage(userPhoto, mContext,R.drawable.noavatar_small,
                ivUserPhoto);
        Log.e(TAG,"用户头像"+ivUserPhoto);
        tvUserName.setText("用户名："+username);
        tvUserPhone.setText(telPhone);
    }

    private void submit(){
        registPhoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1=registPhonePaswd.getText().toString();
                String pass2=registPhonePaswdAgain.getText().toString();
                if (pass1.equals("")){
                    CustomToast.showToast(mContext,"请输入密码");
                }else if(pass1.length()<6){
                    CustomToast.showToast(mContext,"请输入长度6-20位的密码");
                }else if(pass2.equals("")){
                    CustomToast.showToast(mContext,"请确认密码");
                }else if (pass1.equals(pass2)){
                    modifyPassWord(pass1);
                }else {
                    CustomToast.showToast(mContext,"两次输入的密码不一致");
                }
            }
        });
    }

    private void modifyPassWord(String password){
        String PassWord=MD5.getMD5ofStr(password);
        String sign=MD5.getMD5ofStr("10014" + username + telPhone + "iyubaV2");

        Log.e(TAG,"http "+username+" "+telPhone+" "+password);

        ExeProtocol.exe(new RequestChangePassWord(username,PassWord,sign,telPhone), new ProtocolResponse() {
            @Override
            public void finish(BaseHttpResponse bhr) {
                Log.e("RequestForgetCode","请求成功！"+bhr);
                ResponseChangePassWord res = (ResponseChangePassWord) bhr;
                Log.e("RequestForgetCode","请求成功！222");

                if (res != null) {
                    if("101".equals(res.result)||"131".equals(res.result)){
                        //mContext.startActivity(new Intent(mContext,Login.class));
                        handler.sendEmptyMessage(0);//这种网络请求必须用handler! 不能用吐司
                    }else{
                        handler.sendEmptyMessage(2);
                    }
                }else {
                    handler.sendEmptyMessage(1);
                }

            }

            @Override
            public void error() {
                Log.e(TAG,"请求失败");
                handler.sendEmptyMessage(1);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                CustomToast.showToast(mContext, "密码修改成功！");
                finish();
                break;
                case 1:
                    CustomToast.showToast(mContext, "密码修改失败！");
                    break;
                case 2:
                    CustomToast.showToast(mContext, "密码修改失败！请检查后重试");
                    break;
            }
        }
    };


}
