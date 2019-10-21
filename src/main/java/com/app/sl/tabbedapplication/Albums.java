package com.app.sl.tabbedapplication;

import java.io.Serializable;

public class Albums implements Serializable {

    private int Albumid;
    private String AlbumName;
    private String Albumartist;
    private String AlbumArt;



    public Albums(String albumName, String albumArt, String artistname,int albumId) {
        AlbumName = albumName;
       AlbumArt = albumArt;
       Albumartist = artistname;
       Albumid = albumId;
    }

    public String getAlbumContent() {
        return Albumartist;
    }

    public void setAlbumContent(String albumContent) {
        Albumartist = albumContent;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public void setAlbumArt(String albumArt) {
        AlbumArt = albumArt;
    }
    public String getAlbumArt(){
        return AlbumArt;
    }

    public int getAlbumid() {
        return Albumid;
    }

    public void setAlbumid(int albumid) {
        Albumid = albumid;
    }
}
