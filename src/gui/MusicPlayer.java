package gui;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;

public class MusicPlayer extends JFrame {

    public MusicPlayer() {
        this.setupCards();

        // TODO - set up the actual music player

        this.setSize(300, 300);
        this.setResizable(false);
        this.setTitle("Login");
    }

    public void setupCards() {
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
            }
        });

        cards.add(login);
        cards.add(register);

        this.add(cards);
    }



    public static void main(String[] args) {
        MusicPlayer mp = new MusicPlayer();
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mp.setVisible(true);
    }
    
}
