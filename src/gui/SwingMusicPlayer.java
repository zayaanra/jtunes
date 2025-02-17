package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import java.io.*;
import java.util.*;

import java.sql.SQLException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import api.DBManager;

public class SwingMusicPlayer extends JFrame {
    private final Object[] columns = {"Title", "Artist", "Genre", "Year", "Length"};

    private JPanel navCards;
    private JPanel main;
    private Control controlPanel;
    private DefaultTableModel model;

    JFileChooser jc;

    private final String ALLSONGS = "ALL SONGS";
    private JTable allSongs;

    private final String PLAYLISTS = "PLAYLISTS";
    private JPanel playlists;

    private JMenu addMenu;

    private String username;

    public SwingMusicPlayer(String username) {
        this.username = username;

        this.main = new JPanel(new BorderLayout());
        this.navCards = new JPanel(new CardLayout());
        this.addMenu = new JMenu("Add to Playlist");

        this.setupNavPanel();
        this.displayUserData();
        this.setupControlPanel();

        // Create menu bar needed to upload songs
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem uploadItem = new JMenuItem("Upload");
        uploadItem.addActionListener((e) -> {
            // If the user uploads a song, we'll process insert a new record into the DB representing that song
            Object[] row = this.processAudioFile();
            try {
                new DBManager(this.username).insertSong(row);
            } catch (SQLException | FileNotFoundException ex) {
                System.err.println(ex);
            }
        });


        fileMenu.add(uploadItem);
        menuBar.add(fileMenu);
        menuBar.add(this.addMenu);
        this.setJMenuBar(menuBar);

        this.main.setBackground(Color.GRAY);
        this.add(main);
        this.setTitle(this.username);
        this.setSize(750, 500);
    }

    public void setupNavPanel() {
        JPanel navPanel = new JPanel(new GridLayout(2, 1));

        // Set up "Songs" tab
        JButton showSongs = new JButton("Songs");
        showSongs.setForeground(Color.WHITE);
        showSongs.setContentAreaFilled(false);
        showSongs.setBorderPainted(false);
        showSongs.addActionListener((e) -> {
            CardLayout c = (CardLayout)(this.navCards.getLayout());
            c.show(this.navCards, ALLSONGS);
            this.controlPanel.setPlaylist(null);
        });

        // Set up "Playlists" tab
        JButton showPlaylists = new JButton("Playlists");
        showPlaylists.setForeground(Color.WHITE);
        showPlaylists.setContentAreaFilled(false);
        showPlaylists.setBorderPainted(false);
        showPlaylists.addActionListener((e) -> {
            CardLayout c = (CardLayout)(this.navCards.getLayout());
            c.show(this.navCards, PLAYLISTS);
            this.allSongs.clearSelection();
        });

        // Set up navigation panel
        navPanel.setBackground(Color.BLACK);
        navPanel.add(showSongs);
        navPanel.add(showPlaylists);

        this.main.add(navPanel, BorderLayout.WEST);
    }

