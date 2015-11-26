package com.adamdonegan.cogs.models;

import java.util.List;
import java.util.Map;

/**
 * Created by AdamDonegan on 15-11-11.
 */
public class Release {
    private String id;
    private String title;
    private String thumb;
    private List<Track> tracklist;
    private List<Map<String, String>> artists;
    private List<Image> images;
    private String resource_url;
    private String year;
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Map<String, String>> getArtists() {
        return artists;
    }

    public void setArtists(List<Map<String, String>> artists) {
        this.artists = artists;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public List<Track> getTracklist() {
        return tracklist;
    }

    public void setTracklist(List<Track> tracklist) {
        this.tracklist = tracklist;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
