package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.util.Random;

public class RootController {

    final ObjectProperty<World> world = new SimpleObjectProperty<>();
    final ObjectProperty<Actor> actor = new SimpleObjectProperty<>();

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;

    @FXML
    public ActionController actionController;
    @FXML
    public EditorController editorController;
    @FXML
    public ViewController viewController;


    public void initialize() {
        initBinds();
        initWorld();
        viewController.postInitialize();
    }

    private void initBinds() {
        world.bindBidirectional(actionController.world);
        world.bindBidirectional(editorController.world);
        world.bindBidirectional(viewController.world);
        actor.bindBidirectional(actionController.actor);
        actor.bindBidirectional(editorController.actor);
        actor.bindBidirectional(viewController.actor);

        actionController.mouseMode.bindBidirectional(editorController.mouseMode);
        actionController.mouseMode.bindBidirectional(viewController.mouseMode);

        status.textProperty().bindBidirectional(actionController.statusText);
        status.textProperty().bindBidirectional(editorController.statusText);
        status.textProperty().bindBidirectional(viewController.statusText);
    }

    private void initWorld() {
        world.set(new World(10, 10));
        actor.set(new Actor(world.get()));

        for (int x = 0; x < world.get().getSizeX(); x++) {
            for (int y = 0; y < world.get().getSizeY(); y++) {
                if (world.get().isBorder(x, y)) {
                    world.get().placeWall(x,y);
                } else {
                    double random = new Random().nextDouble();
                    if (random < 0.2) {
                        world.get().placeWall(x,y);
                    } else if (random < 0.3) {
                        world.get().placeDanger(x,y);
                    }
                }
            }
        }
        world.get().placeOffice(2,2);
        world.get().placeKarl(2,2);
    }
}
