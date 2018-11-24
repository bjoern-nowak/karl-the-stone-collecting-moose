package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.File;

public class RootController {

    private final Model model = new Model();

    @FXML
    private Tab tab;
    @FXML
    private Label status;

    @FXML
    private MenubarController menubarController;
    @FXML
    private ToolbarController toolbarController;
    @FXML
    private ProgramController programController;
    @FXML
    private WorldController worldController;

    public RootController(TabsController tabsController, File programFile) {
        model.tabsController = tabsController;
        model.programFile = programFile;
    }

    public void initialize(){
        initBinds();
        programController.postInitialize();
        worldController.postInitialize();
    }

    private void initBinds() {
        status.textProperty().bindBidirectional(model.statusText);
        tab.textProperty().bindBidirectional(model.programText);

        menubarController.model = model;
        toolbarController.model = model;
        programController.model = model;
        worldController.model = model;

    }
}
