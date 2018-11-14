package de.nowakhub.miniwelt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
    public void start(Stage primaryStage) throws Exception{
        System.out.println("start -> Thread: " + Thread.currentThread().getName());
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("view/root.fxml"));
        primaryStage.setTitle("Karl the fire prevention officer");
        primaryStage.setScene(new Scene(rootLoader.load()));
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("stop -> Thread: " + Thread.currentThread().getName());
    }

}
