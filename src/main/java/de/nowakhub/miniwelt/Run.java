package de.nowakhub.miniwelt;

import de.nowakhub.miniwelt.controller.RootController;
import de.nowakhub.miniwelt.controller.TabsController;
import de.nowakhub.miniwelt.controller.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Run extends Application {

    private static final String TITLE = "Karl the stone-collecting moose";

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void init() {
        // print start arguments if present
        for (String string : getParameters().getRaw()) {
            System.out.println("init -> Parameter: " + string);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // in any case of unhandled exception: show it (part 1)
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> Platform.runLater(() -> Alerts.showException(t, ex)));
        Thread.currentThread().setUncaughtExceptionHandler(Alerts::showException);

        try {
            // load root fxml document
            FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("view/root.fxml"), Message.getBundle());
            Parent parent = rootLoader.load();
            Scene scene = new Scene(parent);

            // define stage and stage close handling
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(Run.class.getResourceAsStream("icon.png")));
            primaryStage.setOnCloseRequest(event -> TabsController.confirmExitIfNecessaery(event, ((RootController) rootLoader.getController()).getTabs()));

            // finally show application
            primaryStage.show();

            // pre-open derby database (as long application is running)
            ExamplesDB.open();

            // set window title bases on role (tutor/student)
            if (PropsCtx.hasServer())
                if (PropsCtx.isTutor())
                    primaryStage.setTitle(TITLE + String.format(" [Tutor on %s:%d]", PropsCtx.getHost(), PropsCtx.getPort()));
                else
                    Student.idProperty().addListener((observable, oldValue, newValue) ->
                            primaryStage.setTitle(TITLE +String.format(" [Student #%d on %s:%d]", newValue.intValue(), PropsCtx.getHost(), PropsCtx.getPort())));

        } catch (Exception ex) {
            // in any case of unhandled exception: show it (part 2)
            Alerts.showException(Thread.currentThread(), ex);
        }
    }

    @Override
    public void stop() {
        ExamplesDB.close();
        Tutor.stop();
    }

}
