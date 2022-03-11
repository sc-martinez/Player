# Valoración de características de Clean Code 🧼 y prácticas XP 🚀

En este apartado se hará un análisis de las características Clean code de forma transversal a esta solución. 
Se determinará la brecha sobre el estado actual, identificando cuáles características se están cumpliendo, en cuáles se encuentran oportunidades de mejora y en respuesta cuáles prácticas XP nos permitirían reducir la brecha. 

## Código enfocado 👓

En Clean-code el código debería ser enfocado y tener un propósito específico en donde la simplicidad es un patrón común. 

Para el estado actual de la solución, se evidencia que a pesar de que las clases están bien divididas y en teoría los propósitos específicos están bien definidos, aún hay clases extensas en tamaño con multiples propósitos. 

### *Ejemplo # 1 JDBC Connector*
<details>

<summary>Desplegar ejemplo</summary>
<p>

#### Encontrado en Model/JDBCConnector.java
```java
package Model;

import com.mpatric.mp3agic.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static java.lang.Math.negateExact;
import static java.lang.Math.toIntExact;

public class JDBCConnector {


    private static Connection conn = null;

    public static void connect() throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/player", "postgres", "password");
    }

    public static void disconnect() throws SQLException {
        conn.close();
    }

    //add songs,when song with path exist in database, ignore this

    /**
     * @param files list of songs
     */

    public static void addSongs(List<File> files) {
        /*
        INSERT INTO songs
        1-title *required(id3v1/2 - title , else name of file)
        2-path *required
        3-length *required
        4-artist(id3v1/2)
        5-album(id3v1/2)
        6-track(id3v1/2)
        7-year (id3v1/2)
        8-text(id3v2)
         */
        for (File file : files) {
            System.out.println(file.getName());
            System.out.println(file.getAbsolutePath());
            Mp3File mp3file = null;
            String artist = null;
            String album = null;
            try {
                mp3file = new Mp3File(file.getAbsolutePath());
                if (mp3file.hasId3v2Tag()) {
                    System.out.println("id3v2");
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    add(id3v2Tag.getTitle(), file.getAbsolutePath(), mp3file.getLengthInSeconds(), id3v2Tag.getArtist(),
                            id3v2Tag.getAlbum(), id3v2Tag.getTrack(), id3v2Tag.getYear().substring(0,4), id3v2Tag.getLyrics());

                    artist = id3v2Tag.getArtist();
                    album = id3v2Tag.getAlbum();
                    if (id3v2Tag.getGenre() != -1) {
                        System.out.println("genres");
                        String genre = "";
                        switch (id3v2Tag.getGenre()) {
                            case 0:
                                genre = "blues";
                                break;
                            case 1:
                                genre = "classic rock";
                                break;
                            case 2:
                                genre = "country";
                                break;
                            case 9:
                                genre = "metal";
                                break;
                            case 13:
                                genre = "pop";
                                break;
                            case 14:
                                genre = "R&B";
                                break;
                            case 15:
                                genre = "rap";
                                break;
                            case 17:
                                genre = "rock";
                                break;
                            case 24:
                                genre = "soundtrack";
                                break;
                            case 32:
                                genre = "classical";
                                break;
                            case 20:
                                genre = "alternative";
                                break;

                        }
                        updateGenre(genre, file.getAbsolutePath());
                    }


                } else if (mp3file.hasId3v1Tag()) {
                    System.out.println("id3v1");
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();

                    artist = id3v1Tag.getArtist();
                    album = id3v1Tag.getAlbum();
                    add(id3v1Tag.getTitle(), file.getAbsolutePath(), mp3file.getLengthInSeconds(), id3v1Tag.getArtist(),
                            id3v1Tag.getAlbum(), id3v1Tag.getTrack(), id3v1Tag.getYear(), null);
                } else {
                    System.out.println("brak");
                    add(file.getName(), file.getAbsolutePath(), mp3file.getLengthInSeconds(), null,
                            null, null, null, null);
                }
                //Create row in Artist table
                if (artist != null) {
                    addArtist(artist);
                }
                //Create row in album table
                if (album != null) {
                    addAlbum(album);
                }
            } catch (SQLException | IOException | UnsupportedTagException | InvalidDataException e) {
                //do nothing
            }
        }
    }

    public static void addArtist(String artist) throws SQLException {
        String SQL = "INSERT INTO artist(name)VALUES(?)";
        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
        preparedStatement.setString(1, artist);
        preparedStatement.execute();
    }

    public static void addAlbum(String album) throws SQLException {
        String SQL = "INSERT INTO album(name)VALUES(?)";
        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
        preparedStatement.setString(1, album);
        preparedStatement.execute();
    }

    private static void add(String title, String path, long length, String artist, String album, String track, String year, String text) {
        String SQL = "INSERT INTO songs(title,path,length,artist,album,track,year,text)VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(SQL);
            preparedStmt.setString(1, title);
            preparedStmt.setString(2, path);
            preparedStmt.setInt(3, toIntExact(length));
            preparedStmt.setString(4, artist);
            preparedStmt.setString(5, album);
            preparedStmt.setString(6, track);
            preparedStmt.setString(7, year);
            preparedStmt.setString(8, text);
            preparedStmt.execute();

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    private static void updateGenre(String genre, String path) {
        String SQL = "UPDATE songs set genre=genre|| '{" + genre + "}'WHERE path=?";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(SQL);
            preparedStmt.setString(1, path);
            preparedStmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static String[] returnGenreMood(String path,String gm){
        ResultSet rs=null;
        Array genresmoods=null;
        String[] g=null;
        String SQL="SELECT "+gm+" from songs WHERE path=?";
        try {
            PreparedStatement preparedStatement=conn.prepareStatement(SQL);
            preparedStatement.setString(1,path);
            rs=preparedStatement.executeQuery();
            while (rs.next()) {
                genresmoods = rs.getArray(gm.toUpperCase());
                if(genresmoods!=null)
                    g = (String[]) genresmoods.getArray();
            }
        }catch (SQLException ex){
        }
        return g;
    }

    public static ObservableList<Song> returnSongs() throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs";
        return returndata(SQL);
    }

    public static ObservableList<Song> returnSongsByRegex(String regex) throws SQLException {
        regex = regex.toLowerCase();
        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs WHERE LOWER(title) LIKE '%" + regex + "%'OR " +
                "LOWER(artist) LIKE '%" + regex + "%' OR LOWER(album) LIKE '%" + regex + "%'";
        return returndata(SQL);
    }

    public static ObservableList<Song> returnSongsByMoodOrGenre(String regex) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs WHERE '" + regex + "'=ANY(moods) OR '" + regex +
                "'=ANY(genre)";
        return returndata(SQL);
    }

    private static ObservableList<Song> returndata(String SQL) throws SQLException {
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(SQL);

        ObservableList<Song> data =
                FXCollections.observableArrayList();
        try {
            while (rs.next()) {
                ObservableList<Song> row = FXCollections.observableArrayList();
                data.add(new Song.SongBuilder(rs.getString(7)).title(rs.getString(1)).artist(rs.getString(2)).
                        album(rs.getString(3)).year(rs.getString(4)).rate(rs.getInt(5)).
                        track(rs.getString(6)).text(rs.getString(8)).image(rs.getString(9)).build());
            }
        }catch (Exception ex){
            System.out.println("Return data   "+ ex.getMessage());
        }
        return data;
    }



/*====================================================
====================PLAYLIST==========================
======================================================
 */







    /*===================================================
    ======================ALBUM==========================
    =====================================================
     */
    public static ObservableList<Album> returnAlbums() throws SQLException {
        String SQL = "SELECT name,image,year,label,artist,description FROM album ";
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(SQL);
        ObservableList<Album> data =
                FXCollections.observableArrayList();
        while (rs.next()) {
            data.add(new Album(rs.getString(1),rs.getString(2),rs.getInt(3),
                    rs.getString(4),rs.getString(5),rs.getString(6)));
        }
        return data;
    }
    public static ObservableList<Song> returnByAlbum(String album) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs WHERE album='" + album + "'";
        return returndata(SQL);
    }
    public static void updateAlbum(String image,String name,int year,String artist,String description,String label,String oldname){
        System.out.println(oldname);
        System.out.println(image);
        String SQL="UPDATE album SET image=?,name=?,artist=?,year=?,description=?,label=? WHERE name=? ";
        try{
            PreparedStatement preparedStatement=conn.prepareStatement(SQL);
            preparedStatement.setString(1,image);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,artist);
            preparedStatement.setInt(4,year);
            preparedStatement.setString(5,description);
            preparedStatement.setString(6,label);
            preparedStatement.setString(7,oldname);
            preparedStatement.executeUpdate();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    public static String returnImage(String album){
        String SQL="SELECT image FROM album WHERE name=?";
        String a=null;
        try{
            PreparedStatement preparedStatement=conn.prepareStatement(SQL);
            preparedStatement.setString(1,album);
            ResultSet rs=preparedStatement.executeQuery();
            rs.next();
            a=rs.getString(1);
        }catch (Exception ex){
        }
        return a;
    }



    /*====================================================
    =======================ARTIST=========================
    ======================================================
     */
    public static ObservableList<Artist> returnArtists() throws SQLException {
        String SQL = "SELECT name,image,webstie,youtubewebsite,description FROM artist ";
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(SQL);

        ObservableList<Artist> data =
                FXCollections.observableArrayList();
        while (rs.next()) {
            data.add(new Artist(rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5)));
        }
        return data;
    }
    public static ObservableList<Song> returnByArtist(String artist) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs WHERE artist='" + artist + "'";
        return returndata(SQL);
    }
    public static void updateArtist(String image,String name,String website,String youtubewebsite,String description,String oldname){
        String SQL="UPDATE artist SET image=?,name=?,webstie=?,youtubewebsite=?,description=? WHERE name=?";
        try{
            PreparedStatement preparedStatement=conn.prepareStatement(SQL);
            preparedStatement.setString(1,image);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,website);
            preparedStatement.setString(4,youtubewebsite);
            preparedStatement.setString(5,description);
            preparedStatement.setString(6,oldname);
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

//============================================================
//=========================UPDATE SONG========================
//============================================================

    public static void updateSong(String title,String artist, String album,String[] genre,String[] moods,String text,String image,String path){
        String SQL="UPDATE songs SET title=?,artist=?,album=?,genre=?,moods=?,text=?,image=? WHERE path=?";

        try {
            PreparedStatement preparedStatement=conn.prepareStatement(SQL);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,artist);
            preparedStatement.setString(3,album);
            preparedStatement.setArray(4,conn.createArrayOf("text",genre));
            preparedStatement.setArray(5,conn.createArrayOf("text",moods));
            preparedStatement.setString(6,text);
            preparedStatement.setString(7,image);
            preparedStatement.setString(8,path);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
```
</details>

