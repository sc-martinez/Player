package ModelTests;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import java.sql.*;

import static org.junit.Assert.*;

public class AlbumTest extends ApplicationTest {

    AnchorPane pane;

    @Override
    public void start(Stage stage) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        pane = new AnchorPane();
        stage.setScene(new Scene( pane
                , 100, 100));
        stage.show();
        JDBCConnector.connect();
        JDBCConnector.addArtist("Linkin Park");

    }


    @Test
    public void initDataWhenAlbumIsIdeal() {
        Album album = new Album("Meteora", "image",2005,  "Meteora", "Linkin Park", "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            assertEquals(album.getArtist(), "Linkin Park");
        }));
    }

    @Test
    public void initDataWhenAlbumIsNotIdeal() {
        Album album = new Album("Meteora", "image",-5010,  "Meteora", "Linkin Park", "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            assertEquals(album.getArtist(), "Linkin Park");
        }));
    }


    @Test
    public void save(){
        Album album = new Album("Meteora", "image",-5010,  "Meteora", "Linkin Park", "desc");
        Platform.runLater( new Thread(()-> {
            album.initData(pane);
            album.save();
            try{
                JDBCConnector.addAlbum("Meteora");
                assertTrue(JDBCConnector.returnAlbums().sorted().size() > 0);
            }catch (SQLException e){}

        }));

    }
}