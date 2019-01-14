package de.nowakhub.miniwelt;

import de.nowakhub.miniwelt.controller.Alerts;
import de.nowakhub.miniwelt.controller.ExamplesDB;
import de.nowakhub.miniwelt.controller.RootController;
import de.nowakhub.miniwelt.controller.TabsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Run extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void init() {
        System.out.println("init -> Thread: " + Thread.currentThread().getName());
        for (String string : getParameters().getRaw()) {
            System.out.println("init -> Parameter: " + string);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> Platform.runLater(() -> Alerts.showException(t, ex)));
        Thread.currentThread().setUncaughtExceptionHandler(Alerts::showException);

        try {
            System.out.println("start -> Thread: " + Thread.currentThread().getName());
            FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("view/root.fxml"));
            primaryStage.setTitle("Karl the stone-collecting moose");
            Parent parent = rootLoader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                TabsController.confirmExitIfNecessaery(event, ((RootController) rootLoader.getController()).getTabs());
                if (event.isConsumed()) ExamplesDB.close();
            });
            primaryStage.show();
            ExamplesDB.open();
        } catch (Exception ex) {
            Alerts.showException(Thread.currentThread(), ex);
        }
    }

    @Override
    public void stop() {
        System.out.println("stop -> Thread: " + Thread.currentThread().getName());
    }

}
