package com.example.xhz636.cinematicket;

public class ScheduleInfo {

    private int id;
    private String begintime;
    private String endtime;
    private String dimension;
    private String hall;
    private float price;
    private int surplus;
    private String cinemaid;
    private String movieid;

    public ScheduleInfo() {
    }

    public ScheduleInfo(int id, String begintime, String endtime, String dimension, String hall, float price, int surplus, String cinemaid, String movieid) {
        this.id = id;
        this.begintime = begintime;
        this.endtime = endtime;
        this.dimension = dimension;
        this.hall = hall;
        this.price = price;
        this.surplus = surplus;
        this.cinemaid = cinemaid;
        this.movieid = movieid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public String getCinemaid() {
        return cinemaid;
    }

    public void setCinemaid(String cinemaid) {
        this.cinemaid = cinemaid;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }
}
