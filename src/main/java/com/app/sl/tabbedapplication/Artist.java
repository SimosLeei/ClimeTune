package com.app.sl.tabbedapplication;

public class Artist {

    private String NameofArtist;
    private String NameofAlbum;
    private int ArtistIdl;
    private String NoAlbums;
    private String NoTracks;



    public Artist(String nameofArtist ,String noAlbums,String noTracks) { //String nameofAlbum, int id, int artistId,
        NameofArtist = nameofArtist;
       NoAlbums = noAlbums;
       NoTracks = noTracks;
        }

    public String getNameofArtist() {
        return NameofArtist;
    }

    public void setNameofArtist(String nameofArtist) {
        NameofArtist = nameofArtist;
    }

    public String getNameofAlbum() {
        return NameofAlbum;
    }

    public void setNameofAlbum(String nameofAlbum) {
        NameofAlbum = nameofAlbum;
        }
//    public int getId() {
//        return Id;
//    }
//
//    public void setId(int id) {
//        Id = id;
//    }

    public int getArtistIdl() {
        return ArtistIdl;
    }

    public void setArtistIdl(int artistIdl) {
        ArtistIdl = artistIdl;
    }

    public String getNoAlbums() {
        return NoAlbums;
    }

    public void setNoAlbums(String noAlbums) {
        NoAlbums = noAlbums;
    }

    public String getNoTracks() {
        return NoTracks;
    }

    public void setNoTracks(String noTracks) {
        NoTracks = noTracks;
    }


}
