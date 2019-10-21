package com.app.sl.tabbedapplication;

import java.io.Serializable;

public class AlbumArt implements Serializable {

    private String AlbumArt;
    private int AlbumArtId;

    public AlbumArt(String albumArt, int albumArtId) {
        AlbumArt = albumArt;
        AlbumArtId = albumArtId;
    }

    public int getAlbumArtId() {
        return AlbumArtId;
    }

    public void setAlbumArtId(int albumArtId) {
        AlbumArtId = albumArtId;
    }

    public String getAlbumArt() {
        return AlbumArt;
    }

    public void setAlbumArt(String albumArt) {
        AlbumArt = albumArt;
    }

}
