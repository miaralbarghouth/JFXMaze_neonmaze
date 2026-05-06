package com.mazegame.logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer player;
    private static double vol = 0.5;
    private static boolean muted = false;

    public static void play() {
        if (player == null) {
            try {
                String path = MusicManager.class.getResource("/music/background.mp3").toExternalForm();
                player = new MediaPlayer(new Media(path));
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.setVolume(vol);
                if (!muted) player.play();
            } catch (Exception e) {}
        } else if (!muted && player.getStatus() != MediaPlayer.Status.PLAYING) {
            player.play();
        }
    }

    public static void setMuted(boolean mute) {
        muted = mute;
        if (player != null) {
            if (muted) player.pause();
            else player.play();
        }
    }

    public static void setVolume(double v) {
        vol = v;
        if (player != null) player.setVolume(v);
    }
}
