package gui;

import java.awt.*;
import javax.swing.*;

public class MusicPlayer extends JFrame {

    public MusicPlayer() {
        JPanel main = new JPanel(new BorderLayout());
        
        JPanel control = new Control();

        main.add(control, BorderLayout.SOUTH);
        main.setBackground(Color.GRAY);

        // TODO - Set title name of the user

        this.add(main);
        //Toolkit.getDefaultToolkit().getScreenSize()
        this.setSize(750, 500);
    }

}
