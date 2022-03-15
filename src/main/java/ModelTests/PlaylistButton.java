package ModelTests;


import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlaylistButton extends Button {


    public PlaylistButton(String name) throws FileNotFoundException {
        this.setMinWidth(220);

        this.setHeight(80);
        FileInputStream inputstream = new FileInputStream("/home/dell/Obrazy/pulpit/marvel.jpg");
        Image iv = new Image(inputstream);
        ImageView view=new ImageView(iv);
        view.setFitHeight(80);
        view.setFitWidth(80);
        view.setLayoutX(0.);
        view.setLayoutY(0.);
        this.getChildren().add(view);
        this.setText("  "+name);
        this.setStyle("-fx-alignment: top-left");

        super.setGraphic(view);
    }
}