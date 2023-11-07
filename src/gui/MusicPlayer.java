package gui;

import java.awt.CardLayout;

import javax.swing.*;

public class MusicPlayer extends JFrame {

    public MusicPlayer() {
        JPanel cards = new JPanel(new CardLayout());

        Login login = new Login();
        Register register = new Register();

        login.switchToReg.addActionListener((e) -> {
            CardLayout c = (CardLayout)(cards.getLayout());
            c.next(cards);
        });

        register.switchToLog.addActionListener((e) -> {
            CardLayout c = (CardLayout)(cards.getLayout());
            c.first(cards);
        });
 
        cards.add(login);
        cards.add(register);

        //infoPanel.add(switchToReg);

        this.add(cards);

        this.setSize(300, 300);
        this.setResizable(false);
        this.setTitle("Login");
    }



    public static void main(String[] args) {
        MusicPlayer mp = new MusicPlayer();
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mp.setVisible(true);
    }
    
}
