package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class Control extends JPanel {
    // TODO - used for song queue
    public Queue<File> q;
    
    private JTable allSongs;
    
    public Control(JTable allSongs) {
        this.setLayout(new BorderLayout());

        this.allSongs = allSongs;

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
        play.addActionListener((e) -> {
            String songName = this.allSongs.getModel().getValueAt(this.allSongs.getSelectedRow(), 0).toString();
            // TODO - need to look up songName in database to get file associated with it
        });

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
