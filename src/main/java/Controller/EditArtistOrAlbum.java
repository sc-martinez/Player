package Controller;

import ModelTests.ArtistAlbum;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
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
