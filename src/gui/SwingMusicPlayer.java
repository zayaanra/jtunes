package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

import java.sql.SQLException;
import java.sql.*;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import api.DBManager;
import api.MusicPlayer;

public class SwingMusicPlayer extends JFrame {
    private MusicPlayer mp;

    private JPanel cards;
    private JPanel main;
    private DefaultTableModel model;

    JFileChooser jc;

    private final String ALLSONGS = "ALL SONGS";
    private JTable allSongs;

    private final String PLAYLISTS = "PLAYLISTS";
    private JPanel playlists;

    private String username;

    public SwingMusicPlayer(String username) {
        this.username = username;

        this.mp = new MusicPlayer();
        this.main = new JPanel(new BorderLayout());
        this.cards = new JPanel(new CardLayout());

        this.setupNavPanel();
        this.displayUserData();
        this.setupControlPanel();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem uploadItem = new JMenuItem("Upload");
        uploadItem.addActionListener((e) -> {
            // TODO - Add song to database
            Object[] row = this.processAudioFile();
            try {
                new DBManager(this.username, "").insertSong(row);
            } catch (SQLException | FileNotFoundException ex) {
                System.err.println(ex);
            }
            

        });

        fileMenu.add(uploadItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        this.main.setBackground(Color.GRAY);
        this.add(main);
        //Toolkit.getDefaultToolkit().getScreenSize()
        this.setTitle(this.username);
        this.setSize(750, 500);
    }

    public void setupNavPanel() {
        JPanel navPanel = new JPanel(new GridLayout(2, 1));
        JButton showSongs = new JButton("Songs");
        showSongs.setForeground(Color.WHITE);
        showSongs.setContentAreaFilled(false);
        showSongs.setBorderPainted(false);
        showSongs.addActionListener((e) -> {
            CardLayout c = (CardLayout)(this.cards.getLayout());
            c.show(this.cards, ALLSONGS);
        });

        JButton showPlaylists = new JButton("Playlists");
        showPlaylists.setForeground(Color.WHITE);
        showPlaylists.setContentAreaFilled(false);
        showPlaylists.setBorderPainted(false);
        showPlaylists.addActionListener((e) -> {
            CardLayout c = (CardLayout)(this.cards.getLayout());
            c.show(this.cards, PLAYLISTS);
        });

        navPanel.setBackground(Color.BLACK);
        navPanel.add(showSongs);
        navPanel.add(showPlaylists);

        this.main.add(navPanel, BorderLayout.WEST);
    }

    public void displayUserData() {
        Object[] columns = {"Title", "Artist", "Genre", "Year", "Length"};
        this.model = new DefaultTableModel(columns, 0);

        // Customize table that is used to display songs/playlists.
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        this.allSongs = new JTable(model);
        this.allSongs.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        this.allSongs.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        this.allSongs.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        this.allSongs.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        this.allSongs.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        this.allSongs.setBackground(new Color(39, 41, 40));
        this.allSongs.setForeground(Color.WHITE);
        this.allSongs.setDefaultEditor(Object.class, null);
        JScrollPane songScroller = new JScrollPane(this.allSongs);
        
        // TODO - need to fetch database and all playlists
        this.playlists = new JPanel(new BorderLayout());
        GridLayout gr = new GridLayout(1, 1);
        JPanel gridPanel = new JPanel(gr);

        // Update the table of songs displayed for the user with all songs they have ever added
        try {
            ArrayList<Object[]> rows = new DBManager(this.username, "").fetchUserSongs();
            for(Object[] row : rows) {
                this.model.addRow(row);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }

        
        JButton create = new JButton("Create Playlist");
        create.addActionListener((e) -> {
            String playlistName = JOptionPane.showInputDialog("Enter Playlist Name");
            if (playlistName != null && !playlistName.trim().isEmpty()) {
                // TODO - fix playlists display
                JComboBox<String> pl = new JComboBox<>();
                pl.addItem(playlistName);
                gridPanel.add(pl);
                this.revalidate();
                this.validate();
                // TODO - add playlist to database
            }
        });
        this.playlists.add(create, BorderLayout.NORTH);
        this.playlists.add(gridPanel, BorderLayout.WEST);
        
        this.cards.add(songScroller, ALLSONGS);
        this.cards.add(playlists, PLAYLISTS);    

        this.main.add(this.cards, BorderLayout.CENTER);
    }

    public void setupControlPanel() {
        JPanel control = new Control(this.allSongs);
        this.main.add(control, BorderLayout.SOUTH);
    }


	private Object[] processAudioFile() {
		if (jc == null) jc = new JFileChooser("."); 
		
		int returnValue = jc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File audioFile = jc.getSelectedFile();
			    try {
                    // Here, we read the metadata tags provided by the MP3 file.
                    // However, the user MUST define these properties BEFORE uploading the MP3 file to Jtunes.
                    AudioFile audio = AudioFileIO.read(audioFile);
                    AudioHeader audioHeader = audio.getAudioHeader();
                    String trackLength = String.format("%d:%02d", audioHeader.getTrackLength()/60, audioHeader.getTrackLength()%60);
                    Tag tag = audio.getTag();
                    // TODO - Fix genre tag
                    Object[] row = {tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.GENRE), tag.getFirst(FieldKey.YEAR), trackLength};
                    this.model.addRow(row);
                    Object[] newRow = {tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.GENRE), trackLength, Integer.valueOf(tag.getFirst(FieldKey.YEAR)), audioFile};
                    return newRow;
            } catch (Exception ex) {
                System.err.println("Failed to read audio file");
            }
		}
        return null;
	}
}
