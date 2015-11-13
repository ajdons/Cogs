package com.adamdonegan.cogs.models;

import java.util.List;

/**
 * Created by AdamDonegan on 15-11-13.
 */
public class ArtistReleaseVersions {
    private List<ArtistRelease> releases;

    public List<ArtistRelease> getReleases() {
        return releases;
    }

    public void setReleases(List<ArtistRelease> releases) {
        this.releases = releases;
    }
}
