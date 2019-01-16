package de.nowakhub.miniwelt.controller.util;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

public final class Sounds {

    private static AudioClip alarm = new AudioClip(Sounds.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());
    private static Media music = new Media("file:///" + Paths.get("src/main/resources/sounds/background/music_calm_tree_of_life.wav").toAbsolutePath().toString().replace('\\', '/'));
    private static MediaPlayer player = new MediaPlayer(music);

    static {
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setAutoPlay(true);
        player.setVolume(1.0);
    }

    public static void playWarning() {
        Platform.runLater(() -> alarm.play(0.2));
    }

    // TODO play background music per tab or list of music across tabs
    public static boolean toggleMusic() {
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)) player.pause();
        else player.play();
        return player.getStatus().equals(MediaPlayer.Status.PLAYING);
    }
}
