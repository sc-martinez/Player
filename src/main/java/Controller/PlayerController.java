package Controller;


import Model.*;
import com.jfoenix.controls.JFXListView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import javax.annotation.processing.Generated;
import javax.script.Bindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {
    private LinkedList<Song>songs=new LinkedList<>();

    @FXML
    private Button musicbutton,moviebutton,exit,displayAlbums,displayArtists,displaySongsButton,
            searchButton,addSongsButton,playlistCreateButton;

    @FXML
    private AnchorPane moviePane,musicPane,musicMenu,movieMenu,
            displayWithInfo,displayAlbumsArtists,displaySongs,mainMusicPane,
            createPlaylistPane,musicBar;

    @FXML
    private ListView<Button>playlisty;
    @FXML
    private ListView<DisplayArtistAlbum>AAView;
    @FXML
    private Label info,printSongInfo,numberOfSongs;

    @FXML
    private JFXListView<Button>genresListView,moodsListView;

    @FXML
    private TableView<Song> tableOfSongs,songsOfPlaylist;
    @FXML
    private TableColumn<Song,String>title,artist,album,year,track,
                                    titleP,artistP,albumP,yearP,trackP;
    @FXML
    private TableColumn<Song,Integer>rate,rateP;
    @FXML
    private TextField searchField,playlistName;
    @FXML
    private TextArea playlistDescription;
    @FXML
    private ImageView playlistImage,imageView;
    @FXML
    private Pane songPane;
    @FXML
    private Button replaySong;

    @FXML
    private void search(){
        String regex=searchField.getText();
        try {
            updateTable(2,regex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    Mp3player mp3player=null;



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
            Platform.exit();
            System.exit(0);
        }
        else if(event.getTarget()==displayAlbums){
            loadAlbums();
            displayAlbumsArtists.toFront();
        }
        else if(event.getTarget()==displayArtists){
            loadArtists();
            displayAlbumsArtists.toFront();

        }
        else if(event.getTarget()==displaySongsButton){
            displaySongs.toFront();
            try {
                updateTable(1,null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //create Playlist
    @FXML
    private void createPlaylist(){
        createPlaylistPane.toFront();
    }

    @FXML
    private void playlistCreator(){
        String name=playlistName.getText();
        String description=playlistDescription.getText();
        String imagepath=playlistImage.getImage().getUrl();

    }


    @FXML
    private void loadArtists(){

        ObservableList<Artist>artists=null;
        try {
            artists=JDBCConnector.returnArtists();
            if(artists!=null){
                AAView.getItems().clear();
                for(Artist artist:artists){
                    AAView.getItems().add(new DisplayArtistAlbum(artist,1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AAView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                    String[] parts = AAView.getSelectionModel().getSelectedItem().getAccessibleText().split("\\|");
                    System.out.println(parts[1]);
                    displayPlaylistPane(2,parts[0],parts[1]);
            }
        });
    }
    private void loadAlbums(){
        ObservableList<Album>albums=null;
        try {
            albums=JDBCConnector.returnAlbums();
            if(albums!=null){
                AAView.getItems().clear();
                for(Album album:albums){
                    AAView.getItems().add(new DisplayArtistAlbum(album,2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AAView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                    String[] parts = AAView.getSelectionModel().getSelectedItem().getAccessibleText().split("\\|");
                    displayPlaylistPane(3,parts[0],parts[1]);
            }
        });
    }


    @FXML
    private  void loadPlaylistImage(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image","*.png","*.jpeg","*.jpg"));
        File file=fileChooser.showOpenDialog(new Stage());
        String path=null;
        path=file.getAbsolutePath();
        FileInputStream inputstream = null;
        try {
            System.out.println(path);
            inputstream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
        }
        Image iv = new Image(inputstream);

        playlistImage.setImage(iv);

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
                        try {
                            updateTable(3,b.getText());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                listView.getItems().add(b);
            }
        }
    }


    public Stage showEditSongWindow(Song s,String fxml) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        fxml
                )
        );

        Stage stage = new Stage(StageStyle.DECORATED);
        try {
            stage.setScene(
                    new Scene(
                            (Pane) loader.load()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        editSongController controller =
                loader.<editSongController>getController();
        controller.initData(s);

        stage.show();

        return stage;
    }




    private void editSong(Song song){
        showEditSongWindow(song,"/editSong.fxml");
    }


    /**
     *
     * @param i 1-all songs 2,regex 3-moods or genres
     * @param regex regex or mood(genre) if i=1 regex=null
     * @throws SQLException
     */
    private void updateTable(int i,String regex) throws SQLException {

        try {
            ObservableList<Song> data=null;
            switch (i){
                case 1:
                    data= JDBCConnector.returnSongs();
                    break;
                case 2:
                    displaySongs.toFront();
                    data=JDBCConnector.returnSongsByRegex(regex);
                    break;
                case 3:
                    displaySongs.toFront();
                    data=JDBCConnector.returnSongsByMoodOrGenre(regex);
                    break;
            }
            tableOfSongs.setItems(data);

            tableOfSongs.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {
                    if(click.getButton()== MouseButton.SECONDARY){
                        editSong(tableOfSongs.getSelectionModel().getSelectedItem());
                    }else if(click.getClickCount()==2){
                        songs.clear();
                        for(Song d:tableOfSongs.getItems()){
                            songs.add(d);
                        }mp3player.loadSongs(songs);
                            try {
                                mp3player.setCurrentSong(tableOfSongs.getSelectionModel().getFocusedIndex());
                            }catch (NullPointerException ex){
                                System.out.println("Brak plików");
                            }

                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void addSongs(){
        List<File> files=null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add new songs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio files","*.mp3","*.3gp","*.flac"));
        files=fileChooser.showOpenMultipleDialog(new Stage());
        if(files!=null)
        JDBCConnector.addSongs(files);

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

    /**
     *
     * @param i   1-playlist 2-artist 3-album
     * @param name name of playlist/artist/album
     * @param image image of -||-
     */
    private void displayPlaylistPane(int i,String name,String image){
        displayWithInfo.toFront();

        try {
            imageView.setImage(new Image(new FileInputStream(image)));
        } catch (FileNotFoundException e) {
            System.out.println("obrazek   "+e.getMessage());
        }
        ObservableList<Song> data=null;
        switch (i){
            case 1:
                break;
            case 2:
                try {
                    data=JDBCConnector.returnByArtist(name);
                } catch (SQLException e) {

                }
                break;
            case 3:
                try {
                    data=JDBCConnector.returnByAlbum(name);
                } catch (SQLException e) {

                }
                break;
        }

        songsOfPlaylist.setItems(data);
        if (!songs.isEmpty())songs.clear();
        songs.addAll(data);
        mp3player.loadSongs(songs);
        songsOfPlaylist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if(click.getButton()== MouseButton.SECONDARY){
                    editSong(songsOfPlaylist.getSelectionModel().getSelectedItem());
                }else if(click.getClickCount()==2){
                    try {
                        mp3player.setCurrentSong(songsOfPlaylist.getSelectionModel().getSelectedIndex());
                    }catch (NullPointerException ex){
                        System.out.println("Brak plików");
                    }
                }
            }
        });
        info.setText(name);
        numberOfSongs.setText("Number of songs: "+data.size());
    }

    @FXML
    public void play_pause_song(ActionEvent event){
        mp3player.play_pause();
    }
    @FXML
    public void next_song(){
        mp3player.next();
    }
    @FXML
    public void prev_song(){
        mp3player.prev();
    }
    @FXML
    public void autoreplay(){
        boolean replaybutton=mp3player.setAutoreplay();
        if(replaybutton){
            replaySong.setStyle("-fx-background-color: white");
            replaySong.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    replaySong.setStyle("-fx-background-color: #4F5459");
                }
            });
            replaySong.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    replaySong.setStyle("-fx-background-color: white");
                }
            });
        }else{
            replaySong.setStyle("-fx-background-color: #2D3237");
            replaySong.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    replaySong.setStyle("-fx-background-color: #4F5459");
                }
            });
            replaySong.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    replaySong.setStyle("-fx-background-color: #2D3237");
                }
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mp3player=new Mp3player();
        mp3player.loadBar(musicBar);

        updateListView(moodsListView,Moods.moods);
        updateListView(genresListView, Genres.genres);



        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        artist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        album.setCellValueFactory(new PropertyValueFactory<>("album"));
        track.setCellValueFactory(new PropertyValueFactory<>("track"));
        year.setCellValueFactory(new PropertyValueFactory<>("year"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        titleP.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistP.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumP.setCellValueFactory(new PropertyValueFactory<>("album"));
        trackP.setCellValueFactory(new PropertyValueFactory<>("track"));
        yearP.setCellValueFactory(new PropertyValueFactory<>("year"));
        rateP.setCellValueFactory(new PropertyValueFactory<>("rate"));
    }
}


