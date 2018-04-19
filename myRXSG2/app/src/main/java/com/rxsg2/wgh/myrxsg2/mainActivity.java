package com.rxsg2.wgh.myrxsg2;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import java.security.MessageDigest;

public class mainActivity extends AppCompatActivity {
    private generalReceiver myReceiver;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK)
        {
            System.out.println(data.getExtras().getString("login_verificationCodeEdit"));

            Intent service = new Intent(this,httpService.class);
            service.setAction("login");
            service.putExtra("verficationCode",data.getExtras().getString("login_verificationCodeEdit"));
            service.putExtra("verificationCookie",data.getExtras().getString("verificationCookie"));
            startService(service);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoginActivity();

        myReceiver = new generalReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("mainActivity");
        registerReceiver(myReceiver,intentFilter);

        // TODO:处理其他线程的消息
        myReceiver.setOnUpdateUI(new OnUpdateUI() {
            @Override
            public void updateUI(Intent intent) {
                String action = intent.getExtras().getString("ACTION");
                if("LoginParamter".equals(action)) {
                    //intent.putExtra("ACTION", "LoginParamter");
                    String g_server_id = intent.getExtras().getString("g_server_id");
                    String g_pass_type = intent.getExtras().getString("g_pass_type");
                    String g_port = intent.getExtras().getString("g_port");
                    String g_pass_token = intent.getExtras().getString("g_pass_token");
                    String g_version = intent.getExtras().getString("g_version");
                    String g_host = intent.getExtras().getString("g_host");
                    String g_pass_port = intent.getExtras().getString("g_pass_port");

                    Intent intent1 = new Intent(mainActivity.this,socketService.class);
                    intent1.putExtra("ACTION","Login");
                    intent1.putExtra("g_server_id",g_server_id);
                    intent1.putExtra("g_pass_type",g_pass_type);
                    intent1.putExtra("g_port",g_port);
                    intent1.putExtra("g_pass_token",g_pass_token);
                    intent1.putExtra("g_version",g_version);
                    intent1.putExtra("g_host",g_host);
                    intent1.putExtra("g_pass_port",g_pass_port);
                    startService(intent1);

                }
            }
        });
        System.out.println("You can debug");
        String aaa = "FFAACC";
        Byte a = ((byte)Integer.parseInt("1A", 16));

        System.out.println(("123456789"));
    }
    private void startLoginActivity()
    {
        // 启动登陆界面
        Intent _intent = new Intent(this,loginActivity.class);
        startActivityForResult(_intent,1000);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

}
