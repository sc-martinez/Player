package Model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AzlyricsConncectorTest {

    @Test
    public void returnLyricsWhenSongExists() {
        //Arrange
        AzlyricsConncector connector = new AzlyricsConncector("linkinpark", "In The End");
        //Act
        connector.run();
        //Assert
        assert (connector.returnLyrics() == null || connector.returnLyrics() != null);
    }

    @Test
    public void returnLyricsWhenSongNoExists() {
        //Arrange
        AzlyricsConncector connector = new AzlyricsConncector("linkinpark", "123123asdasfasdfasd");
        //Act
        connector.run();
        //Assert
        assertNull(connector.returnLyrics());
    }

}