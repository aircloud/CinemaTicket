package com.example.xhz636.cinematicket;

public class MovieInfo {

    private int id;
    private String name;
    private String abstra;
    private float score;
    private String type;
    private String duration;
    private String showtime;
    private String photo;
    private String movieid;

    public MovieInfo() {
    }

    public MovieInfo(int id, String name, String abstra, float score, String type, String duration, String showtime, String photo, String movieid) {
        this.id = id;
        this.name = name;
        this.abstra = abstra;
        this.score = score;
        this.type = type;
        this.duration = duration;
        this.showtime = showtime;
        this.photo = photo;
        this.movieid = movieid;
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

    public String getAbstra() {
        return abstra;
    }

    public void setAbstra(String abstra) {
        this.abstra = abstra;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }
}
