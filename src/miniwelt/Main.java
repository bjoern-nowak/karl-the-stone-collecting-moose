package miniwelt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


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

        Parent root = FXMLLoader.load(getClass().getResource("miniwelt.fxml"));
        primaryStage.setTitle("Karl der Brandschutzbeauftragte");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("stop -> Thread: " + Thread.currentThread().getName());
    }

}
