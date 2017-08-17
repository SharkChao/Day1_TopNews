package com.lenovohit.administrator.treelistview.tree_bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SharkChao on 2017-08-16.
 */

public class Node implements Comparable<Node>{
    //当前节点的基本属性
    private String id;
    private String pid;
    private String title;
    //当前节点的级别
    private String level;
    //是否展开
    private boolean isExpand = true;
    //图片资源id
    private int resId;
    //父节点
    private Node parentNode;
    //子节点
    private List<Node>childNode = new ArrayList<>();

    public Node(String id,String pid,String title){
        this.id = id;
        this.pid = pid;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
        if (!expand){
            for (Node node:childNode){
                node.setExpand(expand);
            }
        }
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public List<Node> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<Node> childNode) {
        this.childNode = childNode;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }


    @Override
    public int compareTo(@NonNull Node o) {
        return Integer.parseInt(this.id) - Integer.parseInt(o.id);
    }
}
