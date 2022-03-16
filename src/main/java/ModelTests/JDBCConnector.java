package ModelTests;

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
                    System.out.println(id3v2Tag.toString());
                    add(id3v2Tag.getTitle(), file.getAbsolutePath(), mp3file.getLengthInSeconds(), id3v2Tag.getArtist(),
                            id3v2Tag.getAlbum(), id3v2Tag.getTrack(), (id3v2Tag.getYear() != null ) ? id3v2Tag.getYear().substring(0,4) : "1970" , id3v2Tag.getLyrics());

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



