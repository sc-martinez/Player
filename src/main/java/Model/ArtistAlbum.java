package Model;

public class ArtistAlbum {
    protected String name;
    protected String image;

    public ArtistAlbum(String name, String image) {
        this.name = name;
        this.image = image;
        if(image==null){
            this.image="/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/default.png";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
