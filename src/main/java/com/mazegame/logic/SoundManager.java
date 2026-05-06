package com.mazegame.logic;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip clip;
    private static double vol = 0.5;

    public static void play() {
        if (clip == null) {
            try {
                String path = SoundManager.class.getResource("/music/collect.wav").toExternalForm();
                clip = new AudioClip(path);
            } catch (Exception e) {}
        }
        if (clip != null) {
            clip.setVolume(vol);
            clip.play();
        }
    }

    public static void setVolume(double v) {
        vol = v;
    }
}
