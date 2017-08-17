package com.lenovohit.administrator.treelistview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lenovohit.administrator.treelistview.R;
import com.lenovohit.administrator.treelistview.bean.Bean;
import com.lenovohit.administrator.treelistview.tree_bean.TreeNodeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Bean>mDatas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        ListView lvList = (ListView) findViewById(R.id.id_tree);
        lvList.setAdapter(new TreeNodeAdapter<Bean>(mDatas,this,lvList));

    }
    public void initData(){
        mDatas.add(new Bean("1", "0", "根目录1"));
        mDatas.add(new Bean("2", "0", "根目录2"));
        mDatas.add(new Bean("3", "0", "根目录3"));
        mDatas.add(new Bean("4", "0", "根目录4"));
        mDatas.add(new Bean("5", "1", "子目录1-1"));
        mDatas.add(new Bean("6", "1", "子目录1-2"));
        mDatas.add(new Bean("7", "5", "子目录1-1-1"));
        mDatas.add(new Bean("8", "2", "子目录2-1"));
        mDatas.add(new Bean("9", "4", "子目录4-1"));
        mDatas.add(new Bean("10", "4", "子目录4-2"));
        mDatas.add(new Bean("11", "10", "子目录4-2-1"));
        mDatas.add(new Bean("12", "10", "子目录4-2-3"));
        mDatas.add(new Bean("13", "10", "子目录4-2-2"));
        mDatas.add(new Bean("14", "9", "子目录4-1-1"));
        mDatas.add(new Bean("15", "9", "子目录4-1-2"));
        mDatas.add(new Bean("16", "9", "子目录4-1-3"));
        mDatas.add(new Bean("17", "16", "子目录4-1-3"));
        mDatas.add(new Bean("18", "16", "子目录4-1-3"));
    }
}
