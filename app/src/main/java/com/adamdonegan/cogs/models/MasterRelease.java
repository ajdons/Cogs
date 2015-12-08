package com.adamdonegan.cogs.models;

import java.util.List;
import java.util.Map;

/**
 * Created by AdamDonegan on 15-11-13.
 */
public class MasterRelease {
    private String id;
    private String title;
    private List<Map<String, String>> artists;
    private List<Image> images;
    private String versions_url;
    private String resource_url;
    private List<Track> tracklist;
    private List<String> genres;
    private List<String> styles;
    private String year;

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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getVersions_url() {
        return versions_url;
    }

    public void setVersions_url(String versions_url) {
        this.versions_url = versions_url;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    public List<Track> getTracklist() {
        return tracklist;
    }

    public void setTracklist(List<Track> tracklist) {
        this.tracklist = tracklist;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
