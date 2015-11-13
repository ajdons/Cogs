package com.adamdonegan.cogs.models;

/**
 * Created by AdamDonegan on 15-11-12.
 */
public class Track {
    private String position;
    private String title;
    private String duration;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
