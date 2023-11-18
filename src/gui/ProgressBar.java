package gui;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JPanel {
    public ProgressBar() {
        //this.setLayout();

        // TODO - need prev, play, pause, and next images to act as icons
        JSlider pb = new JSlider();
        //pb.setStringPainted(true);


        this.add(pb);
        // this.add(prev, BorderLayout.SOUTH);
        // this.add(play, BorderLayout.SOUTH);
        // this.add(next, BorderLayout.SOUTH);
        
        this.setBackground(Color.BLACK);
    }
}
