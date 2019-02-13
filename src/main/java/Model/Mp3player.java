package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


public class Mp3player implements Subject{
    @FXML
    AnchorPane anchorPane;

    private Label titleAndArtist;
    private ImageView imageView;
    private JFXSlider musicSlider,volumeSlider;

    private Duration duration;
    Media media=null;
    MediaPlayer player=null;

    private LinkedList<Song>songs=new LinkedList<>();
    private int index;
    private int length;
    private boolean autoreplay=false;

    public Mp3player(){

    }
    public void loadBar(AnchorPane pane){
        anchorPane=pane;



        titleAndArtist=new Label();
        titleAndArtist.getStyleClass().add("titleAndArtist");
        AnchorPane.setTopAnchor(titleAndArtist, 25.0);
        AnchorPane.setLeftAnchor(titleAndArtist, 150.0);
        musicSlider=new JFXSlider();

        musicSlider.setMinWidth(800);
        AnchorPane.setTopAnchor(musicSlider,100.);
        AnchorPane.setLeftAnchor(musicSlider,20.);

        musicSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ( musicSlider.isValueChanging()) {
                player.seek(duration.multiply(newValue.doubleValue()/100.));
            }
            if(! musicSlider.isValueChanging()){
                if(Math.abs(newValue.doubleValue()/100.-oldValue.doubleValue()/100.)>0.005){
                    player.seek(duration.multiply(newValue.doubleValue()/100.));
                }
            }
        });



        volumeSlider=new JFXSlider(0,100,10);
        volumeSlider.setMajorTickUnit(10);
        volumeSlider.setMinorTickCount(0);
        volumeSlider.setBlockIncrement(10);
        volumeSlider.setMinWidth(100);
        AnchorPane.setTopAnchor(volumeSlider,100.);
        AnchorPane.setLeftAnchor(volumeSlider,840.);

        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ( volumeSlider.isValueChanging()) {
                player.setVolume(newValue.doubleValue()/100.);
            }
            if(! volumeSlider.isValueChanging()){
                if(Math.abs(newValue.doubleValue()/100.-oldValue.doubleValue()/100.)>0.005){
                    player.setVolume(newValue.doubleValue()/100.);
                }
            }
        });

        anchorPane.getChildren().addAll(titleAndArtist,musicSlider,volumeSlider);


    }


    private void update(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Duration currentTime = player.getCurrentTime();
                musicSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
            }
        });
    }

    public void loadSongs(LinkedList<Song>songs){
        this.songs=songs;
        length=songs.size();
    }

    public boolean setCurrentSong(int i)throws NullPointerException{
        if(i<0)i=songs.size()-1;
        if(i>=songs.size())i=0;
        if(player!=null)player.stop();
        this.index=i;
        titleAndArtist.setText(songs.get(index).getTitle()+"\n"+songs.get(index).getArtist());
        Image image=new Image("file:"+songs.get(index).getImage());
        imageView=new ImageView(image);
        imageView.setFitWidth(64.);
        imageView.setFitHeight(64.);
        AnchorPane.setTopAnchor(imageView, 25.0);
        AnchorPane.setLeftAnchor(imageView, 50.0);
        anchorPane.getChildren().add(imageView);
        File file = new File(songs.get(index).getPath());
        boolean exists=file.exists();
        if(!exists){
            if(length==0)throw new  NullPointerException() ;
            length--;
            next();
            return false;
        }
        String path=file.toURI().toASCIIString();
        media = new Media(path);
        player = new MediaPlayer(media);
        notifyAllObservers(index);
        play();

        player.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                update();
            }
        });
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                duration = player.getMedia().getDuration();
                update();

            }
        });
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (!autoreplay)next();
                else {
                    setCurrentSong(index);
                }
            }
        });

        return true;
    }



    private void play(){
        player.setVolume(volumeSlider.getValue()/100.);
        player.play();
    }
    public void next(){
        setCurrentSong(++index);
    }
    public void prev(){
        setCurrentSong(--index);
    }
    public boolean setAutoreplay(){
        if(autoreplay)autoreplay=false;
        else autoreplay=true;
        return autoreplay;
    }
    public void play_pause(){
        if(player!=null){
            if(player.getStatus().equals(MediaPlayer.Status.PLAYING)){
                player.pause();
            }else {
                player.play();
            }
        }
    }

    private ArrayList<Observer> observers=new ArrayList<>();
    @Override
    public void register(Observer o) {
        observers.add(o);
        System.out.println("Dodano obserwatora: "+o);
    }
    public void watchYoutube(){
        songs.get(index).watchOnYoutube();
        player.pause();
    }

    @Override
    public void unregister(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers(int s) {
        for (Observer observer:observers){
            observer.update(s);
        }
    }
}
