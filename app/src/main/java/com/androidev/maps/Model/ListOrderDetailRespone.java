package com.androidev.maps.Model;

import java.util.ArrayList;

public class ListOrderDetailRespone {
    private ArrayList<OrderDetail> order_details;

    public ListOrderDetailRespone(ArrayList<OrderDetail> order_details) {
        this.order_details = order_details;
    }

    public ArrayList<OrderDetail> getOrder_details() {
        return order_details;
    }

    public void setOrder_details(ArrayList<OrderDetail> order_details) {
        this.order_details = order_details;
    }
}
