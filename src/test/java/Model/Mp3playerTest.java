package Model;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class Mp3playerTest extends ApplicationTest {

    AnchorPane pane;

    @Override
    public void start(Stage stage) {
        pane = new AnchorPane();
        stage.setScene(new Scene( pane
                , 100, 100));
        stage.show();
    }

    @Test
    public void loadSongs() {
        Song sa = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("Breaking The Habit")
                .build();
        Song sb = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("Numb")
                .build();
        LinkedList<Song> songs = new LinkedList<Song>(List.of(new Song[]{sa, sb}));
        Mp3player mp3 = new Mp3player();
        Platform.runLater( new Thread(()-> {
            mp3.loadBar(pane);
            mp3.loadSongs(songs);
            //Throws exception due that file extension does not exists, but the song is queued into the player
            assertThrows(NullPointerException.class, ()-> { mp3.setCurrentSong(0); });
        }));
    }


    @Test
    public void next() {
        Song sa = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("Breaking The Habit")
                .build();
        LinkedList<Song> songs = new LinkedList<Song>(List.of(new Song[]{sa}));
        Mp3player mp3 = new Mp3player();
        Platform.runLater( new Thread(()-> {
            mp3.loadBar(pane);
            mp3.loadSongs(songs);
            //Throws exception due that file extension does not exists, but the song is queued into the player
            assertThrows(NullPointerException.class, ()-> { mp3.next(); });
        }));

    }

    @Test
    public void prev() {
        Song sa = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("Breaking The Habit")
                .build();
         LinkedList<Song> songs = new LinkedList<Song>(List.of(new Song[]{sa}));
        Mp3player mp3 = new Mp3player();
        Platform.runLater( new Thread(()-> {
            mp3.loadBar(pane);
            mp3.loadSongs(songs);
            //Throws exception due that file extension does not exists, but the song is queued into the player
            assertThrows(NullPointerException.class, ()-> { mp3.prev(); });
        }));
    }

    @Test
    public void setAutoreplay() {
        Song sa = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("Breaking The Habit")
                .build();
        Mp3player mp3 = new Mp3player();
        Platform.runLater( new Thread(()-> {
            mp3.loadBar(pane);
            //Throws exception due that file extension does not exists, but the song is queued into the player
            assertTrue(mp3.setAutoreplay());
            assertFalse(mp3.setAutoreplay());
        }));
    }
}