En este ejemplo se puede ver una clase con un proposito desenfocado, su próposito general es el de resolver el problema de persistencia en base de datos para todas las entidades de la solución.
A pesar de que el proposito pareciera estar bien definido, los detalles y particularidades de persistencia de cada una de las operaciones de cada entidad hacen que esta clase tenga demasiada responsabilidad, haciendola innecesariamente compleja, incumpliendo así, los principios SOLID.

### Recomendación XP 🚀

Para estos casos es recomendable usar la práctica XP de refactoring para segregar y segmentar el próposito de esta clase, en clases auxiliares con alcances bien definidos. 

</p>

## Regla del boy scout 👦🏻

Esta característica no se cumple, pues hasta este punto la solución ha sido construida por una sola persona (El autor original).
Una recomendación en el plan de refactorización sería mantener los principios de Clean Code despues de cada iteración, garantizando que el código fuente mejora en simplicidad, legibilidad y abstracción despues de cada modificación. 

### Recomendación XP 🚀

En caso de involucrar a un equipo de desarrollo en versiones posteriores, se recomienda usar estrategias XP cómo Pair-Programming o Peer reviewing, lo que evitará el sesgo por confirmación (El creador siempre cree que su construcción no pudo haberse hecho mejor) y garantizará la regla del boy scout en cada modificación (commit).


