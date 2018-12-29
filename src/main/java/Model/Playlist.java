package Model;

import java.util.LinkedList;

public class Playlist extends ArtistAlbum {

    private String description;
    LinkedList<Song>songs=new LinkedList<>();


    public Playlist(String name, String image) {
        super(name, image);
    }
}
