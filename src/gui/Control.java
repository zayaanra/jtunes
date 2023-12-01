package gui;

import javax.swing.*;


import api.DBManager;

import java.awt.*;
import java.util.Queue;
import java.io.*;
import java.sql.SQLException;
import javax.swing.table.*;

import java.util.*;

import api.Playback;
import javazoom.jl.decoder.JavaLayerException;

public class Control extends JPanel {
    public Queue<String> q;
    
    private String username;
    private JTable allSongs;

    private String currentSong;

    private Playback playback;

    private JButton play;

    private boolean fromQueue;
    
    public Control(JTable allSongs, String username) {
        this.setLayout(new BorderLayout());

        this.allSongs = allSongs;
        this.username = username;
        this.fromQueue = false;

        // Shuffle queue
        this.q = new LinkedList<>();
        this.shuffle();
        

        // This slider represents the progress of the song that's being played. (TODO)
        JSlider pb = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        pb.setBackground(Color.BLACK);
        pb.setSize(new Dimension(400, 50));

        JButton prev = new JButton("Previous");
        this.play = new JButton("Play");
        JButton next = new JButton("Next");
        JButton shuffle = new JButton("Shuffle");
        
        // TODO - play song depending on if user is on Songs or Playlists tab

        prev.setForeground(Color.WHITE);
        prev.setContentAreaFilled(false);
        prev.setBorderPainted(false);
        
        this.play.setForeground(Color.WHITE);
        this.play.setContentAreaFilled(false);
        this.play.setBorderPainted(false);
        this.play.addActionListener((e) -> {
            // If we are playing a song and the stop button is pressed, stop playback immediately.
            if (this.play.getText().equals("Stop")) {
                this.play.setText("Play");
                this.currentSong = null;
                this.playback.pause();
            } else if (!this.fromQueue || this.q.isEmpty()) {
                // If user decides to begin playing from selected song and not queue, set up normal playback. (or if the queue is empty).
                this.setupPlaybackQDisabled();
            } else {
                // Set up playback using the queue.
                this.setupPlaybackQEnabled();
            }
        });

        next.setForeground(Color.WHITE);
        next.setContentAreaFilled(false);
        next.setBorderPainted(false);

        shuffle.setForeground(Color.WHITE);
        shuffle.setContentAreaFilled(false);
        shuffle.setBorderPainted(false);
        shuffle.addActionListener((e) -> {
            this.shuffle();
        });

        JPanel centerGrid = new JPanel(new GridLayout(2, 1));
        JPanel bottom = new JPanel();
        bottom.add(prev);
        bottom.add(play);
        bottom.add(next);
        bottom.add(shuffle);
        bottom.setBackground(Color.BLACK);

        centerGrid.add(pb);
        centerGrid.add(bottom);

        this.add(centerGrid);
        this.setBackground(Color.BLACK);
    }

    public void setFromQueue(boolean fromQueue) {
        this.fromQueue = fromQueue;
    }

    public void setupPlaybackQDisabled() {
        if (this.allSongs.getRowCount() != 0) {
            String selectedSong = this.allSongs.getModel().getValueAt(this.allSongs.getSelectedRow(), 0).toString();
            // This condition prevents the DB from being queried so often.
            // Since we store MP3 files as MEDIUMBLOBS, it becomes quite inefficient to constantly fetch a MEDIUMBLOB.
            if (!selectedSong.equals(currentSong)) {
                try {
                    this.currentSong = selectedSong;
                    InputStream audio = new DBManager(this.username).fetchSong(this.currentSong);
                    if (audio != null) {
                        // We only ever create a new Playback(audio) if it's a new song that's been selected.
                        // This is more efficient than if we just kept querying and creating new playback threads (if the same song was selected).
                        this.playback = new Playback(audio, () -> {
                            this.currentSong = null;
                            this.play.setText("Play");
                        });
                        System.out.println("Now playing - " + this.currentSong);
                        this.playback.play();
                    }
                } catch (JavaLayerException | SQLException ex) {
                    System.err.println(ex);
                }
            }
            this.play.setText("Stop");
        }         
    }

    public void setupPlaybackQEnabled() {
        if (this.q.isEmpty()) {
            //
        }
        // Otherwise, start playing from the queue.
        new Thread(() -> {
            try {
                for (String title : this.q) {
                    this.currentSong = title;
                    InputStream audio = new DBManager(this.username).fetchSong(title);
                    if (audio != null) {
                        this.playback = new Playback(audio, () -> {
                            // this.currentSong = title;
                            this.play.setText("Play");
                        });
                        // Play the current song and then wait until its finished to play the next.
                        System.out.println("Now playing - " + this.currentSong);
                        this.playback.play();
                        this.play.setText("Stop");
                        this.playback.t.join();
                        // TODO - when pause pressed, song gets skipped and immediately starts playing next in q.
                    }
                }
                this.q.clear();
            } catch (SQLException | JavaLayerException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // Shuffles the song queue
    public void shuffle() {
        this.q.clear();

       TableModel m = this.allSongs.getModel();
       int rows = m.getRowCount();

       for (int i = 0; i < rows; i++) {
        String title = String.valueOf(m.getValueAt(i, 0));
        this.q.offer(title);
       }

       LinkedList<String> list = new LinkedList<>(this.q);
       Collections.shuffle(list);
       this.q = new LinkedList<>(list);

       // System.out.println("Queue is now: " + this.q);
    }
    
}
