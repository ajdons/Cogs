package com.adamdonegan.cogs.models;

import java.util.List;

/**
 * Created by AdamDonegan on 15-11-13.
 */
public class MasterReleaseVersions {
    private List<Version> versions;

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }
}
