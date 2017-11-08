package com.example.administrator.httpsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements HttpsUrlUtils.CallBack{

    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpsUrlUtils.getInstance().getUrlData(MainActivity.this,"https://www.12306.cn/mormhweb/",MainActivity.this);
            }
        });
    }

    @Override
    public void Success(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(data);
            }
        });
    }

    @Override
    public void Fail(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(e.toString());
            }
        });
    }
}
