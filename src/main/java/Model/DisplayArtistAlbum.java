package Model;

import Controller.EditArtistOrAlbum;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DisplayArtistAlbum extends AnchorPane {

    public DisplayArtistAlbum(ArtistAlbum a,int i)  {
        super();
        this.setMinSize(850,140);
        this.setPrefSize(850,140);
        FileInputStream inputstream = null;
        Image iv=null;
        ImageView view=null;
        try {
            inputstream = new FileInputStream(a.getImage());
            iv = new Image(inputstream);
        } catch (FileNotFoundException | NullPointerException e) {
            iv=new Image("file:/home/dell/Dokumenty/AGH/Java/Player/src/main/resources/images/default.png");
        }

        view=new ImageView(iv);
        view.setId("displayImage");
        view.setFitHeight(132);
        view.setFitWidth(132);

        this.getChildren().add(view);
        this.setId("displaybutton");
        view.setTranslateX(0);
        view.setTranslateX(0);
        Label name=new Label(a.getName());
        this.getChildren().add(name);
        name.setPrefHeight(100);
        name.setMaxWidth(300);
        name.setTranslateX(200);
        name.setTranslateY(20);

        Button button=new Button("Edit");
        button.setPrefSize(200,100);
        button.setTranslateX(600);
        button.setTranslateY(20);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showEditArtistAlbumWindow(a,i);
            }

        });
        this.getChildren().add(button);
        this.setAccessibleText(a.getName()+"|"+a.getImage());


    }
    Stage showEditArtistAlbumWindow(ArtistAlbum a,int i) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/editArtistAlbum.fxml"
                )
        );

        Stage stage = new Stage(StageStyle.DECORATED);
        try {
            stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditArtistOrAlbum controller =
                loader.<EditArtistOrAlbum>getController();
        controller.initData(a);

        stage.show();


        stage.show();

        return stage;
    }
}
