package com.lenovohit.administrator.treelistview.bean;

import com.lenovohit.administrator.treelistview.tree_bean.TreeNodeId;
import com.lenovohit.administrator.treelistview.tree_bean.TreeNodePid;
import com.lenovohit.administrator.treelistview.tree_bean.TreeNodeTitle;

/**
 * Created by SharkChao on 2017-08-16.
 * bean类是用来给用户用的
 */

public class Bean {
    @TreeNodeId
     String id;
    @TreeNodePid
     String pid;
    @TreeNodeTitle
     String title;

    public Bean(String id, String pid, String title) {
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
}
