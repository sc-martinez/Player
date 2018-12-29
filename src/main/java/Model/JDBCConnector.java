package Model;

import com.mpatric.mp3agic.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

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
                            id3v2Tag.getAlbum(), id3v2Tag.getTrack(), id3v2Tag.getYear(), id3v2Tag.getLyrics());

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

    public static ObservableList<Song> returnSongs() throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track from songs";
        return returndata(SQL);
    }

    public static ObservableList<Song> returnSongsByRegex(String regex) throws SQLException {
        regex = regex.toLowerCase();
        String SQL = "Select title,artist,album,year,rate,track from songs WHERE LOWER(title) LIKE '%" + regex + "%'OR " +
                "LOWER(artist) LIKE '%" + regex + "%' OR LOWER(album) LIKE '%" + regex + "%'";
        return returndata(SQL);
    }

    public static ObservableList<Song> returnSongsByMoodOrGenre(String regex) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track from songs WHERE '" + regex + "'=ANY(moods) OR '" + regex +
                "'=ANY(genre)";
        return returndata(SQL);
    }

    private static ObservableList<Song> returndata(String SQL) throws SQLException {
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(SQL);

        ObservableList<Song> data =
                FXCollections.observableArrayList();
        while (rs.next()) {
            ObservableList<Song> row = FXCollections.observableArrayList();
            data.add(new Song(rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getInt(5), rs.getString(6)));
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
    String SQL = "SELECT name,image,year,label,artist FROM album ";
    ResultSet rs = null;
    Statement stmt = conn.createStatement();
    rs = stmt.executeQuery(SQL);
    ObservableList<Album> data =
            FXCollections.observableArrayList();
    while (rs.next()) {
        data.add(new Album(rs.getString(1),rs.getString(2),rs.getInt(3),
                rs.getString(4),rs.getString(5)));
    }
    return data;
}
    public static ObservableList<Song> returnByAlbum(String album) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track from songs WHERE album='" + album + "'";
        return returndata(SQL);
    }


/*====================================================
=======================ARTIST=========================
======================================================
 */
    public static ObservableList<Artist> returnArtists() throws SQLException {
        String SQL = "SELECT name,image,members,pastmembers,webstie,youtubewebsite,description FROM artist ";
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(SQL);

        ObservableList<Artist> data =
                FXCollections.observableArrayList();
        while (rs.next()) {
            data.add(new Artist(rs.getString(1), rs.getString(2), rs.getArray(3),
                    rs.getArray(4), rs.getString(5), rs.getString(6), rs.getString(7)));
        }
        return data;
    }
    public static ObservableList<Song> returnByArtist(String artist) throws SQLException {

        String SQL = "Select title,artist,album,year,rate,track from songs WHERE artist='" + artist + "'";
        return returndata(SQL);
    }
}
