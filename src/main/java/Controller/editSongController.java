package Controller;

import Model.Song;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class editSongController implements Initializable {
    @FXML
    private Label song;
    void initData(Song s) {
        song.setText(s.getTitle());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
