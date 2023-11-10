package gui;

import javax.swing.*;
import java.awt.*;

public class Control extends JPanel {
    
    public Control() {
        // TODO - need reverse, play, pause, and forward images to act as icons
        JButton reverse = new JButton("Reverse");
        JButton play = new JButton("Play");
        JButton forward = new JButton("Forward");

        this.add(reverse);
        this.add(play);
        this.add(forward);
        this.setBackground(Color.BLACK);
    }
    
}
