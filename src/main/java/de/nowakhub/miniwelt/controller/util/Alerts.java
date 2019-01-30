package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.exceptions.PublicException;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;

public class Alerts {

    private static AudioClip alarm = new AudioClip(Alerts.class.getResource("/sounds/alarm_beep_warning_01.wav").toString());


    public static boolean isPublicException(Throwable ex) {
        return ex instanceof PublicException;
    }

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
     * Request confirmation; consumes event if user cancels
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
    }

    /**
     * Request text input
     */
    public static Optional<String> requestInput(String defaultValue, String headerText, String contentText) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog.showAndWait();
    }

    /**
     * Request make a choice from a dropbox
     */
    public static Optional<String> requestDecision(Collection<String> choices, String headerText, String contentText) {
        if (choices.isEmpty()) return Optional.empty();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.iterator().next(), choices);
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog.showAndWait();

    }

    /**
     * Show dialog as error
     */
    public static void showError(String header, String content) {
        Sounds.playWarning();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * shows a dialog about the exception, only if the exception is an instance of {@link PublicException}
     * @param ex must be set
     */
    public static void showException(Throwable ex) {
        showException(null, ex);
    }


    /**
     * shows a dialog about the exception, only if the exception is an instance of {@link PublicException}, except debug property is set
     * @see <a href="https://code.makery.ch/blog/javafx-dialogs-official/">vgl. https://code.makery.ch/blog/javafx-dialogs-official/</a> (visited 30.01.2019)
     * @param th may be null
     * @param ex must be set
     */
    public static void showException(Thread th, Throwable ex) {
        if (PropsCtx.isDebug() || isPublicException(ex))
            showExceptionInternal(ex);
        else if (isPublicException(ex.getCause()))
            showExceptionInternal(ex.getCause());
        else if (ex instanceof InvocationTargetException && isPublicException(((InvocationTargetException) ex).getTargetException()))
            showExceptionInternal(((InvocationTargetException) ex).getTargetException());
        else if (ex.getCause() instanceof InvocationTargetException && isPublicException(((InvocationTargetException) ex.getCause()).getTargetException()))
            showExceptionInternal(((InvocationTargetException) ex.getCause()).getTargetException());
        else
            showError("Oops that should'nt happen", "Sorry but something went internally wrong");
    }

    private static void showExceptionInternal(Throwable ex) {
        Sounds.playWarning();
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Oops, something has gone wrong.");
        String contentText = ex.getMessage();
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


    /**
     * Request two text input
     * @see <a href="https://code.makery.ch/blog/javafx-dialogs-official/">vgl. https://code.makery.ch/blog/javafx-dialogs-official/</a> (visited 30.01.2019)
     */
    public static Optional<Pair<String, String>> requestDoubleInput(String headerText, String contentText, String label1, String label2) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Double Input Dialog");
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        // Set the button types.
        ButtonType submit = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submit, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField value1 = new TextField();
        value1.setPromptText(label1);
        grid.add(new Label(label1), 0, 0);
        grid.add(value1, 1, 0);

        TextField value2 = new TextField();
        value2.setPromptText(label2);
        grid.add(new Label(label2), 0, 1);
        grid.add(value2, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node submitBtn = dialog.getDialogPane().lookupButton(submit);
        submitBtn.setDisable(true);
        value1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty() && !value2.textProperty().get().trim().isEmpty()) submitBtn.setDisable(false);
        });
        value2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty() && !value1.textProperty().get().trim().isEmpty()) submitBtn.setDisable(false);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(value1::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(btn -> {
            if (btn == submit) {
                return new Pair<>(value1.getText(), value2.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
