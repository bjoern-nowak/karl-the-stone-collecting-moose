package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class RootController {

    private final Model model;

    @FXML
    private Tab tab;
    @FXML
    private Label status;

    @FXML
    private ActionController actionController;
    @FXML
    private ProgramController programController;
    @FXML
    private WorldController worldController;

    public RootController(Model model) {
        this.model = model;
        model.getWorld().random();
        model.worldChanged.addListener((obs, oldV, newV) -> {
            if (newV) {
                worldController.postInitialize();
                model.worldChanged.set(false);
            }
        });
    }

    public void initialize(){
        initBinds();
        postInitialize();
    }

    private void postInitialize() {
        programController.postInitialize();
        worldController.postInitialize();
        actionController.postInitialize();
    }

    private void initBinds() {
        actionController.setModel(model);
        programController.setModel(model);
        worldController.setModel(model);

        status.textProperty().bindBidirectional(model.statusText);
    }

    void compileSilently() {
        actionController.compile(true);
    }
}
