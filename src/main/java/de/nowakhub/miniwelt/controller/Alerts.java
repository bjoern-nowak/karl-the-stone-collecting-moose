package de.nowakhub.miniwelt.controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Alerts {

    private static AudioClip alarm = new AudioClip(Alerts.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());

    /**
     * Show info
     */
    public static void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Consumes event if user canceles action
     */
    public static void confirmClose(Event event, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait()
                .filter(buttonType -> buttonType.equals(ButtonType.CANCEL))
                .ifPresent(buttonType -> event.consume());
        // TODO ask to save all files
    }

    /**
     * Request text input
     */
    public static Optional<String> requestInput(Event event, String defaultValue, String headerText, String contentText) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
            dialog.setTitle("Input Dialog");
            dialog.setHeaderText(headerText);
            dialog.setContentText(contentText);
            return dialog.showAndWait();
    }

    /**
     * Show error
     */
    public static void showError(String header, String content) {
        playWarning();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showException(Throwable ex) {
        showException(null, ex);
    }

    // vgl. https://code.makery.ch/blog/javafx-dialogs-official/
    public static void showException(Thread th, Throwable ex) {
        playWarning();
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Oops, something has gone wrong.");
        String contentText = ex.toString();
        if (ex instanceof InvocationTargetException) {
            contentText = ((InvocationTargetException) ex).getTargetException().getMessage();
        }
        alert.setContentText(contentText);
        Label label = new Label("The exception stacktrace was:");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(600.0);
        textArea.setPrefWidth(800.0);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void playWarning() {
        Platform.runLater(() -> alarm.play(0.2));
    }
}
