package com.lenovohit.administrator.contactsdemo;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class Contact {
    //联系人姓名
    private String name;
    //分组字母
    private String sortKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
