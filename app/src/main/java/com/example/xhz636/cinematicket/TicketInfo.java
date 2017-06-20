package com.example.xhz636.cinematicket;

public class TicketInfo {

    private String movie;
    private String cinema;
    private String begintime;
    private String hall;
    private String dimension;
    private int row;
    private int column;
    private String ordernumber;

    public TicketInfo() {
    }

    public TicketInfo(String movie, String cinema, String begintime, String hall, String dimension, int row, int column, String ordernumber) {
        this.movie = movie;
        this.cinema = cinema;
        this.begintime = begintime;
        this.hall = hall;
        this.dimension = dimension;
        this.row = row;
        this.column = column;
        this.ordernumber = ordernumber;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }
}
