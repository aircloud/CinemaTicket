package com.example.xhz636.cinematicket;

public class CinemaInfo {

    private int id;
    private String name;
    private String address;
    private float beginprice;
    private String cinemaid;

    public CinemaInfo() {
    }

    public CinemaInfo(int id, String name, String address, float beginprice, String cinemaid) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.beginprice = beginprice;
        this.cinemaid = cinemaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getBeginprice() {
        return beginprice;
    }

    public void setBeginprice(float beginprice) {
        this.beginprice = beginprice;
    }

    public String getCinemaid() {
        return cinemaid;
    }

    public void setCinemaid(String cinemaid) {
        this.cinemaid = cinemaid;
    }
}
