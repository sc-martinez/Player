package Controller;

import Model.Album;
import Model.Artist;
import Model.ArtistAlbum;
import Model.JDBCConnector;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class EditArtistOrAlbum implements Initializable {

    @FXML
    private ImageView image;
    @FXML
    private AnchorPane pane;

    private ArtistAlbum a;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void initData(ArtistAlbum a) {
        this.a=a;
        this.a.initData(pane);
            Image iv = new Image("file:" + a.getImage());
            if(iv.isError()) iv=new Image("file:/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/default.png");
            this.image.setImage(iv);

    }
    @FXML
    private void edit(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image","*.png","*.jpeg","*.jpg"));
            File file=fileChooser.showOpenDialog(new Stage());
            String path=file.getAbsolutePath();
            FileInputStream inputstream = null;
            inputstream = new FileInputStream(path);
            a.setImage(path);
            Image iv = new Image(inputstream);
            this.image.setImage(iv);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    @FXML
    private void save(){
        a.save();
    }
}
