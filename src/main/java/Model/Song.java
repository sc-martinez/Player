package Model;

import com.jfoenix.controls.JFXTextArea;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Song  {
    private String path;
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty year;
    private SimpleIntegerProperty rate;
    private SimpleStringProperty track;
    private String text;
    private String image;


    public Song(SongBuilder songBuilder){
        this.path=songBuilder.path;
        this.title =songBuilder.title;
        this.artist = songBuilder.artist;
        this.album =songBuilder.album;
        this.year = songBuilder.year;
        this.rate = songBuilder.rate;
        this.track =songBuilder.track;
        this.text=songBuilder.text;
        this.image=songBuilder.image;
    }
    public static class SongBuilder{
        private String path;
        private SimpleStringProperty title;
        private SimpleStringProperty artist;
        private SimpleStringProperty album;
        private SimpleStringProperty year;
        private SimpleIntegerProperty rate;
        private SimpleStringProperty track;
        private String text;
        private String image;

        public SongBuilder(String path){
            this.path=path;
        }
        public SongBuilder title(String title){
            this.title=new SimpleStringProperty(title);
            return this;
        }
        public SongBuilder artist(String artist){
            this.artist=new SimpleStringProperty(artist);
            return this;
        }
        public SongBuilder album(String album){
            this.album=new SimpleStringProperty(album);
            return this;
        }
        public SongBuilder year(String year){
            this.year=new SimpleStringProperty(year);
            return this;
        }
        public SongBuilder rate(int rate){
            this.rate=new SimpleIntegerProperty(rate);
            return this;
        }
        public SongBuilder track(String track){
            this.track=new SimpleStringProperty(track);
            return this;
        }
        public SongBuilder text(String text){
            this.text=text;
            return this;
        }
        public SongBuilder image(String image){
            if(image!=null)this.image=image;
            else {
//                get image of album if exist
                this.image=JDBCConnector.returnImage(album.get());
            }
//            set default image if is null
            if(this.image==null)
                this.image="/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/default.png";

            return this;
        }
        public Song build(){
            return new Song(this);
        }

    }






    public String getPath() {
        return path;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    //==================================================
    public boolean findLyrics(JFXTextArea textArea){
        AzlyricsConncector conncector=new AzlyricsConncector(artist.get(),title.get());
        Thread t=new Thread(conncector);
        t.start();
        try {
            t.join();
            text=conncector.returnLyrics();
            textArea.setText(text);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;


    }



}