##Entendible 👨‍💻

En este segmento se evidencia buen manejo del lenguaje de dominio en el código fuente original, hay pocas funciones que no se lean naturalmente. 
Se detectan algunas oportunidades de mejora, cómo métodos largos que disminuyen la legibilidad.

### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

#### Encontrado en Model/Mp3player.java
```java
public boolean setCurrentSong(int i)throws NullPointerException{
        if(i<0)i=songs.size()-1;
        if(i>=songs.size())i=0;
        if(player!=null)player.stop();
        this.index=i;
        titleAndArtist.setText(songs.get(index).getTitle()+"\n"+songs.get(index).getArtist());
        Image image=new Image("file:"+songs.get(index).getImage());
        imageView=new ImageView(image);
        imageView.setFitWidth(64.);
        imageView.setFitHeight(64.);
        AnchorPane.setTopAnchor(imageView, 25.0);
        AnchorPane.setLeftAnchor(imageView, 50.0);
        anchorPane.getChildren().add(imageView);
        File file = new File(songs.get(index).getPath());
        boolean exists=file.exists();
        if(!exists){
            if(length==0)throw new  NullPointerException() ;
            length--;
            next();
            return false;
        }
        String path=file.toURI().toASCIIString();
        media = new Media(path);
        player = new MediaPlayer(media);
        notifyAllObservers(index);
        play();

        player.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                update();
            }
        });
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                duration = player.getMedia().getDuration();
                update();

            }
        });
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (!autoreplay)next();
                else {
                    setCurrentSong(index);
                }
            }
        });

        return true;
    }
```
</details>
</p>

