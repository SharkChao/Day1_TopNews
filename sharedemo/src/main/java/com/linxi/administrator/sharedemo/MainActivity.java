package com.linxi.administrator.sharedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void shareBtn1(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,"我用手机自带功能测试分享内容");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent,"我是弹出框的标题"));
    }
}
