package api;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import java.sql.*;

import java.util.*;

import java.io.*;

public class DBManager {
    private final String JDBC_URL = "";
    private final String DB_PASSWORD = "";

    private final String username;
    private String password;

    public DBManager(String username) {
        this.username = username;
    }

    public DBManager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Attempt to register the given user
    public boolean register() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection conn = null;
        // Connect to our AWS RDS DB instance
        try {
            String query;

            // We first check if the username already exists. If it does, the username cannot be re-registered. We'll prompt the user.
            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            query = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);

            ResultSet resultSet = preparedStatement.executeQuery();
            // If the query result returns something, it means the username is not available.
            if (resultSet.next()) {
                return false;
            }

            // Generate a salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Use salt and hash the password
            KeySpec spec = new PBEKeySpec(this.password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            String hash = Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());

            String storedSalt = Base64.getEncoder().encodeToString(salt);

            // Insert the newly registered user into our database
            query = "INSERT INTO Users (username, password, salt) VALUES (?, ?, ?)";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, hash);
            preparedStatement.setString(3, storedSalt);
            preparedStatement.executeUpdate();

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println(ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }

    // Attempt to authenticate the given user
    public boolean authenticate() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection conn = null;
        try {
            String query;

            // We'll fetch the username from the database. If the username doesn't exist, then we'll prompt the user.
            // Assuming it does exist, we'll compare the given password and the hashed password in the database. If both match, the user is successfully authenticated.
            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            query = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);

            ResultSet resultSet = preparedStatement.executeQuery();
            // If the query result does not return something, then it means there is no registered account under that username.
            if (!resultSet.next()) {
                return false;
            }

            // Retrieve stored salt from DB
            byte[] storedSalt = Base64.getDecoder().decode(resultSet.getString("salt"));
            byte[] storedPassword = Base64.getDecoder().decode(resultSet.getString("password"));

            // Use stored salt and hash the password
            KeySpec spec = new PBEKeySpec(this.password.toCharArray(), storedSalt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // If the hashed password stored in the database doesn't match we the hashed user given password, then authentication has failed.
            if (!Arrays.equals(hash, storedPassword)) {
                return false;
            }

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println(ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }

    public void insertSong(Object[] row) throws SQLException, FileNotFoundException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Get fields necessary to insert the new song
            String title = String.valueOf(row[0]);
            String artist = String.valueOf(row[1]);
            String genre = String.valueOf(row[2]);
            String length = String.valueOf(row[3]);
            int year = (int) (row[4]);
            File audioFile = (File) row[5];

            // Read bytes of the MP3 file
            InputStream inputStream = new FileInputStream(audioFile);

            // Insert the new song
            query = "INSERT INTO Songs (title, artist, genre, length, yr, audiofile) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setString(3, genre);
            preparedStatement.setString(4, length);
            preparedStatement.setInt(5, year);
            preparedStatement.setBinaryStream(6, inputStream, audioFile.length());
            preparedStatement.executeUpdate();

            // Insert into UserSongs (uid, sid)
            query = "INSERT INTO UserSongs (uid, sid) SELECT uid, (SELECT MAX(sid) FROM Songs) FROM Users WHERE username = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);
            preparedStatement.executeUpdate();
        } catch (SQLException | FileNotFoundException ex) {
            System.err.println(ex);
        } finally {
            conn.close();
        }
    }
    
    public ArrayList<Object[]> fetchUserSongs() throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Grab all songs associated with the given user and return it
            query = "SELECT title, artist, genre, yr, length FROM Users NATURAL JOIN UserSongs NATURAL JOIN Songs WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);

            ArrayList<Object[]> rows = new ArrayList<>();

            // Loop through result set and get all user songs
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String genre = resultSet.getString("genre");
                int yr = resultSet.getInt("yr");
                String length = resultSet.getString("length");
                Object[] row = {title, artist, genre, yr, length};
                rows.add(row);
            }    
            return rows;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            conn.close();
        }
        return null;
    }

    public InputStream fetchSong(String songName) throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Fetch the given song name from the DB
            query = "SELECT audiofile FROM Users NATURAL JOIN UserSongs NATURAL JOIN Songs WHERE username = ? AND title = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, songName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBinaryStream("audiofile");
            }

            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            conn.close();
        }
        return null;
    }

    public boolean insertPlaylistSong(String playlistName, String songName) throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Get correct playlist to insert song into
            PreparedStatement ps1 = conn.prepareStatement("SELECT pid FROM Playlists NATURAL JOIN Users WHERE pname = ? AND username = ?");
            ps1.setString(1, playlistName);
            ps1.setString(2, this.username);
            ResultSet rs1 = ps1.executeQuery();

            // Get correct sid for the given song name
            PreparedStatement ps2 = conn.prepareStatement("SELECT sid FROM UserSongs NATURAL JOIN Users NATURAL JOIN Songs WHERE username = ? AND title = ?");
            ps2.setString(1, this.username);
            ps2.setString(2, songName);
            ResultSet rs2 = ps2.executeQuery();

            rs1.next();
            rs2.next();

            int pid = rs1.getInt("pid");
            int sid = rs2.getInt("sid");
            
            // Insert the song into the playlist
            query = "INSERT INTO PlaylistSongs (pid, sid) VALUES (?, ?)";
            PreparedStatement ps3 = conn.prepareStatement(query);
            ps3.setInt(1, pid);
            ps3.setInt(2, sid);
            ps3.executeUpdate();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn.close();
        }
        return false;
    }

    public void insertPlaylist(String playlistName) throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Insert given playlist into DB
            query = "INSERT INTO Playlists (uid, pname) SELECT uid, ? as pname FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, playlistName);
            preparedStatement.setString(2, this.username);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public ArrayList<Object[]> fetchSongsForPlaylist(String playlistName) throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Get the title, artist, genre, yr, and length of each song for the provided playlist for this user
            query = "SELECT title, artist, genre, yr, length FROM Users NATURAL JOIN Playlists NATURAL JOIN PlaylistSongs NATURAL JOIN Songs WHERE username = ? AND pname = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, playlistName);

            // Loop through all songs found for the specified playlists
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String genre = resultSet.getString("genre");
                int yr = resultSet.getInt("yr");
                String length = resultSet.getString("length");
                Object[] row = {title, artist, genre, yr, length};
                rows.add(row);
            }    
            return rows;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn.close();
        }
        return null;
    }

    public ArrayList<String> fetchPlaylists() throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Fetch all playlists for the current user
            query = "SELECT pname FROM Playlists NATURAL JOIN Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);

            ArrayList<String> pnames = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String playlistName = resultSet.getString("pname");
                pnames.add(playlistName);
            }    
            return pnames;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn.close();
        }
        return null;
    }

    public void deletePlaylist(String pname) throws SQLException {
        Connection conn = null;
        try {
            String query;

            conn = DriverManager.getConnection(this.JDBC_URL, "admin", this.DB_PASSWORD);

            // Find the uid for this user
            query = "SELECT uid FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, this.username);

            ResultSet rs1 = preparedStatement.executeQuery();
            rs1.next();
            int uid = rs1.getInt("uid");
            
            // Find the pid for the given playlist of this user
            query = "SELECT pid FROM Playlists WHERE uid = ? AND pname = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, uid);
            preparedStatement.setString(2, pname);

            ResultSet rs2 = preparedStatement.executeQuery();
            rs2.next();
            int pid = rs2.getInt("pid");

            // Delete the playlist
            query = "DELETE FROM Playlists WHERE pid = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, pid);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn.close();
        }
    }
}
