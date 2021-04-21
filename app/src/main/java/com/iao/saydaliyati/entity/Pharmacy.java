package com.iao.saydaliyati.entity;

import com.google.android.gms.maps.model.LatLng;

public class Pharmacy {

    private String id;
    private String name;
    private String owner;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String arrondissement;
    private float lat;
    private float lng;

    public Pharmacy() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getArrondissement() {
        return arrondissement;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public LatLng getLagLng() { return new LatLng(lat, lng); }

    public void setId(String id) {
        this.id = id;
    }
}
