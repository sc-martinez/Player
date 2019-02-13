package Model;

import Controller.PlayerController;
import View.Player;
import javafx.scene.layout.AnchorPane;

import java.sql.Array;
import java.sql.SQLException;

public class Artist extends ArtistAlbum {

    private String[]members;
    private String website;
    private String youtubewebsite;
    private String description;

    public Artist(String name, String image, String website, String youtubewebsite, String description) {
        super(name,image);

        this.website = website;
        this.youtubewebsite = youtubewebsite;
        this.description = description;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getYoutubewebsite() {
        return youtubewebsite;
    }

    public void setYoutubewebsite(String youtubewebsite) {
        this.youtubewebsite = youtubewebsite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void initData(AnchorPane pane) {
        super.initData(pane);
        System.out.println("Edycja artysty");



    }

    @Override
    public void save() {

    }
}
