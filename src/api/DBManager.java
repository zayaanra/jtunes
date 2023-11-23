package api;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.sql.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import java.util.*;

public class DBManager {
    private final String JDBC_URL = "jdbc:mysql://instance-csgy-6083.cu7elpgf7g3u.us-east-2.rds.amazonaws.com:3306/jtunes";
    private String username;
    private String password;

    // TODO - Should this class be run in its own thread?

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
            // TODO - check if password leak is allowed here
            conn = DriverManager.getConnection(this.JDBC_URL, "admin", "i_}S-*cZ5gqh=Hq");
            //Class.forName("com.mysql.cj.jdbc.Drive");

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
            conn = DriverManager.getConnection(this.JDBC_URL, "admin", "i_}S-*cZ5gqh=Hq");

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
}
