package gui;

import javax.swing.*;


import api.DBManager;

import java.awt.*;
import java.util.Queue;
import java.io.*;
import java.sql.SQLException;

import api.Playback;
import javazoom.jl.decoder.JavaLayerException;

public class Control extends JPanel {
    // TODO - used for song queue
    public Queue<File> q;
    
    private String username;
    private JTable allSongs;

    private String currentSong;

    private Playback playback;
    
    public Control(JTable allSongs, String username) {
        this.setLayout(new BorderLayout());

        this.allSongs = allSongs;
        this.username = username;

        // This slider represents the progress of the song that's being played. (TODO)
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
            if (play.getText().equals("⏸")) {
                play.setText("▶");
                this.currentSong = null;
                this.playback.pause();
            } else {
                if (this.allSongs.getRowCount() != 0) {
                    String selectedSong = this.allSongs.getModel().getValueAt(this.allSongs.getSelectedRow(), 0).toString();
                    // This condition prevents the DB from being queried so often.
                    // Since we store MP3 files as MEDIUMBLOBS, it becomes quite inefficient to constantly fetch a MEDIUMBLOB.
                    if (!selectedSong.equals(currentSong)) {
                        try {
                            this.currentSong = selectedSong;
                            InputStream audio = new DBManager(this.username, "").fetchSong(this.currentSong);
                            if (audio != null) {
                                // We only ever create a new Playback(audio) if it's a new song that's been selected.
                                // This is more efficient than if we just kept querying and creating new playback threads (if the same song was selected).
                                this.playback = new Playback(audio, () -> {
                                    this.currentSong = null;
                                    play.setText("▶");
                                });
                                this.playback.play();
                            }
                        } catch (JavaLayerException | SQLException ex) {
                            System.err.println(ex);
                        }
                    }
                    play.setText("⏸");
                }   
            }
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
