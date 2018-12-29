package Model;

public class Album extends ArtistAlbum{

    private int year;
    private String label;
    private String artist;

    public Album(String name, String image, int year, String label, String artist) {
        super(name,image);
        this.year = year;
        this.label = label;
        this.artist = artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
