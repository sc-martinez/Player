package Model;

import Base.BaseTest;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Test;

import java.sql.*;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class AlbumTest extends BaseTest {

    AnchorPane pane;
    String artist = UUID.randomUUID().toString() ;


    @Override
    public void start(Stage stage) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        pane = new AnchorPane();
        stage.setScene(new Scene( pane
                , 100, 100));
        //stage.show();
        JDBCConnector.connect();
        JDBCConnector.addArtist(artist);

    }


    @Test
    public void initDataWhenAlbumIsIdeal() throws InterruptedException {
        Album album = new Album("Meteora", "image",2005,  "Meteora", "Linkin Park", "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            assertEquals(album.getArtist(), "Linkin Park");
        }));
        waitForRunLater();
    }

    @Test
    public void initDataWhenAlbumIsNotIdeal() throws InterruptedException {
        Album album = new Album("Meteora", "image",-5010,  "Meteora", "Linkin Park", "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            assertEquals(album.getArtist(), "Linkin Park");
        }));
        waitForRunLater();
    }


    @Test
    public void save() throws InterruptedException {
        String albumName = UUID.randomUUID().toString();
        Album album = new Album(albumName, albumName, new Random().nextInt(),  albumName, artist, "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            try{
                JDBCConnector.addAlbum(albumName);
                album.save();
                assertTrue(JDBCConnector.returnAlbums().sorted().size() > 0);
            }catch (SQLException e){}

        }));
        waitForRunLater();
    }
}