package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AllSongsCard extends JPanel {

    JPanel[] songs;

    public AllSongsCard() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(39, 41, 40));

        // TODO - Populate "songs[]" with all songs in database


    }

    public void addSong() {
        // TODO - add song to database
    }



}
