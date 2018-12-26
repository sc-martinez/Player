package Controller;


import Model.DisplayButton;
import Model.Genres;
import Model.Moods;
import Model.PlaylistButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML
    private Button musicbutton,moviebutton,exit,displayAlbums,displayArtists,displaySongsButton,
            searchButton,addSongsButton;

    @FXML
    private AnchorPane moviePane,musicPane,musicMenu,movieMenu,
            displayWithInfo,displayAlbumsArtists,displaySongs,mainMusicPane;

    @FXML
    private ListView<Button>playlisty;

    @FXML
    private Label info;

    @FXML
    private JFXListView<Button>genresListView,moodsListView;

    @FXML
    private void search(){
        System.out.println(1);
    }




    public void handleButton(ActionEvent event){

        if(event.getTarget()==musicbutton){
            fadeOut(musicPane,moviePane);
            movieMenu.setVisible(false);
            musicMenu.setVisible(true);
            mainMusicPane.toFront();
        }
        else if(event.getTarget()==moviebutton){
            fadeOut(moviePane,musicPane);
            movieMenu.setVisible(true);
            musicMenu.setVisible(false);
        }
        else if(event.getTarget()==exit){
            System.out.println("o chuj");
        }
        else if(event.getTarget()==displayAlbums){
            displayAlbumsArtists.toFront();
        }
        else if(event.getTarget()==displayArtists){
            displayAlbumsArtists.toFront();
        }
        else if(event.getTarget()==displaySongsButton){
            displaySongs.toFront();
        }
    }

    @FXML
    private void createPlaylist(){

    }


    private void updateListView(ListView<Button>listView,Map<String,String>map){
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            ImageView imageView=null;
            try{
                imageView = new ImageView(
                        new Image(entry.getValue())

                );

            }catch (Exception ex){
                imageView=new ImageView();
            }finally {
                Button b=new Button(entry.getKey(),imageView);
                b.setId("gmbutton");
                b.setContentDisplay(ContentDisplay.TOP);
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        System.out.println(b.getText());
                    }
                });

                listView.getItems().add(b);
            }
        }
    }

    @FXML
    private void addSongs(){
        List<File> files=null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add new songs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio files","*.mp3","*.3gp","*.flac"));
        files=fileChooser.showOpenMultipleDialog(new Stage());

    }

    private void fadeIN(AnchorPane node,AnchorPane node1){

        FadeTransition fadeTransition=new FadeTransition();
        fadeTransition.setDuration(Duration.millis(200));
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

    }
    private void fadeOut(AnchorPane node,AnchorPane node1) {
        System.out.println(node.getOpacity());
        System.out.println(node1.getOpacity());
        if (node1.getOpacity() !=0) {
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setDuration(Duration.millis(200));
            fadeTransition.setNode(node1);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);

            fadeTransition.setOnFinished((ActionEvent event) -> {
                fadeIN(node, node1);
            });
            fadeTransition.play();
        }
    }

    private void displayPlaylistPane(String name){
        displayWithInfo.toFront();
        info.setText(name+"\n Average rate: "+5+"\n Number of songs: "+1);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i=0;i<6;i++){
            PlaylistButton button= null;
            try {
                button = new PlaylistButton("Playlista"+i);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            PlaylistButton finalButton = button;
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    displayPlaylistPane(finalButton.getText());
                }
            });
            playlisty.getItems().add(button);
        }
        updateListView(moodsListView,Moods.moods);
        updateListView(genresListView, Genres.genres);





    }
}


