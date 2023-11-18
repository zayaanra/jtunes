package gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.*;

public class AllSongsCard extends JPanel {
    public AllSongsCard() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(39, 41, 40));

        // TODO - Fetch all songs from database and add to card
        JLabel tmp = new JLabel("SHOW ALL SONGS HERE");
        this.add(tmp);
    }
}
