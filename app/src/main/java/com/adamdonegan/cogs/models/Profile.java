package com.adamdonegan.cogs.models;


public class Profile {

    private String id;
    private String username;
    private String name;
    private String profile;
    private String location;
    private String avatar_url;
    private String num_collection;
    private String collection_folders_url;
    private String num_wantlist;
    private String wantlist_url;
    private String resource_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getNum_collection() {
        return num_collection;
    }

    public void setNum_collection(String num_collection) {
        this.num_collection = num_collection;
    }

    public String getCollection_folders_url() {
        return collection_folders_url;
    }

    public void setCollection_folders_url(String collection_folders_url) {
        this.collection_folders_url = collection_folders_url;
    }

    public String getNum_wantlist() {
        return num_wantlist;
    }

    public void setNum_wantlist(String num_wantlist) {
        this.num_wantlist = num_wantlist;
    }

    public String getWantlist_url() {
        return wantlist_url;
    }

    public void setWantlist_url(String wantlist_url) {
        this.wantlist_url = wantlist_url;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }
}
