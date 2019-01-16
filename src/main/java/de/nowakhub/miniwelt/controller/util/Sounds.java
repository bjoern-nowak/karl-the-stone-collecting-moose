package de.nowakhub.miniwelt.controller.util;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

public class Sounds {

    private static AudioClip alarm = new AudioClip(Sounds.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());

    public static void playWarning() {
        Platform.runLater(() -> alarm.play(0.2));
    }

    // TODO add background sound (per tab)
}
