package com.androidev.maps.Model;

import java.util.Date;

public class OrderDetail {
   private String thumbnail;
   private String food_name;
   private String price;
   private int amount;

    public OrderDetail(String thumbnail, String food_name, String price, int amount) {
        this.thumbnail = thumbnail;
        this.food_name = food_name;
        this.price = price;
        this.amount = amount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
