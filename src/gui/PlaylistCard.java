package gui;

import javax.swing.*;
import java.awt.*;

public class PlaylistCard extends JPanel {
    public PlaylistCard() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(39, 41, 40));

        // TODO - Fetch playlists from database and add to card
        JButton create = new JButton("Create Playlist");
        create.addActionListener((e) -> {
            String playlistName = JOptionPane.showInputDialog("Enter Playlist Name");
            if (playlistName != null && !playlistName.trim().isEmpty()) {
                // TODO - add playlist to database
            }
        });

        this.add(create, BorderLayout.NORTH);
    }
}
