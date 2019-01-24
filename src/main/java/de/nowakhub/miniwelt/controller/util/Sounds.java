package de.nowakhub.miniwelt.controller.util;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Sounds {

    private static AudioClip alarm = new AudioClip(Sounds.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());
    private static final List<MediaPlayer> players = new ArrayList<>();
    private static MediaPlayer activePlayer;

    static {
        // get audio files of folder
        final File musicDir = Paths.get("src/main/resources/sounds/background/").toAbsolutePath().toFile();
        if (musicDir != null && musicDir.exists() && musicDir.isDirectory())
            for (File music : new ArrayList<>(Arrays.asList(musicDir.listFiles(file -> file.getName().endsWith(".wav")))))
                players.add(new MediaPlayer(new Media("file:///" + music.toString().replace("\\", "/").replaceAll(" ", "%20"))));

        // initial playlist shuffle
        Collections.shuffle(players);

        // set a loop of players
        for (int i = 0; i < players.size(); i++) {
            final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
            players.get(i).setOnEndOfMedia(() -> {
                nextPlayer.seek(Duration.ZERO);
                nextPlayer.play();
                activePlayer = nextPlayer;
            });
        }

        // set first track
        activePlayer = players.get(0);
    }


    public static void playWarning() {
        Platform.runLater(() -> alarm.play(0.2));
    }

    public static boolean toggleMusic() {
        if (activePlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
            activePlayer.pause();
            return false;
        } else {
            activePlayer.play();
            return true;
        }
    }
}
