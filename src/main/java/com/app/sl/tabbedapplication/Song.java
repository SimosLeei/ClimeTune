package com.app.sl.tabbedapplication;



import java.io.Serializable;

public class Song implements Serializable{
    private long id;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private  int albumId;



    public Song(long id, String title, String artist, String album, String duration, int albumId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.albumId = albumId;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {

        this.album = album;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getDuration() {
        return duration;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }


}
