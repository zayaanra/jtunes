package gui;

import api.DBManager;
import api.Playback;

import javax.swing.*;
import java.awt.*;

import java.io.*;

import java.sql.SQLException;

public class Control extends JPanel {
    private String username;
    private String currentSong;

    private JTable allSongs;
    private JButton play;

    private Playback playback;

    private JTable playlist;

    public Control(JTable allSongs, String username) {
        this.setLayout(new BorderLayout());

        this.allSongs = allSongs;
        this.username = username;
        this.playlist = null;
        
        // TODO - This slider represents the progress of the song that's being played.
        JSlider pb = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        pb.setBackground(Color.BLACK);
        pb.setSize(new Dimension(400, 50));

        this.play = new JButton("Play");
        
        // TODO - play song depending on if user is on Songs or Playlists tab

        this.play.setForeground(Color.WHITE);
        this.play.setContentAreaFilled(false);
        this.play.setBorderPainted(false);
        this.play.addActionListener((e) -> {
            // If we are playing a song and the stop button is pressed, stop playback immediately.
            if (this.play.getText().equals("Stop")) {
                this.play.setText("Play");
                this.currentSong = null;
                this.playback.pause();
            } else {
                this.setupPlayback();
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(play);
        bottom.setBackground(Color.BLACK);

        JPanel centerGrid = new JPanel(new GridLayout(2, 1));
        centerGrid.add(pb);
        centerGrid.add(bottom);

        this.add(centerGrid);
        this.setBackground(Color.BLACK);
    }

    public void setupPlayback() {
        String selectedSong;
        if (this.playlist == null) {
            selectedSong = this.allSongs.getModel().getValueAt(this.allSongs.getSelectedRow(), 0).toString();
        } else {
            selectedSong = this.playlist.getModel().getValueAt(this.playlist.getSelectedRow(), 0).toString();
        }
        
        if (this.allSongs.getRowCount() != 0) {
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
                } catch (SQLException ex) {
                    System.err.println(ex);
                }
            }
            this.play.setText("Stop");
        }         
    }

    public void setPlaylist(JTable playlist) {
        this.playlist = playlist;
    }

}
