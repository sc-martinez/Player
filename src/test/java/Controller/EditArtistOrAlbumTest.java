package Controller;

import ModelTests.Album;
import ModelTests.JDBCConnector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class EditArtistOrAlbumTest extends ApplicationTest {

    FXMLLoader loader;
    String artist = UUID.randomUUID().toString() ;

    @Override
    public void start(Stage stage) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {
        loader= new FXMLLoader(
                getClass().getResource(
                        "/editArtistAlbum.fxml"
                )
        );
        stage.setScene(new Scene((Pane) loader.load()));
        //stage.show();
        JDBCConnector.connect();
        JDBCConnector.addArtist(artist);

    }

    @Test
    public void initializeWhenAlbum() {

        Album album = new Album("Meteora", "image",2005,  "Meteora", artist, "desc");
        Platform.runLater( new Thread(()-> {
            EditArtistOrAlbum controller =
                    loader.<EditArtistOrAlbum>getController();
            controller.initData(album);
            assertEquals(album.getArtist(), artist);
        }));
    }
}