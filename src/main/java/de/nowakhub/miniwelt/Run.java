package de.nowakhub.miniwelt;

import de.nowakhub.miniwelt.controller.ActionController;
import de.nowakhub.miniwelt.controller.EditorController;
import de.nowakhub.miniwelt.controller.RootController;
import de.nowakhub.miniwelt.controller.WorldController;
import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;
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


        World world = new World(10, 10);
        Actor actor = new Actor(world);

        RootController rootController = new RootController(world, actor);
        ActionController actionController = new ActionController(world, actor);
        EditorController editorController = new EditorController(world, actor);
        WorldController worldController = new WorldController(world, actor);

        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("view/root.fxml"));
        rootLoader.setControllerFactory(type -> {
            if (type.equals(RootController.class)) return rootController;
            if (type.equals(ActionController.class)) return actionController;
            if (type.equals(EditorController.class)) return editorController;
            if (type.equals(WorldController.class)) return worldController;
            try {
                return type.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        });

        primaryStage.setTitle("Karl the fire prevention officer");
        primaryStage.setScene(new Scene(rootLoader.load()));
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("stop -> Thread: " + Thread.currentThread().getName());
    }

}
