package com.lenovohit.administrator.treelistview.tree_bean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovohit.administrator.treelistview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SharkChao on 2017-08-17.
 */

public  class TreeNodeAdapter<T> extends BaseAdapter{

    private  List mVisableNode = new ArrayList();
    private  List mAllList = new ArrayList();
    private Context mContext;
    private ListView lvlist;

    public TreeNodeAdapter(List<T>list, Context context, ListView lvlist){
        try {
            final NodeHelper helper=new NodeHelper(list);
            mAllList = helper.getList();
            mVisableNode = helper.getVisableNode(mAllList);
            this.mContext = context;
            this.lvlist = lvlist;
            lvlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Node node = (Node) mVisableNode.get(position);
                    if (node.isExpand()){
                        node.setExpand(false);
                        node.getChildNode();
                        mVisableNode = helper.getVisableNode(mAllList);
                        notifyDataSetChanged();
                    }else {
                        node.setExpand(true);
                        mVisableNode = helper.getVisableNode(mAllList);
                        notifyDataSetChanged();
                    }
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getCount() {
        return mVisableNode.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.list_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_treenode_icon);
        TextView textView = (TextView) view.findViewById(R.id.id_treenode_label);
        Node node = (Node) mVisableNode.get(position);
        if (node.getResId() != -1){
            imageView.setImageResource(node.getResId());
        }else {
            imageView.setImageResource(0);
        }
        textView.setText(node.getTitle());
        view.setPadding(Integer.parseInt(node.getLevel()) * 30, 3, 3, 3);
        return view;
    }

}
