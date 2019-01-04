package com.androidev.maps.Response;

import com.androidev.maps.Model.OrderMainShipper;

import java.util.ArrayList;

public class ListOrderRespone {
    private ArrayList<OrderMainShipper> free_orders;

    public ListOrderRespone(ArrayList<OrderMainShipper> free_orders) {
        this.free_orders = free_orders;
    }

    public ListOrderRespone() {

    }

    public ArrayList<OrderMainShipper> getFree_orders() {
        return free_orders;
    }

    public void setFree_orders(ArrayList<OrderMainShipper> free_orders) {
        this.free_orders = free_orders;
    }
}
