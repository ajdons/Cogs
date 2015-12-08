package com.adamdonegan.cogs.models;

import java.util.List;
import java.util.Map;

/**
 * Created by AdamDonegan on 15-11-11.
 */
public class Artist {
    private String id;
    private List<String> namevariations;
    private String name;
    private String profile;
    private String releases_url;
    private List<String> urls;
    private List<Member> members;
    private List<Image> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getNamevariations() {
        return namevariations;
    }

    public void setNamevariations(List<String> namevariations) {
        this.namevariations = namevariations;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getReleases_url() {
        return releases_url;
    }

    public void setReleases_url(String releases_url) {
        this.releases_url = releases_url;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
