package com.rxsg2.wgh.myrxsg2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

                setResult(Activity.RESULT_OK,data);
                finish();
            }
        });
    }
}
