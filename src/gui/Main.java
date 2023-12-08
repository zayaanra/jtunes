package gui;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import java.sql.SQLException;

import api.DBManager;

public class Main extends JFrame {

    public Main() {
        this.setupCards();

        this.setSize(300, 300);
        this.setResizable(true);
        this.setTitle("Login");
    }

    public void setupCards() {
        Main main = this;

        // Cards used to switch between the Login and Register panel whenever users needs to
        JPanel cards = new JPanel(new CardLayout());

        // Set up login and register panels
        Login login = new Login();
        Register register = new Register();

        // Login and switch labels have some mouse listener so their the cursor changes when hovering over them

        login.switchToReg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                login.switchToReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout c = (CardLayout)(cards.getLayout());
                c.next(cards);
                main.setTitle("Register");
            }
        });

        
        register.switchToLog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                register.switchToLog.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout c = (CardLayout)(cards.getLayout());
                c.next(cards);
                main.setTitle("Login");
            }
        });

        /*
         * Whenever user hits the button, a connection is made to the DB instance using DBManager(). We'll either authenticate/login the user or register new credentials
         * into the new DB.
         */

        login.loginBtn.addActionListener((e) -> {
            String username = login.username.getText();
            String password = String.valueOf(login.password.getPassword());
            
            // If the given username doesn't exist, show a warning.
            try {
                if(!new DBManager(username, password).authenticate()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Login failed. It's possible that the username does not exist or you entered your password incorrectly.");
                } else {
                    this.setupMusicPlayer(username);
                }
            } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                System.err.println(ex);
            }
        });

        register.registerBtn.addActionListener((e) -> {
            String username = register.username.getText();
            String password = String.valueOf(register.password.getPassword());
            String confirmed = String.valueOf(register.confirmation.getPassword());
            // If the given username or password is too short, show a warning.
            // If the password doesn't match the confirmed password, show a warning.
            if (username.length() < 5 || password.length() < 5) {
                JOptionPane.showMessageDialog(new JFrame(), "Username or Password too short! They both should be greater than 5 characters in length.");
            } else if (!password.equals(confirmed)) {
                JOptionPane.showMessageDialog(new JFrame(), "Passwords do not match!");
            } else {
                try {
                    if (!new DBManager(username, password).register()) {
                        JOptionPane.showMessageDialog(new JFrame(), "Something went wrong. It's likely that you tried registering under a username that's already taken.");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Register successful!");
                    }
                } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    System.err.println(ex);
                }
            }
        });


        cards.add(login);
        cards.add(register);

        this.add(cards);
    }

    public void setupMusicPlayer(String username) {
        // We destroy the current frame. At this point, the user has been successfully authenticated and is allowed to access the actual JTunes GUI.

        this.dispose();

        SwingMusicPlayer mp = new SwingMusicPlayer(username);
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mp.setVisible(true);
        
    }



    public static void main(String[] args) {
        Main main = new Main();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
    
}
