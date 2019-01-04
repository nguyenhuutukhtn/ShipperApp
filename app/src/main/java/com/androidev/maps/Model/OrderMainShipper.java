package com.androidev.maps.Model;

public class OrderMainShipper {
    private Integer order_id;
    private String store_name;
    private String customer_name;
    private String from;
    private String to;

    public OrderMainShipper() {
    }

    public OrderMainShipper(Integer order_id, String store_name, String customer_name, String from, String to) {
        this.order_id = order_id;
        this.store_name = store_name;
        this.customer_name = customer_name;
        this.from = from;
        this.to = to;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
