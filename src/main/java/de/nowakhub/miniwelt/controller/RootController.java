package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.World;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;

import java.util.Random;

public class RootController {

    final ObjectProperty<World> world = new SimpleObjectProperty<>();

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;


    @FXML
    public MenuBar menubar;

    @FXML
    public MenubarController menubarController;
    @FXML
    public ToolbarController toolbarController;
    @FXML
    public EditorController editorController;
    @FXML
    public ViewController viewController;


    public void initialize() {
        initBinds();
        initModel();
        viewController.postInitialize();
    }

    private void initBinds() {
        // TODO refactor using a own model controller (pass this to other controller)
        world.bindBidirectional(menubarController.world);
        world.bindBidirectional(toolbarController.world);
        world.bindBidirectional(editorController.world);
        world.bindBidirectional(viewController.world);

        menubarController.mouseMode.bindBidirectional(toolbarController.mouseMode);
        menubarController.mouseMode.bindBidirectional(editorController.mouseMode);
        menubarController.mouseMode.bindBidirectional(viewController.mouseMode);

        status.textProperty().bindBidirectional(menubarController.statusText);
        status.textProperty().bindBidirectional(toolbarController.statusText);
        status.textProperty().bindBidirectional(editorController.statusText);
        status.textProperty().bindBidirectional(viewController.statusText);
    }


    private void initModel() {
        world.set(new World(10, 10));
        world.get().placeStart(2,2);
        world.get().placeActor(2,2);

        for (int x = 0; x < world.get().sizeRow(); x++) {
            for (int y = 0; y < world.get().sizeCol(); y++) {
                if (world.get().isFieldAtBorder(x, y)) {
                    world.get().placeObstacle(x,y);
                } else {
                    double random = new Random().nextDouble();
                    if (random < 0.2) {
                        world.get().placeObstacle(x,y);
                    } else if (random < 0.3) {
                        world.get().placeItem(x,y);
                    }
                }
            }
        }
    }
}
