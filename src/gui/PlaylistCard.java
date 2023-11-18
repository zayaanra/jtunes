package gui;

import javax.swing.*;
import java.awt.*;

public class PlaylistCard extends JPanel {
    public PlaylistCard() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(39, 41, 40));

        // TODO - Fetch playlists from database and add to card
        JLabel tmp = new JLabel("SHOW PLAYLISTS HERE");
        this.add(tmp);
    }
}
