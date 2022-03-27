package Controller;

import Base.BaseTest;
import Model.JDBCConnector;

import Model.Song;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class editSongControllerTest extends BaseTest {
    FXMLLoader loader;
    String artist = UUID.randomUUID().toString() ;
    String song = UUID.randomUUID().toString() ;
    List<Song> songs = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {
        loader= new FXMLLoader(
                getClass().getResource(
                        "/editSong.fxml"
                )
        );
        stage.setScene(new Scene((Pane) loader.load()));
        stage.show();
        Song sa = new Song
                .SongBuilder("")
                .album("meteora")
                .artist(artist)
                .title(song)
                .build();
        songs.add(sa);
        JDBCConnector.connect();
        JDBCConnector.addArtist(artist);
    }


    @Test
    public void initDataAndRenderContent() throws InterruptedException {
        Platform.runLater( new Thread(()-> {
            editSongController controller = loader.getController();
            controller.initData(songs.get(0));
            assertEquals(songs.get(0).getArtist(), artist);
        }));
    }

    @Test
    public void initialize() throws InterruptedException {
        Platform.runLater( new Thread(()-> {
            editSongController controller = loader.getController();
            controller.initialize(null, null);
            assertEquals(songs.get(0).getArtist(), artist);
        }));
    }
}