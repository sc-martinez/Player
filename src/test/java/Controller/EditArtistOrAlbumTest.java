package Controller;

import Model.Album;
import Model.JDBCConnector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
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
        stage.show();
        stage.toFront();
        JDBCConnector.connect();
        JDBCConnector.addArtist(artist);

    }


    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[] {});
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