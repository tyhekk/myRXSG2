package com.rxsg2.wgh.myrxsg2;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class loginActivity extends AppCompatActivity {
    private generalReceiver myReceiver;
    private String verificationCookie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //  点击按钮，返回用户名，密码，验证码
        Button _button = (Button)findViewById(R.id.login_Button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();

                String val;

                val = ((EditText)findViewById(R.id.login_name)).getText().toString();
                data.putExtra("login_name",val);
                val = ((EditText)findViewById(R.id.login_password)).getText().toString();
                data.putExtra("login_password",val);
                val = ((EditText)findViewById(R.id.login_verificationCodeEdit)).getText().toString();
                data.putExtra("login_verificationCodeEdit",val);
                // 验证码cookie
                data.putExtra("verificationCookie",verificationCookie);

                setResult(Activity.RESULT_OK,data);
                finish();
            }
        });

        myReceiver = new generalReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE");
        registerReceiver(myReceiver,intentFilter);

        myReceiver.setOnUpdateUI(new OnUpdateUI() {
            @Override
            public void updateUI(Intent intent) {
                //System.out.println(intent.getExtras().get("HttpContext"));
                ((ImageView)findViewById(R.id.verificationCodeImage)).setImageBitmap((Bitmap)intent.getExtras().get("bitmap"));
                verificationCookie = (String)intent.getExtras().get("verificationCookie");
            }
        });

        startVerificationCodeService();
    }
    // 启动service，获取验证码图片
    private void startVerificationCodeService(){

        Intent service = new Intent(this,verificationCodeService.class);
        startService(service);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }


}
