package com.androidev.maps.Model;

public class StoreInfoRespose {
    private int id;
    private String avatar;
    private String city;
    private String district;
    private String name;
    private String phone;
    private String revenue;
    private String street;
    private String type;
    private String username;

    public StoreInfoRespose(int id, String avatar, String city, String district, String name, String phone, String revenue, String street, String type, String username) {
        this.id = id;
        this.avatar = avatar;
        this.city = city;
        this.district = district;
        this.name = name;
        this.phone = phone;
        this.revenue = revenue;
        this.street = street;
        this.type = type;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
