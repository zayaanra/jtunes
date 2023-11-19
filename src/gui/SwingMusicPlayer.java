package gui;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import api.MusicPlayer;

public class SwingMusicPlayer extends JFrame {
    private MusicPlayer mp;

    private JPanel cards;

    JFileChooser jc;

    private final String ALLSONGS = "ALL SONGS";
    private AllSongsCard allSongs;

    private final String PLAYLISTS = "PLAYLISTS";
    private PlaylistCard playlists;

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

        allSongs = new AllSongsCard();
        playlists = new PlaylistCard();

        this.cards.add(allSongs, ALLSONGS);
        this.cards.add(playlists, PLAYLISTS);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem uploadItem = new JMenuItem("Upload");
        uploadItem.addActionListener((e) -> {
            // TODO - Add song to database and update displayed song list
            String songName = this.loadFile();
            // TODO - if current card is ALLSONGS, call AllSongs.add()
            
            System.out.println(songName);
        });

        fileMenu.add(uploadItem);
        menuBar.add(fileMenu);

        main.add(navPanel, BorderLayout.WEST);
        main.add(control, BorderLayout.SOUTH);
        main.add(this.cards, BorderLayout.CENTER);
        main.setBackground(Color.GRAY);

        // TODO - Set window title name of the user

        this.setJMenuBar(menuBar);
        this.add(main);

        //Toolkit.getDefaultToolkit().getScreenSize()
        this.setSize(750, 500);
    }


	private String loadFile() {
		String text = "";
		// alternately, you can have it return
		// a File object or file path String or whatever you
		// like.
		if (jc == null) jc = new JFileChooser("."); 
		
		int returnValue = jc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jc.getSelectedFile();
			return selectedFile.getName();
			
		}
		return "";
	}

}
