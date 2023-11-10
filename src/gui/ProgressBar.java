package gui;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JPanel {
    public ProgressBar() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        // TODO - need prev, play, pause, and next images to act as icons
        JProgressBar pb = new JProgressBar();
        JButton prev = new JButton("Prev");
        JButton play = new JButton("Play");
        JButton next = new JButton("Next");

        this.add(pb);
        this.add(prev);
        this.add(play);
        this.add(next);
        
        this.setBackground(Color.BLACK);
    }
}
