package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

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

        world.set(new World(10, 10));
        actor.set(new Actor(world.get()));

        for (int x = 0; x < world.get().getSizeX(); x++) {
            for (int y = 0; y < world.get().getSizeY(); y++) {
                world.get().placeWall(x,y);
            }
        }
        world.get().placeDanger(1,1);
        world.get().placeKarl(2,2);
        world.get().placeOffice(3,3);

        viewController.postInitialize();
    }
}
