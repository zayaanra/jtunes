package gui;

import javax.swing.*;
import java.awt.*;

public class Control extends JPanel {
    
    public Control() {
        this.setLayout(new BorderLayout());

        JSlider pb = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        pb.setBackground(Color.BLACK);
        pb.setSize(new Dimension(400, 50));

        JButton prev = new JButton("⏮");
        JButton play = new JButton("▶");
        JButton next = new JButton("⏭");

        prev.setForeground(Color.WHITE);
        prev.setContentAreaFilled(false);
        prev.setBorderPainted(false);
        
        play.setForeground(Color.WHITE);
        play.setContentAreaFilled(false);
        play.setBorderPainted(false);

        next.setForeground(Color.WHITE);
        next.setContentAreaFilled(false);
        next.setBorderPainted(false);

        JPanel centerGrid = new JPanel(new GridLayout(2, 1));
        JPanel bottom = new JPanel();
        bottom.add(prev);
        bottom.add(play);
        bottom.add(next);
        bottom.setBackground(Color.BLACK);

        centerGrid.add(pb);
        centerGrid.add(bottom);

        this.add(centerGrid);
        this.setBackground(Color.BLACK);
    }
    
}
