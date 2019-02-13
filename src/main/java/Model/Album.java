package Model;

import Controller.PlayerController;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.LinkedList;

public class Album extends ArtistAlbum{

//    pola
    private ChoiceBox<Integer> chooseYear;
    private TextArea JXdescription;
    private TextField JXartist;
    private TextField JXlabel;

    private int year;
    private String label;
    private String artist;
    private String description;

    public Album(String name, String image, int year, String label, String artist, String description) {
        super(name,image);
        this.year = year;
        this.label = label;
        this.artist = artist;
        this.description=description;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public void initData(AnchorPane pane) {
        super.initData(pane);
        System.out.println("Edycja albumu");

        JXdescription=new TextArea();
        JXdescription.setPromptText("Add description");
        JXdescription.setPrefWidth(460.);
        JXdescription.setWrapText(true);

        JXdescription.setText(this.getDescription());
        pane.setTopAnchor(JXdescription,40.);

        Label year=new Label("Released: ");
        LinkedList<Integer>years=new LinkedList<>();

        for(int i=1900;i<2020;i++){
            years.add(i);
        }

        chooseYear=new ChoiceBox<>(FXCollections.observableArrayList(years));
        chooseYear.setValue(this.year);


        pane.setTopAnchor(year,240.);
        pane.setTopAnchor(chooseYear,240.);
        pane.setLeftAnchor(chooseYear,140.);

        Label artistLabel=new Label("artist");
        JXartist=new JFXTextField(artist);
        pane.setTopAnchor(artistLabel,295.);
        pane.setTopAnchor(JXartist,290.);
        pane.setLeftAnchor(JXartist,140.);

        Label recordlabel=new Label("Record Label: ");
        JXlabel=new JFXTextField(label);
        pane.setTopAnchor(recordlabel,345.);
        pane.setTopAnchor(JXlabel,340.);
        pane.setLeftAnchor(JXlabel,140.);

        pane.getChildren().addAll(JXdescription,year,chooseYear,artistLabel,JXartist,recordlabel,JXlabel);

    }

    @Override
    public void save() {
        JDBCConnector.updateAlbum(image,Jname.getText(),chooseYear.getValue(),JXartist.getText(),JXdescription.getText(),JXlabel.getText(),name);

    }

}