### Recomendación XP 🚀

Una práctica XP que permitiría mantener este atributo de calidad vigente, sería la definición de un estándar de códificación para el proyecto. 
Esto permitiría mantener un patrón consistente de nombramiento de variables, clases, métodos. Allí se definirían los patrones de diseño a usar, la arquitectura general y definiría una línea base de implementación. 



## Duplicidad 👨‍💼👨‍💼
Bueno, aquí no hay mucho que explicar, por fuera de una clara violación al principio DRY (Do not repeat yourself).
Estos casos ya se habian tratado en el análisis anterior y se habian detectado cómo Code-Smells. 
### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Controller/editSongController.java - Model/Genres.java
```java
  @Override
public void initialize(URL url, ResourceBundle resourceBundle) {

        for(String name:Moods.moods.keySet()){
        moodsList.getItems().add(name);
        }
        for (String name: Genres.genres.keySet()){
        genresList.getItems().add(name);
        }
        moodsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

}
```
```java
package Model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Genres {
    public static Map<String ,String > genres=new LinkedHashMap<>();
    static {
        genres.put("rock", "images/genres/rock.jpg");
        genres.put("R&B", "images/genres/R&B.jpg");
        genres.put("country", "images/genres/country.jpeg");
        genres.put("alternative", "images/genres/alternative.jpg");
        genres.put("pop", "images/genres/pop.jpg");
        genres.put("musical", "images/genres/musical.jpg");
        genres.put("classic rock", "images/genres/classicrock.jpg");
        genres.put("blues", "images/genres/blues.jpg");
        genres.put("classical", "images/genres/classical.jpg");
        genres.put("electronic", "images/genres/electronic.jpg");
        genres.put("jazz", "images/genres/jazz.jpg");
        genres.put("latin", "images/genres/latin.jpg");
        genres.put("rap", "images/genres/rap.jpg");
        genres.put("soundtrack", "images/genres/soundtrack.jpg");
        genres.put("metal", "images/genres/metal.jpg");
        genres.put("indie", "images/genres/indie.jpg");
    }
}

```
</details></p>

### Recomendación XP 🚀
Las prácticas XP que nos permiten mitigar el código duplicado, son las relacionadas con Diseño Simple (Simple design). Pues al tener clases y métodos cortos y simples abrimos la puerta a la reutilización de funcionalidades. 

Una constante aplicación de técnicas de refactoring y estrategias de Pair Programming, permitirán tener múltiples visiones en la implementación y evidenciar puntos de repetición que pueden ser abstraidos, generalizados y llevados a métodos o clases de propósito general. 

## Abstracción ⚪ 🟥 🔺
En el apartado anterior se habian identificado algunos errores de abstracción relacionados con herencia de clases.


### *Ejemplo # 1*
<details><summary>Desplegar ejemplo</summary>
<p>

#### Encontrado en Model/Album.java - Model/Artist.java - Model/ArtistAlbum.java - Controller/EditArtistOrAlbum.java
```java
public class Album extends ArtistAlbum{}
 
public class Artist extends ArtistAlbum {}

 public abstract class ArtistAlbum {
     protected String name;
     protected String image;
     ......
 }
 
public class EditArtistOrAlbum implements Initializable {
    .....
}
```
</details></p>
Por la implementación de la lógica en el código fuente, se observa la abstracción de las cáracteristicas Nombre e imagen, que es compartida por las entidades Album y Artista, en una clase superior nombrada ArtistaAlbum, lo que causa problemas de semántica.

