# Deuda técnica en el código fuente - Identificando Code-Smells 💩

# _Bloaters_

## # Métodos largos 🍝
Métodos mayores a diez (10) líneas de código, normalmente en estilo spaguetti 🍝 (Yummi!).

### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

#### Encontrado en Controller/editSongController.java
```java
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
```

</details></p>

### Reporte de ocurrencias - Top 10

|  Paquete   |       Clase        |   Método   |        Líneas        |
|:----------:|:------------------:|:-----------:|:--------------------:|
| Controller | editSongController |   initData  |  Line 42 -  Line 85  |
| Controller |  PlayerController  |   handleButton  | Line 105 -  Line 140 |
| Controller |  PlayerController  |   loadArtists  | Line 157 -  Line 179 |
| Controller |  PlayerController  |   updateTable  | Line 299 -  Line 338 |
| Controller |  PlayerController  |   displayPlaylistPane  | Line 391 -  Line 441 |
| Controller |  PlayerController  |   autoreplay  | Line 464 -  Line 490 |
|   Model    |       Album        |   initData  | Line 67 -  Line 108  |
|   Model    |   JDBCConnector    |   addSongs  | Line 37 -  Line 133  |
|   Model    |     Mp3Player      |   loadBar  |  Line 42 -  Line 92  |


## # Clase Enorme 🏰
Clases que tienen demasiadas responsabilidades, normalmente evidencia de baja cohesión. 

### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
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
                            id3v2Tag.getAlbum(), id3v2Tag.getTrack(), id3v2Tag.getYear().substring(0, 4), id3v2Tag.getLyrics());

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

    public static String[] returnGenreMood(String path, String gm) {
        ResultSet rs = null;
        Array genresmoods = null;
        String[] g = null;
        String SQL = "SELECT " + gm + " from songs WHERE path=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, path);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                genresmoods = rs.getArray(gm.toUpperCase());
                if (genresmoods != null)
                    g = (String[]) genresmoods.getArray();
            }
        } catch (SQLException ex) {
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
        } catch (Exception ex) {
            System.out.println("Return data   " + ex.getMessage());
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
            data.add(new Album(rs.getString(1), rs.getString(2), rs.getInt(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)));
        }
        return data;
    }

    public static ObservableList<Song> returnByAlbum(String album) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track,path,text,image from songs WHERE album='" + album + "'";
        return returndata(SQL);
    }

    public static void updateAlbum(String image, String name, int year, String artist, String description, String label, String oldname) {
        System.out.println(oldname);
        System.out.println(image);
        String SQL = "UPDATE album SET image=?,name=?,artist=?,year=?,description=?,label=? WHERE name=? ";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, image);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, artist);
            preparedStatement.setInt(4, year);
            preparedStatement.setString(5, description);
            preparedStatement.setString(6, label);
            preparedStatement.setString(7, oldname);
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String returnImage(String album) {
        String SQL = "SELECT image FROM album WHERE name=?";
        String a = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, album);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            a = rs.getString(1);
        } catch (Exception ex) {
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

    public static void updateArtist(String image, String name, String website, String youtubewebsite, String description, String oldname) {
        String SQL = "UPDATE artist SET image=?,name=?,webstie=?,youtubewebsite=?,description=? WHERE name=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, image);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, website);
            preparedStatement.setString(4, youtubewebsite);
            preparedStatement.setString(5, description);
            preparedStatement.setString(6, oldname);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//============================================================
//=========================UPDATE SONG========================
//============================================================

    public static void updateSong(String title, String artist, String album, String[] genre, String[] moods, String text, String image, String path) {
        String SQL = "UPDATE songs SET title=?,artist=?,album=?,genre=?,moods=?,text=?,image=? WHERE path=?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setString(3, album);
            preparedStatement.setArray(4, conn.createArrayOf("text", genre));
            preparedStatement.setArray(5, conn.createArrayOf("text", moods));
            preparedStatement.setString(6, text);
            preparedStatement.setString(7, image);
            preparedStatement.setString(8, path);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
```
</details></p>

### Reporte de ocurrencias - Top 10

|  Paquete   |      Clase       |
|:----------:|:----------------:|
|   Model    |  JDBCConnector   |
|   Model    |       Song       |
| Controller | PlayerController |

## # Métodos con Gran cantidad de párametros 🎛️
Métodos con una innecesaria cantidad de parametros, más de tres o cuatro cómo norma general de normalidad. 
### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

#### Encontrado en Model/JDBCConnector.java
```java
  private static void add(String title, String path, long length, String artist, String album, String track, String year, String text){
        String SQL="INSERT INTO songs(title,path,length,artist,album,track,year,text)VALUES(?,?,?,?,?,?,?,?)";
        try{
        PreparedStatement preparedStmt=conn.prepareStatement(SQL);
        preparedStmt.setString(1,title);
        preparedStmt.setString(2,path);
        preparedStmt.setInt(3,toIntExact(length));
        preparedStmt.setString(4,artist);
        preparedStmt.setString(5,album);
        preparedStmt.setString(6,track);
        preparedStmt.setString(7,year);
        preparedStmt.setString(8,text);
        preparedStmt.execute();

        }catch(SQLException e){
        System.out.println(e.toString());
        }
  }
```
</details></p>

### Reporte de ocurrencias - Top 10
| Paquete |       Clase        | Método |
|:-------:|:------------------:|:------:|
|  Model  | JDBCConnector |  add   |


## # Primitive Obsession 🐒
El uso de constantes o tipos primitivos para decodificar información estructurada. 
### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Model/JDBCConnector.java, notar el uso de la variable i
```java
/**
     *
     * @param i 1-all songs 2,regex 3-moods or genres
     * @param regex regex or mood(genre) if i=1 regex=null
     * @throws SQLException
     */
    private void updateTable(int i,String regex) throws SQLException {

        try {
            ObservableList<Song> data=null;
            switch (i){
                case 1:
                    data= JDBCConnector.returnSongs();
                    break;
                case 2:
                    displaySongs.toFront();
                    data=JDBCConnector.returnSongsByRegex(regex);
                    break;
                case 3:
                    displaySongs.toFront();
                    data=JDBCConnector.returnSongsByMoodOrGenre(regex);
                    break;
            }
            tableOfSongs.setItems(data);

            tableOfSongs.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {
                    if(click.getButton()== MouseButton.SECONDARY){
                        editSong(tableOfSongs.getSelectionModel().getSelectedItem());
                    }else if(click.getClickCount()==2){
                        songs.clear();
                        for(Song d:tableOfSongs.getItems()){
                            songs.add(d);
                        }mp3player.loadSongs(songs);
                            try {
                                musicBar.setVisible(true);
                                additionalInfo.setText("");
                                mp3player.setCurrentSong(tableOfSongs.getSelectionModel().getFocusedIndex());
                            }catch (NullPointerException ex){
                                additionalInfo.setText("Songs not found");
                            }

                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
```
</details></p>

### Reporte de ocurrencias - Top 10

|  Paquete   |       Clase        |   Método   |
|:----------:|:------------------:|:-----------:|
| Controller |  PlayerController  |   updateTable  | 
| Controller |  PlayerController  |   displayPlaylistPane  | 
|   Model    |  JDBCConnector  |   addSongs  | 


# Object-Orientation Abusers
## # Switch Statements 🔃
Sequenciación de `if` statements o `Switch-Case`. Normalmente evidencia de abstracciones pobres o de comportamientos múltiples mal manejados.

### *Ejemplo*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Model/PlayerController.java
```java
public void handleButton(ActionEvent event){

        if(event.getTarget()==musicbutton){
        fadeOut(musicPane,moviePane);
        movieMenu.setVisible(false);
        musicMenu.setVisible(true);
        mainMusicPane.toFront();
        }
        else if(event.getTarget()==moviebutton){
        fadeOut(moviePane,musicPane);
        movieMenu.setVisible(true);
        musicMenu.setVisible(false);
        }
        else if(event.getTarget()==exit){
        Platform.exit();
        System.exit(0);
        }
        else if(event.getTarget()==displayAlbums){
        loadAlbums();
        displayAlbumsArtists.toFront();
        }
        else if(event.getTarget()==displayArtists){
        loadArtists();
        displayAlbumsArtists.toFront();

        }
        else if(event.getTarget()==displaySongsButton){
        displaySongs.toFront();
        try {
        updateTable(1,null);
        } catch (SQLException e) {
        e.printStackTrace();
        }
        
    }
}
```
</details></p>

### Reporte de ocurrencias - Top 10

|  Paquete   |       Clase        |   Método   |
|:-------:|:------------------:|:------:|
|  Controller  | PlayerController |  handleButton   |
|  Controller  | PlayerController |  updateTable   |
|  Controller  | PlayerController |  displayPlaylistPane   |
|  Controller  | PlayerController |  handleButton   |
|  Controller  | JDBCConnector |  addSongs   |
|  Controller  | PlayerController |  setCurrentSong   |

## # Refused Bequest 🐈=?🪑
Discrepancias en la lógica de clases que implementan superclases, fallas en la lógica de la abstracción de la superclase.
Común de verse discrepancias semánticas, ¿ El gato hereda el comportamiento de la silla, sólo porque tiene patas ? 

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
Por la implementación de la lógica en el código fuente, se observa la abstracción de las cáracteristicas Nombre e imagen, que es compartida por las entidades Album y Artista, en una clase superior nombrada ArtistaAlbum, lo que causa problemas de semántica. 

A pesar de que comparten estas cáracteristicas se debe trabajar en la verbosidad y revisar estrategias para representar esta misma abstracción de forma más limpia. 
</details></p>

### *Ejemplo #2*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Model/AzlyricsConncector y Model/LyricsConnector

```java
package Model;

public abstract class LyricsConnector {

    //    find url of web on Google
    abstract protected String findWeb();

    abstract protected void setText(String url);
}
```

```java
package Model;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AzlyricsConncector extends LyricsConnector implements Runnable {

    private String URL;
    private String artist;
    private String title;
    private String text;

    public AzlyricsConncector(String artist, String title) {
        this.artist = artist;
        this.title = title;
        this.URL = "azlyrics";
    }


    @Override
    protected String findWeb() {
        String key = "AIzaSyDbdxrTsdm5pgTHnHJKHV9XPEuwv6IaOjg";
        String qry = "azlyrics/" + artist.toLowerCase().replaceAll(" ", "") + "/" + title.toLowerCase().replaceAll(" ", "");
        System.out.println(qry);
        try {
            URL url = new URL(
                    "https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=013036536707430787589:_pqjad5hr1a&q=" + qry + "&alt=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {

                if (output.contains("\"link\": \"")) {
                    String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(), output.indexOf("\","));
                    if (link.matches("^https://www.azlyrics.com/lyrics/.*")) {
                        return link;
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    protected void setText(String _url) {
        Pattern pattern = Pattern.compile("<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->(.*?)</div><br><br><!-- MxM banner -->");
        StringBuilder html = new StringBuilder();
        try {
            URL url = new URL(_url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String htmlString = html.toString();
        Matcher matcher = pattern.matcher(htmlString);
        if (matcher.find()) {
            text = matcher.group(1).replaceAll("<br>", "\n");
            text = text.replaceAll("<i>", " ");
            text = text.replaceAll("</i>", " ");
        }
    }

    public String returnLyrics() {
        System.out.println("hmh\n" + text);
        return text;
    }


    @Override
    public void run() {
        String link = findWeb();
        if (link != null) {
            setText(link);
        } else {
            System.out.println("puste");
        }
    }
}

```
Esta herencia es un claro ejemplo de la diferencia entre una interface y una clase abstracta: 
Si el comportamiento de 'LyricsConnector' es solamente por medio de métodos, entonces no debería ser una clase abstracta. 
</details></p>


## # Código duplicado 👥️️
Bueno, aquí no hay mucho que explicar, por fuera de una clara violación al principio DRY (Do not repeat yourself).

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

import java.util.LinkedHashMap;
import java.util.Map;

public class Genres {
    public static Map<String, String> genres = new LinkedHashMap<>();

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

## # Cadenas de conexión cómo parte del código fuente
Por varias razones, muchas relacionadas con seguridad de la información, las credenciales y los detalles de las cadenas de conexión no deben estar expuestas cómo parte de la solución o código fuente. 

Se recomienda utilizar mecanismos de inyección de estas propiedades en tiempo de construcción. 

### *Ejemplo # 1*
<details><summary>Desplegar ejemplo</summary>
<p>

### Encontrado en Model/JDBCConnector.java
```java
public class JDBCConnector {


    private static Connection conn = null;

    public static void connect() throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/player", "postgres", "password");
    }
```

### Encontrado en Model/AzlyricsConncector.java
```java
public class AzlyricsConncector extends LyricsConnector implements Runnable{

    private String URL;
    private String artist;
    private String title;
    private String text;

    public AzlyricsConncector(String artist,String title){
        this.artist=artist;
        this.title=title;
        this.URL="azlyrics";
    }


    @Override
    protected String  findWeb() {
        String key="AIzaSyDbdxrTsdm5pgTHnHJKHV9XPEuwv6IaOjg";
        String qry="azlyrics/"+artist.toLowerCase().replaceAll(" ","")+"/"+title.toLowerCase().replaceAll(" ","");
        System.out.println(qry);
        ...
    }
```

</details></p>

## # Pocos Comentarios 💬
En general a lo largo del proyecto se ven muy pocos comentarios dicientes en medio de la implementación. 
Gran parte de la deuda técnica en este aspecto, es la ausencia de comentarios y de información general de instalación. 



# Resultados
Se detectan dos clases que acumulan gran parte de la deuda técnica de código fuente, estas son `JDBCConnector` y `PlayerController` que concentrarán la mayor parte de los esfuerzos de refactorización.

En general se encuentran falencias en los principios SOLID, encapsulamiento y segregación de responsabilidades. 

Se recomiendan aplicar las estrategias de composición de métodos y movilización de características: 
`Extract Method`
`Replace Method with Method Object`
`Extract Class`
`Replace Temp with Query`
por mencionar algunas. 

Se recomienda crear una suite de pruebas unitarias previo a cualquier actividad de refactorización. 

