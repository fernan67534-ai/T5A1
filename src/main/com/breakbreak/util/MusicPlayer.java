package main.com.breakbreak.util;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Random;

public final class MusicPlayer {
    public static final String GAME_OVER_MUSIC = "/sounds/game-over.mp3";
    public static final String WIN_MUSIC = "/sounds/win.mp3";

    private static final String[] LEVEL_MUSIC = {
            "/sounds/game-1.mp3",
            "/sounds/game-2.mp3",
            "/sounds/game-3.mp3",
            "/sounds/game-4.mp3",
            "/sounds/game-5.mp3"
    };

    private static final double MUSIC_VOLUME = 0.35;
    private static MusicPlayer instance;
    private static boolean fxInitialized = false;

    private MediaPlayer currentPlayer;
    private String lastLevelMusic = "";
    private final Random random = new Random();

    private MusicPlayer() {
        initJavaFX();
    }

    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    private static synchronized void initJavaFX() {
        if (!fxInitialized) {
            try {
                Platform.startup(() -> {});
                fxInitialized = true;
            } catch (IllegalStateException e) {
                // El toolkit de JavaFX ya estaba iniciado previamente
                fxInitialized = true;
            }
        }
    }

    public void preload() {
        Platform.runLater(() -> {
            MusicCache cache = MusicCache.getInstance();
            for (String track : LEVEL_MUSIC) {
                cache.getMedia(track);
            }
            cache.getMedia(WIN_MUSIC);
            cache.getMedia(GAME_OVER_MUSIC);
        });
    }

    private String pickLevelMusic() {
        if (LEVEL_MUSIC.length == 1) return LEVEL_MUSIC[0];
        String selected;
        do {
            selected = LEVEL_MUSIC[random.nextInt(LEVEL_MUSIC.length)];
        } while (selected.equals(lastLevelMusic));
        return selected;
    }

    public void playLevelMusic() {
        String track = pickLevelMusic();
        lastLevelMusic = track;
        play(track, true);
    }

    public void playWinMusic() {
        play(WIN_MUSIC, false);
    }

    public void playGameOverMusic() {
        play(GAME_OVER_MUSIC, false);
    }

    private void play(String path, boolean loop) {
        Platform.runLater(() -> {
            stopInternal();
            Media media = MusicCache.getInstance().getMedia(path);
            if (media != null) {
                currentPlayer = new MediaPlayer(media);
                currentPlayer.setVolume(MUSIC_VOLUME);
                if (loop) {
                    currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                }
                currentPlayer.play();
            }
        });
    }

    public void stop() {
        Platform.runLater(this::stopInternal);
    }

    private void stopInternal() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }
}
