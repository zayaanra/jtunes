package gui;

import java.awt.*;
import javax.swing.*;

import api.MusicPlayer;

public class SwingMusicPlayer extends JFrame {
    private MusicPlayer mp;

    public SwingMusicPlayer() {
        this.mp = new MusicPlayer();

        JPanel main = new JPanel(new BorderLayout());
        
        JPanel control = new Control();

        main.add(control, BorderLayout.SOUTH);
        main.setBackground(Color.GRAY);

        // TODO - Set window title name of the user

        this.add(main);
        //Toolkit.getDefaultToolkit().getScreenSize()
        this.setSize(750, 500);
    }

}