A pesar de que comparten estas cáracteristicas se debe trabajar en la verbosidad y revisar estrategias para representar esta misma abstracción de forma más limpia.


Esto es una violación a los principios de Interface Segregation y Liskov Substitution Principle.


### *Ejemplo #2*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Model/DisplayArtistAlbum.java - Model/Genres.java
```java
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
```
</details></p>
En este ejemplo de una clase que debería pertenecer a otro paquete, toda su lógica es de peresentación, pero su implementación se encuentra en el paquete de Modelos (Persistencia ? ). 
Cumple dos própositos, por lo que viola el principio solid de Single Responsability.


### *Ejemplo #3*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Controller/PlayerController.java - Model/Genres.java
```java
private void loadArtists(){

        ObservableList<Artist>artists=null;
        try {
            artists=JDBCConnector.returnArtists();
            if(artists!=null){
                AAView.getItems().clear();
                for(Artist artist:artists){
                    AAView.getItems().add(new DisplayArtistAlbum(artist,1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AAView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                    String[] parts = AAView.getSelectionModel().getSelectedItem().getAccessibleText().split("\\|");
                    System.out.println(parts[1]);
                    displayPlaylistPane(2,parts[0],parts[1]);
            }
        });
    }
```
</details></p>
Esta es una evidencia de una violación al principio de Dependency Inversion, el controlador del reproductor dependen de la implementación concreta de artista y de album, cuando se ilustró en este mismo apartado que ambas comparten una superclase ArtistAlbum, lo que evidencia que hay un problema de generalización en estas dos entidades. 

### Recomendación XP 🚀
Una aproximación por TDD, con una aplicación constante de refactorización y una postura obsesiva de pruebas apoyaría a la gestión e identificación de la deuda en abstracción. 

El diseño simple tiende a disminuir está deuda al máximo, pues no recae en la sobre-ingeniería o en la sobre generalización, un enfoque así es necesario para esta solución. 



## Testeable 🧪 👁️‍🗨️

La solución actual no es testeable de forma automática, sólo de forma funcional. Por lo que esta característica de Clean Code no se cumple.  

### Recomendación XP 🚀

Se recomienda implementar una suite de pruebas automatizadas para la solución. Se recomienda implementar pruebas unitarias siguiendo los principios de implementación de pruebas por comportamientos AAA ( Arrange, Act, Assert).

## Principio de menor asombro 😲 ❌
En este principio las implementaciones deberían estar en función de su nombre. 

### *Ejemplo # 1*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Controller/editSongController.java
```java
package Controller;

import Model.Genres;
import Model.JDBCConnector;
import Model.Moods;
import Model.Song;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class editSongController implements Initializable {
@FXML
private JFXTextField titleSong,artistSong,albumSong;

    @FXML
    private ListView<String>genresList,moodsList;
    @FXML
    private ImageView imageSong;
    @FXML
    private JFXTextArea lirycsSong;
    @FXML
    private Button saveButton;
    private String path="";
    private Song s;
    void initData(Song s) {
        this.s=s;
        path=s.getPath();
        List<String> genreList = new LinkedList<String>(Genres.genres.keySet());
        List<String> moodList = new LinkedList<String>(Moods.moods.keySet());
        titleSong.setText(s.getTitle());
        artistSong.setText(s.getArtist());
        albumSong.setText(s.getAlbum());
        lirycsSong.setText(s.getText());

        String[]genres=JDBCConnector.returnGenreMood(s.getPath(),"genre");
        if(genres!=null) {
            for (String genre : genres) {
                int index = genreList.indexOf(genre);
                genresList.getSelectionModel().select(index);
            }
        }
        String[]moods=JDBCConnector.returnGenreMood(s.getPath(),"moods");
        if(moods!=null) {
            for (String mood : moods) {
                int index = moodList.indexOf(mood);
                moodsList.getSelectionModel().select(index);
            }
        }
        Image image=new Image("file:"+s.getImage());
        imageSong.setImage(image);
        imageSong.setStyle("-fx-cursor: hand");
        imageSong.setOnMouseClicked((MouseEvent event)->{
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image","*.png","*.jpeg","*.jpg"));
                File file=fileChooser.showOpenDialog(new Stage());
                String path=file.getAbsolutePath();
                FileInputStream inputstream =new FileInputStream(path);
                Image iv = new Image(inputstream);
                s.setImage(path);
                imageSong.setImage(iv);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(String name:Moods.moods.keySet()){
            moodsList.getItems().add(name);
        }
        for (String name: Genres.genres.keySet()){
            genresList.getItems().add(name);
        }
        moodsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }
    @FXML
    private void save(){
        String[]genres= genresList.getSelectionModel().getSelectedItems().toArray(new String[0]);
        String[]moods=moodsList.getSelectionModel().getSelectedItems().toArray(new String[0]);
        JDBCConnector.updateSong(titleSong.getText(),artistSong.getText(),albumSong.getText(),genres,moods,
               lirycsSong.getText(),s.getImage(),path );
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void find_lyrics(){
       s.findLyrics(lirycsSong);
    }
}
```
</details></p>
En este ejemplo vemos que la implementación del método Save, realmente realiza una actualización de las canciones y realiza operaciones en la ventana actual. 
Aquí el nombre no se relaciona con la acción final y se está cumpliendo más de un propósito en la implementación. 

