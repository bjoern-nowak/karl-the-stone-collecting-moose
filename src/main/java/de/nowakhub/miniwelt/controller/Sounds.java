package de.nowakhub.miniwelt.controller;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

public class Sounds {

    private static AudioClip alarm = new AudioClip(Sounds.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());

    public static void playWarning() {
        Platform.runLater(() -> alarm.play(0.2));
    }
}
