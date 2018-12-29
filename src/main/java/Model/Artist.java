package Model;

import java.sql.Array;
import java.sql.SQLException;

public class Artist extends ArtistAlbum {

    private String[]members;
    private String[]pastmembers;
    private String website;
    private String youtubewebsite;
    private String description;

    public Artist(String name, String image, Array members, Array pastmembers, String website, String youtubewebsite, String description) {
        super(name,image);
        try {
            this.members = (String[]) members.getArray();
            this.pastmembers = (String[])pastmembers.getArray();
        } catch (SQLException | NullPointerException e) {
            System.out.println("nieudana konwersja");
        }
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

    public String[] getPastmembers() {
        return pastmembers;
    }

    public void setPastmembers(String[] pastmembers) {
        this.pastmembers = pastmembers;
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
}