### Recomendación XP 🚀
Se recomienda hacer uso del estándar de codificación, recaer en los principios del diseño simple y realizar operaciones de refactorización acompañados de pruebas unitarias en cada iteración. 


## Escalable 📐
En general se hace uso de los principios POO y el lenguaje de dominio se utiliza bien. A lo largo de este apartado se han mostrado violaciones a los principios SOLID y DRY cómo oportunidades de mejora inmediatas. 

Se identificaron dos clases Genres y Moods que podrían ser datos de configuración de la solución. 



### *Ejemplo # 1*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Controller/Genres.java
```java
package Model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Genres {
    public static Map<String ,String > genres=new LinkedHashMap<>();
    static {
        genres.put("rock", "images/genres/rock.jpg");
        genres.put("R&B", "images/genres/R&B.jpg");
        genres.put("country", "images/genres/country.jpeg");
        genres.put("alternative", "images/genres/alternative.jpg");
        genres.put("pop", "images/genres/pop.jpg");
        genres.put("musical", "images/genres/musical.jpg");
        genres.put("classic rock", "images/genres/classicrock.jpg");
        genres.put("blues", "images/genres/blues.jpg");
        genres.put("classical", "images/genres/classical.jpg");
        genres.put("electronic", "images/genres/electronic.jpg");
        genres.put("jazz", "images/genres/jazz.jpg");
        genres.put("latin", "images/genres/latin.jpg");
        genres.put("rap", "images/genres/rap.jpg");
        genres.put("soundtrack", "images/genres/soundtrack.jpg");
        genres.put("metal", "images/genres/metal.jpg");
        genres.put("indie", "images/genres/indie.jpg");
    }
}

```

### Encontrado en Controller/Moods.java
```java
package Model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Moods {
    public static Map <String ,String > moods=new LinkedHashMap<>();
    static {
        moods.put("chill", "images/moods/chill.jpg");
        moods.put("dinner", "images/moods/dinner.jpeg");
        moods.put("focus", "images/moods/focus.jpg");
        moods.put("happy", "images/moods/happy.jpg");
        moods.put("meditation", "images/moods/meditation.jpeg");
        moods.put("party", "images/moods/party.jpeg");
        moods.put("relax", "images/moods/relax.jpg");
        moods.put("sad", "images/moods/sad.jpg");
        moods.put("sleep", "images/moods/sleep.jpeg");
        moods.put("summer", "images/moods/summer.jpg");
        moods.put("workout", "images/moods/workout.jpg");
    }


}

```
</details></p>

Se recomienda incluir los datos de estas dos clases en una migración de base de datos, definiendo que ambas pueden tener un nombre y una imagen asociada cómo atributos destacables.

### Recomendación XP 🚀
Se recomienda hacer uso del estándar de codificación, recaer en los principios del diseño simple y realizar operaciones de refactorización acompañados de pruebas unitarias en cada iteración. 



