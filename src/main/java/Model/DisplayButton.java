package Model;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DisplayButton extends Button {

    public DisplayButton(String name) throws FileNotFoundException {
        super(name);
        this.setMinSize(160,240);
        this.setPrefSize(160,240);
        FileInputStream inputstream = new FileInputStream("/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/moods/workout.jpg");
        Image iv = new Image(inputstream);
        ImageView view=new ImageView(iv);
        view.setId("displayImage");
        view.setFitHeight(120);
        view.setFitWidth(150);
        view.setLayoutX(0);
        view.setLayoutY(0);
        this.getChildren().add(view);
        super.setGraphic(view);
        this.setId("displaybutton");
    }
}
