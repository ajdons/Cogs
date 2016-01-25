package com.adamdonegan.cogs.models;

import java.util.List;

public class Collection {
    Pagination pagination;
    List<CollectionRelease> releases;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<CollectionRelease> getReleases() {
        return releases;
    }

    public void setReleases(List<CollectionRelease> releases) {
        this.releases = releases;
    }
}
