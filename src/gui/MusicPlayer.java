package gui;

import java.awt.*;
import javax.swing.*;

public class MusicPlayer extends JFrame {

    public MusicPlayer() {
        JPanel main = new JPanel(new BorderLayout());

        ProgressBar pb = new ProgressBar();
        //Control c = new Control();

        main.add(pb, BorderLayout.SOUTH);
        // main.add(c, BorderLayout.CENTER);
        main.setBackground(Color.GRAY);

        this.add(main);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

}
