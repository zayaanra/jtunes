package gui;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;

public class Main extends JFrame {

    public Main() {
        this.setupCards();

        // TODO - set up the actual music player

        this.setSize(300, 300);
        this.setResizable(false);
        this.setTitle("Login");
    }

    public void setupCards() {
        Main main = this;

        JPanel cards = new JPanel(new CardLayout());

        Login login = new Login();
        Register register = new Register();

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

        login.loginBtn.addActionListener((e) -> {
            // TODO - Authenticate user by querying database
            // TODO - escape SQL injection
            this.setupMusicPlayer();

        });

        register.registerBtn.addActionListener((e) -> {
            // TOOD - Register credentials into the database
            // TODO - escape SQL injection
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

        cards.add(login);
        cards.add(register);

        this.add(cards);
    }

    public void setupMusicPlayer() {
        this.dispose();

        MusicPlayer mp = new MusicPlayer();
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mp.setVisible(true);
        
    }



    public static void main(String[] args) {
        Main main = new Main();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
    
}
