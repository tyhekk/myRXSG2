package com.rxsg2.wgh.myrxsg2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class mainActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK)
        {
            System.out.println(data.getExtras().getString("login_verificationCodeEdit"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动登陆界面
        Intent _intent = new Intent(this,loginActivity.class);
        startActivityForResult(_intent,1000);
    }


}