    public void displayUserData() {
        JScrollPane songScroller = this.setupSongDisplay();
        
        this.playlists = new JPanel(new BorderLayout());
        
        // Top panel represents a bar which allows us to create a playlist.
        // Center panel represents each playlist.
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        JPanel centerPanel = new JPanel(new CardLayout());

        // Playlists are represented through combo boxes.
        // We show the playlist's table upon clicking a certain playlist in combo box
        JComboBox<String> pl = new JComboBox<>();
        pl.addActionListener((e) -> {
            String playlistName = pl.getItemAt(pl.getSelectedIndex());
            CardLayout c = (CardLayout) (centerPanel.getLayout());
            c.show(centerPanel, playlistName);
        });

        ArrayList<String> pnames;
        try {
            // Fetch all playlists and add them to "AddToPlaylist" menu item and combo box.
            pnames = new DBManager(this.username).fetchPlaylists();
            for (String pname : pnames) {
                DefaultTableModel pl_model = new DefaultTableModel(this.columns, 0);
                JTable playlist = constructTable(pl_model);      
                playlist.getSelectionModel().addListSelectionListener((e) -> {
                    this.controlPanel.setPlaylist(playlist);
                });

                ArrayList<Object[]> rows = new DBManager(this.username).fetchSongsForPlaylist(pname);
                for (Object[] row : rows) {
                    pl_model.addRow(row);
                }
                pl.addItem(pname);

                JMenuItem pitem = new JMenuItem(pname);
                pitem.addActionListener((e) -> {
                    this.updatePlaylistTable(pl_model, pname);
                });

                this.addMenu.add(pitem);
                centerPanel.add(new JScrollPane(playlist), pname);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Set up the button used to create a new playlist
        JButton create = new JButton("Create Playlist");
        create.addActionListener((e) -> {
            String pname = JOptionPane.showInputDialog("Enter Playlist Name");

            // Check for duplicate playlist names.
            for (int i = 0; i < pl.getModel().getSize(); i++) {
                if (pl.getModel().getElementAt(i).equals(pname)) {
                    JOptionPane.showMessageDialog(this, "Playlist " + pname + " already exists");
                    return;
                }
            }

            if (pname != null && !pname.trim().isEmpty()) {
                // Create table to display specific playlist
                pl.addItem(pname);
                
                DefaultTableModel pl_model = new DefaultTableModel(this.columns, 0);
                JTable playlist = constructTable(pl_model);
                playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                playlist.getSelectionModel().addListSelectionListener((e2) -> {
                    this.controlPanel.setPlaylist(playlist);
                });

                // Insert playlist into DB only if playlist doesn't already exist. Then, update AddToPlaylist menu and add as a card.
                try {                
                    new DBManager(this.username).insertPlaylist(pname);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                JMenuItem pitem = new JMenuItem(pname);
                pitem.addActionListener((e2) -> {
                    this.updatePlaylistTable(pl_model, pname);
                });

                this.addMenu.add(pitem);
                centerPanel.add(new JScrollPane(playlist), pname);
            }
        });

        JButton delete = new JButton("Delete Playlist");
        delete.addActionListener((e) -> {
            String pname = JOptionPane.showInputDialog("Enter Playlist Name for deletion");

            int removalIdx = 0;

            // Remove the playlist name from the addMenu
            for(int i = 0; i < this.addMenu.getItemCount(); i++) {
                if (this.addMenu.getItem(i).getText().equals(pname)) {
                    this.addMenu.remove(i);
                    removalIdx = i;
                    break;
                }
            }

            // Remove the playlist name from the playlist combo box
            pl.removeItem(pname);

            // Remove the respective component for the deleted playlist
            if (pl.getItemCount() == 0) {
                centerPanel.removeAll();
            } else {
                CardLayout c = (CardLayout) (centerPanel.getLayout());
                c.removeLayoutComponent(centerPanel.getComponent(removalIdx));
            }
            
            try {
                new DBManager(this.username).deletePlaylist(pname);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        });
        
        topPanel.add(create);
        topPanel.add(pl);
        topPanel.add(delete);

        this.playlists.add(topPanel, BorderLayout.NORTH);
        this.playlists.add(centerPanel, BorderLayout.CENTER);
        
        this.navCards.add(songScroller, ALLSONGS);
        this.navCards.add(playlists, PLAYLISTS);    

        this.main.add(this.navCards, BorderLayout.CENTER);
    }

    public void setupControlPanel() {
        this.controlPanel = new Control(this.allSongs, this.username);
        this.main.add(this.controlPanel, BorderLayout.SOUTH);
    }

    private JScrollPane setupSongDisplay() {
        // Give table model the appropriate columns
        this.model = new DefaultTableModel(this.columns, 0);
        
        this.allSongs = constructTable(this.model);

        // Update the table of songs displayed for the user with all songs they have ever added
        try {
            ArrayList<Object[]> rows = new DBManager(this.username).fetchUserSongs();
            for(Object[] row : rows) {
                this.model.addRow(row);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return new JScrollPane(this.allSongs);
    }

    private void setupPlaylistDisplay() {

    }

    // Customize table that is used to display songs/playlists.
    private JTable constructTable(DefaultTableModel m) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        JTable table = new JTable(m);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.setBackground(new Color(39, 41, 40));
        table.setForeground(Color.WHITE);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    public void updatePlaylistTable(DefaultTableModel pl_model, String pname) {
        try {
            String selectedSong = this.allSongs.getModel().getValueAt(this.allSongs.getSelectedRow(), 0).toString();
            Object[] row = new Object[this.allSongs.getColumnCount()];
            for (int c = 0; c < this.allSongs.getColumnCount(); c++) {
                row[c] = this.allSongs.getValueAt(this.allSongs.getSelectedRow(), c);
            }
            if (new DBManager(this.username).insertPlaylistSong(pname, selectedSong)) {
                pl_model.addRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Song " + selectedSong + " already exists in playlist " + pname);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

	private Object[] processAudioFile() {
        // Process the MP3 file the user has selected.

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
