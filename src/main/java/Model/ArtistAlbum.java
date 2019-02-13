package Model;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;

public abstract class ArtistAlbum {
    protected String name;
    protected String image;
    JFXTextField Jname;
    ArtistAlbum(String name, String image) {
        this.name = name;
        this.image = image;
        if(image==null){
            this.image="/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/default.png";
        }

    }

    public void initData(AnchorPane anchorPane){
        Jname=new JFXTextField(name);
        Jname.setPrefWidth(464.);
        Jname.setAlignment(Pos.CENTER);
        anchorPane.getChildren().add(Jname);
    };
    public abstract void save();

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
