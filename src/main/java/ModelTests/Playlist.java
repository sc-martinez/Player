package ModelTests;

import javafx.scene.layout.AnchorPane;

import java.util.LinkedList;

public class Playlist extends ArtistAlbum {

    private String description;
    LinkedList<Song>songs=new LinkedList<>();


    public Playlist(String name, String image) {
        super(name, image);
    }

    @Override
    public void initData(AnchorPane anchorPane) {

    }


    @Override
    public void save() {

    }
}
