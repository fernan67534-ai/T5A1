package main.com.breakbreak.util;

import javafx.scene.media.AudioClip;

public final class SoundPlayer {
    public static final String BLOCK_SOUND = "/sounds/block.mp3";
    public static final String BOUNCE_SOUND = "/sounds/bounce.mp3";
    public static final String LASER_SOUND = "/sounds/laser.mp3";
    public static final String BONUS_SOUND = "/sounds/bonus.wav";
    public static final String LOSE_BALL_SOUND = "/sounds/lose-ball.wav";

    private static final double EFFECT_VOLUME = 0.6;
    private static SoundPlayer instance;

    private SoundPlayer() {}

    public static synchronized SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    public void preload() {
        SoundCache cache = SoundCache.getInstance();
        cache.getSound(BLOCK_SOUND);
        cache.getSound(BOUNCE_SOUND);
        cache.getSound(LASER_SOUND);
        cache.getSound(BONUS_SOUND);
        cache.getSound(LOSE_BALL_SOUND);
    }

    private void play(String path) {
        AudioClip clip = SoundCache.getInstance().getSound(path);
        if (clip != null) {
            clip.play(EFFECT_VOLUME);
        }
    }

    public void playBlock() { play(BLOCK_SOUND); }
    public void playBounce() { play(BOUNCE_SOUND); }
    public void playLaser() { play(LASER_SOUND); }
    public void playBonus() { play(BONUS_SOUND); }
    public void playLoseBall() { play(LOSE_BALL_SOUND); }
}
