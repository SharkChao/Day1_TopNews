package com.linxi.administrator.updateapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.linxi.administrator.updateapp.R;
import com.linxi.administrator.updateapp.service.UpdateService;
import com.linxi.administrator.updateapp.utils.Global;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkVersion();
    }

    /**
     * 如果服务器版本大于本地版本，弹出更新对话框
     */
    public void checkVersion(){
        if (Global.serverVersion > Global.localVersion){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("是否选择更新到最新版本").setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //新开服务去更新
                            Intent intent = new Intent(MainActivity.this, UpdateService.class);
                            startService(intent);

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }
}
