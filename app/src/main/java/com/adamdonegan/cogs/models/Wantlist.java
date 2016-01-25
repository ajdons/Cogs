package com.adamdonegan.cogs.models;

import java.util.List;


public class Wantlist {
    Pagination pagination;
    List<Want> wants;

    public List<Want> getWants() {
        return wants;
    }

    public void setWants(List<Want> wants) {
        this.wants = wants;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
