package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Song  {
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty year;
    private SimpleIntegerProperty rate;
    private SimpleStringProperty track;



    public Song(String title, String artist, String album, String year, int rate,String track) {
        this.title =new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.album =new SimpleStringProperty (album);
        this.year = new SimpleStringProperty(year);
        this.rate = new SimpleIntegerProperty(rate);
        this.track =new SimpleStringProperty (track);
    }


    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getArtist() {
        return artist.get();
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getAlbum() {
        return album.get();
    }

    public SimpleStringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public int getRate() {
        return rate.get();
    }

    public SimpleIntegerProperty rateProperty() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate.set(rate);
    }

    public String getTrack() {
        return track.get();
    }

    public SimpleStringProperty trackProperty() {
        return track;
    }

    public void setTrack(String track) {
        this.track.set(track);
    }
}
