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
    }

    public void initialize(){
        initBinds();
        programController.postInitialize();
        worldController.postInitialize();
    }

    private void initBinds() {
        actionController.setModel(model);
        programController.setModel(model);
        worldController.setModel(model);

        tab.textProperty().bindBidirectional(model.tabText);
        status.textProperty().bindBidirectional(model.statusText);

    }
}