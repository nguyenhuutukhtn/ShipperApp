package com.androidev.maps.Model;

public class OrderInfoResponse {
    private int id;
    private int customer_id;
    private int shipper_id;
    private String date;
    private int status;
    private String sum_price;
    private String address_customer;
    private String phone;
    private int store_id;

    public OrderInfoResponse(int id, int customer_id, int shipper_id, String date, int status, String sum_price, String address_customer, String phone, int store_id) {
        this.id = id;
        this.customer_id = customer_id;
        this.shipper_id = shipper_id;
        this.date = date;
        this.status = status;
        this.sum_price = sum_price;
        this.address_customer = address_customer;
        this.phone = phone;
        this.store_id = store_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getShipper_id() {
        return shipper_id;
    }

    public void setShipper_id(int shipper_id) {
        this.shipper_id = shipper_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSum_price() {
        return sum_price;
    }

    public void setSum_price(String sum_price) {
        this.sum_price = sum_price;
    }

    public String getAddress_customer() {
        return address_customer;
    }

    public void setAddress_customer(String address_customer) {
        this.address_customer = address_customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }
}
