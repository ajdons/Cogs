package com.adamdonegan.cogs.models;

import java.util.List;

/**
 * Created by AdamDonegan on 15-11-13.
 */
public class SearchResults {
    List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
