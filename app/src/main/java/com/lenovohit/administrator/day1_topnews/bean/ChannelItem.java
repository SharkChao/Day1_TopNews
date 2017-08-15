package com.lenovohit.administrator.day1_topnews.bean;

/**
 * Created by SharkChao on 2017-08-15.
 */

public class ChannelItem {
    private String id;
    private String name;
    private String orderId;
    private String selected;

    public ChannelItem(String id, String name, String orderId, String selected) {
        this.id = id;
        this.name = name;
        this.orderId = orderId;
        this.selected = selected;
    }
    public ChannelItem(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
