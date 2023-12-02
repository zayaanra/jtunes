package api;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Playback {
    public final int PLAYING = 0;
    public final int PAUSED = 1;
    public final int FINISHED = 2;

    public int status;

    private AdvancedPlayer player;
    public Thread t;
    
    public Playback(InputStream audio, Runnable f) {
        try {
            this.player = new AdvancedPlayer(audio);
            this.player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent e) {
                    f.run();
                }
            });
        } catch (JavaLayerException ex) {
            System.err.println(ex);
        }
    }

    // Plays music based on the selected song.
    public void play() {
        if (this.player != null && (this.t == null || !this.t.isAlive())) {
            this.t = new Thread(() -> {
                try {
                    this.player.play();
                } catch (JavaLayerException ex) {
                    System.err.println(ex);
                }
            });
            this.status = PLAYING;
            this.t.start();
        }
    }

    // Pauses the currently playing music by simply closing the player.
    public void pause() {
        if (this.player != null && this.t != null && this.t.isAlive()) {
            this.status = PAUSED;
            this.player.close();
        }
    }

}
