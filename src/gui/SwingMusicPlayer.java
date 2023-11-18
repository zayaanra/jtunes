package gui;

import java.awt.*;
import javax.swing.*;

import api.MusicPlayer;

public class SwingMusicPlayer extends JFrame {
    private MusicPlayer mp;

    private JPanel cards;

    private final String ALLSONGS = "ALL SONGS";
    private final String PLAYLISTS = "PLAYLISTS";

    public SwingMusicPlayer() {
        this.mp = new MusicPlayer();

        JPanel main = new JPanel(new BorderLayout());

        JPanel control = new Control();

        this.cards = new JPanel(new CardLayout());

        JPanel navPanel = new JPanel(new GridLayout(2, 1));
        JButton showSongs = new JButton("Songs");
        showSongs.setForeground(Color.WHITE);
        showSongs.setContentAreaFilled(false);
        showSongs.setBorderPainted(false);
        showSongs.addActionListener((e) -> {
            CardLayout c = (CardLayout)(cards.getLayout());
            c.show(this.cards, ALLSONGS);
        });

        JButton showPlaylists = new JButton("Playlists");
        showPlaylists.setForeground(Color.WHITE);
        showPlaylists.setContentAreaFilled(false);
        showPlaylists.setBorderPainted(false);
        showPlaylists.addActionListener((e) -> {
            CardLayout c = (CardLayout)(cards.getLayout());
            c.show(this.cards, PLAYLISTS);
        });

        navPanel.setBackground(Color.BLACK);
        navPanel.add(showSongs);
        navPanel.add(showPlaylists);

        
        this.cards.add(new AllSongsCard(), ALLSONGS);
        this.cards.add(new PlaylistCard(), PLAYLISTS);

        main.add(navPanel, BorderLayout.WEST);
        main.add(control, BorderLayout.SOUTH);
        main.add(this.cards, BorderLayout.CENTER);
        main.setBackground(Color.GRAY);

        // TODO - Set window title name of the user

        this.add(main);
        //Toolkit.getDefaultToolkit().getScreenSize()
        this.setSize(750, 500);
    }

}
