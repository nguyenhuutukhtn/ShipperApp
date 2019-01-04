package com.androidev.maps.Request;

public class UpdateOrderRequest {
    private int order_id;
    private int shipper_id;

    public UpdateOrderRequest(int order_id, int shipper_id) {
        this.order_id = order_id;
        this.shipper_id = shipper_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getShipper_id() {
        return shipper_id;
    }

    public void setShipper_id(int shipper_id) {
        this.shipper_id = shipper_id;
    }
}
