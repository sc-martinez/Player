package ModelTests;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import java.sql.*;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class AlbumTest extends ApplicationTest {
//
//    AnchorPane pane;
//    String artist = UUID.randomUUID().toString() ;
//
//    @Override
//    public void start(Stage stage) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
//        pane = new AnchorPane();
//        stage.setScene(new Scene( pane
//                , 100, 100));
//        //stage.show();
//        JDBCConnector.connect();
//        JDBCConnector.addArtist(artist);
//
//    }
//
//
//    @Test
//    public void initDataWhenAlbumIsIdeal() {
//        Album album = new Album("Meteora", "image",2005,  "Meteora", "Linkin Park", "desc");
//        Platform.runLater( new Thread(()-> {
//            album.initData(pane);
//            assertEquals(album.getArtist(), "Linkin Park");
//        }));
//    }
//
//    @Test
//    public void initDataWhenAlbumIsNotIdeal() {
//        Album album = new Album("Meteora", "image",-5010,  "Meteora", "Linkin Park", "desc");
//        Platform.runLater( new Thread(()-> {
//            album.initData(pane);
//            assertEquals(album.getArtist(), "Linkin Park");
//        }));
//    }
//
//
//    @Test
//    public void save(){
//        String albumName = UUID.randomUUID().toString();
//        Album album = new Album(albumName, albumName, new Random().nextInt(),  albumName, artist, "desc");
//        Platform.runLater( new Thread(()-> {
//            album.initData(pane);
//            try{
//                JDBCConnector.addAlbum(albumName);
//                album.save();
//                assertTrue(JDBCConnector.returnAlbums().sorted().size() > 0);
//            }catch (SQLException e){}
//
//        }));
//
//    }
}