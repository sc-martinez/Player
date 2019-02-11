package Controller;

import Model.Genres;
import Model.JDBCConnector;
import Model.Moods;
import Model.Song;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class editSongController implements Initializable {
    @FXML
    private JFXTextField titleSong,artistSong,albumSong;

    @FXML
    private ListView<String>genresList,moodsList;
    @FXML
    private ImageView imageSong;
    @FXML
    private JFXTextArea lirycsSong;
    @FXML
    private Button saveButton;
    private String path="";
    private Song s;
    void initData(Song s) {
        this.s=s;
        path=s.getPath();
        List<String> genreList = new LinkedList<String>(Genres.genres.keySet());
        List<String> moodList = new LinkedList<String>(Moods.moods.keySet());
        titleSong.setText(s.getTitle());
        artistSong.setText(s.getArtist());
        albumSong.setText(s.getAlbum());
        lirycsSong.setText(s.getText());

        String[]genres=JDBCConnector.returnGenreMood(s.getPath(),"genre");
        if(genres!=null) {
            for (String genre : genres) {
                int index = genreList.indexOf(genre);
                genresList.getSelectionModel().select(index);
            }
        }
        String[]moods=JDBCConnector.returnGenreMood(s.getPath(),"moods");
        if(moods!=null) {
            for (String mood : moods) {
                int index = moodList.indexOf(mood);
                moodsList.getSelectionModel().select(index);
            }
        }
        Image image=new Image("file:"+s.getImage());
        imageSong.setImage(image);

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(String name:Moods.moods.keySet()){
            moodsList.getItems().add(name);
        }
        for (String name: Genres.genres.keySet()){
            genresList.getItems().add(name);
        }
        moodsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }
    @FXML
    private void save(){
        String[]genres= genresList.getSelectionModel().getSelectedItems().toArray(new String[0]);
        String[]moods=moodsList.getSelectionModel().getSelectedItems().toArray(new String[0]);
        JDBCConnector.updateSong(titleSong.getText(),artistSong.getText(),albumSong.getText(),genres,moods,
               lirycsSong.getText(),imageSong.getImage().getUrl(),path );
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void find_lyrics(){
       s.findLyrics(lirycsSong);
    }
}
