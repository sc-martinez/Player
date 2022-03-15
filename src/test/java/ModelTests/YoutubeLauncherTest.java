package ModelTests;

import org.junit.Test;

import static org.junit.Assert.*;

public class YoutubeLauncherTest {

    @Test
    public void findYoutubeLinkWhenSongExists() {
        //Arrange
        Song s = new Song
                .SongBuilder("")
                .album("meteora")
                .artist("Linkin Park")
                .title("In The End")
                .build();
        YoutubeLauncher yb = new YoutubeLauncher(s);
        //Act
        String result = yb.findYoutubeLink();
        //Assert
        assertNotNull(result);
    }

    @Test
    public void findYoutubeLinkWhenSongNotExists() {
        //Arrange
        Song s = new Song
                .SongBuilder("")
                .album("unknown")
                .artist("unknown")
                .title("1231asdazsdasdasd12w112312312312@!@##")
                .build();
        YoutubeLauncher yb = new YoutubeLauncher(s);
        //Act
        String result = yb.findYoutubeLink();
        //Assert
        assertNull(result);
    }


}